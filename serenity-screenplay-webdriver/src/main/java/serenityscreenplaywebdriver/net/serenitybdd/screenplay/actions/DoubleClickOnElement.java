package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions;

import serenitycore.net.serenitybdd.core.pages.WebElementFacade;
import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Interaction;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import serenitymodel.net.thucydides.core.annotations.Step;

public class DoubleClickOnElement implements Interaction {
    private final WebElementFacade element;

    @Step("{0} clicks on #element")
    public <T extends Actor> void performAs(T theUser) {
        BrowseTheWeb.as(theUser).withAction().moveToElement(element).doubleClick().perform();
    }

    public DoubleClickOnElement(WebElementFacade element) {
        this.element = element;
    }

}
