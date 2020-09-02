package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.thucydides.core.annotations.Step;

public class EnterValueIntoElement extends EnterValue {

    private WebElementFacade element;

    public EnterValueIntoElement(WebElementFacade element, CharSequence... theText) {
        super(theText);
        this.element = element;
    }

    @Step("{0} enters #theTextAsAString into #element")
    public <T extends Actor> void performAs(T theUser) {
        element.type(theText);
        if (getFollowedByKeys().length > 0) {
            element.sendKeys(getFollowedByKeys());
        }
    }
}
