package net.thucydides.core;

import net.thucydides.model.domain.DataTable;
import net.thucydides.model.domain.Story;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestResult;
import net.thucydides.model.screenshots.ScreenshotAndHtmlSource;
import net.thucydides.model.steps.ExecutedStepDescription;
import net.thucydides.model.steps.StepFailure;
import net.thucydides.model.steps.StepListener;
import org.openqa.selenium.WebDriver;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class ListenerInWrongPackage implements StepListener {
    public void testSuiteStarted(Class<?> storyClass) {
        
    }

    public void testSuiteStarted(Story story) {
        
    }

    public void testSuiteFinished() {
    }

    public void testStarted(String description) {
        
    }

    @Override
    public void testStarted(String description, String id) {

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

    public void lastStepFailed(StepFailure failure) {
    }

    public void stepIgnored() {
        
    }

    public void stepIgnored(String message) {
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

    public void testFailed(TestOutcome testOutcome, Throwable cause) {
    }

    public void testIgnored() {
    }

    @Override
    public void testSkipped() {

    }

    @Override
    public void testAborted() {

    }

    @Override
    public void testPending() {

    }

    @Override
    public void testIsManual() {

    }

    public List<TestOutcome> getTestOutcomes() {
        return null;  
    }

    public WebDriver getDriver() {
        return null;  
    }

    public void notifyScreenChange() {
    }

    public void useExamplesFrom(DataTable table) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addNewExamplesFrom(DataTable table) {

    }

    public void exampleStarted(Map<String,String> data) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void exampleFinished() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void assumptionViolated(String message) {
        //To change body of implemented methods use File | Settings | File Templates.
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
