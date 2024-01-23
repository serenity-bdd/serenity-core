package net.serenitybdd.screenplay.playwright.interactions;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SerenityRunner.class)
public class InteractingWithKeyboardTest {
    Actor will = Actor.named("Will").whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());

    @Test
    public void pressKeyboardButton() {
        will.attemptsTo(
                Open.url("https://todomvc.com/examples/angular/dist/browser/#/all"),
                Enter.theValue("Feed the cat").into(".new-todo"),
                Press.keys("Enter")
        );
    }

    Target TODO_FIELD = Target.the("Todo field").locatedBy(".new-todo");

    @Test
    public void pressKeyboardButtonUsingATarget() {
        will.attemptsTo(
                Open.url("https://todomvc.com/examples/angular/dist/browser/#/all"),
                Enter.theValue("Feed the cat").into(TODO_FIELD),
                Press.keys("Enter")
        );
    }

}
