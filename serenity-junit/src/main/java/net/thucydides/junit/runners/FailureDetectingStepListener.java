package net.thucydides.junit.runners;

import net.thucydides.core.model.DataTable;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.steps.ExecutedStepDescription;
import net.thucydides.core.steps.StepFailure;
import net.thucydides.core.steps.StepListener;

import java.util.Map;

public class FailureDetectingStepListener implements StepListener {

    private boolean lastTestFailed = false;

    public void reset() {
        lastTestFailed = false;
    }

    public boolean lastTestFailed() {
        return lastTestFailed;
    }
    
    public void testFailed(TestOutcome testOutcome, Throwable cause) {
        lastTestFailed = true;
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

    


    
    public void stepIgnored() {
        
    }

    
    public void stepPending() {
        
    }

    
    public void stepPending(String message) {
        
    }

    
    public void stepFinished() {
        
    }

    
    public void testIgnored() {
        
    }

    @Override
    public void testSkipped() {

    }

    @Override
    public void testPending() {

    }


    public void notifyScreenChange() {
        
    }

    
    public void useExamplesFrom(DataTable table) {
        
    }

    
    public void exampleStarted(Map<String, String> data) {
        
    }

    
    public void exampleFinished() {
        
    }

    
    public void assumptionViolated(String message) {
        
    }
}
