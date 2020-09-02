package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.thucydides.core.annotations.Step;

public class OpenPage implements Interaction {

    private final PageObject targetPage;

    public OpenPage(PageObject targetPage) {
        this.targetPage = targetPage;
    }

    @Step("{0} opens the #targetPage")
    public <T extends Actor> void performAs(T theUser) {
        targetPage.setDriver(BrowseTheWeb.as(theUser).getDriver());
        targetPage.open();
    }

}
