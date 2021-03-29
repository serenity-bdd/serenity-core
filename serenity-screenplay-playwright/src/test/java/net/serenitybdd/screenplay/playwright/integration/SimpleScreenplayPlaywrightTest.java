package net.serenitybdd.screenplay.playwright.integration;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.interactions.Click;
import net.serenitybdd.screenplay.playwright.interactions.Enter;
import net.serenitybdd.screenplay.playwright.interactions.Open;
import net.serenitybdd.screenplay.playwright.questions.PlaywrightQuestions;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;

@RunWith(SerenityRunner.class)
public class SimpleScreenplayPlaywrightTest {

    Actor daffy;

    @Before
    public void prepareActor() {
        daffy = Actor.named("Daffy").whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration().withHeadlessMode(false).withBrowserType("firefox"));
    }

    private static final Target SEARCH_FIELD = Target.the("Search field").locatedBy("#search_form_input_homepage");
    private static final Target SEARCH_BUTTON = Target.the("Search button").locatedBy("#search_button_homepage");
    private static final Target MODULE_TITLE = Target.the("Module title").locatedBy(".module__title__link");

    @Test
    public void simpleSearch() {
        daffy.attemptsTo(
                Open.url("https://www.duckduckgo.com"),
                Enter.theValue("Penguins").into(SEARCH_FIELD),
                Click.on(SEARCH_BUTTON)
        );
        daffy.should(
                seeThat(PlaywrightQuestions.pageTitle(), Matchers.containsString("Penguins at DuckDuckGo")),
                seeThat(PlaywrightQuestions.textOf(MODULE_TITLE), Matchers.containsString("Penguin"))
        );
    }
}
