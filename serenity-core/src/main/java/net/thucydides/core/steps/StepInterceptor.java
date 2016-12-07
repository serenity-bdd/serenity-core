package net.thucydides.core.steps;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import net.serenitybdd.core.IgnoredStepException;
import net.serenitybdd.core.PendingStepException;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.core.SkipNested;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.serenitybdd.core.exceptions.SerenityManagedException;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.annotations.*;
import net.thucydides.core.model.stacktrace.StackTraceSanitizer;
import net.thucydides.core.steps.service.CleanupMethodAnnotationProvider;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import org.junit.internal.AssumptionViolatedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import static net.thucydides.core.steps.ErrorConvertor.forError;

/**
 * Listen to step results and publish notification messages.
 * The step interceptor is designed to work on a given test case or user story.
 * It logs test step results so that they can be reported on at the end of the test case.
 *
 * @author johnsmart
 */
public class StepInterceptor implements MethodInterceptor, MethodErrorReporter {

    private final Class<?> testStepClass;
    private Throwable error = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(StepInterceptor.class);
    private final EnvironmentVariables environmentVariables;
    private final List<String> cleanupMethodsAnnotations = new ArrayList<>();

    public StepInterceptor(final Class<?> testStepClass) {
        this.testStepClass = testStepClass;
        this.environmentVariables = ConfiguredEnvironment.getEnvironmentVariables();
        Iterable<CleanupMethodAnnotationProvider> cleanupMethodAnnotationProviders = ServiceLoader.load(CleanupMethodAnnotationProvider.class);
        for(CleanupMethodAnnotationProvider cleanupMethodAnnotationProvider : cleanupMethodAnnotationProviders) {
            cleanupMethodsAnnotations.addAll(cleanupMethodAnnotationProvider.getCleanupMethodAnnotations());
        }
    }

    public Object intercept(final Object obj, final Method method,
                            final Object[] args, final MethodProxy proxy) throws Throwable {

        Object result;
        if (baseClassMethod(method, obj.getClass())) {
            result = runBaseObjectMethod(obj, method, args, proxy);
        } else {
            result = testStepResult(obj, method, args, proxy);
        }
        return result;

    }

    private final List<String> OBJECT_METHODS
            = Arrays.asList("toString",
            "equals",
            "hashcode",
            "clone",
            "notify",
            "notifyAll",
            "wait",
            "finalize",
            "getMetaClass");

    private boolean baseClassMethod(final Method method, final Class callingClass) {
        boolean isACoreLanguageMethod = (OBJECT_METHODS.contains(method.getName()));
        boolean methodDoesNotComeFromThisClassOrARelatedParentClass = !declaredInSameDomain(method, callingClass);
        return (isACoreLanguageMethod || methodDoesNotComeFromThisClassOrARelatedParentClass);
    }

    private boolean declaredInSameDomain(Method method, final Class callingClass) {
        return domainPackageOf(getRoot(method)).equals(domainPackageOf(callingClass));
    }

    private String domainPackageOf(Class callingClass) {
        Package classPackage = callingClass.getPackage();
        String classPackageName = (classPackage != null) ? classPackage.getName() : "";
        return packageDomainName(classPackageName);
    }

    private String packageDomainName(String methodPackage) {
        List<String> packages = Lists.newArrayList(Splitter.on(".").omitEmptyStrings().split(methodPackage));

        if (packages.size() == 0) {
            return "";
        } else if (packages.size() == 1) {
            return packages.get(0);
        } else {
            return packages.get(0) + "." + packages.get(1);
        }
    }

    private String domainPackageOf(Method method) {
        Package methodPackage = method.getDeclaringClass().getPackage();
        String methodPackageName = (methodPackage != null) ? methodPackage.getName() : "";
        return packageDomainName(methodPackageName);
    }

    private Method getRoot(Method method) {
        try {
            method.getClass().getDeclaredField("root").setAccessible(true);
            return (Method) method.getClass().getDeclaredField("root").get(method);
        } catch (IllegalAccessException e) {
            return method;
        } catch (NoSuchFieldException e) {
            return method;
        }
    }

    private Object testStepResult(final Object obj, final Method method,
                                  final Object[] args, final MethodProxy proxy) throws Throwable {

        if (!isATestStep(method)) {
            return runNormalMethod(obj, method, args, proxy);
        }

        if (shouldSkip(method) && !stepIsCalledFromCleanupMethod()) {
            return skipStepMethod(obj, method, args, proxy);
        } else {
            notifyStepStarted(obj, method, args);
            return runTestStep(obj, method, args, proxy);
        }
    }

