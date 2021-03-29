package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions;

import serenitycore.net.serenitybdd.core.pages.WebElementFacade;
import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenitymodel.net.thucydides.core.annotations.Step;

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
