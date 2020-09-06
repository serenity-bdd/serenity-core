package net.serenitybdd.screenplay.webtests.integration;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.abilities.BrowsingTheWeb;
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

import static net.serenitybdd.screenplay.GivenWhenThen.givenThat;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
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
        BrowsingTheWeb.as(dana).onPage(JLHomePage.class).navigateToPage("AddSite");
        /*dana.should(
                seeThat(the(JLSitesAddSitePage.DDCUSTOMER),isCurrentlyVisible()),
                seeThat(the(JLSitesAddSitePage.TXTSITE),isCurrentlyVisible()),
                seeThat(the(JLSitesAddSitePage.DDTAGS),isCurrentlyVisible()),
                seeThat(the(JLSitesAddSitePage.TXTADDSTREET),isCurrentlyVisible()),
                seeThat(the(JLSitesAddSitePage.TXTADDAREA),isCurrentlyVisible()),
                seeThat(the(JLSitesAddSitePage.TXTADDCITY),isCurrentlyVisible()),
                seeThat(the(JLSitesAddSitePage.TXTADDCOUNTY),isCurrentlyVisible()),
                seeThat(the(JLSitesAddSitePage.TXTPOSTCODE),isCurrentlyVisible()),
                seeThat(the(JLSitesAddSitePage.TXTTEL),isCurrentlyVisible()),
                seeThat(the(JLSitesAddSitePage.DDAREA),isCurrentlyVisible()),
                seeThat(the(JLSitesAddSitePage.TXTREFNO),isCurrentlyVisible()),
                seeThat(the(JLSitesAddSitePage.TXTCNTFIRSTNAME),isCurrentlyVisible()),
                seeThat(the(JLSitesAddSitePage.TXTCNTLASTNAME),isCurrentlyVisible()),
                seeThat(the(JLSitesAddSitePage.TXTCNTTEL),isCurrentlyVisible()),
                seeThat(the(JLSitesAddSitePage.TXTCNTEMAIL),isCurrentlyVisible()),
                seeThat(the(JLSitesAddSitePage.TXTCNTJOBPOS),isCurrentlyVisible()),
                seeThat(the(JLSitesAddSitePage.BTNCANCEL),isCurrentlyVisible()),
                seeThat(the(JLSitesAddSitePage.BTNSAVE),isCurrentlyVisible())
        );*/
        dana.wasAbleTo(jlAddNewSite);
        //when(dana).attemptsTo(jlSitesAddSitePage.enterSiteInfo("Cust083001", "Site083001"));
    }

    @Steps
    JLOpenWebApp jlOpenWebApp;

    @Steps
    JLLoginWebApp jlLoginJLWebApp;

    @Steps
    JLAddNewSite jlAddNewSite;
}
