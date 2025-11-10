package net.serenitybdd.screenplay;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.markers.CanBeSilent;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.model.steps.ExecutedStepDescription;
import net.thucydides.model.steps.StepFailure;

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
        try {
            actions.accept(actor);
        } catch (Throwable e) {
            StepEventBus.getParallelEventBus().stepFailed(new StepFailure(ExecutedStepDescription.withTitle(e.getMessage()), e));
            throw e;
        }
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
