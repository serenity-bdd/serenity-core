package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.Locator;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.assertions.Ensure;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

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
            Ensure.that("text='Delete'").isVisible(),
            Click.on("text='Delete'").withOptions(new Locator.ClickOptions().setClickCount(2)),
            Ensure.that("text='Delete'").isHidden()
        );
    }

    @Test
    public void clickingOnButtonsWithOptions() {
        fred.attemptsTo(
            Open.url("http://the-internet.herokuapp.com/add_remove_elements/"),
            Ensure.that("text='Delete'").isHidden(),
            // Double-click to add two elements, then verify at least one Delete button is visible
            Click.on("text='Add Element'").withOptions(new Locator.ClickOptions().setClickCount(2)),
            Ensure.that("text='Delete' >> nth=0").isVisible()
        );
    }

    @Test
    public void clickingOnCheckboxes() {
        fred.attemptsTo(
            Open.url("http://the-internet.herokuapp.com/checkboxes"),
            // Use first checkbox specifically
            Ensure.that("#checkboxes input >> nth=0").isNotChecked(),
            Click.on("#checkboxes input >> nth=0"),
            Ensure.that("#checkboxes input >> nth=0").isChecked()
        );
    }

}
