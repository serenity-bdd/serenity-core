package net.serenitybdd.screenplay;


public class InteractionBuilder {
    private final String title;

    public InteractionBuilder(String title) {
        this.title = title;
    }

    public <T extends Interaction> AnonymousInteraction whereTheActorAttemptsTo(T... steps) {
        return Interaction.where(title, steps);
    }
}
