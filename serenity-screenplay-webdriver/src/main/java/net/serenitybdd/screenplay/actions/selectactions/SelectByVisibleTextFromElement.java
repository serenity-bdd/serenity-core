package net.serenitybdd.screenplay.actions.selectactions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.thucydides.core.annotations.Step;

public class SelectByVisibleTextFromElement implements Interaction {
    private final WebElementFacade element;
    private final String visibleText;

    public SelectByVisibleTextFromElement(WebElementFacade element, String visibleText) {
        this.element = element;
        this.visibleText = visibleText;
    }

    @Step("{0} selects #visibleText")
    public <T extends Actor> void performAs(T theUser) {
        element.selectByVisibleText(visibleText);
    }
}
