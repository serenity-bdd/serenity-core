package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.thucydides.core.annotations.Step;

import java.util.stream.Stream;

public class EnterValueIntoElement extends EnterValue {

    private WebElementFacade element;

    public EnterValueIntoElement(String theText, WebElementFacade element) {
        super(theText);
        this.element = element;
    }

    @Step("{0} enters '#theText' into #element")
    public <T extends Actor> void performAs(T theUser) {
        element.type(theText);
        if (getFollowedByKeys().length > 0) {
            element.sendKeys(getFollowedByKeys());
        }
    }
}
