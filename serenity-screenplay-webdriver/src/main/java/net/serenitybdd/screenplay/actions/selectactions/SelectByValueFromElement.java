package net.serenitybdd.screenplay.actions.selectactions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.annotations.Step;

public class SelectByValueFromElement implements Interaction {
    private WebElementFacade element;
    private String[] values;

    SelectByValueFromElement() {}

    public SelectByValueFromElement(WebElementFacade element, String... values) {
        this.element = element;
        this.values = values;
    }

    @Step("{0} selects #value in #element")
    public <T extends Actor> void performAs(T theUser) {
        for(String value : values) {
            element.selectByValue(value);
        }
    }
}
