package net.serenitybdd.demos.todos.screenplay.features.maintain_my_todo_list;

import net.serenitybdd.annotations.Managed;
import net.serenitybdd.demos.todos.screenplay.model.TodoStatusFilter;
import net.serenitybdd.demos.todos.screenplay.questions.CurrentFilter;
import net.serenitybdd.demos.todos.screenplay.questions.TheItems;
import net.serenitybdd.demos.todos.screenplay.tasks.CompleteItems;
import net.serenitybdd.demos.todos.screenplay.tasks.FilterItems;
import net.serenitybdd.demos.todos.screenplay.tasks.Start;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.ensure.Ensure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.WebDriver;

import static net.serenitybdd.screenplay.GivenWhenThen.*;

@ExtendWith(SerenityJUnit5Extension.class)
public class FilteringTodos {

    private Actor james = Actor.named("James");

    @Managed
    private WebDriver hisBrowser;

    @BeforeEach
    public void jamesCanBrowseTheWeb() {
        james.can(BrowseTheWeb.with(hisBrowser));
    }

    @ParameterizedTest
    @CsvSource(delimiterString = "|", value = {
            // initialTodos                   | itemsToComplete | filters   | expectedDisplayedItems           | selectedFilter
            "Walk the dog;Put out the garbage | Walk the dog    | Completed | Walk the dog                     | Completed",
            "Walk the dog;Put out the garbage | Walk the dog    | Active    | Put out the garbage              | Active",
            "Walk the dog;Put out the garbage | Walk the dog    | All       | Walk the dog;Put out the garbage | All",
            "Walk the dog                     | Walk the dog    | Completed |                                  | Completed"})
    public void should_be_able_to_filter_todos(String initialTodos, String itemsToComplete, String filter,
                                               String expectedDisplayedItems, String selectedFilter
    ) {
        TodoStatusFilter filterToApply = TodoStatusFilter.valueOf(filter);
        TodoStatusFilter expectedFilter = TodoStatusFilter.valueOf(selectedFilter);

        givenThat(james).wasAbleTo(
                Start.withATodoListContaining(itemsIn(initialTodos))
        );

        when(james).attemptsTo(
                CompleteItems.called(itemsIn(itemsToComplete)),
                FilterItems.toShow(filterToApply)
        );

        then(james).attemptsTo(
                Ensure.that(TheItems.displayed()).contains(itemsIn(expectedDisplayedItems)),
                Ensure.that(CurrentFilter.selected()).isEqualTo(expectedFilter)
        );
    }

    private String[] itemsIn(String listOfItems) {
        if (listOfItems == null || listOfItems.trim().isEmpty()) {
            return new String[]{};
        }
        return listOfItems.split(";");
    }

}
