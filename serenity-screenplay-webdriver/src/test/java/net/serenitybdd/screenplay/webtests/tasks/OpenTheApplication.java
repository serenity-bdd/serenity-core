package net.serenitybdd.screenplay.webtests.tasks;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.webtests.pages.HomePage;

public class OpenTheApplication implements Performable {

    HomePage homePage;

    @Step("{0} opens the application")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Open.browserOn().the(homePage));
        // or
        actor.attemptsTo(Open.browserOn().the(HomePage.class));
    }
}