    private boolean stepIsCalledFromCleanupMethod(){
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for(StackTraceElement stackTraceElement : stackTrace)
        {
            try {
                Method m = Class.forName(stackTraceElement.getClassName()).getMethod(stackTraceElement.getMethodName());
                if( m.getAnnotations() != null && m.getAnnotations().length > 0) {
                    for (Annotation a : m.getAnnotations()) {
                        if (cleanupMethodsAnnotations.contains(a.toString())) {
                            return true;
                        }
                    }
                }
            } catch(Exception ex) {}
        }
        return false;
    }

    private Object skipStepMethod(final Object obj, Method method, final Object[] args, final MethodProxy proxy) throws Exception {
        if ((aPreviousStepHasFailed() || testAssumptionViolated()) && (!shouldExecuteNestedStepsAfterFailures())) {
            notifySkippedStepStarted(obj, method, args);
            notifySkippedStepFinishedFor(method, args);
            return null;
        } else {
            notifySkippedStepStarted(obj, method, args);
            return skipTestStep(obj, method, args, proxy);
        }
    }

    private boolean shouldExecuteNestedStepsAfterFailures() {
        return ThucydidesSystemProperty.DEEP_STEP_EXECUTION_AFTER_FAILURES.booleanFrom(environmentVariables, false);
    }

    private Object skipTestStep(Object obj, Method method, Object[] args, MethodProxy proxy) throws Exception {
        Object skippedReturnObject = runSkippedMethod(obj, method, args, proxy);
        notifyStepSkippedFor(method, args);
        LOGGER.debug("SKIPPED STEP: {}", StepName.fromStepAnnotationIn(method).or(method.getName()));
        return appropriateReturnObject(skippedReturnObject, obj, method);
    }

    private Object runSkippedMethod(Object obj, Method method, Object[] args, MethodProxy proxy) {
        LOGGER.trace("Running test step " + StepName.fromStepAnnotationIn(method).or(method.getName()));
        StepEventBus.getEventBus().temporarilySuspendWebdriverCalls();
        Object result = runIfNestedMethodsShouldBeRun(obj, method, args, proxy);
        StepEventBus.getEventBus().reenableWebdriverCalls();
        return result;
    }

    private Object runIfNestedMethodsShouldBeRun(Object obj, Method method, Object[] args, MethodProxy proxy) {
        Object result = null;
        try {
            if (shouldRunNestedMethodsIn(method)) {
                result = invokeMethod(obj, args, proxy);
            }
        } catch (Throwable anyException) {
            LOGGER.trace("Ignoring exception thrown during a skipped test", anyException);
        }
        return result;
    }

    private boolean shouldRunNestedMethodsIn(Method method) {
        return !(TestAnnotations.shouldSkipNested(method) || shouldSkipNestedIn(method.getDeclaringClass()));
    }

    private boolean shouldSkipNestedIn(Class testStepClass) {
        return (SkipNested.class.isAssignableFrom(testStepClass));
    }

    Object appropriateReturnObject(final Object returnedValue, final Object obj, final Method method) {
        if (returnedValue != null) {
            return returnedValue;
        } else {
            return appropriateReturnObject(obj, method);
        }
    }

    Object appropriateReturnObject(final Object obj, final Method method) {
        if (method.getReturnType().isAssignableFrom(obj.getClass())) {
            return obj;
        } else {
            return null;
        }
    }

    private boolean shouldSkipMethod(final Method methodOrStep, final Class callingClass) {
        return ((aPreviousStepHasFailed() || testIsPending() || isDryRun()) && declaredInSameDomain(methodOrStep, callingClass));
    }

    private boolean shouldSkip(final Method methodOrStep) {
        if (aPreviousStepHasFailed() && !isSoftAssert()) {
            return true;
        }
        return testIsPending() || isDryRun() || isPending(methodOrStep) || isIgnored(methodOrStep);
    }

    private boolean testIsPending() {
        return StepEventBus.getEventBus().currentTestIsSuspended();
    }

    private boolean testAssumptionViolated() {
        return StepEventBus.getEventBus().assumptionViolated();
    }

    private boolean aPreviousStepHasFailed() {
        boolean aPreviousStepHasFailed = false;
        if (StepEventBus.getEventBus().aStepInTheCurrentTestHasFailed()) {
            aPreviousStepHasFailed = true;
        }
        return aPreviousStepHasFailed;
    }

    private boolean isDryRun() {
        return StepEventBus.getEventBus().isDryRun();
    }

