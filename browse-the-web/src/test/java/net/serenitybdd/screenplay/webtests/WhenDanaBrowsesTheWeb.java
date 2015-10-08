package net.serenitybdd.screenplay.webtests;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.webtests.tasks.OpenedTheApplication;
import net.serenitybdd.screenplay.webtests.tasks.UpdateHerProfile;
import net.serenitybdd.screenplay.webtests.tasks.ViewMyProfile;
import net.serenitybdd.screenplay.webtests.tasks.TheProfile;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.Steps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static net.serenitybdd.screenplay.GivenWhenThen.*;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SerenityRunner.class)
public class WhenDanaBrowsesTheWeb {

    @Managed(driver = "phantomjs")
    WebDriver firstBrowser;

    @Managed(driver = "phantomjs")
    WebDriver anotherBrowser;

    @Test
    public void danaCanUpdateHerProfile() {

        Actor dana = new Actor("Dana");
        dana.can(BrowseTheWeb.with(firstBrowser));

        givenThat(dana).has(openedTheApplication);

        when(dana).attemptsTo(viewHerProfile);
        and(dana).attemptsTo(UpdateHerProfile.withName("Dana").andCountryOfResidence("France"));

        then(dana).should(seeThat(TheProfile.name(), equalTo("Dana")));
        and(dana).should(seeThat(TheProfile.country(), equalTo("France")));
    }

    @Test
    public void multipleUsersCanUpdateTheirProfilesSimultaneously() {

        Actor dana = new Actor("Dana");
        dana.can(BrowseTheWeb.with(firstBrowser));

        Actor jane = new Actor("Jane");
        jane.can(BrowseTheWeb.with(anotherBrowser));

        givenThat(dana).has(openedTheApplication);
        andThat(jane).has(openedTheApplication);

        when(dana).attemptsTo(viewHerProfile);
        and(dana).attemptsTo(UpdateHerProfile.withName("Dana").andCountryOfResidence("France"));

        and(jane).attemptsTo(viewHerProfile);
        and(jane).attemptsTo(UpdateHerProfile.withName("Jane").andCountryOfResidence("United Kingdom"));

        then(dana).should(seeThat(TheProfile.name(), equalTo("Dana")));
        and(dana).should(seeThat(TheProfile.country(), equalTo("France")));

        and(jane).should(seeThat(TheProfile.name(), equalTo("Jane")));
        and(jane).should(seeThat(TheProfile.country(), equalTo("United Kingdom")));

    }

    @Test
    public void multipleUsersCanShareTheSameBrowser() {

        Actor dana = new Actor("Dana");
        dana.can(BrowseTheWeb.with(firstBrowser));

        Actor jane = new Actor("Jane");
        jane.can(BrowseTheWeb.with(firstBrowser));

        givenThat(dana).has(openedTheApplication);
        andThat(jane).has(openedTheApplication);

        when(dana).attemptsTo(viewHerProfile);
        and(dana).attemptsTo(UpdateHerProfile.withName("Dana").andCountryOfResidence("France"));

        then(dana).should(seeThat(TheProfile.name(), equalTo("Dana")));
        and(dana).should(seeThat(TheProfile.country(), equalTo("France")));

        when(jane).attemptsTo(viewHerProfile);
        and(jane).attemptsTo(UpdateHerProfile.withName("Jane").andCountryOfResidence("United Kingdom"));

        and(jane).should(seeThat(TheProfile.name(), equalTo("Jane")));
        and(jane).should(seeThat(TheProfile.country(), equalTo("United Kingdom")));

    }

    @Steps
    OpenedTheApplication openedTheApplication;

    @Steps
    ViewMyProfile viewHerProfile;

    @Steps
    TheProfile herProfile;

}
