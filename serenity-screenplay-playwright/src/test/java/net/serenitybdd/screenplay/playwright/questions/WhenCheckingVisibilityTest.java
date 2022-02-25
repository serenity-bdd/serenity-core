package net.serenitybdd.screenplay.playwright.questions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.assertions.Ensure;
import net.serenitybdd.screenplay.playwright.interactions.Click;
import net.serenitybdd.screenplay.playwright.interactions.Open;
import net.serenitybdd.screenplay.playwright.interactions.WaitFor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SerenityRunner.class)
public class WhenCheckingVisibilityTest {

    Actor fred;

    @Before
    public void prepareActor() {
        fred = Actor.named("Fred").whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());
    }

    @Test
    public void weCanCheckForHiddenAndVisibleElements() {
        fred.attemptsTo(
            Open.url("http://the-internet.herokuapp.com/add_remove_elements/"),
            Ensure.that("text='Delete'").withTimeout(0.0).isHidden(),
            Click.on("text='Add Element'"),
            Ensure.that("text='Delete'").isVisible()
        );
    }

    @Test
    public void weCanCheckForDisabledElements() {
        fred.attemptsTo(
            Open.url("http://the-internet.herokuapp.com/dynamic_controls"),
            Ensure.that("#input-example input").isDisabled(),
            Click.on("text='Enable'"),
            WaitFor.selector("text='It's enabled!'"),
            Ensure.that("#input-example input").isEnabled()
        );
    }

}
