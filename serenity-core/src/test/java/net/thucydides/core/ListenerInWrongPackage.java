package net.thucydides.core;

import net.thucydides.core.model.DataTable;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.steps.ExecutedStepDescription;
import net.thucydides.core.steps.StepFailure;
import net.thucydides.core.steps.StepListener;
import org.openqa.selenium.WebDriver;

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

    public void testFinished(TestOutcome result) {
        
    }

    public void testRetried() {
    }

    public void stepStarted(ExecutedStepDescription description) {
        
    }

    public void skippedStepStarted(ExecutedStepDescription description) {
    }

    public void stepFailed(StepFailure failure) {
        
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

    public void testFailed(TestOutcome testOutcome, Throwable cause) {
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
}
