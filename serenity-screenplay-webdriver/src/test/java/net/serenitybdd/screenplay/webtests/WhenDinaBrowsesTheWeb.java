package net.serenitybdd.screenplay.webtests;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.actions.InTheBrowser;
import net.serenitybdd.screenplay.questions.Presence;
import net.serenitybdd.screenplay.questions.Value;
import net.serenitybdd.screenplay.questions.Visibility;
import net.serenitybdd.screenplay.questions.WebDriverQuestion;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.waits.Wait;
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
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.util.Collection;

import static net.serenitybdd.screenplay.GivenWhenThen.*;
import static net.serenitybdd.screenplay.matchers.ConsequenceMatchers.displays;
import static net.serenitybdd.screenplay.matchers.ReportedErrorMessages.reportsErrors;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.hasValue;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.*;
import static net.serenitybdd.screenplay.questions.WebElementQuestion.the;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SerenityRunner.class)
public class WhenDinaBrowsesTheWeb {

    @Managed(driver = "chrome", options = "--headless")
    WebDriver firstBrowser;

    @Managed(driver = "chrome", options = "--headless")
    WebDriver anotherBrowser;

    ProfileQuestion profile = new ProfileQuestion();

    BankBalanceQuestion balances = new BankBalanceQuestion();

    @BeforeClass
    public static void setupDriver() {
        WebDriverManager.chromedriver().setup();
    }

    @Test
    public void dinaCanUpdateHerProfile() {

        Actor dina = new Actor("dina");
        dina.can(BrowseTheWeb.with(firstBrowser));
        givenThat(dina).has(openedTheApplication);

        when(dina).attemptsTo(viewHerProfile);
        and(dina).attemptsTo(UpdateHerProfile.withName("dina").andCountryOfResidence("France"));

        then(dina).should(seeThat(profile, displays("name", equalTo("dina"))));
        and(dina).should(seeThat(profile, displays("country", equalTo("France"))));

        and(dina).should(
                seeThat(
                        WebDriverQuestion.about("the country").answeredBy(
                                browser -> browser.findBy("#country").getValue()
                        ),
                        equalTo("FR")
                )
        );
    }

    public static class TheProfileName {

        public static Question<Boolean> isDisplayed() {
            return Visibility.of(ProfilePage.NAME);
        }
    }


    @Test
    public void dinaCanAskQuestionsAboutWebElements() {

        Actor dina = new Actor("dina");
        dina.can(BrowseTheWeb.with(firstBrowser));

        givenThat(dina).has(openedTheApplication);

        when(dina).attemptsTo(viewHerProfile);
        and(dina).attemptsTo(UpdateHerProfile.withName("dina").andCountryOfResidence("France"));

        int nameLength = Value.of(ProfilePage.NAME).map(String::length).answeredBy(dina);
        String firstLetter = Value.of(ProfilePage.NAME).map(value -> value.substring(0, 1)).answeredBy(dina);

        assertThat(nameLength, equalTo(4));
        assertThat(firstLetter, equalTo("d"));
    }

    @Test
    public void dinaCanAskQuestionsAboutCollectionsOfWebElements() {

        Actor dina = new Actor("dina");
        dina.can(BrowseTheWeb.with(firstBrowser));

        givenThat(dina).has(openedTheApplication);

        when(dina).attemptsTo(viewHerProfile);
        and(dina).attemptsTo(UpdateHerProfile.withName("dina").andCountryOfResidence("France"));

        Collection<String> firstLetters = Value.ofEach(ProfilePage.NAME).mapEach(value -> value.substring(0, 1)).answeredBy(dina);

        assertThat(firstLetters, hasItems("d"));
    }

    @Test
    public void dinaCanMakeAssertionsAboutWebElements() {

        Actor dina = new Actor("dina");
        dina.can(BrowseTheWeb.with(firstBrowser));

        givenThat(dina).has(openedTheApplication);

        when(dina).attemptsTo(viewHerProfile);
        and(dina).attemptsTo(UpdateHerProfile.withName("dina").andCountryOfResidence("France"));

        then(dina).should(seeThat(the(ProfilePage.NAME), isVisible()));

        then(dina).should(seeThat(the(ProfilePage.NAME), isCurrentlyVisible()));
        and(dina).should(seeThat(the(ProfilePage.NAME), isEnabled()));
        and(dina).should(seeThat(the(ProfilePage.NAME), isCurrentlyEnabled()));
        and(dina).should(seeThat(TheProfileName.isDisplayed()));
        and(dina).should(seeThat(Visibility.of(ProfilePage.NAME)));
        and(dina).should(seeThat(Presence.of(ProfilePage.NAME)));
        assertThat(Visibility.of(ProfilePage.NAME).answeredBy(dina), is(true));
        assertThat(Visibility.of(ProfilePage.NAME).answeredBy(dina), is(true));
    }


