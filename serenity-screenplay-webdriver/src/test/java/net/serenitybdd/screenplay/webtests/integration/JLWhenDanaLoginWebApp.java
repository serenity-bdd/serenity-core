package net.serenitybdd.screenplay.webtests.integration;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.webtests.pages.JLLoginPage;
import net.serenitybdd.screenplay.webtests.questions.BankBalanceQuestion;
import net.serenitybdd.screenplay.webtests.questions.ProfileQuestion;
import net.serenitybdd.screenplay.webtests.tasks.*;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.Steps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static net.serenitybdd.screenplay.GivenWhenThen.*;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isCurrentlyVisible;
import static net.serenitybdd.screenplay.questions.WebElementQuestion.the;

@RunWith(SerenityRunner.class)
public class JLWhenDanaLoginWebApp {
    @Managed(driver = "chrome", options = "--whitelisted-ips") //--headless
    WebDriver browser;

    ProfileQuestion profile = new ProfileQuestion();
    BankBalanceQuestion balances = new BankBalanceQuestion();

    Actor dana = new Actor("Dana");

    @Before
    public void prepareBrowser() {
        dana.can(BrowseTheWeb.with(browser));
        givenThat(dana).has(jlOpenWebApp);
    }

    @Test
    public void canLoginJLWebApp() {
        dana.should(
                seeThat(the(JLLoginPage.EMAIL),isCurrentlyVisible()),
                seeThat(the(JLLoginPage.PASSWORD),isCurrentlyVisible()),
                seeThat(the(JLLoginPage.LOGIN),isCurrentlyVisible())
        );
        //BrowsingTheWeb.as(dana).onPage(JLLoginPage.class).loginJLPage("lienn-devgo@joblogic.com","1");
        when(dana).attemptsTo(jlLoginWebApp);
        //then(dana).should(seeThat(the()));
    }

    @Steps
    JLOpenWebApp jlOpenWebApp;

    @Steps
    JLLoginWebApp jlLoginWebApp;
}
