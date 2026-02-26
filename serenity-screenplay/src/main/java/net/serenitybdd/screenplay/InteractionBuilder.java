package net.serenitybdd.screenplay;


public class InteractionBuilder {
    private final String title;

    public InteractionBuilder(String title) {
        this.title = title;
    }

    public <T extends Performable> AnonymousTask whereTheActorAttemptsTo(T... steps) {
        return Task.where(title, steps);
    }
}
