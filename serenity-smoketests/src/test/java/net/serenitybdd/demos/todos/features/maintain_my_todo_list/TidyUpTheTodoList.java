package net.serenitybdd.demos.todos.features.maintain_my_todo_list;

import net.serenitybdd.demos.todos.tasks.AddItems;
import net.serenitybdd.demos.todos.tasks.ClearCompletedItems;
import net.serenitybdd.demos.todos.tasks.CompleteItem;
import net.serenitybdd.demos.todos.tasks.DisplayedItems;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.Steps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static net.serenitybdd.screenplay.GivenWhenThen.*;
import static org.hamcrest.Matchers.contains;

/**
 * This example illustrates using the Journey pattern with JUnit.
 */
@RunWith(SerenityRunner.class)
public class TidyUpTheTodoList {

    @Managed
    WebDriver hisBrowser;

    @Steps AddItems addedSomeItems;
    @Steps DisplayedItems theDisplayedItems;
    @Steps ClearCompletedItems clearTheCompletedItems;

    Actor joe = Actor.named("Joe");

    @Before
    public void joe_can_use_a_browser() {
        joe.can(BrowseTheWeb.with(hisBrowser));
    }

    @Test
    public void should_be_able_to_remove_completed_items_from_the_todo_list() {

        givenThat(joe).has(addedSomeItems.called("Buy the milk", "Walk the dog"));

        when(joe).attemptsTo(
                CompleteItem.called("Buy the milk"),
                clearTheCompletedItems
        );

        then(joe).should(seeThat(theDisplayedItems, contains("Walk the dog")));

    }
}
