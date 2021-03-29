package serenityscreenplay.net.serenitybdd.screenplay;

import serenitycore.net.serenitybdd.markers.CanBeSilent;
import serenitymodel.net.thucydides.core.annotations.Step;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class AnonymousPerformableFunction implements Performable, CanBeSilent {
    private final String title;
    private final Map<String, Object> fieldValues = new HashMap();
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
