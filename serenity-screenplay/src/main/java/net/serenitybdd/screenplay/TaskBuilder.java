package net.serenitybdd.screenplay;


public class TaskBuilder {
    private final String title;

    public TaskBuilder(String title) {
        this.title = title;
    }

    public <T extends Task> AnonymousTask whereTheActorAttemptsTo(T... steps) {
        return Task.where(title, steps);
    }
}
