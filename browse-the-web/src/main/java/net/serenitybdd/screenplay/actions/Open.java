package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.screenplay.Action;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.thucydides.core.annotations.Step;

import static net.serenitybdd.screenplay.Tasks.instrumented;

//@Task("Opens the #targetPage {0}")
public class Open implements Action {

    private PageObject targetPage;

    @Step("Opens the #targetPage")
    public <T extends Actor> void performAs(T theUser) {
        targetPage.setDriver(BrowseTheWeb.as(theUser).getDriver());
        targetPage.open();
    }

    public Open the(PageObject targetPage) {
        this.targetPage = targetPage;
        return this;
    }

    public static Open browserOn() {
        return instrumented(Open.class);
    }


}
