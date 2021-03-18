package net.serenitybdd.junit.listener;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.stacktrace.FailureCause;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.StepListener;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

import java.util.*;

import static net.thucydides.core.steps.TestSourceType.TEST_SOURCE_JUNIT;

/**
 * Intercepts JUnit events and reports them to Serenity.
 */
public class JUnitListener implements TestExecutionListener {
    private final BaseStepListener baseStepListener;
    private final StepListener[] extraListeners;
    private final TestIdentifier testClass;
    private final Map<String, List<String>> failedTests;
    private ThreadLocal<Boolean> testStarted;



    public  JUnitListener(TestIdentifier testClass, BaseStepListener baseStepListener, StepListener... listeners) {
        initializeListener();
        failedTests = Collections.synchronizedMap(new HashMap<>());
        this.baseStepListener = baseStepListener;
        this.extraListeners = listeners;
        this.testClass = testClass;

        registerSerenityListeners();
    }

    @Override
    public void testPlanExecutionStarted(TestPlan testPlan){
        if (!getBaseStepListener().testSuiteRunning()) {
            stepEventBus().testSuiteStarted(testPlan.getClass());
        }
    }
    @Override
    public void testPlanExecutionFinished(TestPlan testPlan){
        stepEventBus().testRunFinished();
        TestExecutionListener.super.testPlanExecutionFinished(testPlan);
    }
    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult){
        if(testExecutionResult.getStatus() == TestExecutionResult.Status.FAILED){
            if(testExecutionResult.getThrowable().isPresent())
                stepEventBus().testFailed(testExecutionResult.getThrowable().get());
            updateFailureList(testIdentifier);

        }else {
            stepEventBus().testFinished();
            stepEventBus().setTestSource(TEST_SOURCE_JUNIT.getValue());
        }
        endTest();
    }
    @Override
    public void executionStarted(TestIdentifier testIdentifier){
        stepEventBus().clear();
        stepEventBus().setTestSource(TEST_SOURCE_JUNIT.getValue());
        stepEventBus().testStarted(
                Optional.ofNullable(testIdentifier.getDisplayName()).orElse("Initialisation"),
                testIdentifier.getClass());
        startTest();
    }
    @Override
    public void executionSkipped(TestIdentifier testIdentifier, String reason){
        stepEventBus().testIgnored();
        endTest();
    }

    public void initializeListener(){
        testStarted = new ThreadLocal<>();
        testStarted.set(false);
    }

    public void registerSerenityListeners() {
        StepEventBus.getEventBus().registerListener(baseStepListener);

        for(StepListener listener : extraListeners) {
            StepEventBus.getEventBus().registerListener(listener);
        }
    }
    public void dropSerenityListeners() {
        StepEventBus.getEventBus().dropListener(baseStepListener);
        for(StepListener listener : extraListeners) {
            stepEventBus().dropListener(listener);
        }
    }

    public BaseStepListener getBaseStepListener() {
        return baseStepListener;
    }

    public List<TestOutcome> getTestOutcomes() {
        return baseStepListener.getTestOutcomes();
    }

    public FailureCause getError() {
        return baseStepListener.getTestFailureCause();
    }

    private StepEventBus stepEventBus() {
        return baseStepListener.getEventBus();
    }
    private void startTest() {
        testStarted.set(true);
    }
    private void endTest() {
        testStarted.set(false);
    }

    protected Class<?> getTestClass() {
        return testClass.getClass();
    }
    private void updateFailureList(TestIdentifier testIdentifier) {
        String failedClassName = testIdentifier.getClass().getName();
        List<String> failedMethods = failedTests.computeIfAbsent(failedClassName, k -> new ArrayList<>());
        failedMethods.add(testIdentifier.getDisplayName());
    }

    public Map<String,List<String>> getFailedTests(){
        return failedTests;
    }

}
