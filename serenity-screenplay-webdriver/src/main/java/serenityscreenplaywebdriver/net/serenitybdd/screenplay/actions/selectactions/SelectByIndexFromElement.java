package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions.selectactions;

import serenitycore.net.serenitybdd.core.pages.WebElementFacade;
import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Interaction;
import serenitymodel.net.thucydides.core.annotations.Step;

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
