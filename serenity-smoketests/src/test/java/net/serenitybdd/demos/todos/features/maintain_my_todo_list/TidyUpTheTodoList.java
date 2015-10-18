package net.serenitybdd.demos.todos.features.maintain_my_todo_list;

import net.serenitybdd.demos.todos.tasks.*;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.annotations.WithDriver;
import net.thucydides.core.annotations.WithTag;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static net.serenitybdd.screenplay.GivenWhenThen.*;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;

/**
 * This example illustrates using the Journey pattern with JUnit.
 * Also illustrates using different browsers for different tests.
 */
@RunWith(SerenityRunner.class)
@WithTag("smoketest")

public class TidyUpTheTodoList {

    @Managed
    WebDriver hisBrowser;

    @Steps AddItems addedSomeItems;
    @Steps DisplayedItems theDisplayedItems;
    @Steps ClearCompletedItems clearTheCompletedItems;
    @Steps OpenTheApplication openedTheTodoApplication;

    Actor joe = Actor.named("Joe");

    @Before
    public void joe_can_use_a_browser() {
        joe.can(BrowseTheWeb.with(hisBrowser));
        joe.has(openedTheTodoApplication);
    }

    @Test
    @WithDriver("firefox")
    public void should_be_able_to_remove_completed_items_from_the_todo_list() {

        givenThat(joe).has(addedSomeItems.called("Buy the milk", "Walk the dog"));

        when(joe).attemptsTo(
                CompleteItem.called("Buy the milk"),
                clearTheCompletedItems
        );

        then(joe).should(seeThat(theDisplayedItems, contains("Walk the dog")));

    }

    @Test
    @WithDriver("chrome")
    public void should_be_able_to_remove_completed_items_from_an_almost_empty_list() {

        givenThat(joe).has(addedSomeItems.called("Buy the milk"));

        when(joe).attemptsTo(
                CompleteItem.called("Buy the milk"),
                clearTheCompletedItems
        );

        then(joe).should(seeThat(theDisplayedItems, hasSize(0)));
    }

}
