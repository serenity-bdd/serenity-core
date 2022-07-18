package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.interactions.Actions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class ChainablePerformable implements WithChainableActions {

    private List<Consumer<Actions>> actionsToPerform = new ArrayList<>();

    protected ChainablePerformable() {}

    protected void addAction(Consumer<Actions> action) {
        actionsToPerform.add(action);
    }

    protected void addActionAtStart(Consumer<Actions> action) {
        actionsToPerform.add(0,action);
    }

    public WithChainableActions andThen(Consumer<Actions> nextAction) {
        actionsToPerform.add(nextAction);
        return this;
    }

    public <T extends Actor> void performSubsequentActionsAs(Actor actor) {
        Actions browserActions = BrowseTheWeb.as(actor).withAction();
        actionsToPerform.forEach(
                action -> action.accept(browserActions)
        );
        browserActions.build().perform();
    }
}
