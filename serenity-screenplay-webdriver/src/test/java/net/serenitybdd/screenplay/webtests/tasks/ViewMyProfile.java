package net.serenitybdd.screenplay.webtests.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.MoveMouse;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.webtests.pages.HomePage;
import net.serenitybdd.screenplay.webtests.pages.ProfilePage;
import net.thucydides.core.annotations.Step;
import org.openqa.selenium.interactions.Actions;

import java.util.function.Consumer;

public class ViewMyProfile implements Performable {

    Target theProfileButton = Target.the("View profile button").locatedBy(HomePage.VIEW_PROFILE);
    HomePage homePage;

    @Step("{0} views her profile")
    public <T extends Actor> void performAs(T actor) {

        actor.attemptsTo(
                Open.browserOn(homePage),
                Click.on(theProfileButton)
        );
    }
}