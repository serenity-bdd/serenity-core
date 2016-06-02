package net.serenitybdd.screenplay.actions.selectactions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Action;
import net.serenitybdd.screenplay.Actor;
import net.thucydides.core.annotations.Step;

public class SelectByVisibleTextFromElement implements Action {
    private final WebElementFacade element;
    private final String visibleText;

    public SelectByVisibleTextFromElement(WebElementFacade element, String visibleText) {
        this.element = element;
        this.visibleText = visibleText;
    }

    @Step("{0} clicks on #target")
    public <T extends Actor> void performAs(T theUser) {
        element.selectByVisibleText(visibleText);
    }
}
