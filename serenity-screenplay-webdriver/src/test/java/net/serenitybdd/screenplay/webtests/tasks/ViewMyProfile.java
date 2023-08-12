package net.serenitybdd.screenplay.webtests.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.actions.PerformOn;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.webtests.pages.HomePage;
import net.serenitybdd.annotations.Step;

public class ViewMyProfile implements Performable {

    Target theProfileButton = Target.the("View profile button").locatedBy(HomePage.VIEW_PROFILE);
    Target travelOption = Target.the("travel option").locatedBy(HomePage.TRAVEL_OPTION);
    HomePage homePage;

    @Step("{0} views her profile")
    public <T extends Actor> void performAs(T actor) {

        actor.attemptsTo(
                PerformOn.eachMatching(travelOption, Click::on),
                Open.browserOn(homePage),
                Click.on(theProfileButton)
        );
    }
}
