package net.serenitybdd.screenplay.actions.selectactions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.thucydides.core.annotations.Step;

public class SelectByIndexFromElement implements Interaction {
    private final WebElementFacade element;
    private final Integer index;

    public SelectByIndexFromElement(WebElementFacade element, Integer index) {
        this.element = element;
        this.index = index;
    }

    @Step("{0} selects index #index")
    public <T extends Actor> void performAs(T theUser) {
        element.selectByIndex(index);
    }


}
