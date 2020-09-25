package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.thucydides.core.annotations.Step;

public class ClickOnElement extends ClickOnClickable {
    private final WebElementFacade element;

    @Step("{0} clicks on #element")
    public <T extends Actor> void performAs(T theUser) {
        element.click(getClickStrategy());
    }

    public ClickOnElement(WebElementFacade element) {
        this.element = element;
    }

}
