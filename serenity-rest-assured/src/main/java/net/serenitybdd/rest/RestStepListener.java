package net.serenitybdd.rest;

import net.thucydides.model.domain.DataTable;
import net.thucydides.model.domain.Story;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestResult;
import net.thucydides.model.screenshots.ScreenshotAndHtmlSource;
import net.thucydides.model.steps.ExecutedStepDescription;
import net.thucydides.model.steps.StepFailure;
import net.thucydides.model.steps.StepListener;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class RestStepListener implements StepListener {

    @Override
    public void testSuiteStarted(Class<?> storyClass) {

    }

    @Override
    public void testSuiteStarted(Story story) {

    }

    @Override
    public void testSuiteFinished() {

    }

    @Override
    public void testStarted(String description) {

    }

    @Override
    public void testStarted(String description, String id) {

    }

    @Override
    public void testStarted(String description, String id, ZonedDateTime startTime) {

    }

    @Override
    public void testFinished(TestOutcome result) {

    }

    @Override
    public void testFinished(TestOutcome result, boolean isInDataDrivenTest, ZonedDateTime finishTime) {

    }

    @Override
    public void testRetried() {

    }

    @Override
    public void stepStarted(ExecutedStepDescription description) {

    }

    @Override
    public void skippedStepStarted(ExecutedStepDescription description) {

    }

    @Override
    public void stepFailed(StepFailure failure) {

    }

    @Override
    public void stepFailed(StepFailure failure, List<ScreenshotAndHtmlSource> screenshotList) {

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
    public void stepFinished() {

    }

    @Override
    public void stepFinished(List<ScreenshotAndHtmlSource> screenshotList) {

    }

    @Override
    public void stepFinished(List<ScreenshotAndHtmlSource> screenshotList, ZonedDateTime time) {

    }

    @Override
    public void testFailed(TestOutcome testOutcome, Throwable cause) {

    }

    @Override
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

    @Override
    public void notifyScreenChange() {

    }

    @Override
    public void useExamplesFrom(DataTable table) {

    }

    @Override
    public void addNewExamplesFrom(DataTable table) {

    }

    @Override
    public void exampleStarted(Map<String, String> data) {

    }

    @Override
    public void exampleFinished() {

    }

    @Override
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
}
