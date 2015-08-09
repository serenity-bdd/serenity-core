package net.serenitybdd.screenplay;

import net.serenitybdd.screenplay.eventbus.EventBusInterface;
import net.thucydides.core.annotations.Step;

public class Actor implements PerformsTasks {

    private final String name;
    private final PerformedTaskTally taskTally = new PerformedTaskTally();
    private EventBusInterface eventBusInterface = new EventBusInterface();

    public Actor(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    public void start() {
        taskTally.reset();
    }

    @SafeVarargs
    public final <T extends Task> void has(T... todos) {
        attemptsTo(todos);
    }

    @SafeVarargs
    public final <T extends Task> void attemptsTo(T... todos) {
        for (Task todo : todos) {
            perform(todo);
        }
    }

    private void perform(Task todo) {
        try {
            taskTally.newTask();
            todo.performAs(this);

            if (anOutOfStepErrorOccurred()) {
                eventBusInterface.mergePreviousStep();
            }
        } catch (Throwable e) {
            eventBusInterface.reportStepFailureFor(todo, e);
        } finally {
            eventBusInterface.updateOverallResult();
        }

    }


    private boolean anOutOfStepErrorOccurred() {
        return eventBusInterface.getStepCount() > taskTally.getPerformedTaskCount();
    }


    @Step("Then #this should see that {0}")
    public <ANSWER> ANSWER seesThat(Question<ANSWER> question) {
        return question.answeredBy(this);
    }

    @SafeVarargs
    public final <T> void shouldSee(Consequence<T>... consequences) {
        for (Consequence<T> consequence : consequences) {
            check(consequence);
        }
    }

    private <T> void check(Consequence<T> consequence) {
        try {
            eventBusInterface.reportNewStepWithTitle(consequence.shortDescription());
            consequence.evaluate();
            eventBusInterface.reportStepFinished();
        } catch (Throwable e) {
            eventBusInterface.reportStepFailureFor(consequence, e);
        }
    }

    public static Actor named(String name) {
        return new Actor(name);
    }
}
