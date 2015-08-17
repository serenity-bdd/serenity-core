package net.serenitybdd.screenplay.webtests.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.tasks.Click;
import net.serenitybdd.screenplay.webtests.pages.HomePage;
import net.thucydides.core.annotations.Step;

public class ViewMyProfile implements Performable {

    HomePage homePage;

    @Step("{0} views her profile")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Click.on(HomePage.VIEW_PROFILE));
    }
}