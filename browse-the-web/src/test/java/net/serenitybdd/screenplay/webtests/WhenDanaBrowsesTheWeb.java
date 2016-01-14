package net.serenitybdd.screenplay.webtests;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.webtests.model.Client;
import net.serenitybdd.screenplay.webtests.pages.ProfilePage;
import net.serenitybdd.screenplay.webtests.questions.ProfileQuestion;
import net.serenitybdd.screenplay.webtests.tasks.OpenTheApplication;
import net.serenitybdd.screenplay.webtests.tasks.UpdateHerProfile;
import net.serenitybdd.screenplay.webtests.tasks.ViewMyProfile;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.Steps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static net.serenitybdd.screenplay.GivenWhenThen.*;
import static net.serenitybdd.screenplay.matchers.ConsequenceMatchers.displays;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.*;
import static net.serenitybdd.screenplay.questions.WebElementQuestion.the;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(SerenityRunner.class)
public class WhenDanaBrowsesTheWeb {

    @Managed(driver = "htmlunit")
    WebDriver firstBrowser;

    @Managed(driver = "htmlunit")
    WebDriver anotherBrowser;

    ProfileQuestion profile = new ProfileQuestion();

    @Test
    public void danaCanUpdateHerProfile() {

        Actor dana = new Actor("Dana");
        dana.can(BrowseTheWeb.with(firstBrowser));

        givenThat(dana).has(openedTheApplication);

        when(dana).attemptsTo(viewHerProfile);
        and(dana).attemptsTo(UpdateHerProfile.withName("Dana").andCountryOfResidence("France"));

        then(dana).should(seeThat(profile, displays("name", equalTo("Dana"))));
        and(dana).should(seeThat(profile, displays("country", equalTo("France"))));
    }

    @Test
    public void danaCanMakeAssertionsAboutWebElements() {

        Actor dana = new Actor("Dana");
        dana.can(BrowseTheWeb.with(firstBrowser));

        givenThat(dana).has(openedTheApplication);

        when(dana).attemptsTo(viewHerProfile);
        and(dana).attemptsTo(UpdateHerProfile.withName("Dana").andCountryOfResidence("France"));

        then(dana).should(seeThat(the(ProfilePage.NAME), isVisible()));

        then(dana).should(seeThat(the(ProfilePage.NAME), isCurrentlyVisible()));
        and(dana).should(seeThat(the(ProfilePage.NAME), isEnabled()));
        and(dana).should(seeThat(the(ProfilePage.NAME), isCurrentlyEnabled()));
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

        then(dana).should(seeThat(profile, displays("name", equalTo("Dana"))));
        and(dana).should(seeThat(profile, displays("country", equalTo("France"))));

        then(jane).should(seeThat(profile, displays("name", equalTo("Jane"))));
        and(jane).should(seeThat(profile, displays("country", equalTo("United Kingdom"))));

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

        then(dana).should(seeThat(profile, displays("name", equalTo("Dana"))));
        and(dana).should(seeThat(profile, displays("country", equalTo("France"))));

        when(jane).attemptsTo(viewHerProfile);
        and(jane).attemptsTo(UpdateHerProfile.withName("Jane").andCountryOfResidence("United Kingdom"));

        then(jane).should(seeThat(profile, displays("name", equalTo("Jane"))));
        and(jane).should(seeThat(profile, displays("country", equalTo("United Kingdom"))));

    }

    @Test
    public void shouldSeeCorrectClientDetails() {

        Actor dana = new Actor("Dana");
        dana.can(BrowseTheWeb.with(firstBrowser));

        givenThat(dana).has(openedTheApplication);

        when(dana).attemptsTo(viewHerProfile);
        and(dana).attemptsTo(
                UpdateHerProfile
                        .withName("Dana")
                        .andCountryOfResidence("France")
                        .andDateOfBirth("10/10/1969")
                        .andFavoriteColor("Red"));

        then(dana).should(
                seeThat(Client.name(), is(equalTo("Dana"))),
                seeThat(Client.color(), is(equalTo("Red"))),
                seeThat(Client.dateOfBirth(), is(equalTo("10/10/1969"))),
                seeThat(Client.country(), is(equalTo("France"))));

    }
    @Steps
    OpenTheApplication openedTheApplication;

    @Steps
    ViewMyProfile viewHerProfile;

}
