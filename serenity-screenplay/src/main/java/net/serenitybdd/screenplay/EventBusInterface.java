package net.serenitybdd.screenplay;

import net.thucydides.core.steps.ExecutedStepDescription;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.StepFailure;


public class EventBusInterface {

    public void reportStepFailureFor(Performable todo, Throwable e) {
        ExecutedStepDescription taskDescription =  ExecutedStepDescription.of(todo.getClass(),"attemptsTo");
        StepEventBus.getEventBus().stepFailed(new StepFailure(taskDescription, e));
    }

    public <T> void reportStepFailureFor(Consequence<T> consequence, Throwable e) {
        ExecutedStepDescription consequenceDescription = ExecutedStepDescription.withTitle(consequence.toString());
        StepEventBus.getEventBus().stepFailed(new StepFailure(consequenceDescription, e));
    }

    public int getRunningStepCount() {
        return StepEventBus.getEventBus().getBaseStepListener().getRunningStepCount();
    }

    public void mergePreviousStep() {
        StepEventBus.getEventBus().mergePreviousStep();
    }

    public void updateOverallResult() {
        StepEventBus.getEventBus().updateOverallResults();
    }

    public void startQuestion(String title) {
        StepEventBus.getEventBus().stepStarted(ExecutedStepDescription.withTitle(title).asAQuestion());
    }

    public void finishQuestion() {
        StepEventBus.getEventBus().stepFinished();
    }

    public void reportStepFinished() {
        StepEventBus.getEventBus().stepFinished();
    }

    public void reportStepIgnored() {
        StepEventBus.getEventBus().stepIgnored();
    }

    public void reportStepSkippedFor(Performable todo) {
        ExecutedStepDescription taskDescription =  ExecutedStepDescription.of(todo.getClass(),"performAs");
        StepEventBus.getEventBus().stepStarted(taskDescription);
        StepEventBus.getEventBus().stepIgnored();
    }


    public boolean aStepHasFailed() { return StepEventBus.getEventBus().getBaseStepListener().aStepHasFailed(); }
    public boolean aStepHasFailedInTheCurrentExample() { return StepEventBus.getEventBus().getBaseStepListener().aStepHasFailedInTheCurrentExample(); }

    public boolean shouldIgnoreConsequences() {
        if (StepEventBus.getEventBus().softAssertsActive() && !StepEventBus.getEventBus().currentTestIsSuspended() ) {
            return false;
        }
        return (StepEventBus.getEventBus().currentTestIsSuspended() || StepEventBus.getEventBus().aStepInTheCurrentTestHasFailed());
    }

    public void enableSoftAsserts() {
        StepEventBus.getEventBus().enableSoftAsserts();
    }

    public void disableSoftAsserts() {
        StepEventBus.getEventBus().disableSoftAsserts();
    }

    public boolean stepsAreRunning() {
        return StepEventBus.getEventBus().areStepsRunning();
    }
}
