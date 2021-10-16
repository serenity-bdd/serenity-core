package net.serenitybdd.junit5;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
import net.thucydides.core.model.DataTable;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.reports.ReportService;
import net.thucydides.core.steps.*;
import net.thucydides.core.util.SystemEnvironmentVariables;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.PreconditionViolationException;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.thucydides.core.reports.ReportService.getDefaultReporters;
import static net.thucydides.core.steps.TestSourceType.TEST_SOURCE_JUNIT5;

public class SerenityTestExecutionListener implements TestExecutionListener {

    private static List<Class> expectedExceptions = Collections.synchronizedList(new ArrayList<>());

    static {
        ByteBuddyAgent.install();
        new ByteBuddy()
                .rebase(Assertions.class)
                .visit(Advice.to(AssertThrowsAdvice.class).on(named("assertThrows")))
                .make().load(Assertions.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }

    private final Logger logger = LoggerFactory.getLogger(SerenityTestExecutionListener.class);

    private ReportService reportService;

    private TestPlan currentTestPlan;

    private SerenityTestExecutionSummary summary;

    private Pages pages;

    //key-> "ClassName.MethodName"
    //entries-> DataTable associated with method
    private Map<String, DataTable> dataTables = new HashMap<>();
    ;

    private int parameterSetNumber = 0;

    private BaseStepListener baseStepListener;

    private Class<?> testClass;

    private boolean isDataDrivenTest = false;

    public SerenityTestExecutionListener() {
        File outputDirectory = getOutputDirectory();
        baseStepListener = Listeners.getBaseStepListener().withOutputDirectory(outputDirectory);
        StepEventBus.getEventBus().registerListener(baseStepListener);
    }

    private File getOutputDirectory() {
        SystemPropertiesConfiguration systemPropertiesConfiguration = new SystemPropertiesConfiguration(new SystemEnvironmentVariables());
        return systemPropertiesConfiguration.getOutputDirectory();
    }

    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {
        this.currentTestPlan = testPlan;
        this.summary = new SerenityTestExecutionSummary(testPlan);
        logger.debug("->TestPlanExecutionStarted " + testPlan);
        Set<TestIdentifier> roots = testPlan.getRoots();
        for (TestIdentifier root : roots) {
            logger.trace("TestIdentifier Root " + root.getUniqueId() + root.getDisplayName() + root.getSource());
            Set<TestIdentifier> children = testPlan.getChildren(root.getUniqueId());
            for (TestIdentifier child : children) {
                if (isClassSource(child)) {
                    Class<?> javaClass = ((ClassSource) child.getSource().get()).getJavaClass();
                    Map<String, DataTable> parameterTablesForClass = JUnit5DataDrivenAnnotations.forClass(javaClass).getParameterTables();
                    if (!parameterTablesForClass.isEmpty()) {
                        dataTables.putAll(parameterTablesForClass);
                    }
                }
            }
        }
    }

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        logger.debug("->TestPlanExecutionFinished " + testPlan);
    }

    private void generateReportsForTest() {
        if (isDataDrivenTest) {
            generateReportsForParameterizedTest();
            isDataDrivenTest = false;
        } else {
            generateReports();
        }
        baseStepListener.clearTestOutcomes();
    }

    @Override
    public void dynamicTestRegistered(TestIdentifier testIdentifier) {
    }

