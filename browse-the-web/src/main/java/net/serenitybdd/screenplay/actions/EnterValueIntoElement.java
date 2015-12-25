package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.thucydides.core.annotations.Step;

public class EnterValueIntoElement extends WebAction {

    private String theText;
    private WebElementFacade element;

    public EnterValueIntoElement(String theText, WebElementFacade element) {
        this.theText = theText;
        this.element = element;
    }

    @Step("{0} enters '#theText' into #element")
    public <T extends Actor> void performAs(T theUser) {
        element.type(theText);
    }
}
