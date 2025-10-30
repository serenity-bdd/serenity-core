package net.serenitybdd.screenplay.actions.selectactions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;

public class SelectByIndexFromElement implements Interaction {
    private WebElementFacade element;
    private Integer[] indexes;

    public SelectByIndexFromElement() {}

    public SelectByIndexFromElement(WebElementFacade element, Integer... indexes) {
        this.element = element;
        this.indexes = indexes;
    }

    @Step("{0} selects index #index")
    public <T extends Actor> void performAs(T theUser) {
        for(Integer index : indexes) {
            element.selectByIndex(index);
        }
    }


}
