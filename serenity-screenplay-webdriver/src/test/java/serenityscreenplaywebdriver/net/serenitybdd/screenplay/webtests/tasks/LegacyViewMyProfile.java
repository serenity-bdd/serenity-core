package serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.tasks;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Performable;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions.Click;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions.Open;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.pages.HomePage;
import serenitymodel.net.thucydides.core.annotations.Step;

public class LegacyViewMyProfile implements Performable {

    Target theProfileButton = Target.the("View profile button").locatedBy(HomePage.VIEW_PROFILE);

    HomePage homePage;

    @Step("{0} views her profile")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Open.browserOn().the(homePage),
                Click.on(theProfileButton)
        );
    }
}