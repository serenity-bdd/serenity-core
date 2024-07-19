package net.serenitybdd.screenplay.actions.type;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.annotations.Step;

public class TypeValueIntoElement extends TypeValue {

    private WebElementFacade element;

    public TypeValueIntoElement(){}

    public TypeValueIntoElement(WebElementFacade element, CharSequence... theText) {
        super(theText);
        this.element = element;
    }

    @Step("{0} enters #theTextAsAString into #element")
    public <T extends Actor> void performAs(T theUser) {
        textValue().ifPresent(
                text -> element.sendKeys(text)
        );
        if (getFollowedByKeys().length > 0) {
            element.sendKeys(getFollowedByKeys());
        }
    }
}
