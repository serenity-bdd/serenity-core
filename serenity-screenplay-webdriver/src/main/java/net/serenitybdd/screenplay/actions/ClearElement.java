package net.serenitybdd.screenplay.actions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;

public class ClearElement implements Interaction {
    private final WebElementFacade element;

    @Step("{0} clears field #element")
    public <T extends Actor> void performAs(T theUser) {
        element.clear();
    }

    public ClearElement(WebElementFacade element) {
        this.element = element;
    }

}
