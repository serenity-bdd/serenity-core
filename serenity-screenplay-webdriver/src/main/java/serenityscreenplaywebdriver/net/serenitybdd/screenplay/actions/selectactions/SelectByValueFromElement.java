package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions.selectactions;

import serenitycore.net.serenitybdd.core.pages.WebElementFacade;
import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Interaction;
import serenitymodel.net.thucydides.core.annotations.Step;

public class SelectByValueFromElement implements Interaction {
    private final WebElementFacade element;
    private final String value;

    public SelectByValueFromElement(WebElementFacade element, String value) {
        this.element = element;
        this.value = value;
    }

    @Step("{0} selects #value in #element")
    public <T extends Actor> void performAs(T theUser) {
        element.selectByValue(value);
    }


}
