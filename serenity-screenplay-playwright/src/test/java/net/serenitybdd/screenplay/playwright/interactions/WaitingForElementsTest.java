package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.ElementHandle;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.assertions.Ensure;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SerenityRunner.class)
public class WaitingForElementsTest {

    Actor fred;

    @Before
    public void prepareActor() {
        fred = Actor.named("Fred").whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());
    }

    @Test
    public void waitingForAnElementToAppear() {
        fred.attemptsTo(
                Open.url("http://the-internet.herokuapp.com/add_remove_elements/"),
                Click.on("text='Add Element'"),
                WaitFor.selector("text='Delete'"),
                Ensure.that("text='Delete'").isVisible()
        );
    }

    @Test
    public void waitingForAnElementToAppearAndThenPerformingSomeAction() {
        fred.attemptsTo(
                Open.url("http://the-internet.herokuapp.com/add_remove_elements/"),
                Click.on("text='Add Element'"),
                WaitFor.selector("text='Delete'").andThen(ElementHandle::click),
                Ensure.that("text='Delete'").isHidden()
        );
    }

}
