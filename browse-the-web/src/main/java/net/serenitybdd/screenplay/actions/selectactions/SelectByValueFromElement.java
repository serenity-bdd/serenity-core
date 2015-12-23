package net.serenitybdd.screenplay.actions.selectactions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.targets.Target;
import net.serenitybdd.screenplay.Action;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.thucydides.core.annotations.Step;

public class SelectByValueFromElement implements Action {
    private final WebElementFacade element;
    private final String value;

    public SelectByValueFromElement(WebElementFacade element, String value) {
        this.element = element;
        this.value = value;
    }

    @Step("{0} clicks on #target")
    public <T extends Actor> void performAs(T theUser) {
        element.selectByVisibleText(value);
    }


}
