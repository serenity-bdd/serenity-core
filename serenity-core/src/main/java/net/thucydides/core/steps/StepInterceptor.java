package net.thucydides.core.steps;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import net.bytebuddy.implementation.bind.annotation.*;
import net.serenitybdd.annotations.*;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.core.SkipNested;
import net.serenitybdd.core.steps.HasCustomFieldValues;
import net.serenitybdd.markers.CanBeSilent;
import net.serenitybdd.markers.IsHidden;
import net.serenitybdd.markers.IsSilent;
import net.serenitybdd.model.IgnoredStepException;
import net.serenitybdd.model.PendingStepException;
import net.serenitybdd.model.environment.ConfiguredEnvironment;
import net.serenitybdd.model.exceptions.SerenityManagedException;
import net.thucydides.core.steps.events.*;
import net.thucydides.core.steps.interception.DynamicExampleStepInterceptionListener;
import net.thucydides.core.steps.interception.StepInterceptionListener;
import net.thucydides.core.steps.session.TestSession;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.adapters.TestFramework;
import net.thucydides.model.domain.TestResult;
import net.thucydides.model.domain.stacktrace.StackTraceSanitizer;
import net.thucydides.model.screenshots.ScreenshotAndHtmlSource;
import net.thucydides.model.steps.AnnotatedStepDescription;
import net.thucydides.model.steps.ExecutedStepDescription;
import net.thucydides.model.steps.ScreenplayInspector;
import net.thucydides.model.steps.StepFailure;
import net.thucydides.model.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static net.thucydides.model.ThucydidesSystemProperty.MANUAL_TASK_INSTRUMENTATION;

/**
 * Listen to step results and publish notification messages.
 * The step interceptor is designed to work on a given test case or user story.
 * It logs test step results so that they can be reported on at the end of the test case.
 *
 * @author johnsmart
 */
public class StepInterceptor implements MethodErrorReporter, Interceptor {

    private final Class<?> testStepClass;
    private Throwable error = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(StepInterceptor.class);
    private final EnvironmentVariables environmentVariables;
    private static final ThreadLocal<Class> expectedExceptionType = new ThreadLocal<>();

    public static void setExpectedExceptionType(Class expectedException) {
        expectedExceptionType.set(expectedException);
    }

    public static void resetExpectedExceptionType() {
        expectedExceptionType.remove();
    }

    private final List<StepInterceptionListener> listeners = new ArrayList<>();

    CleanupMethodLocator cleanupMethodLocator;

    StepInterceptor(final Class<?> testStepClass) {
        this.testStepClass = testStepClass;
        this.environmentVariables = ConfiguredEnvironment.getEnvironmentVariables();
        this.cleanupMethodLocator = new CleanupMethodLocator();

        listeners.add(new DynamicExampleStepInterceptionListener());
    }

