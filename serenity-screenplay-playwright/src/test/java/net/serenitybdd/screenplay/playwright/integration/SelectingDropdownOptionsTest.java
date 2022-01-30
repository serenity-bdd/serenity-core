package net.serenitybdd.screenplay.playwright.integration;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.assertions.Ensure;
import net.serenitybdd.screenplay.playwright.interactions.Open;
import net.serenitybdd.screenplay.playwright.interactions.SelectFromOptions;
import net.serenitybdd.screenplay.playwright.questions.SelectOptions;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;

@RunWith(SerenityRunner.class)
public class SelectingDropdownOptionsTest {

    Actor daffy;

    @Before
    public void prepareActor() {
        daffy = Actor.named("Daffy").whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());
    }

    @Test
    public void checkingOptionsOfDropdown() {
        daffy.attemptsTo(
            Open.url("http://the-internet.herokuapp.com/dropdown")
        );
        daffy.should(seeThat(
            SelectOptions.of("#dropdown"),
            Matchers.containsInAnyOrder("Please select an option", "Option 1", "Option 2")
        ));
    }

    @Test
    public void selectingOptionsFromDropdown() {
        String dropdown = "#dropdown";
        daffy.attemptsTo(
            Open.url("http://the-internet.herokuapp.com/dropdown"),
            SelectFromOptions.byValue("1").from(dropdown),
            Ensure.that(dropdown).currentValue("1"),
            SelectFromOptions.byVisibleText("Option 2").from(dropdown),
            Ensure.that(dropdown).currentValue("2"),
            SelectFromOptions.byIndex(1).from(dropdown),
            Ensure.that(dropdown).currentValue("1")
        );
    }
}
