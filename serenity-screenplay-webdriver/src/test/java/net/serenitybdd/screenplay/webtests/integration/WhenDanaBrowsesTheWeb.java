package net.serenitybdd.screenplay.webtests.integration;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.questions.WebDriverQuestion;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.waits.WaitUntil;
import net.serenitybdd.screenplay.webtests.model.Client;
import net.serenitybdd.screenplay.webtests.pages.BankAccountEntry;
import net.serenitybdd.screenplay.webtests.pages.ProfilePage;
import net.serenitybdd.screenplay.webtests.questions.BankBalanceQuestion;
import net.serenitybdd.screenplay.webtests.questions.ProfileQuestion;
import net.serenitybdd.screenplay.webtests.questions.TheValidationMessages;
import net.serenitybdd.screenplay.webtests.tasks.*;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.Steps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static net.serenitybdd.screenplay.GivenWhenThen.*;
import static net.serenitybdd.screenplay.matchers.ConsequenceMatchers.displays;
import static net.serenitybdd.screenplay.matchers.ReportedErrorMessages.reportsErrors;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.*;
import static net.serenitybdd.screenplay.questions.WebElementQuestion.the;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(SerenityRunner.class)
public class WhenDanaBrowsesTheWeb {

    @Managed(driver = "chrome", options = "--headless")
    WebDriver firstBrowser;

    @Managed(driver = "chrome", options = "--headless")
    WebDriver anotherBrowser;

    ProfileQuestion profile = new ProfileQuestion();

    BankBalanceQuestion balances = new BankBalanceQuestion();

    @Test
    public void danaCanUpdateHerProfile() {

        Actor dana = new Actor("Dana");
        dana.can(BrowseTheWeb.with(firstBrowser));
        givenThat(dana).has(openedTheApplication);

        when(dana).attemptsTo(viewHerProfile);
        and(dana).attemptsTo(UpdateHerProfile.withName("Dana").andCountryOfResidence("France"));

        then(dana).should(seeThat(profile, displays("name", equalTo("Dana"))));
        and(dana).should(seeThat(profile, displays("country", equalTo("France"))));

        and(dana).should(
                seeThat(
                        WebDriverQuestion.about("the country").answeredBy(
                                browser -> browser.findBy("#country").getValue()
                        ),
                        equalTo("FR")
                )
        );
    }

    public static class TheProfileName implements Question<Boolean> {

        @Override
        public Boolean answeredBy(Actor actor) {
            return the(ProfilePage.NAME).answeredBy(actor).isCurrentlyVisible();
        }

        public static TheProfileName isDisplayed() {
            return new TheProfileName();
        }
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
        and(dana).should(seeThat(TheProfileName.isDisplayed()));
    }


    @Test
    public void danaCanMakeAssertionsAboutTheContentsOfWebElements() {

        Actor dana = new Actor("Dana");
        dana.can(BrowseTheWeb.with(firstBrowser));

        givenThat(dana).has(openedTheApplication);

        when(dana).attemptsTo(viewHerProfile);
        and(dana).attemptsTo(UpdateHerProfile.withName("Dana").andCountryOfResidence("France"));

        then(dana).should(seeThat(the(ProfilePage.NAME), hasValue("Dana")));
    }

    @Test
    public void danaCanWaitForTheStateOfTheWebPage() {

        Target nameField = Target.the("nameField").locatedBy("#name");

        Actor dana = new Actor("Dana");
        dana.can(BrowseTheWeb.with(firstBrowser));

        givenThat(dana).has(openedTheApplication);

        when(dana).attemptsTo(viewHerProfile);

        and(dana).attemptsTo(WaitUntil.the(nameField, isVisible()));

        assertThat(the(nameField).answeredBy(dana), isVisible());
    }


    ProfilePage profilePage;

    @Test
    public void danaCanMakeAssertionsAboutRawWebElements() {

        Actor dana = new Actor("Dana");
        dana.can(BrowseTheWeb.with(firstBrowser));

        givenThat(dana).has(openedTheApplication);

        when(dana).attemptsTo(viewHerProfile);

        profilePage.setDriver(firstBrowser);
        assertThat(profilePage.dob, isVisible());
    }

