package net.serenitybdd.screenplay;

import net.thucydides.core.steps.ExecutedStepDescription;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.StepFailure;


public class EventBusInterface {

    public void reportStepFailureFor(Task todo, Throwable e) {
        ExecutedStepDescription taskDescription =  ExecutedStepDescription.of(todo.getClass(),"attemptsTo");
        StepEventBus.getEventBus().stepFailed(new StepFailure(taskDescription, e));
    }

    public <T> void reportStepFailureFor(Consequence<T> consequence, Throwable e) {
        ExecutedStepDescription consequenceDescription = ExecutedStepDescription.withTitle(consequence.toString());
        StepEventBus.getEventBus().stepFailed(new StepFailure(consequenceDescription, e));
    }

    public int getStepCount() {
        return StepEventBus.getEventBus().getBaseStepListener().getStepCount();
    }

    public void mergePreviousStep() {
        StepEventBus.getEventBus().mergePreviousStep();
    }

    public void updateOverallResult() {
        StepEventBus.getEventBus().updateOverallResults();
    }

    public void reportNewStepWithTitle(String title) {
        StepEventBus.getEventBus().stepStarted(ExecutedStepDescription.withTitle(title));
    }

    public void reportStepFinished() {
        StepEventBus.getEventBus().stepFinished();
    }
}
