package net.serenitybdd.screenplay.actions.type;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.thucydides.core.annotations.Step;

public class TypeValueIntoElement extends TypeValue {

    private WebElementFacade element;

    public TypeValueIntoElement(String theText, WebElementFacade element) {
        super(theText);
        this.element = element;
    }

    @Step("{0} enters '#theText' into #element")
    public <T extends Actor> void performAs(T theUser) {
        element.sendKeys(theText);
        element.sendKeys(getFollowedByKeys());
    }
}
