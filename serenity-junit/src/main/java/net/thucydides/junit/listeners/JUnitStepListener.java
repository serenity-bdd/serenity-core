package net.thucydides.junit.listeners;

import com.google.common.base.Optional;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.stacktrace.FailureCause;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.StepListener;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.io.File;
import java.util.*;

/**
 * Intercepts JUnit events and reports them to Thucydides.
 */
public class JUnitStepListener extends RunListener {

    private BaseStepListener baseStepListener;
    private StepListener[] extraListeners;
    private Map<String,List<String>> failedTests = Collections.synchronizedMap(new HashMap<String,List<String>>());
    private Class<?> testClass;
    private boolean testStarted;

    public static JUnitStepListenerBuilder withOutputDirectory(File outputDirectory) {
        return new JUnitStepListenerBuilder(outputDirectory);
    }

    protected JUnitStepListener(Class<?> testClass, BaseStepListener baseStepListener, StepListener... listeners) {
        testStarted = false;
        this.baseStepListener = baseStepListener;
        this.extraListeners = listeners;
        this.testClass = testClass;

        registerThucydidesListeners();

    }

    public void registerThucydidesListeners() {
        StepEventBus.getEventBus().registerListener(baseStepListener);

        for(StepListener listener : extraListeners) {
            StepEventBus.getEventBus().registerListener(listener);
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
            stepEventBus().setTestSource(StepEventBus.TEST_SOURCE_JUNIT);
            stepEventBus().testStarted(
                    Optional.fromNullable(description.getMethodName()).or("Initialisation"),
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
            stepEventBus().setTestSource(null);
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
        if (!testStarted) {
           testStarted(description);
        }
    }

    @Override
    public void testIgnored(final Description description) throws Exception {
        if (testingThisTest(description)) {
            stepEventBus().testIgnored();
            endTest();
        }
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
        StepEventBus.getEventBus().dropListener(baseStepListener);
        for(StepListener listener : extraListeners) {
            stepEventBus().dropListener(listener);
        }
    }

    private void startTest() {
        testStarted = true;
    }
    private void endTest() {
        testStarted = false;
    }

    private boolean testingThisTest(Description description) {
        return description.getTestClass().equals(testClass);
    }

    protected Class<?> getTestClass() {
        return testClass;
    }

    public Map<String,List<String>> getFailedTests(){
        return failedTests;
    }

}
