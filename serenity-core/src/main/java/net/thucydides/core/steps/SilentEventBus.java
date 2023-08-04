package net.thucydides.core.steps;

import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.thucydides.core.model.*;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SilentEventBus extends StepEventBus {
    public SilentEventBus(EnvironmentVariables environmentVariables) {
        super(environmentVariables, ConfiguredEnvironment.getConfiguration());
    }

    @Override
    public StepEventBus registerListener(StepListener listener) {
        return super.registerListener(listener);
    }

    @Override
    public boolean isBaseStepListenerRegistered() {
        return super.isBaseStepListenerRegistered();
    }

    @Override
    public BaseStepListener getBaseStepListener() {
        return super.getBaseStepListener();
    }

    @Override
    public void testStarted(String testName) {
    }

    @Override
    public void testStarted(String testName, String id) {
    }

    @Override
    public boolean isUniqueSession() {
        return super.isUniqueSession();
    }

    @Override
    public void setUniqueSession(boolean uniqueSession) {
    }

    @Override
    public void testStarted(String newTestName, Story story) {
    }

    @Override
    public void testStarted(String newTestName, Class<?> testClass) {
    }

    @Override
    protected List<StepListener> getAllListeners() {
        return super.getAllListeners();
    }

    @Override
    public void testSuiteStarted(Class<?> testClass) {
    }

    @Override
    public void testSuiteStarted(Story story) {
    }

    @Override
    public void clear() {
    }

    @Override
    public void testFinished() {
    }

    @Override
    public void testFinished(TestOutcome result) {
    }

    @Override
    public void testRetried() {
    }

    @Override
    public void clearStepFailures() {
    }

    @Override
    public boolean aStepInTheCurrentTestHasFailed() {
        return super.aStepInTheCurrentTestHasFailed();
    }

    @Override
    public boolean isCurrentTestDataDriven() {
        return false;
    }

    @Override
    public void stepStarted(ExecutedStepDescription stepDescription) {
    }

    public void stepStarted(ExecutedStepDescription stepDescription, boolean isPrecondition) {
    }

    @Override
    public void skippedStepStarted(ExecutedStepDescription executedStepDescription) {
    }

    @Override
    public void stepFinished() {
    }

    @Override
    public void stepFailed(StepFailure failure) {
    }

    @Override
    public void lastStepFailed(StepFailure failure) {
    }

    @Override
    public void stepIgnored() {
    }

    @Override
    public void stepPending() {
    }

    @Override
    public void stepPending(String message) {
    }

    @Override
    public void assumptionViolated(String message) {
    }

    @Override
    public void dropListener(StepListener stepListener) {
    }

    @Override
    public void dropAllListeners() {
    }

    @Override
    public boolean webdriverCallsAreSuspended() {
        return false;
    }

    @Override
    public void reenableWebdriverCalls() {
    }

    @Override
    public void temporarilySuspendWebdriverCalls() {
    }

    @Override
    public void testFailed(Throwable cause) {
    }

    @Override
    public void testPending() {
    }

    @Override
    public void testIsManual() {
    }

    @Override
    public void suspendTest() {
    }

    @Override
    public void suspendTest(TestResult result) {
    }

    @Override
    public boolean currentTestIsSuspended() {
        return super.currentTestIsSuspended();
    }

    @Override
    public boolean assumptionViolated() {
        return super.assumptionViolated();
    }

    @Override
    public void testIgnored() {
    }

    @Override
    public void testSkipped() {
    }

    @Override
    public boolean areStepsRunning() {
        return super.areStepsRunning();
    }

    @Override
    public void notifyScreenChange() {
    }

    @Override
    public void testSuiteFinished() {
    }

    @Override
    public void testRunFinished() {
    }

    @Override
    public void updateCurrentStepTitle(String stepTitle) {
    }

    @Override
    public void updateCurrentStepTitleAsPrecondition(String stepTitle) {
    }

    @Override
    public void addIssuesToCurrentStory(List<String> issues) {
    }

    @Override
    public void addIssuesToCurrentTest(List<String> issues) {
    }

    @Override
    public void addTagsToCurrentTest(List<TestTag> tags) {
    }

    @Override
    public void addTagsToCurrentStory(List<TestTag> tags) {
    }

    @Override
    public void addDescriptionToCurrentTest(String description) {
    }

    @Override
    public void setBackgroundTitle(String title) {
    }

    @Override
    public void setBackgroundDescription(String description) {
    }

    @Override
    public void useExamplesFrom(DataTable table) {
    }

    @Override
    public void addNewExamplesFrom(DataTable newTable) {
    }

    @Override
    public void exampleStarted(Map<String, String> data) {
    }

    @Override
    public void exampleFinished() {
    }

    @Override
    public boolean currentTestOutcomeIsDataDriven() {
        return super.currentTestOutcomeIsDataDriven();
    }

    @Override
    public void takeScreenshot() {
    }

    @Override
    public boolean testSuiteHasStarted() {
        return super.testSuiteHasStarted();
    }

    @Override
    public String getAssumptionViolatedMessage() {
        return super.getAssumptionViolatedMessage();
    }

    @Override
    public Optional<TestStep> getCurrentStep() {
        return super.getCurrentStep();
    }

    @Override
    public void setAllStepsTo(TestResult result) {
    }

    @Override
    public Optional<TestResult> getForcedResult() {
        return super.getForcedResult();
    }

    @Override
    public synchronized boolean isDryRun() {
        return super.isDryRun();
    }

    @Override
    public synchronized void enableDryRun() {
    }

    @Override
    public void exceptionExpected(Class<? extends Throwable> expected) {
    }

    @Override
    public Optional<TestResult> resultSoFar() {
        return super.resultSoFar();
    }

    @Override
    public void mergePreviousStep() {
    }

    @Override
    public void updateOverallResults() {
    }

    @Override
    public void reset() {
    }

    @Override
    public void disableSoftAsserts() {
    }

    @Override
    public void enableSoftAsserts() {
    }

    @Override
    public boolean softAssertsActive() {
        return false;
    }

    @Override
    public String getTestSource() {
        return "";
    }

    @Override
    public void setTestSource(String testSource) {
    }

    @Override
    public void cancelPreviousTest() {
    }

    @Override
    public void lastTestPassedAfterRetries(int remainingTries, List<String> failureMessages, TestFailureCause testFailureCause) {
    }
}
