package net.serenitybdd.junit.runners;

import net.serenitybdd.model.collect.NewList;
import net.thucydides.model.domain.DataTable;
import net.thucydides.model.domain.Story;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestResult;
import net.thucydides.model.screenshots.ScreenshotAndHtmlSource;
import net.thucydides.model.steps.ExecutedStepDescription;
import net.thucydides.model.steps.StepFailure;
import net.thucydides.model.steps.StepListener;
import net.thucydides.model.steps.TestFailureCause;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FailureDetectingStepListener implements StepListener {

    private boolean lastTestFailed = false;
    private final List<String> failureMessages = new ArrayList<>();
    private TestFailureCause testFailureCause;

    public void reset() {
        lastTestFailed = false;
        failureMessages.clear();
    }

    public boolean lastTestFailed() {
        return lastTestFailed;
    }

    public void testFailed(TestOutcome testOutcome, Throwable cause) {
        lastTestFailed = true;
        String failingStep = testOutcome.getFailingStep().isPresent() ? testOutcome.getFailingStep().get().getDescription() + ":" : "";
        failureMessages.add(failingStep + testOutcome.getErrorMessage());
        testFailureCause = TestFailureCause.from(cause);
    }

    public void lastStepFailed(StepFailure failure) {

    }

    public void testSuiteStarted(Class<?> storyClass) {

    }


    public void testSuiteStarted(Story storyOrFeature) {

    }


    public void testSuiteFinished() {

    }


    public void testStarted(String description) {
        lastTestFailed = false;
    }

    @Override
    public void testStarted(String description, String id) {
        lastTestFailed = false;
    }

    @Override
    public void testStarted(String description, String id, ZonedDateTime startTime) {

    }


    public void testFinished(TestOutcome result) {

    }

    @Override
    public void testFinished(TestOutcome result, boolean isInDataDrivenTest, ZonedDateTime finishTime) {

    }


    public void testRetried() {

    }


    public void stepStarted(ExecutedStepDescription description) {

    }


    public void skippedStepStarted(ExecutedStepDescription description) {

    }


    public void stepFailed(StepFailure failure) {

    }

    @Override
    public void stepFailed(StepFailure failure, List<ScreenshotAndHtmlSource> screenshotList) {

    }


    public void stepIgnored() {

    }


    public void stepPending() {

    }


    public void stepPending(String message) {

    }


    public void stepFinished() {

    }

    @Override
    public void stepFinished(List<ScreenshotAndHtmlSource> screenshotList) {

    }

    @Override
    public void stepFinished(List<ScreenshotAndHtmlSource> screenshotList, ZonedDateTime time) {

    }


    public void testIgnored() {

    }

    @Override
    public void testSkipped() {

    }

    @Override
    public void testPending() {

    }

    @Override
    public void testIsManual() {

    }


    public void notifyScreenChange() {

    }


    public void useExamplesFrom(DataTable table) {

    }

    @Override
    public void addNewExamplesFrom(DataTable table) {

    }


    public void exampleStarted(Map<String, String> data) {

    }


    public void exampleFinished() {

    }


    public void assumptionViolated(String message) {

    }

    @Override
    public void testRunFinished() {

    }

    @Override
    public void takeScreenshots(List<ScreenshotAndHtmlSource> screenshots) {

    }

    @Override
    public void takeScreenshots(TestResult testResult, List<ScreenshotAndHtmlSource> screenshots) {

    }

    public TestFailureCause getTestFailureCause(){
        return testFailureCause;
    }

    public List<String> getFailureMessages() {
        return NewList.copyOf(failureMessages);
    }
}
