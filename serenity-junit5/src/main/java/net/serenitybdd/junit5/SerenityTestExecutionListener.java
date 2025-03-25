package net.serenitybdd.junit5;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import net.serenitybdd.core.di.SerenityInfrastructure;
import net.serenitybdd.junit5.utils.ClassUtil;
import net.thucydides.model.configuration.SystemPropertiesConfiguration;
import net.thucydides.model.logging.ConsoleLoggingListener;
import net.thucydides.model.domain.*;
import net.thucydides.core.pages.Pages;
import net.thucydides.model.reports.ReportService;
import net.thucydides.core.steps.*;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.steps.StepListener;
import net.thucydides.model.util.Inflector;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.PreconditionViolationException;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.thucydides.model.reports.ReportService.getDefaultReporters;
import static net.thucydides.model.steps.TestSourceType.TEST_SOURCE_JUNIT5;

public class SerenityTestExecutionListener implements TestExecutionListener {

    private static final List<Class> expectedExceptions = Collections.synchronizedList(new ArrayList<>());

    private static final Logger logger = LoggerFactory.getLogger(SerenityTestExecutionListener.class);

    static {
        Instrumentation inst = ByteBuddyAgent.install();
        new AgentBuilder.Default()
                .type(ElementMatchers.named("org.junit.jupiter.api.Assertions"))
                .transform((builder, typeDescription, classLoader, module, protectionDomain) ->
                        builder.method(ElementMatchers.named("assertThrows"))
                                .intercept(Advice.to(AssertThrowsAdvice.class))
                ).installOn(inst);
    }

    private ReportService reportService;

    private SerenityTestExecutionSummary summary;

    private Pages pages;

    //key-> "ClassName.MethodName"
    //entries-> DataTable associated with method
    private final Map<String, DataTable> dataTables = Collections.synchronizedMap(new HashMap<>());


    private boolean isSerenityTest = false;

    public SerenityTestExecutionListener() {
    }

    private static File getOutputDirectory() {
        SystemPropertiesConfiguration systemPropertiesConfiguration = new SystemPropertiesConfiguration(new SystemEnvironmentVariables());
        return systemPropertiesConfiguration.getOutputDirectory();
    }

    @Override
    public synchronized void testPlanExecutionStarted(TestPlan testPlan) {
        this.summary = new SerenityTestExecutionSummary(testPlan);
        testPlan.getRoots().forEach(root -> {
            Set<TestIdentifier> children = testPlan.getChildren(root.getUniqueId());
            children.stream().filter(this::isClassSource).filter(this::isASerenityTest).forEach(this::configureParameterizedTestDataFor);
        });
    }

    private boolean isASerenityTest(TestIdentifier child) {
        return isSerenityTestClass(((ClassSource) child.getSource().get()).getJavaClass());
    }

    private void configureParameterizedTestDataFor(TestIdentifier serenityTest) {
        Class<?> javaClass = ((ClassSource) serenityTest.getSource().get()).getJavaClass();
        Map<String, DataTable> parameterTablesForClass = JUnit5DataDrivenAnnotations.forClass(javaClass).getParameterTables();
        if (!parameterTablesForClass.isEmpty()) {
            dataTables.putAll(parameterTablesForClass);
        }
    }

    @Override
    public synchronized void testPlanExecutionFinished(TestPlan testPlan) {
        if (!isSerenityTest) return;
        List<TestIdentifier> testIdentifiers = new ArrayList<>();
        List<TestIdentifier> parameterizedTestIdentifiers = new ArrayList<>();

        //TODO use getDescendants()
        testPlan.getRoots().forEach(testIdentifier -> {
            generateReportsForTest(testPlan, testIdentifier, testIdentifiers, parameterizedTestIdentifiers);
        });
        testIdentifiers.forEach(this::generateReports);
        generateReportsForParameterizedTests(parameterizedTestIdentifiers);

        logger.debug("->TestPlanExecutionFinished " + testPlan);
    }

    private void generateReportsForTest(TestPlan testPlan, TestIdentifier testIdentifier, List<TestIdentifier> testIdentifiers, List<TestIdentifier> parameterizedTestIdentifiers) {
        logger.debug("->GenerateReportsForTest  " + testIdentifier);
        if (testIdentifier.getUniqueId().contains("test-template-invocation")) {
            parameterizedTestIdentifiers.add(testIdentifier);
        } else {
            testIdentifiers.add(testIdentifier);
        }
        testPlan.getChildren(testIdentifier).forEach(ti -> generateReportsForTest(testPlan, ti, testIdentifiers, parameterizedTestIdentifiers));
    }

