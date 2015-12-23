package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Action;
import net.serenitybdd.screenplay.Actor;
import net.thucydides.core.annotations.Step;

public class ClickOnElement implements Action {
    private final WebElementFacade element;

    @Step("{0} clicks on #target")
    public <T extends Actor> void performAs(T theUser) {
        element.click();
    }

    public ClickOnElement(WebElementFacade element) {
        this.element = element;
    }

}
