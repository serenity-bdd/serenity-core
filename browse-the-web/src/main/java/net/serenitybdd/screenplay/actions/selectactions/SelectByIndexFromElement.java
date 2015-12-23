package net.serenitybdd.screenplay.actions.selectactions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Action;
import net.serenitybdd.screenplay.Actor;
import net.thucydides.core.annotations.Step;

public class SelectByIndexFromElement implements Action {
    private final WebElementFacade element;
    private final Integer index;

    public SelectByIndexFromElement(WebElementFacade element, Integer index) {
        this.element = element;
        this.index = index;
    }

    @Step("{0} clicks on #target")
    public <T extends Actor> void performAs(T theUser) {
        element.selectByIndex(index);
    }


}