    @Override
    public void dynamicTestRegistered(TestIdentifier testIdentifier) {
    }

    @Override
    public void executionSkipped(TestIdentifier testIdentifier, String reason) {
        if (!isSerenityTest) return;
        processTestMethodAnnotationsFor(testIdentifier);
    }

    private void processTestMethodAnnotationsFor(TestIdentifier testIdentifier) {
        Optional<TestSource> testSource = testIdentifier.getSource();
        if (testSource.isPresent() && (testSource.get() instanceof MethodSource)) {
            MethodSource methodTestSource = ((MethodSource) testIdentifier.getSource().get());
            String className = methodTestSource.getClassName();
            String methodName = methodTestSource.getMethodName();
            //method parameter types are class names as strings comma separated : java.langString,java.lang.Integer
            String methodParameterTypes = methodTestSource.getMethodParameterTypes();
            List<Class> methodParameterClasses = null;

            if (methodParameterTypes != null && !methodParameterTypes.isEmpty()) {
                methodParameterClasses = Arrays.asList(methodParameterTypes.split(",")).stream().map(parameterClassName -> {
                    try {
                        //ClassUtils handles also simple data type like int, char..
                        return ClassUtil.forName(parameterClassName.trim(), this.getClass().getClassLoader());
                    } catch (ClassNotFoundException e) {
                        logger.error("Problem when getting parameter classes ", e);
                        return null;
                    }
                }).collect(Collectors.toList());
            }
            try {
                if (isIgnored(getProcessedMethod(className, methodName, methodParameterClasses))) {
                    startTestAtEventBus(testIdentifier);
                    eventBusFor(testIdentifier).testIgnored();
                    eventBusFor(testIdentifier).testFinished();
                }
            } catch (ClassNotFoundException | NoSuchMethodException exception) {
                logger.error("Exception when processing method annotations", exception);
            }
        }
    }


    private Method getProcessedMethod(String className, String methodName, List<Class> methodParameterClasses) throws NoSuchMethodException, ClassNotFoundException {
        if (!isNullOrEmpty(methodParameterClasses)) {
            Class[] classesArray = new Class[methodParameterClasses.size()];
            return Class.forName(className).getMethod(methodName, methodParameterClasses.toArray(classesArray));
        }
        // check whether the class with name className has a method with name methodName and return it if it exists
        if (Arrays.stream(Class.forName(className).getDeclaredMethods()).anyMatch(method -> method.getName().equals(methodName))) {
            return Class.forName(className).getDeclaredMethod(methodName);
        }
        return Class.forName(className).getMethod(methodName);
    }

    private boolean isNullOrEmpty(List<Class> methodParameterClasses) {
        if ((methodParameterClasses == null) || (methodParameterClasses.isEmpty())) {
            return true;
        }
        return (methodParameterClasses.stream().allMatch(Objects::isNull));
    }

    private boolean isIgnored(Method child) {
        return child.getAnnotation(Disabled.class) != null;
    }


    private void startTestAtEventBus(TestIdentifier testIdentifier) {
        eventBusFor(testIdentifier).setTestSource(TEST_SOURCE_JUNIT5.getValue());
        String displayName = removeEndBracketsFromDisplayName(testIdentifier.getDisplayName());
        if (isMethodSource(testIdentifier)) {
            String className = ((MethodSource) testIdentifier.getSource().get()).getClassName();
            try {
                eventBusFor(testIdentifier).testStarted(Optional.ofNullable(displayName).orElse("Initialisation"), Class.forName(className));
            } catch (ClassNotFoundException exception) {
                logger.error("Exception when starting test at event bus ", exception);
            }
        }
    }

    private String removeEndBracketsFromDisplayName(String displayName) {
        if (displayName != null && displayName.endsWith("()")) {
            displayName = displayName.substring(0, displayName.length() - 2);
        }
        return displayName;
    }

