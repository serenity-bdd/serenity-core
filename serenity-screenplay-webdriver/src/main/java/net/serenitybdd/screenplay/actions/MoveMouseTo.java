package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

abstract class MoveMouseTo implements WithChainableActions {

    private List<Consumer<Actions>> actionsToPerfomm = new ArrayList<>();

    protected MoveMouseTo() {
    }

    protected void performMouseMoveAs(Actor actor, WebElement element) {
        Actions browserActions = BrowseTheWeb.as(actor).withAction();

        browserActions.moveToElement(element);
        actionsToPerfomm.forEach(
                action -> action.accept(browserActions)
        );
        browserActions.perform();
    }

    public WithChainableActions andThen(Consumer<Actions> nextAction) {
        actionsToPerfomm.add(nextAction);
        return this;
    }

}