package net.serenitybdd.screenplay;

import net.serenitybdd.markers.CanBeSilent;
import net.serenitybdd.annotations.Step;

import java.util.function.Consumer;

public class AnonymousPerformableFunction implements Performable, CanBeSilent {
    private final String title;
    private final Consumer<Actor> actions;
    private boolean isSilent = false;

    public AnonymousPerformableFunction(String title, Consumer<Actor> actions) {
        this.title = title;
        this.actions = actions;
    }

    @Override
    @Step("!#title")
    public <T extends Actor> void performAs(T actor) {
        actions.accept(actor);
    }

    @Override
    public boolean isSilent() {
        return isSilent;
    }

    public AnonymousPerformableFunction withNoReporting() {
        this.isSilent = true;
        return this;
    }
}
