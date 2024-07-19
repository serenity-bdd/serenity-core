package net.serenitybdd.screenplay.webtests;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.abilities.BrowsingTheWeb;
import net.serenitybdd.screenplay.webtests.pages.HomePage;
import net.serenitybdd.screenplay.webtests.pages.ProfilePage;
import net.serenitybdd.screenplay.webtests.questions.ProfileQuestion;
import net.serenitybdd.screenplay.webtests.tasks.OpenTheApplication;
import net.serenitybdd.screenplay.webtests.tasks.ViewMyProfile;
import net.serenitybdd.annotations.Managed;
import net.serenitybdd.annotations.Steps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static net.serenitybdd.screenplay.GivenWhenThen.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;

@RunWith(SerenityRunner.class)
public class WhenDinaInteractsWithAPageObject {

    @Managed(driver = "firefox", options="--headless")
    WebDriver herBrowser;

    ProfileQuestion herProfile = new ProfileQuestion();

    @Test
    public void danaCanUpdateHerProfile() {

        Actor dana = new Actor("Dana");
        dana.can(BrowseTheWeb.with(herBrowser));

        givenThat(dana).wasAbleTo(openTheApplication);

        // This would normally happen in one of the steps - it is shown here to illustrate how to call a page object directly
        BrowsingTheWeb.as(dana).onPage(HomePage.class).viewProfile();

        // Retrieving and interacting with a page object in Dana's browser
        ProfilePage onTheProfilePage = BrowsingTheWeb.as(dana).onPage(ProfilePage.class);

        onTheProfilePage.updateNameTo("Dana");
        onTheProfilePage.updateCountryTo("France");

        then(dana).should(seeThat(herProfile, hasProperty("name", equalTo("Dana"))));
        and(dana).should(seeThat(herProfile, hasProperty("country", equalTo("France"))));
    }

    @Steps
    OpenTheApplication openTheApplication;

    @Steps
    ViewMyProfile viewHerProfile;
}
