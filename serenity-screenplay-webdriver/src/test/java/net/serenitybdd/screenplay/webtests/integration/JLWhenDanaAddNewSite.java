package net.serenitybdd.screenplay.webtests.integration;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.abilities.BrowsingTheWeb;
import net.serenitybdd.screenplay.webtests.actions.NavigateToPage;
import net.serenitybdd.screenplay.webtests.model.Category;
import net.serenitybdd.screenplay.webtests.model.SubCategory;
import net.serenitybdd.screenplay.webtests.pages.JLHomePage;

import net.serenitybdd.screenplay.webtests.pages.JLSitesAddSitePage;
import net.serenitybdd.screenplay.webtests.tasks.JLAddNewSite;
import net.serenitybdd.screenplay.webtests.tasks.JLLoginWebApp;
import net.serenitybdd.screenplay.webtests.tasks.JLOpenWebApp;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.Steps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import static net.serenitybdd.screenplay.GivenWhenThen.*;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isCurrentlyVisible;
import static net.serenitybdd.screenplay.questions.WebElementQuestion.the;

@RunWith(SerenityRunner.class)
public class JLWhenDanaAddNewSite {
    @Managed(driver = "chrome", options = "--whitelisted-ips")
    WebDriver browser;

    Actor dana = new Actor("Dana");

    @Before
    public void prepareBrowser() {
        dana.can(BrowseTheWeb.with(browser));
        givenThat(dana).wasAbleTo(jlOpenWebApp);
        givenThat(dana).wasAbleTo(jlLoginJLWebApp);
    }

    @Test
    public void addNewSite(){
        //BrowsingTheWeb.as(dana).onPage(JLHomePage.class).navigateToPage("AddSite");
       /* Wait<WebDriver> wait = new FluentWait<WebDriver>(browser)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofSeconds(3))
                .ignoring(NoSuchElementException.class);*/
      //  System.out.print(Category.site + " " + SubCategory.ADDSITE);
        //givenThat(dana).wasAbleTo(NavigateToPage.withMenu(Category.Sites).selectItem(SubCategory.ADDSITE));
        //BrowsingTheWeb.as(dana).onPage(JLHomePage.class).navigateToPage("AddSite");
      givenThat(dana).wasAbleTo(NavigateToPage.withMenu(Category.Sites).selectItem(SubCategory.ADDSITE));
      /*  dana.should(
                seeThat(the(JLSitesAddSitePage.DDCUSTOMER),isCurrentlyVisible()),
                seeThat(the(JLSitesAddSitePage.TXTSITE),isCurrentlyVisible())
        );*/
        //dana.wasAbleTo(jlAddNewSite);
       /* when(dana).attemptsTo(JLAddNewSite
                .withCustomer("Candy Crush")
                .withSite("TESTSITE091420"));*/

    }

    @Steps
    JLOpenWebApp jlOpenWebApp;

    @Steps
    JLLoginWebApp jlLoginJLWebApp;
}
