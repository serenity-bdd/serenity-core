package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.thucydides.core.annotations.Step;

public class DoubleClickOnElement implements Interaction {
    private final WebElementFacade element;

    @Step("{0} clicks on #element")
    public <T extends Actor> void performAs(T theUser) {
        BrowseTheWeb.as(theUser).withAction().moveToElement(element).doubleClick().perform();
    }

    public DoubleClickOnElement(WebElementFacade element) {
        this.element = element;
    }

}
