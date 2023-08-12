package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.annotations.Step;

public class JSClickOnElement implements Interaction {
    private final WebElementFacade element;

    @Step("{0} clicks on #element")
    public <T extends Actor> void performAs(T theUser) {
        BrowseTheWeb.as(theUser).evaluateJavascript("arguments[0].click();", element);
    }

    public JSClickOnElement(WebElementFacade element) {
        this.element = element;
    }

}