    @Test
    public void danaCanMakeAssertionsAboutWebElementsInTheSameAssertion() {

        Actor dana = new Actor("Dana");
        dana.can(BrowseTheWeb.with(firstBrowser));

        givenThat(dana).has(openedTheApplication);

        when(dana).attemptsTo(viewHerProfile);
        and(dana).attemptsTo(UpdateHerProfile.withName("Dana").andCountryOfResidence("France"));

        then(dana).should(seeThat(the(ProfilePage.NAME),
                isVisible(),
                isCurrentlyVisible(),
                isEnabled()));
    }

    /*
  public void should_not_be_able_to_add_invalid_or_impossible_bank_account_details() {
        givenThat(alice).wasAbleTo(AddADriverProfileViaApi.forASydneyIndependentDriver());
        givenThat(alice).wasAbleTo(Login.toToIngogoPortal());

        when(alice).attemptsTo(ViewTheDrivers.profileDetails());

        then(alice).should(

                seeThat(TheValidationMessages.displayed(), reportsErrors("BSB must be 6 digits")).
                        whenAttemptingTo(EnterABankAccount.bsbValueOf("qwerty")).
                        because("BSB cannot have alphabetical characters"),

                seeThat(TheValidationMessages.displayed(), reportsErrors("BSB must be 6 digits")).
                        whenAttemptingTo(EnterABankAccount.bsbValueOf("~!@#$%^&*(")).
                        because("BSB cannot have symbols"),

                seeThat(TheValidationMessages.displayed(), reportsErrors("BSB must be 6 digits")).
                        whenAttemptingTo(EnterABankAccount.bsbValueOf("1234")).
                        because("BSB cannot be a number with less than 6 digits")

 */
    @Test
    public void itShouldBeEasyForDanaToEnterAccurateBankAccountDetails() {

        Actor dana = new Actor("Dana");
        dana.can(BrowseTheWeb.with(firstBrowser));

        givenThat(dana).has(openedTheApplication);

        when(dana).attemptsTo(viewHerProfile);

        then(dana).should(
                seeThat(TheValidationMessages.displayed(), reportsErrors("BSB must be 6 digits")).
                        whenAttemptingTo(EnterABankAccount.bsbValueOf("qwerty")).
                        because("BSB cannot have alphabetical characters")
        );
        then(dana).should(
                seeThat(TheValidationMessages.displayed(), reportsErrors("BSB must be 6 digits")).
                        whenAttemptingTo(EnterABankAccount.bsbValueOf("12345")).
                        because("BSB cannot have less than 6 digits")
        );
    }

    @Test
    public void danaShouldBeAbleToSeeHerBankBalancesEvenIfTheyAreInANestedIFrame() {

        Actor dana = new Actor("Dana");
        dana.can(BrowseTheWeb.with(firstBrowser));

        givenThat(dana).has(openedTheApplication);

        when(dana).attemptsTo(viewHerProfile);
        then(dana).should(seeThat(balances, displays("currentAccount", equalTo("£100.36"))));
        and(dana).should(seeThat(balances, displays("savingsAccount", equalTo("£1024.12"))));
    }

    @Test
    public void danaShouldBeAbleToInteractWithTargetsInMultipleIFrames() {

        Actor dana = new Actor("Dana");
        dana.can(BrowseTheWeb.with(firstBrowser));

        givenThat(dana).has(openedTheApplication);
        when(dana).attemptsTo(viewHerProfile,
                Enter.theValue("HSBC").into(BankAccountEntry.ACCOUNT_NAME));
        then(dana).should(seeThat(balances, displays("currentAccount", equalTo("£100.36"))));
        and(dana).attemptsTo(Enter.theValue("Dana").into(ProfilePage.NAME));
        then(dana).should(seeThat(balances, displays("savingsAccount", equalTo("£1024.12"))));
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

    @Test
    public void shouldSeeCorrectLegacyClientDetails() {

        Actor dana = new Actor("Dana");
        dana.can(BrowseTheWeb.with(firstBrowser));

        givenThat(dana).has(openedTheApplication);

        when(dana).attemptsTo(viewHerOldProfile);
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
    LegacyViewMyProfile viewHerOldProfile;

    @Steps
    ViewMyProfile viewHerProfile;

}
