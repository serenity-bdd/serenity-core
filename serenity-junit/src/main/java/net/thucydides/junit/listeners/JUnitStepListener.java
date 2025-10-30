package net.thucydides.junit.listeners;

import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.stacktrace.FailureCause;
import net.thucydides.model.steps.StepListener;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.io.File;
import java.util.*;

import static net.thucydides.model.steps.TestSourceType.TEST_SOURCE_JUNIT;

/**
 * Intercepts JUnit events and reports them to Thucydides.
 */
public class JUnitStepListener extends RunListener {

    private final BaseStepListener baseStepListener;
    private final StepListener[] extraListeners;
    private final Map<String,List<String>> failedTests = Collections.synchronizedMap(new HashMap<String,List<String>>());
    private final Class<?> testClass;
    private final ThreadLocal<Boolean> testStarted;

    public static JUnitStepListenerBuilder withOutputDirectory(File outputDirectory) {
        return new JUnitStepListenerBuilder(outputDirectory);
    }

    protected JUnitStepListener(Class<?> testClass, BaseStepListener baseStepListener, StepListener... listeners) {
        testStarted = new ThreadLocal<>();
        testStarted.set(Boolean.valueOf(false));
        this.baseStepListener = baseStepListener;
        this.extraListeners = listeners;
        this.testClass = testClass;

        registerThucydidesListeners();

    }

    public void registerThucydidesListeners() {
        StepEventBus.getParallelEventBus().registerListener(baseStepListener);

        for(StepListener listener : extraListeners) {
            StepEventBus.getParallelEventBus().registerListener(listener);
        }
    }

    public BaseStepListener getBaseStepListener() {
        return baseStepListener;
    }

    @Override
    public void testRunStarted(Description description) throws Exception {
        super.testRunStarted(description);
    }

    StepEventBus stepEventBus() {
        return baseStepListener.getEventBus();
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        stepEventBus().testRunFinished();
        super.testRunFinished(result);
    }

    /**
     * Called when a test starts. We also need to start the test suite the first
     * time, as the testRunStarted() method is not invoked for some reason.
     */
    @Override
    public void testStarted(final Description description) {
        if (testingThisTest(description)) {
            startTestSuiteForFirstTest(description);
            stepEventBus().clear();
            stepEventBus().setTestSource(TEST_SOURCE_JUNIT.getValue());
            stepEventBus().testStarted(
                    Optional.ofNullable(description.getMethodName()).orElse("Initialisation"),
                    description.getTestClass());
            startTest();
        }
    }

    private void startTestSuiteForFirstTest(Description description) {
        if (!getBaseStepListener().testSuiteRunning()) {
            stepEventBus().testSuiteStarted(description.getTestClass());
        }
    }

    @Override
    public void testFinished(final Description description) throws Exception {
        if (testingThisTest(description)) {
            updateResultsUsingTestAnnotations(description);
            stepEventBus().testFinished();
            stepEventBus().setTestSource(TEST_SOURCE_JUNIT.getValue());
            endTest();
        }
    }

    private void updateResultsUsingTestAnnotations(final Description description) {
        Test testAnnotation = description.getAnnotation(Test.class);
        if (testAnnotation.expected() != null) {
            updateResultsForExpectedException(testAnnotation.expected());
        }
    }

    private void updateResultsForExpectedException(Class<? extends Throwable> expected) {
        stepEventBus().exceptionExpected(expected);
    }

    @Override
    public void testFailure(final Failure failure) throws Exception {
        if (testingThisTest(failure.getDescription())) {
            startTestIfNotYetStarted(failure.getDescription());
            stepEventBus().testFailed(failure.getException());
            updateFailureList(failure);
            endTest();
        }
    }

    private void updateFailureList(Failure failure) {
        String failedClassName = failure.getDescription().getClassName();
        List<String> failedMethods = failedTests.get(failedClassName);
        if(failedMethods == null) {
            failedMethods = new ArrayList<>();
            failedTests.put(failedClassName,failedMethods);
        }
        failedMethods.add(failure.getDescription().getMethodName());
    }

    private void startTestIfNotYetStarted(Description description) {
        if (!testStarted.get()) {
           testStarted(description);
        }
    }

    @Override
    public void testIgnored(final Description description) throws Exception {
//        if (testingThisTest(description)) {
//            stepEventBus().testIgnored();
//            endTest();
//        }
    }

    public List<TestOutcome> getTestOutcomes() {
        return baseStepListener.getTestOutcomes();
    }

    public FailureCause getError() {
        return baseStepListener.getTestFailureCause();
    }

    public boolean hasRecordedFailures() {
        return baseStepListener.aStepHasFailed();
    }

    public void dropListeners() {
        StepEventBus.getParallelEventBus().dropListener(baseStepListener);
        for(StepListener listener : extraListeners) {
            stepEventBus().dropListener(listener);
        }
    }

    private void startTest() {
        testStarted.set(true);
    }
    private void endTest() {
        testStarted.set(false);
    }

    private boolean testingThisTest(Description description) {
        return (description.getTestClass() != null) && (description.getTestClass().equals(testClass));
    }

    protected Class<?> getTestClass() {
        return testClass;
    }

    public Map<String,List<String>> getFailedTests(){
        return failedTests;
    }

}
