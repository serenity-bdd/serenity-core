package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.ClickInteraction;
import net.serenitybdd.screenplay.Interaction;
import net.thucydides.core.annotations.Step;

public class ClickOnElement extends ClickOnClickable {
    private final WebElementFacade element;

    @Override
    public WebElementFacade resolveFor(Actor theUser) {
        return element;
    }

    @Override
    protected String getName() {
        return element.toString();
    }

    @Step("{0} clicks on #element")
    public <T extends Actor> void performAs(T theUser) {
        element.click();
    }

    public ClickOnElement(WebElementFacade element) {
        this.element = element;
    }

}
