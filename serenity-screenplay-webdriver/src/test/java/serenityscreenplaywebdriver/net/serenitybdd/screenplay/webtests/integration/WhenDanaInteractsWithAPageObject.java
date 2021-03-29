package serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.integration;

import net.serenitybdd.junit.runners.SerenityRunner;
import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.abilities.BrowsingTheWeb;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.pages.HomePage;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.pages.ProfilePage;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.questions.ProfileQuestion;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.tasks.OpenTheApplication;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.tasks.ViewMyProfile;
import serenitycore.net.thucydides.core.annotations.Managed;
import serenitycore.net.thucydides.core.annotations.Steps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static serenityscreenplay.net.serenitybdd.screenplay.GivenWhenThen.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;

@RunWith(SerenityRunner.class)
public class WhenDanaInteractsWithAPageObject {

    @Managed(driver = "htmlunit")
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