    @Test
    public void dinaCanMakeAssertionsAboutTheContentsOfWebElements() {

        Actor dina = new Actor("dina");
        dina.can(BrowseTheWeb.with(firstBrowser));

        givenThat(dina).has(openedTheApplication);

        when(dina).attemptsTo(viewHerProfile);
        and(dina).attemptsTo(UpdateHerProfile.withName("dina").andCountryOfResidence("France"));

        then(dina).should(seeThat(the(ProfilePage.NAME), hasValue("dina")));
    }

    @Test
    public void dinaCanWaitForTheStateOfTheWebPage() {

        Target nameField = Target.the("nameField").locatedBy("#name");

        Actor dina = new Actor("dina");
        dina.can(BrowseTheWeb.with(firstBrowser));

        givenThat(dina).has(openedTheApplication);

        when(dina).attemptsTo(viewHerProfile);

        and(dina).attemptsTo(WaitUntil.the(nameField, isVisible()));

        assertThat(the(nameField).answeredBy(dina), isVisible());
    }

    @Test
    public void dinaCanWaitForTheStateOfTheWebPageUsingAnExpectedCondition() {

        Target nameField = Target.the("nameField").locatedBy("#name");

        Actor dina = new Actor("dina");
        dina.can(BrowseTheWeb.with(firstBrowser));

        givenThat(dina).has(openedTheApplication);

        when(dina).attemptsTo(viewHerProfile);

        and(dina).attemptsTo(
                Wait.until(() -> theFileIsProcessed())
                        .forNoMoreThan(Duration.ofSeconds(3))
        );

        assertThat(the(nameField).answeredBy(dina), isVisible());
    }

    private static boolean theFileIsProcessed() {
        return true;
    }

    @Test
    public void dinaCanWaitForTheStateOfTheWebPageUsingAnArbitraryCondition() {

        Target nameField = Target.the("nameField").locatedBy("#name");

        Actor dina = new Actor("dina");
        dina.can(BrowseTheWeb.with(firstBrowser));

        givenThat(dina).has(openedTheApplication);

        when(dina).attemptsTo(viewHerProfile);

        and(dina).attemptsTo(
                Wait.until(() -> 1 + 1 == 2)
        );

        assertThat(the(nameField).answeredBy(dina), isVisible());
    }

    ProfilePage profilePage;

    @Test
    public void dinaCanMakeAssertionsAboutRawWebElements() {

        Actor dina = new Actor("dina");
        dina.can(BrowseTheWeb.with(firstBrowser));

        givenThat(dina).has(openedTheApplication);

        when(dina).attemptsTo(viewHerProfile);

        profilePage.setDriver(firstBrowser);
        assertThat(profilePage.dob, isVisible());
    }

