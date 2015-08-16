package net.serenitybdd.screenplay.webtests;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.webtests.tasks.ViewMyProfileField;
import net.serenitybdd.screenplay.webtests.tasks.OpenedTheApplication;
import net.serenitybdd.screenplay.webtests.tasks.UpdateHerProfile;
import net.serenitybdd.screenplay.webtests.tasks.ViewMyProfile;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.Steps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static net.serenitybdd.screenplay.GivenWhenThen.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(SerenityRunner.class)
public class WhenDanaBrowsesTheWeb {

    @Managed(driver="phantomjs")
    WebDriver driver;

    @Test
    public void danaCanClickOnButtons() {

        Actor dana = new Actor("Dana");
        dana.can(BrowseTheWeb.with(driver));

        givenThat(dana).has(openedTheApplication);

        when(dana).attemptsTo(viewHerProfile);
        and(dana).attemptsTo(updateHer.name().to("Dana"));

        then(dana).should(seeThat(herProfile.name(), equalTo("Dana")));
    }

    @Steps
    OpenedTheApplication openedTheApplication;

    @Steps
    ViewMyProfile viewHerProfile;

    @Steps
    UpdateHerProfile updateHer;

    @Steps
    ViewMyProfileField herProfile;

}
