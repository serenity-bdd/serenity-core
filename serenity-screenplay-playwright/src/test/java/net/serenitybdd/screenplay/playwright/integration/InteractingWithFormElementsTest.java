package net.serenitybdd.screenplay.playwright.integration;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.assertions.Ensure;
import net.serenitybdd.screenplay.playwright.interactions.Click;
import net.serenitybdd.screenplay.playwright.interactions.Open;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;

@RunWith(SerenityRunner.class)
public class InteractingWithFormElementsTest {

    Actor fred;

    @Before
    public void prepareActor() {
        fred = Actor.named("Fred").whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());
    }

    @Test
    public void clickingOnButtons() {
        fred.attemptsTo(
                Open.url("http://the-internet.herokuapp.com/add_remove_elements/"),
                Ensure.that("text='Delete'").isHidden(),
                Click.on("text='Add Element'"),
                Ensure.that("text='Delete'").isVisible()
        );
    }

    @Test
    public void clickingOnCheckboxes() {
        fred.attemptsTo(
                Open.url("http://the-internet.herokuapp.com/checkboxes"),
                Ensure.that("#checkboxes input:nth-child(1)").isNotChecked(),
                Click.on("#checkboxes input:nth-child(1)"),
                Ensure.that("#checkboxes input:nth-child(1)").isChecked()
        );
    }

}
