package net.serenitybdd.screenplay;

import net.serenitybdd.markers.CanBeSilent;
import net.thucydides.core.annotations.Step;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SilentPerformableFunction implements Performable, CanBeSilent {
    private final Consumer<Actor> actions;

    public SilentPerformableFunction(Consumer<Actor> actions) {
        this.actions = actions;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actions.accept(actor);
    }

    @Override
    public boolean isSilent() {
        return true;
    }
}
