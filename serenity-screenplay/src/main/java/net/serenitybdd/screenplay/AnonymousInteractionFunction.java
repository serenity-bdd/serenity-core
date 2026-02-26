package net.serenitybdd.screenplay;

import java.util.function.Consumer;

public class AnonymousInteractionFunction extends AnonymousPerformableFunction implements Interaction {
    public AnonymousInteractionFunction(String title, Consumer<Actor> actions) {
        super(title, actions);
    }
}
