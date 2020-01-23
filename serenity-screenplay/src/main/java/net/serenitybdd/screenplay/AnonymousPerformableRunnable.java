package net.serenitybdd.screenplay;

import net.serenitybdd.markers.CanBeSilent;
import net.thucydides.core.annotations.Step;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class AnonymousPerformableRunnable implements Performable, CanBeSilent {
    private final String title;
    private final Map<String, Object> fieldValues = new HashMap();
    private final Runnable actions;
    private boolean isSilent = false;

    public AnonymousPerformableRunnable(String title, Runnable actions) {
        this.title = title;
        this.actions = actions;
    }

    @Override
    @Step("!#title")
    public <T extends Actor> void performAs(T actor) {
        actions.run();
    }

    @Override
    public boolean isSilent() {
        return isSilent;
    }

    public AnonymousPerformableRunnable withNoReporting() {
        this.isSilent = true;
        return this;
    }
}
