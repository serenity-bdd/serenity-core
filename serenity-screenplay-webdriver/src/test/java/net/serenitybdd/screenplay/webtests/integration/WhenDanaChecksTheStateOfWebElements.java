package net.serenitybdd.screenplay.webtests.integration;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.webtests.pages.HomePage;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.Screenshots;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.*;
import static net.serenitybdd.screenplay.questions.WebElementQuestion.the;

@RunWith(SerenityRunner.class)
public class WhenDanaChecksTheStateOfWebElements {

    @Managed(driver = "chrome", options = "--headless")
    WebDriver browser;

    Actor dana = new Actor("Dana");

    @Before
    public void prepareBrowser() {
        dana.can(BrowseTheWeb.with(browser));

        dana.attemptsTo(Open.browserOn().the(HomePage.class));
    }

    @Test(timeout = 15000)
    @Screenshots(onlyOnFailures=true)
    public void shouldSeeVisibilityOfElements() {
        dana.should(
                seeThat(the(fieldLocatedBy("#visibleTextField")), isCurrentlyVisible()),
                seeThat(the(fieldLocatedBy("#visibleTextField")), isVisible()),
                seeThat(the(fieldLocatedBy("#visibleTextField")), isPresent()),
                seeThat(the(fieldLocatedBy("#visibleTextField")), isClickable()),

                seeThat(the(fieldLocatedBy("#invisibleTextField")), isNotCurrentlyVisible()),
                seeThat(the(fieldLocatedBy("#invisibleTextField")), isNotVisible()),
                seeThat(the(fieldLocatedBy("#invisibleTextField")), isPresent())
        );
    }

    private Target fieldLocatedBy(String locator) {
        return Target.the(locator).locatedBy(locator);
    }


}