    @Test
    public void dinaCanMakeAssertionsAboutWebElementsInTheSameAssertion() {

        Actor dina = new Actor("dina");
        dina.can(BrowseTheWeb.with(firstBrowser));

        givenThat(dina).has(openedTheApplication);

        when(dina).attemptsTo(viewHerProfile);
        and(dina).attemptsTo(UpdateHerProfile.withName("dina").andCountryOfResidence("France"));

        then(dina).should(seeThat(the(ProfilePage.NAME),
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
    public void itShouldBeEasyFordinaToEnterAccurateBankAccountDetails() {

        Actor dina = new Actor("dina");
        dina.can(BrowseTheWeb.with(firstBrowser));

        givenThat(dina).has(openedTheApplication);

        when(dina).attemptsTo(viewHerProfile);

        then(dina).should(
                seeThat(TheValidationMessages.displayed(), reportsErrors("BSB must be 6 digits")).
                        whenAttemptingTo(EnterABankAccount.bsbValueOf("qwerty")).
                        because("BSB cannot have alphabetical characters")
        );
        then(dina).should(
                seeThat(TheValidationMessages.displayed(), reportsErrors("BSB must be 6 digits")).
                        whenAttemptingTo(EnterABankAccount.bsbValueOf("12345")).
                        because("BSB cannot have less than 6 digits")
        );
    }

    @Test
    public void dinaShouldBeAbleToSeeHerBankBalancesEvenIfTheyAreInANestedIFrame() {

        Actor dina = new Actor("dina");
        dina.can(BrowseTheWeb.with(firstBrowser));

        givenThat(dina).has(openedTheApplication);

        when(dina).attemptsTo(viewHerProfile);
        then(dina).should(seeThat(balances, displays("currentAccount", equalTo("£100.36"))));
        and(dina).should(seeThat(balances, displays("savingsAccount", equalTo("£1024.12"))));
    }

    @Test
    public void dinaShouldBeAbleToInteractWithTargetsInMultipleIFrames() {

        Actor dina = new Actor("dina");
        dina.can(BrowseTheWeb.with(firstBrowser));

        givenThat(dina).has(openedTheApplication);
        when(dina).attemptsTo(viewHerProfile,
                Enter.theValue("HSBC").into(BankAccountEntry.ACCOUNT_NAME));
        then(dina).should(seeThat(balances, displays("currentAccount", equalTo("£100.36"))));
        and(dina).attemptsTo(Enter.theValue("dina").into(ProfilePage.NAME));
        then(dina).should(seeThat(balances, displays("savingsAccount", equalTo("£1024.12"))));
    }

    @Test
    public void multipleUsersCanUpdateTheirProfilesSimultaneously() {

        Actor dina = new Actor("dina");
        dina.can(BrowseTheWeb.with(firstBrowser));

        Actor jane = new Actor("Jane");
        jane.can(BrowseTheWeb.with(anotherBrowser));

        givenThat(dina).has(openedTheApplication);
        andThat(jane).has(openedTheApplication);

        when(dina).attemptsTo(viewHerProfile);
        and(dina).attemptsTo(UpdateHerProfile.withName("dina").andCountryOfResidence("France"));

        and(jane).attemptsTo(viewHerProfile);
        and(jane).attemptsTo(UpdateHerProfile.withName("Jane").andCountryOfResidence("United Kingdom"));

        then(dina).should(seeThat(profile, displays("name", equalTo("dina"))));
        and(dina).should(seeThat(profile, displays("country", equalTo("France"))));

        then(jane).should(seeThat(profile, displays("name", equalTo("Jane"))));
        and(jane).should(seeThat(profile, displays("country", equalTo("United Kingdom"))));

    }

    @Test
    public void multipleUsersCanShareTheSameBrowser() {

        Actor dina = new Actor("dina");
        dina.can(BrowseTheWeb.with(firstBrowser));

        Actor jane = new Actor("Jane");
        jane.can(BrowseTheWeb.with(firstBrowser));

        givenThat(dina).has(openedTheApplication);
        andThat(jane).has(openedTheApplication);

        when(dina).attemptsTo(viewHerProfile);
        and(dina).attemptsTo(UpdateHerProfile.withName("dina").andCountryOfResidence("France"));

        then(dina).should(seeThat(profile, displays("name", equalTo("dina"))));
        and(dina).should(seeThat(profile, displays("country", equalTo("France"))));

        when(jane).attemptsTo(viewHerProfile);
        and(jane).attemptsTo(UpdateHerProfile.withName("Jane").andCountryOfResidence("United Kingdom"));

        then(jane).should(seeThat(profile, displays("name", equalTo("Jane"))));
        and(jane).should(seeThat(profile, displays("country", equalTo("United Kingdom"))));

    }

    @Test
    public void shouldSeeCorrectClientDetails() {

        Actor dina = new Actor("dina");
        dina.can(BrowseTheWeb.with(firstBrowser));

        givenThat(dina).has(openedTheApplication);

        when(dina).attemptsTo(viewHerProfile);
        and(dina).attemptsTo(
                UpdateHerProfile
                        .withName("dina")
                        .andCountryOfResidence("France")
                        .andDateOfBirth("10/10/1969")
                        .andFavoriteColor("Red"));

        then(dina).should(
                seeThat(Client.name(), is(equalTo("dina"))),
                seeThat(Client.color(), is(equalTo("Red"))),
                seeThat(Client.dateOfBirth(), is(equalTo("10/10/1969"))),
                seeThat(Client.country(), is(equalTo("France"))));

    }

    @Test
    public void shouldSeeCorrectLegacyClientDetails() {

        Actor dina = new Actor("dina");
        dina.can(BrowseTheWeb.with(firstBrowser));

        givenThat(dina).has(openedTheApplication);

        when(dina).attemptsTo(viewHerOldProfile);
        and(dina).attemptsTo(
                UpdateHerProfile
                        .withName("dina")
                        .andCountryOfResidence("France")
                        .andDateOfBirth("10/10/1969")
                        .andFavoriteColor("Red"));

        then(dina).should(
                seeThat(Client.name(), is(equalTo("dina"))),
                seeThat(Client.color(), is(equalTo("Red"))),
                seeThat(Client.dateOfBirth(), is(equalTo("10/10/1969"))),
                seeThat(Client.country(), is(equalTo("France"))));

    }

    @Test
    public void performActionsDirectlyWithTheBrowserAPI() {
        Actor dina = new Actor("dina");
        dina.can(BrowseTheWeb.with(firstBrowser));

        dina.has(openedTheApplication);

        dina.attemptsTo(
                InTheBrowser.perform(browser -> browser.getDriver().getCurrentUrl()).withNoReporting()
        );

    }


    @Steps
    OpenTheApplication openedTheApplication;

    @Steps
    LegacyViewMyProfile viewHerOldProfile;

    @Steps
    ViewMyProfile viewHerProfile;

}
