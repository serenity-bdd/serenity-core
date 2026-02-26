package net.serenitybdd.screenplay;

import java.util.function.Consumer;

public class AnonymousTaskFunction extends AnonymousPerformableFunction implements Task {
    public AnonymousTaskFunction(String title, Consumer<Actor> actions) {
        super(title, actions);
    }
}
