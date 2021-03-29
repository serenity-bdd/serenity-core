package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions;

import serenitycore.net.serenitybdd.core.pages.WebElementFacade;
import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Interaction;
import serenitymodel.net.thucydides.core.annotations.Step;

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
