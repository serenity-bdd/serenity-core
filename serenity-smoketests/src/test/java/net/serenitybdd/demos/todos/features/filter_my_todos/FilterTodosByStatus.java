package net.serenitybdd.demos.todos.features.filter_my_todos;

import net.serenitybdd.demos.todos.tasks.*;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.annotations.WithTag;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static net.serenitybdd.demos.todos.model.TodoStatusFilter.Active;
import static net.serenitybdd.demos.todos.model.TodoStatusFilter.Completed;
import static net.serenitybdd.screenplay.GivenWhenThen.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;

@RunWith(SerenityRunner.class)
@WithTag("smoketest")
public class FilterTodosByStatus {

    @Managed
    WebDriver hisBrowser;

    @Steps AddItems addedSomeItems;
    @Steps DisplayedItems theDisplayedItems;
    @Steps OpenTheApplication openedTheTodoApplication;

    Actor joe = Actor.named("Joe");

    @Before
    public void joe_can_use_a_browser() {
        joe.can(BrowseTheWeb.with(hisBrowser));
        joe.has(openedTheTodoApplication);
    }

    static int tries = 1;
    @Test
    public void filter_by_active_tasks() {
        givenThat(joe).has(addedSomeItems.called("Buy the milk", "Buy Petrol"));
        andThat(joe).attemptsTo(CompleteItem.called("Buy the milk"));

        when(joe).attemptsTo(FilterItems.byStatus(Active));

        then(joe).should(seeThat(theDisplayedItems, contains("Buy Petrol")));
        assertThat(tries++).isGreaterThanOrEqualTo(3);
    }

    @Test
    public void filter_by_completed_tasks() {
        givenThat(joe).has(addedSomeItems.called("Buy the milk", "Buy Petrol"));
        andThat(joe).attemptsTo(CompleteItem.called("Buy the milk"));

        when(joe).attemptsTo(FilterItems.byStatus(Completed));

        then(joe).should(seeThat(theDisplayedItems, contains("Buy the milk")));
    }

}
