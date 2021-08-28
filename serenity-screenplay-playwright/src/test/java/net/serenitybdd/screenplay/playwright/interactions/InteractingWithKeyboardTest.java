package net.serenitybdd.screenplay.playwright.interactions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.assertions.Ensure;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SerenityRunner.class)
public class InteractingWithKeyboardTest {
    Actor tom = Actor.named("Tom").whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());

    @Test
    public void pressKeyboardButton() {
        String searchText = "serenity bdd";
        String searchResultSelector = String.format(".result__a:has-text(\"%s\")", searchText);
        tom.attemptsTo(
                Open.url("https://duckduckgo.com/"),
                Enter.theValue(searchText).into("#search_form_input_homepage"),
                Press.keys("Enter"),
                WaitFor.selector(searchResultSelector),
                Ensure.that(searchResultSelector).isVisible()
        );
    }
}
