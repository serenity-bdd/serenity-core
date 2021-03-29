package serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.tasks;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Performable;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions.Open;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.pages.HomePage;
import serenitymodel.net.thucydides.core.annotations.Step;

public class OpenTheApplication implements Performable {

    HomePage homePage;

    @Step("{0} opens the application")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Open.browserOn().the(homePage));
        // or
        actor.attemptsTo(Open.browserOn().the(HomePage.class));
    }
}