package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.conditions.SilentPerformable;
import org.openqa.selenium.interactions.Actions;

import java.util.function.Consumer;

public class PerformActions extends SilentPerformable {
    private Consumer<Actions> actions;

    private PerformActions(Consumer<Actions> actions) {
        this.actions = actions;
    }

    public static PerformActions with(Consumer<Actions> actions) {
        return new PerformActions(actions);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actions.accept(BrowseTheWeb.as(actor).withAction());
    }
}