    private static final Map<Class<?>, String> TEST_CASE_DISPLAY_NAMES = new ConcurrentHashMap<>();
    private static final Map<String, String> DATA_DRIVEN_TEST_NAMES =  new ConcurrentHashMap<>();
    @Override
    public synchronized void executionStarted(TestIdentifier testIdentifier) {
        Class<?> testClass;
        logger.trace("-->Execution started with TI " + testIdentifier);
        if (!testIdentifier.getSource().isPresent()) {
            logger.trace("No action done at executionStarted because testIdentifier is null");
            return;
        }
        if (isTestContainer(testIdentifier) && isClassSource(testIdentifier)) {
            testClass = ((ClassSource) testIdentifier.getSource().get()).getJavaClass();
            isSerenityTest = isSerenityTestClass(testClass);
            if (!isSerenityTest) {
                logger.trace("-->Execution started but no SerenityClass " + testClass);
                return;
            }
            logger.trace("-->Execution started " + testIdentifier + "----" + testIdentifier.getDisplayName() + "--" + testIdentifier.getType() + "--" + testIdentifier.getSource());
            logger.trace("-->TestSuiteStarted " + testClass);

            // Keep track of the relationship between test classes and the display names
            String testClassName = null;
            if (testIdentifier.getSource().isPresent() && testIdentifier.getSource().get() instanceof ClassSource) {
                testClassName = ((ClassSource) testIdentifier.getSource().get()).getClassName();
            }
            TestClassHierarchy.getInstance().testSuiteStarted(testClassName,
                    testIdentifier.getUniqueId(),
                    testIdentifier.getDisplayName(),
                    testIdentifier.getParentId().orElse(null));

            eventBusFor(testIdentifier).getBaseStepListener().clearTestOutcomes();
            eventBusFor(testIdentifier).testSuiteStarted(testClass, testIdentifier.getDisplayName());
            TEST_CASE_DISPLAY_NAMES.put(testClass, testIdentifier.getDisplayName());
        }

        if (isMethodSource(testIdentifier)) {
            MethodSource methodSource = ((MethodSource) testIdentifier.getSource().get());
            if (isSimpleTest(testIdentifier)) {
                testClass = ((MethodSource) testIdentifier.getSource().get()).getJavaClass();
                testStarted(methodSource, testIdentifier, testClass);
            }
            String sourceMethod = methodSource.getJavaClass().getCanonicalName() + "." + methodSource.getMethodName();
            DataTable dataTable = dataTables.get(sourceMethod);
            if (dataTable != null) {
                logger.trace("FoundDataTable " + dataTable + " " + dataTable.getRows());
                if (isSimpleTest(testIdentifier)) {
                    eventBusFor(testIdentifier).useExamplesFrom(dataTable);
                    logger.trace("-->EventBus.useExamplesFrom" + dataTable);
                    int rowNumber = getTestTemplateInvocationNumber(testIdentifier);
                    logger.trace("-->EventBus.exampleStarted " + rowNumber + "--" + dataTable.row(rowNumber).toStringMap());
                    logger.trace("-->EventBus.useExamplesFrom" + dataTable + " with parameter set number " + rowNumber);
                    eventBusFor(testIdentifier).exampleStarted(dataTable.row(rowNumber).toStringMap());
                } else {
                    DATA_DRIVEN_TEST_NAMES.put(testIdentifier.getUniqueId(), testIdentifier.getDisplayName());
                }
            }
        }
    }

    private boolean isTestContainer(TestIdentifier testIdentifier) {
        return TestDescriptor.Type.CONTAINER == testIdentifier.getType();
    }

    @Override
    public synchronized void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        if (!isSerenityTest) return;

        logger.trace("-->Execution finished " + testIdentifier);
        logger.trace("-->Execution finished " + testIdentifier.getDisplayName() + "--" + testIdentifier.getType() + "--" + testIdentifier.getSource() + " with result " + testExecutionResult.getStatus());
        if (!testIdentifier.getSource().isPresent()) {
            logger.debug("No action done at executionFinished because testIdentifier is null");
            return;
        }

