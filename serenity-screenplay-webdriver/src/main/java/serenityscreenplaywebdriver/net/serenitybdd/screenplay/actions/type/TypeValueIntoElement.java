package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions.type;

import serenitycore.net.serenitybdd.core.pages.WebElementFacade;
import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenitymodel.net.thucydides.core.annotations.Step;

public class TypeValueIntoElement extends TypeValue {

    private WebElementFacade element;

    public TypeValueIntoElement(WebElementFacade element, CharSequence... theText) {
        super(theText);
        this.element = element;
    }

    @Step("{0} enters #theTextAsAString into #element")
    public <T extends Actor> void performAs(T theUser) {
        element.sendKeys(theText);
        if (getFollowedByKeys().length > 0) {
            element.sendKeys(getFollowedByKeys());
        }
    }
}
