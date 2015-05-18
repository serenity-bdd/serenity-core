package net.serenitybdd.rest;

import com.jayway.restassured.specification.RequestSpecification;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.model.DataTable;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.steps.ExecutedStepDescription;
import net.thucydides.core.steps.StepFailure;
import net.thucydides.core.steps.StepListener;

import java.util.Map;

/**
 * Created by john on 18/05/2015.
 */
public class RestStepListener implements StepListener {

    @Override
    public void testSuiteStarted(Class<?> storyClass) {

    }

    @Override
    public void testSuiteStarted(Story story) {

    }

    @Override
    public void testSuiteFinished() {
        SerenityRest.clearQueryData();
    }

    @Override
    public void testStarted(String description) {

    }

    @Override
    public void testFinished(TestOutcome result) {
        SerenityRest.clearQueryData();
    }

    @Override
    public void testRetried() {
        SerenityRest.clearQueryData();
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
    public void testFailed(TestOutcome testOutcome, Throwable cause) {
        SerenityRest.clearQueryData();
    }

    @Override
    public void testIgnored() {
        SerenityRest.clearQueryData();
    }

    @Override
    public void testSkipped() {
        SerenityRest.clearQueryData();
    }

    @Override
    public void testPending() {
        SerenityRest.clearQueryData();
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
        SerenityRest.clearQueryData();
    }

    @Override
    public void assumptionViolated(String message) {
        SerenityRest.clearQueryData();
    }
}
