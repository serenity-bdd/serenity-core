package net.serenitybdd.screenplay;

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

    public static Actor named(String name) {
        return new Actor(name);
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

    @SafeVarargs
    public final <T> void should(Consequence<T>... consequences) {
        for (Consequence<T> consequence : consequences) {
            check(consequence);
        }
    }

    private boolean anOutOfStepErrorOccurred() {
        return eventBusInterface.getStepCount() > taskTally.getPerformedTaskCount();
    }

    private <T> void check(Consequence<T> consequence) {
        try {
            eventBusInterface.reportNewStepWithTitle(consequence.toString());
            consequence.evaluateFor(this);
            eventBusInterface.reportStepFinished();
        } catch (Throwable e) {
            eventBusInterface.reportStepFailureFor(consequence, e);
        }
    }

}
