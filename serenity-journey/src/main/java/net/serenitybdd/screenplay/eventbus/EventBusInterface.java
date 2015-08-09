package net.serenitybdd.screenplay.eventbus;

import net.serenitybdd.screenplay.PerformedTaskTally;
import net.serenitybdd.screenplay.Task;
import net.thucydides.core.steps.ExecutedStepDescription;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.StepFailure;


public class EventBusInterface {
    private int stepCount;

    public void reportStepFailureFor(Task todo, Throwable e) {
        ExecutedStepDescription taskDescription =  ExecutedStepDescription.of(todo.getClass(),"attemptsTo");
        StepEventBus.getEventBus().stepFailed(new StepFailure(taskDescription, e));
    }

    public int getStepCount() {
        return StepEventBus.getEventBus().getBaseStepListener().getStepCount();
    }

    public void mergePreviousStep() {
        StepEventBus.getEventBus().mergePreviousStep();
    }
}