    @RuntimeType
    public Object intercept(
            @Origin Method method,
            @This Object target,
            @AllArguments Object[] args,
            @SuperMethod Method zuper
    ) throws Throwable {
        Object result;
        if (baseClassMethod(method, target) || isAStepThatMayThrowAnException(method)) {
            result = runBaseObjectMethod(target, method, args, zuper);
        } else {
            result = testStepResult(target, method, args, zuper);
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

    private boolean baseClassMethod(final Method method, Object obj) {
        Class callingClass = obj.getClass();
        boolean isACoreLanguageMethod = (OBJECT_METHODS.contains(method.getName()));
        boolean methodDoesNotComeFromThisClassOrARelatedParentClass = !declaredInSameDomain(method, callingClass);
        boolean isSilentMethod = isSilent(callingClass, method, obj);
        boolean isHiddenMethod = isHidden(callingClass);
        return (isACoreLanguageMethod || methodDoesNotComeFromThisClassOrARelatedParentClass || isSilentMethod || isHiddenMethod);
    }

    private boolean isSilent(Class callingClass, Method method, Object obj) {
        if (IsSilent.class.isAssignableFrom(callingClass)) {
            return true;
        }

        if ((CanBeSilent.class.isAssignableFrom(callingClass) && method.getName().equals("isSilent"))) {
            return true;
        }

        if (CanBeSilent.class.isAssignableFrom(callingClass) && ((CanBeSilent) obj).isSilent()) {
            return true;
        }

        if (isNestedInSilentTask()) {
            return true;
        }

        return isNotAStepAnnotatedMethodWhenManualInstrumentationIsActive(method);
    }

    private boolean isHidden(Class<?> callingClass) {
        return IsHidden.class.isAssignableFrom(callingClass);
    }

    private boolean isNotAStepAnnotatedMethodWhenManualInstrumentationIsActive(Method method) {
        if (manualTaskInstrumentation()) {
            return method.getAnnotation(Step.class) == null;
        }
        return false;
    }

    private boolean manualTaskInstrumentation() {
        return (MANUAL_TASK_INSTRUMENTATION.booleanFrom(environmentVariables, false));
    }

    private boolean isNestedInSilentTask() {
        return asList(new Exception().getStackTrace())
                .stream()
                .anyMatch(element -> element.getMethodName().equals("performSilently"));
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
        List<String> packages = Splitter.on(".").omitEmptyStrings().splitToList(methodPackage);

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
        } catch (IllegalAccessException | NoSuchFieldException e) {
            return method;
        }
    }

    private Object testStepResult(final Object obj, final Method method,
                                  final Object[] args, final Method zuperMethod) throws Throwable {

        if (!isATestStep(method)) {
            return runNormalMethod(obj, method, args, zuperMethod);
        }

        listeners.forEach(listener -> listener.start(obj, method, args, zuperMethod));

        Object result = runOrSkipMethod(obj, method, args, zuperMethod);

        listeners.forEach(listener -> listener.end(obj, method, args, zuperMethod));

        return result;
    }

    private Object runOrSkipMethod(Object obj, Method method, Object[] args, Method zuperMethod) throws Throwable {
        Object result;
        if (shouldSkip(method) && !stepIsCalledFromCleanupMethod()) {
            result = skipStepMethod(obj, method, args, zuperMethod);
        } else {
            notifyStepStarted(obj, method, args);
            result = runTestStep(obj, method, args, zuperMethod);
        }
        return result;
    }

    private void endDynamicExampleIfPresent() {
    }

    private void startDynamicExampleIfPresent() {
    }

    private boolean stepIsCalledFromCleanupMethod() {
        return cleanupMethodLocator.currentMethodWasCalledFromACleanupMethod();
    }

    private Object skipStepMethod(final Object obj, Method method, final Object[] args, final Method zuperMethod) throws Exception {
        if ((aPreviousStepHasFailed() || testAssumptionViolated()) && (!shouldExecuteNestedStepsAfterFailures())) {
            notifySkippedStepStarted(obj, method, args);
            notifySkippedStepFinishedFor(method, args);
            return appropriateReturnObject(obj, method);
        } else {
            notifySkippedStepStarted(obj, method, args);
            return skipTestStep(obj, method, args, zuperMethod);
        }
    }

    private boolean shouldExecuteNestedStepsAfterFailures() {
        return ThucydidesSystemProperty.DEEP_STEP_EXECUTION_AFTER_FAILURES.booleanFrom(environmentVariables, false);
    }

    private Object skipTestStep(Object obj, Method method, Object[] args, Method zuperMethod) throws Exception {
        Object skippedReturnObject = runSkippedMethod(obj, method, args, zuperMethod);
        notifyStepSkippedFor(method, args);
        LOGGER.debug("SKIPPED STEP: {}", StepName.fromStepAnnotationIn(method).orElse(method.getName()));
        return appropriateReturnObject(skippedReturnObject, obj, method);
    }

    private Object runSkippedMethod(Object obj, Method method, Object[] args, Method zuperMethod) {
        LOGGER.trace("Running test step " + StepName.fromStepAnnotationIn(method).orElse(method.getName()));
        if (TestSession.isSessionStarted()) {
            SuspendWebdriverCallsEvent suspendWebdriverCallsEvent = new SuspendWebdriverCallsEvent();
            TestSession.addEvent(suspendWebdriverCallsEvent);
        } else {
            StepEventBus.getParallelEventBus().temporarilySuspendWebdriverCalls();
        }

        Object result = runIfNestedMethodsShouldBeRun(obj, method, args, zuperMethod);

        if (TestSession.isSessionStarted()) {
            ReenableWebdriverCallsEvent reenableWebdriverCallsEvent = new ReenableWebdriverCallsEvent();
            TestSession.addEvent(reenableWebdriverCallsEvent);
        } else {
            StepEventBus.getParallelEventBus().reenableWebdriverCalls();
        }

        return result;
    }

    private Object runIfNestedMethodsShouldBeRun(Object obj, Method method, Object[] args, Method zuperMethod) {
        Object result = null;
        try {
            if (shouldRunNestedMethodsIn(method)) {
                result = invokeMethod(obj, args, zuperMethod);
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

    private Object appropriateReturnObject(final Object returnedValue, final Object obj, final Method method) {
        if (returnedValue != null) {
            return returnedValue;
        } else {
            return appropriateReturnObject(obj, method);
        }
    }

    enum PrimitiveReturnType {
        STRING, LONG, INTEGER, DOUBLE, FLOAT, BOOLEAN, VOID, UNSUPPORTED
    }

    private PrimitiveReturnType returnTypeOf(final Method method) {
        Class<?> returnType = method.getReturnType();
        if (returnType == String.class) {
            return PrimitiveReturnType.STRING;
        }

        if (Long.class.isAssignableFrom(returnType) || returnType.getName().equals("long")) {
            return PrimitiveReturnType.LONG;
        }

        if (Integer.class.isAssignableFrom(returnType) || returnType.getName().equals("int")) {
            return PrimitiveReturnType.INTEGER;
        }

        if (Double.class.isAssignableFrom(returnType) || returnType.getName().equals("double")) {
            return PrimitiveReturnType.DOUBLE;
        }

        if (Float.class.isAssignableFrom(returnType) || returnType.getName().equals("float")) {
            return PrimitiveReturnType.FLOAT;
        }

        if (Boolean.class.isAssignableFrom(returnType) || returnType.getName().equals("boolean")) {
            return PrimitiveReturnType.BOOLEAN;
        }

        if (returnType.getName().equals("void")) {
            return PrimitiveReturnType.VOID;
        }

        return PrimitiveReturnType.UNSUPPORTED;

    }

    Object appropriateReturnObject(final Object obj, final Method method) {
        if (method.getReturnType().isAssignableFrom(obj.getClass())) {
            return obj;
        }

        if (returnTypeIsPrimativeFor(method)) {
            return primativeDefaultValueFor(method);
        }
        return mockedReturnObjectFor(method);
    }

    private Object mockedReturnObjectFor(Method method) {
        try {
            return Mockito.mock(method.getReturnType());
        } catch (RuntimeException tooHardToMockLetsJustCallItQuits) {
            return null;
        }
    }

    private boolean returnTypeIsPrimativeFor(Method method) {
        return returnTypeOf(method) != PrimitiveReturnType.UNSUPPORTED;
    }

    private Object primativeDefaultValueFor(Method method) {
        switch (returnTypeOf(method)) {
            case VOID:
                return null;
            case STRING:
                return "";
            case LONG:
                return 0L;
            case INTEGER:
                return 0;
            case FLOAT:
                return 0.0F;
            case DOUBLE:
                return 0.0D;
            case BOOLEAN:
                return Boolean.FALSE;
            default:
                return null;
        }
    }

    private boolean shouldSkip(final Method methodOrStep) {
        if (aPreviousStepHasFailed() && !isSoftAssert()) {
            return true;
        }
        return testIsPending() || isDryRun() || isPending(methodOrStep) || isIgnored(methodOrStep);
    }

    private boolean testIsPending() {
        return getStepEventBus().currentTestIsSuspended();
    }

    private boolean testAssumptionViolated() {
        return getStepEventBus().assumptionViolated();
    }

    private boolean aPreviousStepHasFailed() {
        boolean aPreviousStepHasFailed = getStepEventBus().aStepInTheCurrentTestHasFailed();
        return aPreviousStepHasFailed;
    }

    private boolean isDryRun() {
        return getStepEventBus().isDryRun();
    }

    private boolean isSoftAssert() {
        return getStepEventBus().softAssertsActive();
    }

    private Object runBaseObjectMethod(final Object obj, final Method method, final Object[] args, final Method zuperMethod)
            throws Throwable {
        return invokeMethod(obj, args, zuperMethod);
    }

    private Object runNormalMethod(final Object obj, final Method method, final Object[] args, final Method zuperMethod)
            throws Throwable {

        Object result = DefaultValue.defaultReturnValueFor(method, obj);

        return withNonStepMethodRunner(method, obj.getClass())
                .invokeMethodAndNotifyFailures(obj, method, args, zuperMethod, result);
    }

    private MethodRunner withNonStepMethodRunner(final Method methodOrStep, final Class callingClass) {
        return (shouldRunInDryRunMode(methodOrStep, callingClass)) ? new DryRunMethodRunner() : new NormalMethodRunner(this);
    }

    private boolean shouldRunInDryRunMode(final Method methodOrStep, final Class callingClass) {
        return !stepIsCalledFromCleanupMethod() && ((aPreviousStepHasFailed() || testIsPending() || isDryRun()));// && declaredInSameDomain(methodOrStep, callingClass));
    }

    public void reportMethodError(Throwable generalException, Object obj, Method method, Object[] args) throws Throwable {
        error = SerenityManagedException.detachedCopyOf(generalException);
        Throwable assertionError = ErrorConvertor.convertToAssertion(error);
        notifyStepStarted(obj, method, args);
        notifyOfStepFailure(obj, method, args, assertionError);
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

    private boolean isAStepThatMayThrowAnException(final Method method) {
        return expectedExceptionType.get() != null;
    }

    private boolean isAThucydidesStep(Annotation annotation) {
        return (annotation instanceof Step) || (annotation instanceof StepGroup);
    }

    private boolean isATestStep(final Method method) {
        return isAnnotatedWithAValidStepAnnotation(method) || ScreenplayInspector.isAScreenplayPerformAsMethod(method);
    }

    private boolean isIgnored(final Method method) {
        return TestAnnotations.isIgnored(method);
    }

    private Object runTestStep(final Object obj, final Method method,
                               final Object[] args, final Method zuperMethod) throws Throwable {

        String callingClass = testContext();
        LOGGER.debug("STARTING STEP: {} - {}", callingClass, StepName.fromStepAnnotationIn(method).orElse(method.getName()));
        Object result = null;
        try {
            result = executeTestStepMethod(obj, method, args, zuperMethod, result);
            LOGGER.debug("STEP DONE: {}", StepName.fromStepAnnotationIn(method).orElse(method.getName()));
        } catch (AssertionError failedAssertion) {
            error = failedAssertion;
            logStepFailure(obj, method, args, failedAssertion);
            result = appropriateReturnObject(obj, method);
        } catch (Throwable testErrorException) {
            if (TestFramework.support().isAssumptionViolatedException(testErrorException)) {
                result = appropriateReturnObject(obj, method);
            } else {
                error = SerenityManagedException.detachedCopyOf(testErrorException);
                logStepFailure(obj, method, args, ErrorConvertor.convertToAssertion(error));
                result = appropriateReturnObject(obj, method);
            }
        }
        return result;
    }

    private void logStepFailure(Object object, Method method, Object[] args, Throwable assertionError) throws Throwable {
        if (StepEventBus.getEventBus().aStepInTheCurrentTestHasFailed() && !StepEventBus.getEventBus().softAssertsActive()) {
            if (shouldThrowExceptionImmediately()) {
                // same as in notifyOfStepFailure()
                finishAnyCucumberSteps();
                throw assertionError;
            }
            return;
        }
        notifyOfStepFailure(object, method, args, assertionError);
        LOGGER.debug("STEP FAILED: {} - {}", StepName.fromStepAnnotationIn(method).orElse(method.getName()), assertionError.getMessage());
    }

    private Object executeTestStepMethod(Object obj, Method method, Object[] args, Method zuperMethod, Object result) throws Throwable {
        try {
            result = invokeMethod(obj, args, zuperMethod);
            notifyStepFinishedFor(method, args);
        } catch (PendingStepException pendingStep) {
            notifyStepPending(pendingStep.getMessage());
        } catch (IgnoredStepException ignoredStep) {
            notifyStepIgnored();
        } catch (Throwable throwable) {
            if (TestFramework.support().isAssumptionViolatedException(throwable)) {
                notifyAssumptionViolated(throwable.getMessage());
            } else {
                throw throwable;
            }
        }

        Preconditions.checkArgument(true);
        return result;
    }

    private Object invokeMethod(final Object obj, final Object[] args, final Method zuperMethod) throws Throwable {
        try {
            return zuperMethod.invoke(obj, args);
        } catch (InvocationTargetException invocationTargetException) {
            if (isAnAssertionError(invocationTargetException) && StepEventBus.getEventBus().softAssertsActive()) {
                return null;
            } else {
                throw invocationTargetException.getCause();
            }
        }
    }

    private boolean isAnAssertionError(InvocationTargetException invocationTargetException) {
        return (invocationTargetException.getTargetException() instanceof AssertionError)
                || (invocationTargetException.getCause() instanceof AssertionError);
    }

    private boolean isPending(final Method method) {
        return (method.getAnnotation(Pending.class) != null);
    }

    private void notifyStepFinishedFor(final Method method, final Object[] args) {
        if (TestSession.isSessionStarted()) {
            LOGGER.debug("SRP:Actor finished step in session " + Thread.currentThread());
            List<ScreenshotAndHtmlSource> screenshotList = TestSession.getTestSessionContext().getStepEventBus().takeScreenshots();
            TestSession.addEvent(new StepFinishedEvent(screenshotList));
        } else {
            StepEventBus.getParallelEventBus().stepFinished();
        }
    }

    private void notifySkippedStepFinishedFor(final Method method, final Object[] args) {
        if (TestSession.isSessionStarted()) {
            StepIgnoredEvent stepIgnoredEvent = new StepIgnoredEvent();
            TestSession.addEvent(stepIgnoredEvent);
        } else {
            StepEventBus.getParallelEventBus().stepIgnored();
        }
    }

    private void notifyStepPending(String message) {
        if (TestSession.isSessionStarted()) {
            TestSession.addEvent(new StepPendingEvent(message));
        } else {
            StepEventBus.getParallelEventBus().stepPending(message);
        }
    }

    private void notifyAssumptionViolated(String message) {
        if (TestSession.isSessionStarted()) {
            TestSession.addEvent(new AssumptionViolatedEvent(message));
        } else {
            StepEventBus.getParallelEventBus().assumptionViolated(message);
        }
    }

    private void notifyStepIgnored() {
        if (TestSession.isSessionStarted()) {
            TestSession.addEvent(new StepIgnoredEvent());
        } else {
            StepEventBus.getParallelEventBus().stepIgnored();
        }
    }

    private String getTestNameFrom(final Method method, final Object[] args) {
        return StepNamer.nameFor(method, args);
    }

    private void notifyStepSkippedFor(final Method method, final Object[] args) {
        if (TestSession.isSessionStarted()) {
            if (isPending(method)) {
                TestSession.addEvent(new StepPendingEvent());
            } else {
                TestSession.addEvent(new StepIgnoredEvent());
            }
        } else {
            if (isPending(method)) {
                StepEventBus.getParallelEventBus().stepPending();
            } else {
                StepEventBus.getParallelEventBus().stepIgnored();
            }
        }
    }

    private void notifyOfStepFailure(final Object object, final Method method, final Object[] args,
                                     final Throwable cause) throws Throwable {
        ExecutedStepDescription description = ExecutedStepDescription.of(testStepClass, getTestNameFrom(method, args), args)
                .withDisplayedFields(fieldValuesIn(object));

        StepFailure failure = new StepFailure(description, cause);

        if (!TestSession.isSessionStarted()) {
            //no step failure for the parallel runner, rely only on Cucumber events
            StepEventBus.getParallelEventBus().stepFailed(failure);
        } else {
            List<ScreenshotAndHtmlSource> screenshotList = TestSession.getTestSessionContext().getStepEventBus().takeScreenshots(TestResult.FAILURE);
            TestSession.addEvent(new StepFailedEvent(failure,screenshotList));
        }
        if (shouldThrowExceptionImmediately() && !StepEventBus.getEventBus().softAssertsActive()) {
            finishAnyCucumberSteps();
            throw cause;
        }
    }

    private void finishAnyCucumberSteps() {
        if (TestSession.isSessionStarted()) {
            WrapupCurrentCucumberStepEvent wrapupCurrentCucumberStepEvent = new WrapupCurrentCucumberStepEvent();
            LOGGER.debug("SRP:Actor started event in session " + wrapupCurrentCucumberStepEvent + " " + Thread.currentThread());
            TestSession.addEvent(wrapupCurrentCucumberStepEvent);
        } else {
            StepEventBus.getParallelEventBus().wrapUpCurrentCucumberStep();
        }
    }

    private boolean shouldThrowExceptionImmediately() {
        return Serenity.shouldThrowErrorsImmediately();
    }

    private void notifyStepStarted(final Object object, final Method method, final Object[] args) {
        ExecutedStepDescription description = ExecutedStepDescription.of(testStepClass, getTestNameFrom(method, args), args)
                .withDisplayedFields(fieldValuesIn(object));
        if (TestSession.isSessionStarted()) {
            StepStartedEvent stepStartedEvent = new StepStartedEvent(description);
            LOGGER.debug("SRP:Actor started step in session " + stepStartedEvent + " " + Thread.currentThread());
            TestSession.addEvent(stepStartedEvent);
        } else {
            StepEventBus.getParallelEventBus().stepStarted(description);
        }
    }

    private Map<String, Object> fieldValuesIn(Object object) {
        Map<String, Object> coreFieldValues = Fields.of(object).asMap();

        if (object instanceof HasCustomFieldValues) {
            coreFieldValues.putAll(((HasCustomFieldValues) object).getCustomFieldValues());
        }

        return coreFieldValues;
    }

    private void notifySkippedStepStarted(final Object object, final Method method, final Object[] args) {

        ExecutedStepDescription description = ExecutedStepDescription.of(testStepClass, getTestNameFrom(method, args), args)
                .withDisplayedFields(fieldValuesIn(object));
        getStepEventBus().skippedStepStarted(description);
    }

    String testContext() {
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

    private StepEventBus getStepEventBus() {
        if (TestSession.isSessionStarted()) {
            return TestSession.getTestSessionContext().getStepEventBus();
        } else {
            return StepEventBus.getParallelEventBus();
        }
    }

}
