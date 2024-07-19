package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.annotations.Step;

public class RightClickOnElement implements Interaction {
    private final WebElementFacade element;

    @Step("{0} right-clicks on #element")
    public <T extends Actor> void performAs(T theUser) {
        BrowseTheWeb.as(theUser).withAction().contextClick(element).perform();
    }

    public RightClickOnElement(WebElementFacade element) {
        this.element = element;
    }
}