    private boolean isSoftAssert() {
        return StepEventBus.getEventBus().softAssertsActive();
    }

    private Object runBaseObjectMethod(final Object obj, final Method method, final Object[] args, final MethodProxy proxy)
            throws Throwable {
        return invokeMethod(obj, args, proxy);
    }

    private Object runNormalMethod(final Object obj, final Method method, final Object[] args, final MethodProxy proxy)
            throws Throwable {

        Object result = DefaultValue.defaultReturnValueFor(method, obj);

//        if (shouldNotSkipMethod(method, obj.getClass())) {
//            result = invokeMethodAndNotifyFailures(obj, method, args, proxy, result);
//        }
//        return result;
        return withNonStepMethodRunner(method, obj.getClass())
               .invokeMethodAndNotifyFailures(obj, method, args, proxy, result);
    }

    private MethodRunner withNonStepMethodRunner(final Method methodOrStep, final Class callingClass) {
        return (shouldRunInDryRunMode(methodOrStep, callingClass)) ? new DryRunMethodRunner() : new NormalMethodRunner(this);
    }

    private boolean shouldRunInDryRunMode(final Method methodOrStep, final Class callingClass) {
        return ((aPreviousStepHasFailed() || testIsPending() || isDryRun()) && declaredInSameDomain(methodOrStep, callingClass));
    }

    public void reportMethodError(Throwable generalException, Object obj, Method method, Object[] args) throws Throwable {
        error = SerenityManagedException.detachedCopyOf(generalException);
        Throwable assertionError = forError(error).convertToAssertion();
        notifyStepStarted(obj, method, args);
        notifyOfStepFailure(obj, method, args, assertionError);
    }


    private Object invokeMethodAndNotifyFailures(Object obj, Method method, Object[] args, MethodProxy proxy, Object result) throws Throwable {
        try {
            result = invokeMethod(obj, args, proxy);
        } catch (Throwable generalException) {
            error = SerenityManagedException.detachedCopyOf(generalException);
            Throwable assertionError = forError(error).convertToAssertion();
            notifyStepStarted(obj, method, args);
            notifyOfStepFailure(obj, method, args, assertionError);
        }
        return result;
    }

    private boolean isAnnotatedWithAValidStepAnnotation(final Method method) {
        Annotation[] annotations = method.getAnnotations();
        for (Annotation annotation : annotations) {
            if (isAThucydidesStep(annotation) || (AnnotatedStepDescription.isACompatibleStep(annotation))) {
                return true;
            }
        }
        return false;
    }

    private boolean isAThucydidesStep(Annotation annotation) {
        return (annotation instanceof Step) || (annotation instanceof StepGroup);
    }

    private boolean isATestStep(final Method method) {
        return isAnnotatedWithAValidStepAnnotation(method);
    }

    private boolean isIgnored(final Method method) {
        return TestAnnotations.isIgnored(method);
    }

    private Object runTestStep(final Object obj, final Method method,
                               final Object[] args, final MethodProxy proxy) throws Throwable {

        String callingClass = testContext();
        LOGGER.debug("STARTING STEP: {} - {}", callingClass, StepName.fromStepAnnotationIn(method).or(method.getName()));
        Object result = null;
        try {
            result = executeTestStepMethod(obj, method, args, proxy, result);
            LOGGER.debug("STEP DONE: {}", StepName.fromStepAnnotationIn(method).or(method.getName()));
        } catch (AssertionError failedAssertion) {
            error = failedAssertion;
            logStepFailure(obj, method, args, failedAssertion);
            result = appropriateReturnObject(obj, method);
        } catch (AssumptionViolatedException assumptionFailed) {
            result = appropriateReturnObject(obj, method);
        } catch (Throwable testErrorException) {
            error = SerenityManagedException.detachedCopyOf(testErrorException);
            logStepFailure(obj, method, args, forError(error).convertToAssertion());
            result = appropriateReturnObject(obj, method);
        }
        return result;
    }

    private void logStepFailure(Object object, Method method, Object[] args, Throwable assertionError) throws Throwable {
        notifyOfStepFailure(object, method, args, assertionError);


        LOGGER.debug("STEP FAILED: {} - {}", StepName.fromStepAnnotationIn(method).or(method.getName()), assertionError.getMessage());
    }

