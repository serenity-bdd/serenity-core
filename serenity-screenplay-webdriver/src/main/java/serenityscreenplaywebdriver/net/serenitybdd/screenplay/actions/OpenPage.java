package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions;

import serenitycore.net.serenitybdd.core.pages.PageObject;
import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Interaction;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import serenitymodel.net.thucydides.core.annotations.Step;

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