    @Override
    public void executionSkipped(TestIdentifier testIdentifier, String reason) {
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

            if (methodParameterTypes != null) {
                methodParameterClasses = Arrays.asList(methodParameterTypes.split(",")).stream().map(parameterClassName -> {
                    try {
                        //ClassUtils handles also simple data type like int, char..
                        return ClassUtils.forName(parameterClassName.trim(), this.getClass().getClassLoader());
                    } catch (ClassNotFoundException e) {
                        logger.error("Problem when getting parameter classes ", e);
                        return null;
                    }
                }).collect(Collectors.toList());
            }
            try {
                if (isIgnored(getProcessedMethod(className, methodName, methodParameterClasses))) {
                    startTestAtEventBus(testIdentifier);
                    StepEventBus.getEventBus().testIgnored();
                    StepEventBus.getEventBus().testFinished();
                }
            } catch (ClassNotFoundException | NoSuchMethodException exception) {
                logger.error("Exception when processing method annotations", exception);
            }
        }
    }

    @NotNull
    private Method getProcessedMethod(String className, String methodName, List<Class> methodParameterClasses) throws NoSuchMethodException, ClassNotFoundException {
        if (methodParameterClasses != null) {
            Class[] classesArray = new Class[methodParameterClasses.size()];
            return Class.forName(className).getMethod(methodName, methodParameterClasses.toArray(classesArray));
        } else {
            return Class.forName(className).getMethod(methodName);
        }
    }

    private boolean isIgnored(Method child) {
        return child.getAnnotation(Disabled.class) != null;
    }


    private void startTestAtEventBus(TestIdentifier testIdentifier) {
        StepEventBus.getEventBus().setTestSource(TestSourceType.TEST_SOURCE_JUNIT5.getValue());
        String displayName = removeEndBracketsFromDisplayName(testIdentifier.getDisplayName());
        if (isMethodSource(testIdentifier)) {
            String className = ((MethodSource) testIdentifier.getSource().get()).getClassName();
            try {
                StepEventBus.getEventBus().testStarted(
                        Optional.ofNullable(displayName).orElse("Initialisation"),
                        Class.forName(className));
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

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {
        logger.trace("-->Execution started " + testIdentifier.getDisplayName() + "--" + testIdentifier.getType() + "--" + testIdentifier.getSource());
        if (!testIdentifier.getSource().isPresent()) {
            logger.trace("No action done at executionStarted because testIdentifier is null");
            return;
        }
        if (isTestContainer(testIdentifier) && isClassSource(testIdentifier)) {
            if (hasToClearPreviousTestOutcomes(testIdentifier)) {
                baseStepListener.clearTestOutcomes();
            }
            logger.trace("-->TestSuiteStarted " + ((ClassSource) testIdentifier.getSource().get()).getJavaClass());
            testClass = ((ClassSource) testIdentifier.getSource().get()).getJavaClass();
            StepEventBus.getEventBus().testSuiteStarted(((ClassSource) testIdentifier.getSource().get()).getJavaClass());
        }

        if (isMethodSource(testIdentifier)) {
            MethodSource methodSource = ((MethodSource) testIdentifier.getSource().get());
            if (isSimpleTest(testIdentifier)) {
                testStarted(methodSource, testIdentifier);
            }
            String sourceMethod = methodSource.getClassName() + "." + methodSource.getMethodName();
            DataTable dataTable = dataTables.get(sourceMethod);
            if (dataTable != null) {
                logger.trace("FoundDataTable " + dataTable + " " + dataTable.getRows());
                isDataDrivenTest = true;
                if (isTestContainer(testIdentifier)) {
                    parameterSetNumber = 0;
                } else if (isSimpleTest(testIdentifier)) {
                    StepEventBus.getEventBus().useExamplesFrom(dataTable);
                    logger.trace("-->EventBus.useExamplesFrom" + dataTable);
                    logger.trace("-->EventBus.exampleStarted " + parameterSetNumber + "--" + dataTable.row(parameterSetNumber).toStringMap());
                    StepEventBus.getEventBus().exampleStarted(dataTable.row(parameterSetNumber).toStringMap());
                    //StepEventBus.getEventBus().exampleStarted(dataTable.row(parameterSetNumber).toStringMap(),"Example #" + parameterSetNumber);
                }
            }
        }
    }

    private boolean hasToClearPreviousTestOutcomes(TestIdentifier testIdentifier) {
        //return true;
        //TOOD find out if is necessary to clean all test outcomes

        // Clear any previous test outcomes before starting a new test case, unless we are in a nested test case
        return (testIdentifier.getClass().getAnnotation(Nested.class) != null);
    }

    private boolean isTestContainer(TestIdentifier testIdentifier) {
        return testIdentifier.getType() == TestDescriptor.Type.CONTAINER;
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        logger.trace("-->Execution finished " + testIdentifier.getDisplayName() + "--" + testIdentifier.getType() + "--" + testIdentifier.getSource() + " with result " + testExecutionResult.getStatus());
        if (!testIdentifier.getSource().isPresent()) {
            logger.info("No action done at executionFinished because testIdentifier is null");
            return;
        }
        //TODO - check this
        /**
         * logger.info("-->TestSuiteFinished " + ((ClassSource)testIdentifier.getSource().get()).getJavaClass() );
         *             StepEventBus.getEventBus().testSuiteFinished();
         */
        if (isTestContainer(testIdentifier) && isClassSource(testIdentifier)) {
            logger.info("-->EventBus.TestSuiteFinished " + ((ClassSource) testIdentifier.getSource().get()).getJavaClass());
            StepEventBus.getEventBus().testSuiteFinished();
            generateReportsForTest();
        }
        if (isSimpleTest(testIdentifier)) {
            if (isMethodSource(testIdentifier)) {
                MethodSource methodSource = ((MethodSource) testIdentifier.getSource().get());
                String sourceMethod = methodSource.getClassName() + "." + methodSource.getMethodName();
                testFinished(testIdentifier, methodSource);
                DataTable dataTable = dataTables.get(sourceMethod);
                if (dataTable != null) {
                    logger.info("-->EventBus.exampleFinished " + parameterSetNumber + "--" + dataTable.row(parameterSetNumber).toStringMap());
                    StepEventBus.getEventBus().exampleFinished();
                    parameterSetNumber++;
                }
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
                        //System.out.println("CoNTAINER OK");
                    }
                    if (testIdentifier.isTest()) {
                        this.summary.testsSucceeded.incrementAndGet();
                        //System.out.println("TEST OK");
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
                    StepEventBus.getEventBus().testFailed(testExecutionResult.getThrowable().get());
                    break;
                }
                default:
                    throw new PreconditionViolationException(
                            "Unsupported execution status:" + testExecutionResult.getStatus());
            }
        } finally {
            expectedExceptions.clear();
        }
    }

    private void testFinished(TestIdentifier testIdentifier, MethodSource methodSource) {
        logger.info("Test finished " + testIdentifier);
        //if (testingThisTest(description)) {
        updateResultsUsingTestAnnotations(methodSource);
        StepEventBus.getEventBus().testFinished();
        StepEventBus.getEventBus().setTestSource(TEST_SOURCE_JUNIT5.getValue());
    }

    private void updateResultsUsingTestAnnotations(MethodSource methodSource) {

        if (TestMethodConfiguration.forMethod(methodSource.getJavaMethod()).isManual()) {
            setToManual(methodSource);
        }
        expectedExceptions.stream().forEach(this::updateResultsForExpectedException);
    }

    private void setToManual(MethodSource methodSource) {
        StepEventBus.getEventBus().testIsManual();
        TestResult result = TestMethodConfiguration.forMethod(methodSource.getJavaMethod()).getManualResult();
        StepEventBus.getEventBus().getBaseStepListener().recordManualTestResult(result);
    }

    private void updateResultsForExpectedException(Class<? extends Throwable> expected) {
        StepEventBus.getEventBus().exceptionExpected(expected);
    }

    private boolean isSimpleTest(TestIdentifier testIdentifier) {
        return testIdentifier.getType() == TestDescriptor.Type.TEST;
    }


    /*public void testFailure(final Failure failure) throws Exception {
        if (testingThisTest(failure.getDescription())) {
            startTestIfNotYetStarted(failure.getDescription());
            stepEventBus().testFailed(failure.getException());
            updateFailureList(failure);
            endTest();
        }
    }*/

    @Override
    public void reportingEntryPublished(TestIdentifier testIdentifier, ReportEntry entry) {
        logger.info("-->ReportingEntryPublished " + testIdentifier.getDisplayName() + "--" + testIdentifier.getType() + "--" + testIdentifier.getSource());
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
    private void testStarted(MethodSource methodSource, TestIdentifier testIdentifier/*final Description description*/) {
        if (testingThisTest(testIdentifier)) {
            startTestSuiteForFirstTest(testIdentifier);
            logger.info(Thread.currentThread() + " Test started " + testIdentifier);
            StepEventBus.getEventBus().clear();
            StepEventBus.getEventBus().setTestSource(TEST_SOURCE_JUNIT5.getValue());
            String testName = methodSource.getMethodName();
            try {
                Method javaMethod = methodSource.getJavaMethod();
                if (JUnit5TestMethodAnnotations.forTest(javaMethod).getDisplayName().isPresent()) {
                    testName = JUnit5TestMethodAnnotations.forTest(javaMethod).getDisplayName().get();
                }
            } catch (Exception e) {
                //ignore org.junit.platform.commons.PreconditionViolationException: Could not find method with name
            }
            /*if(testIdentifier.getDisplayName() != null)
            {
                testName.append("%" + testIdentifier.getDisplayName());
            }*/
            StepEventBus.getEventBus().testStarted(Optional.ofNullable(testName).orElse("Initialisation"),methodSource.getJavaClass());

            //
            // Check for @Pending tests
            //
            if (TestMethodConfiguration.forMethod(methodSource.getJavaMethod()).isPending()) {
                StepEventBus.getEventBus().testPending();
            }
        }
    }

    private boolean testingThisTest(TestIdentifier testIdentifier) {
        if (isMethodSource(testIdentifier)) {
            MethodSource methodSource = (MethodSource) testIdentifier.getSource().get();
            if (testClass.equals(methodSource.getJavaClass())) {
                return true;
            }
        }
        return false;
    }


    private void startTestSuiteForFirstTest(TestIdentifier testIdentifier) {
        //if (!getBaseStepListener().testSuiteRunning()) {
        //TODO
        if (isMethodSource(testIdentifier)) {
            logger.trace("-->TestSuiteStarted " + ((MethodSource) testIdentifier.getSource().get()).getJavaClass());
            StepEventBus.getEventBus().testSuiteStarted(((MethodSource) testIdentifier.getSource().get()).getJavaClass());
        }
        //}
    }

    /*private void startTestSuiteForFirstTest(Class<?> javaClass) {
        if (!getBaseStepListener().testSuiteRunning()) {
            StepEventBus.getEventBus().testSuiteStarted(javaClass);
        }
    }*/


    /**
     * Find the current set of test outcomes produced by the test execution.
     *
     * @return the current list of test outcomes
     */
    public List<TestOutcome> getTestOutcomes() {
        return baseStepListener.getTestOutcomes();
    }


    private void generateReports() {
        generateReportsFor(getTestOutcomes());
    }

    private void generateReportsForParameterizedTest() {
        ParameterizedTestsOutcomeAggregator parameterizedTestsOutcomeAggregator = new ParameterizedTestsOutcomeAggregator();
        generateReportsFor(parameterizedTestsOutcomeAggregator.aggregateTestOutcomesByTestMethods());
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

}