    private Object executeTestStepMethod(Object obj, Method method, Object[] args, MethodProxy proxy, Object result) throws Throwable {
        try {
            result = invokeMethod(obj, args, proxy);
            notifyStepFinishedFor(method, args);
        } catch (PendingStepException pendingStep) {
            notifyStepPending(pendingStep.getMessage());
        } catch (IgnoredStepException ignoredStep) {
            notifyStepIgnored(ignoredStep.getMessage());
        } catch (AssumptionViolatedException assumptionViolated) {
            notifyAssumptionViolated(assumptionViolated.getMessage());
        }

        Preconditions.checkArgument(true);
        return result;
    }

    private Object invokeMethod(final Object obj, final Object[] args, final MethodProxy proxy) throws Throwable {
        return proxy.invokeSuper(obj, args);
    }

    private boolean isPending(final Method method) {
        return (method.getAnnotation(Pending.class) != null);
    }

    private void notifyStepFinishedFor(final Method method, final Object[] args) {
        StepEventBus.getEventBus().stepFinished();
    }

    private void notifySkippedStepFinishedFor(final Method method, final Object[] args) {
        StepEventBus.getEventBus().stepIgnored();
    }

    private void notifyStepPending(String message) {
        StepEventBus.getEventBus().stepPending(message);
    }

    private void notifyAssumptionViolated(String message) {
        StepEventBus.getEventBus().assumptionViolated(message);
    }

    private void notifyStepIgnored(String message) {
        StepEventBus.getEventBus().stepIgnored();
    }

    private String getTestNameFrom(final Method method, final Object[] args) {
        return getTestNameFrom(method, args, true);
    }

    private String getTestNameFrom(final Method method, final Object[] args, final boolean addMarkup) {
        if ((args == null) || (args.length == 0)) {
            return method.getName();
        } else {
            return testNameWithArguments(method, args, addMarkup);
        }
    }

    private String testNameWithArguments(final Method method,
                                         final Object[] args,
                                         final boolean addMarkup) {
        StringBuilder testName = new StringBuilder(method.getName());
        testName.append(": ");

        boolean isFirst = true;
        for (Object arg : args) {
            if (!isFirst) {
                testName.append(", ");
            }
            testName.append(StepArgumentWriter.readableFormOf(arg));
            isFirst = false;
        }
        return testName.toString();
    }

    private void notifyStepSkippedFor(final Method method, final Object[] args)
            throws Exception {

        if (isPending(method)) {
            StepEventBus.getEventBus().stepPending();
        } else {
            StepEventBus.getEventBus().stepIgnored();
        }
    }

    private void notifyOfStepFailure(final Object object, final Method method, final Object[] args,
                                     final Throwable cause) throws Throwable {
        ExecutedStepDescription description = ExecutedStepDescription.of(testStepClass, getTestNameFrom(method, args), args)
                .withDisplayedFields(fieldValuesIn(object));

        StepFailure failure = new StepFailure(description, cause);
        StepEventBus.getEventBus().stepFailed(failure);
        if (shouldThrowExceptionImmediately()) {
            throw cause;
        }
    }

    private boolean shouldThrowExceptionImmediately() {
        return Serenity.shouldThrowErrorsImmediately();
    }

    private void notifyStepStarted(final Object object, final Method method, final Object[] args) {
        ExecutedStepDescription description = ExecutedStepDescription.of(testStepClass, getTestNameFrom(method, args), args)
                .withDisplayedFields(fieldValuesIn(object));
        StepEventBus.getEventBus().stepStarted(description);
    }

    private Map<String, Object> fieldValuesIn(Object object) {
        return Fields.of(object).asMap();
    }

    private void notifySkippedStepStarted(final Object object, final Method method, final Object[] args) {

        ExecutedStepDescription description = ExecutedStepDescription.of(testStepClass, getTestNameFrom(method, args), args)
                .withDisplayedFields(fieldValuesIn(object));
        StepEventBus.getEventBus().skippedStepStarted(description);
    }

    public String testContext() {
        StackTraceSanitizer stackTraceSanitizer = StackTraceSanitizer.forStackTrace(new RuntimeException().getStackTrace());
        StackTraceElement[] stackTrace = stackTraceSanitizer.getSanitizedStackTrace();
        return (stackTrace.length > 0) ?
                getTestContextFrom(stackTraceSanitizer.getSanitizedStackTrace()[0]) : "";
    }

    private String getTestContextFrom(StackTraceElement stackTraceElement) {
        return shortenedClassName(stackTraceElement.getClassName()) + "." + stackTraceElement.getMethodName();
    }

    private String shortenedClassName(String className) {
        String[] classNameElements = StringUtils.split(className, ".");
        return classNameElements[classNameElements.length - 1];
    }
}