        if (isTestContainer(testIdentifier) && isClassSource(testIdentifier)) {
            logger.trace("-->EventBus.TestSuiteFinished " + ((ClassSource) testIdentifier.getSource().get()).getJavaClass());
            eventBusFor(testIdentifier).testSuiteFinished();
        }
        if (isSimpleTest(testIdentifier)) {
            if (isMethodSource(testIdentifier)) {
                MethodSource methodSource = ((MethodSource) testIdentifier.getSource().get());
                String sourceMethod = methodSource.getClassName() + "." + methodSource.getMethodName();
//                testFinished(testIdentifier, methodSource, testExecutionResult);
                DataTable dataTable = dataTables.get(sourceMethod);
                if (dataTable != null) {
                    eventBusFor(testIdentifier).exampleFinished();
                }
                testFinished(testIdentifier, methodSource, testExecutionResult);
            }
        }
        recordSummaryData(testIdentifier, testExecutionResult);
    }


    private void recordSummaryData(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        try {
            switch (testExecutionResult.getStatus()) {

                case SUCCESSFUL: {
                    if (testIdentifier.isContainer()) {
                        this.summary.containersSucceeded.incrementAndGet();
                    }
                    if (testIdentifier.isTest()) {
                        this.summary.testsSucceeded.incrementAndGet();
                    }
                    break;
                }

                case ABORTED: {
                    if (testIdentifier.isContainer()) {
                        this.summary.containersAborted.incrementAndGet();
                    }
                    if (testIdentifier.isTest()) {
                        this.summary.testsAborted.incrementAndGet();
                    }
                    break;
                }

                case FAILED: {
                    if (testIdentifier.isContainer()) {
                        this.summary.containersFailed.incrementAndGet();
                    }
                    if (testIdentifier.isTest()) {
                        this.summary.testsFailed.incrementAndGet();
                    }
                    testExecutionResult.getThrowable().ifPresent(throwable -> this.summary.addFailure(testIdentifier, throwable));
                    eventBusFor(testIdentifier).testFailed(testExecutionResult.getThrowable().get());
                    break;
                }
                default:
                    throw new PreconditionViolationException("Unsupported execution status:" + testExecutionResult.getStatus());
            }
        } finally {
            expectedExceptions.clear();
        }
    }

    private void testFinished(TestIdentifier testIdentifier, MethodSource methodSource, TestExecutionResult testExecutionResult) {
        updateResultsUsingTestAnnotations(testIdentifier, methodSource);
//        TestResult result = StepEventBus.getParallelEventBus().getBaseStepListener().getCurrentTestOutcome().getResult();
        TestResult result = eventBusFor(testIdentifier).getBaseStepListener().getCurrentTestOutcome().getResult();
        if (testExecutionResult.getStatus() == TestExecutionResult.Status.ABORTED && result == TestResult.SUCCESS) {
            updateResultsUsingTestExecutionResult(testIdentifier, testExecutionResult);
        } else if (testExecutionResult.getStatus() == TestExecutionResult.Status.FAILED && result.isLessSevereThan(TestResult.FAILURE)) {
            updateResultsUsingTestExecutionResult(testIdentifier, testExecutionResult);
        }

        eventBusFor(testIdentifier).testFinished();
        eventBusFor(testIdentifier).setTestSource(TEST_SOURCE_JUNIT5.getValue());
    }

    private void updateResultsUsingTestExecutionResult(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        testExecutionResult.getThrowable().ifPresent(cause -> eventBusFor(testIdentifier).getBaseStepListener().updateCurrentStepFailureCause(cause));
        if (testExecutionResult.getStatus() == TestExecutionResult.Status.ABORTED) {
            eventBusFor(testIdentifier).getBaseStepListener().overrideResultTo(TestResult.ABORTED);
        }
    }

    private void updateResultsUsingTestAnnotations(TestIdentifier testIdentifier, MethodSource methodSource) {

        if (TestMethodConfiguration.forMethod(methodSource.getJavaMethod()).isManual()) {
            setToManual(testIdentifier, methodSource);
        }
        expectedExceptions.forEach(ex -> updateResultsForExpectedException(testIdentifier, ex));
    }

    private void setToManual(TestIdentifier testIdentifier, MethodSource methodSource) {
        eventBusFor(testIdentifier).testIsManual();
        TestResult result = TestMethodConfiguration.forMethod(methodSource.getJavaMethod()).getManualResult();
        eventBusFor(testIdentifier).getBaseStepListener().recordManualTestResult(result);
    }

    private void updateResultsForExpectedException(TestIdentifier testIdentifier, Class<? extends Throwable> expected) {
        eventBusFor(testIdentifier).exceptionExpected(expected);
    }

    private boolean isSimpleTest(TestIdentifier testIdentifier) {
        return testIdentifier.getType() == TestDescriptor.Type.TEST;
    }

    private boolean isClassSource(TestIdentifier testId) {
        return testId.getSource().isPresent() && (testId.getSource().get() instanceof ClassSource);
    }

    private boolean isMethodSource(TestIdentifier testId) {
        return testId.getSource().isPresent() && (testId.getSource().get() instanceof MethodSource);
    }

    /**
     * Called when a test starts. We also need to start the test suite the first
     * time, as the testRunStarted() method is not invoked for some reason.
     */
    private void testStarted(MethodSource methodSource, TestIdentifier testIdentifier, Class<?> testClass) {
        if (testingThisTest(testIdentifier, testClass)) {
            startTestSuiteForFirstTest(testIdentifier);
            logger.debug(Thread.currentThread() + " Test started " + testIdentifier);
            eventBusFor(testIdentifier).clear();
            eventBusFor(testIdentifier).setTestSource(TEST_SOURCE_JUNIT5.getValue());

//            String testName = StringUtils.isNotEmpty(testIdentifier.getDisplayName()) ? testIdentifier.getDisplayName() : methodSource.getMethodName();
            String testName = Inflector.getInstance().humanize(methodSource.getMethodName());
            if (testIdentifier.getDisplayName().startsWith("[")) {
                testName = testName + testIdentifier.getDisplayName();
            } else if (StringUtils.isNotEmpty(testIdentifier.getDisplayName())) {
                testName = testIdentifier.getDisplayName();
            }
            try {
                Method javaMethod = methodSource.getJavaMethod();

                if (JUnit5TestClassAnnotations.forTest(testClass).getDisplayNameGeneration(javaMethod).isPresent()) {
                    testName = JUnit5TestClassAnnotations.forTest(testClass).getDisplayNameGeneration(javaMethod).get();
                }
                if (JUnit5TestMethodAnnotations.forTest(javaMethod).getDisplayName().isPresent()) {
                    testName = JUnit5TestMethodAnnotations.forTest(javaMethod).getDisplayName().get();
                }
            } catch (Exception e) {
                //ignore org.junit.platform.commons.PreconditionViolationException: Could not find method with name
            }

            eventBusFor(testIdentifier).testStarted(Optional.ofNullable(testName).orElse("Initialisation"),
                    methodSource.getJavaClass(),
                    methodSource.getMethodName(),
                    testIdentifier.getUniqueId(),
                    testIdentifier.getParentId().orElse(testIdentifier.getUniqueId()));

            //
            // Check for @Pending tests
            //
            if (isPending(methodSource)) {
                eventBusFor(testIdentifier).testPending();
            }
        }
    }

    private synchronized StepEventBus eventBusFor(TestIdentifier testIdentifier) {
        String uniqueTestId = testIdentifier.getUniqueId();

        StepEventBus currentEventBus = StepEventBus.eventBusFor(uniqueTestId);
        if (!currentEventBus.isBaseStepListenerRegistered()) {
            File outputDirectory = getOutputDirectory();
            BaseStepListener baseStepListener = Listeners.getBaseStepListener().withOutputDirectory(outputDirectory);
            currentEventBus.registerListener(baseStepListener);
//            currentEventBus.registerListener(new ConsoleLoggingListener(currentEventBus.getEnvironmentVariables()));
            currentEventBus.registerListener(SerenityInfrastructure.getLoggingListener());
            logger.trace("  -> ADDED BASE LISTENER " + baseStepListener);
            StepListener loggingListener = Listeners.getLoggingListener();
            currentEventBus.registerListener(loggingListener);
            logger.trace("  -> ADDED LOGGING LISTENER " + loggingListener);
        }
        logger.trace("SETTING EVENT BUS FOR THREAD " + Thread.currentThread() + " TO " + currentEventBus);
        StepEventBus.setCurrentBusToEventBusFor(uniqueTestId);
        return currentEventBus;
    }


    private boolean isPending(MethodSource methodSource) {
        try {
            return (TestMethodConfiguration.forMethod(methodSource.getJavaMethod()).isPending());
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean testingThisTest(TestIdentifier testIdentifier, Class<?> testClass) {
        if (isMethodSource(testIdentifier)) {
            MethodSource methodSource = (MethodSource) testIdentifier.getSource().get();
            return testClass.equals(methodSource.getJavaClass());
        }
        return false;
    }


    private void startTestSuiteForFirstTest(TestIdentifier testIdentifier) {
        if (isMethodSource(testIdentifier)) {
            Class<?> testCase = ((MethodSource) testIdentifier.getSource().get()).getJavaClass();
            logger.trace("-->TestSuiteStarted " + testCase);
            String testSuiteName = TEST_CASE_DISPLAY_NAMES.getOrDefault(testCase, testCase.getSimpleName());
            eventBusFor(testIdentifier).testSuiteStarted(((MethodSource) testIdentifier.getSource().get()).getJavaClass(), testSuiteName);
        }
    }


    /**
     * Find the current set of test outcomes produced by the test execution.
     *
     * @param testIdentifier
     * @return the current list of test outcomes
     */
    public List<TestOutcome> getTestOutcomes(TestIdentifier testIdentifier) {
        logger.trace("GET TEST OUTCOMES FOR " + testIdentifier);
        logger.trace(" - BASE STEP LISTENER: " + eventBusFor(testIdentifier).getBaseStepListener());
        List<TestOutcome> testOutcomes = eventBusFor(testIdentifier).getBaseStepListener().getTestOutcomes();
        testOutcomes.forEach(
                outcome -> {
                    if (testIdentifier.getParentId().isPresent() && DATA_DRIVEN_TEST_NAMES.get(testIdentifier.getParentId().get()) != null) {
                        outcome.setTestOutlineName(DATA_DRIVEN_TEST_NAMES.get(testIdentifier.getParentId().get()));
                    }
                }
        );
        return testOutcomes;
    }


    private void generateReports(TestIdentifier testIdentifier) {
        logger.trace("GENERATE REPORTS FOR TEST " + testIdentifier.getUniqueId());
        generateReportsFor(getTestOutcomes(testIdentifier));

        StepEventBus.clearEventBusFor(testIdentifier.getUniqueId());
    }

    private void generateReportsForParameterizedTests(List<TestIdentifier> testIdentifiers) {
        logger.trace("GENERATE REPORTS FOR PARAMETERIZED TESTS " + testIdentifiers);
        List<TestOutcome> allTestOutcomes = testIdentifiers
                .stream()
                .map(this::getTestOutcomes)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        ParameterizedTestsOutcomeAggregator parameterizedTestsOutcomeAggregator = new ParameterizedTestsOutcomeAggregator(allTestOutcomes);

        generateReportsFor(parameterizedTestsOutcomeAggregator.aggregateTestOutcomesByTestMethods());

        testIdentifiers.stream().map(TestIdentifier::getUniqueId).forEach(StepEventBus::clearEventBusFor);
    }

    /**
     * A test runner can generate reports via Reporter instances that subscribe
     * to the test runner. The test runner tells the reporter what directory to
     * place the reports in. Then, at the end of the test, the test runner
     * notifies these reporters of the test outcomes. The reporter's job is to
     * process each test run outcome and do whatever is appropriate.
     *
     * @param testOutcomeResults the test results from the previous test run.
     */
    private void generateReportsFor(final List<TestOutcome> testOutcomeResults) {
        getReportService().generateReportsFor(testOutcomeResults);
        getReportService().generateConfigurationsReport();
    }

    private ReportService getReportService() {
        if (reportService == null) {
            reportService = new ReportService(getOutputDirectory(), getDefaultReporters());
        }
        return reportService;
    }

    /**
     * Instantiates the @ManagedPages-annotated Pages instance using current WebDriver.
     *
     * @param testCase A Serenity-annotated test class
     */
    protected void injectAnnotatedPagesObjectInto(final Object testCase) {
        StepAnnotations.injector().injectAnnotatedPagesObjectInto(testCase, pages);
    }

    public static void addExpectedException(Class exceptionClass) {
        expectedExceptions.add(exceptionClass);
    }

    static boolean isSerenityTestClass(Class<?> testClass) {
        return classNestStructure(testClass).stream().flatMap(clazz -> Stream.of(clazz.getAnnotationsByType(ExtendWith.class))).anyMatch(annotation -> Arrays.asList(annotation.value()).contains(SerenityJUnit5Extension.class));
    }

    static private List<Class<?>> classNestStructure(Class<?> testClass) {
        List<Class<?>> nestedStructure = new ArrayList<>();
        nestedStructure.add(testClass);
        Class<?> declaringClass = testClass.getDeclaringClass();
        while (declaringClass != null) {
            nestedStructure.add(declaringClass);
            declaringClass = declaringClass.getDeclaringClass();
        }
        return nestedStructure;
    }

    private int getTestTemplateInvocationNumber(TestIdentifier testIdentifier) {
        return getTestTemplateInvocationNumber(testIdentifier.getUniqueId());
    }

    static int getTestTemplateInvocationNumber(String uniqueTestIdentifier) {
        if (!uniqueTestIdentifier.contains("test-template-invocation")) {
            return -1;
        } else {
            int index1 = uniqueTestIdentifier.lastIndexOf("#");
            int index2 = uniqueTestIdentifier.lastIndexOf("]");
            return Integer.parseInt(uniqueTestIdentifier.substring(index1 + 1, index2)) - 1;
        }
    }
}
