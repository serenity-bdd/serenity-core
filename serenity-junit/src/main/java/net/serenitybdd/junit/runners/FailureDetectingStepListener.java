package net.serenitybdd.junit.runners;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.ImmutableList;
import net.thucydides.core.model.DataTable;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.steps.ExecutedStepDescription;
import net.thucydides.core.steps.StepFailure;
import net.thucydides.core.steps.StepListener;

import java.util.List;
import java.util.Map;

public class FailureDetectingStepListener implements StepListener {

    private boolean lastTestFailed = false;
    private List<String> failureMessages = Lists.newArrayList();

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

    public List<String> getFailureMessages() {
        return ImmutableList.copyOf(failureMessages);
    }
}
