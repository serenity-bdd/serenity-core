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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.serenitybdd.screenplay.GivenWhenThen.*;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

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
    @CsvSource({
            // initialTodos, itemsToComplete, filtersToApply, expectedDisplayedItems, expectedSelectedFilter
            "Walk the dog;Put out the garbage, Walk the dog, Completed, Walk the dog,                      Completed",
            "Walk the dog;Put out the garbage, Walk the dog, Active,    Put out the garbage,               Active",
            "Walk the dog;Put out the garbage, Walk the dog, All,       Walk the dog;Put out the garbage,  All"
    })
    public void should_be_able_to_filter_todos(
            String initialTodos,
            String itemsToComplete,
            String filterToApply,
            String expectedDisplayedItems,
            String expectedSelectedFilter
    ) {
        // Parse the CSV string parameters into lists
        TodoStatusFilter expectedFilter = TodoStatusFilter.valueOf(expectedSelectedFilter);

        // Given
        givenThat(james).wasAbleTo(Start.withATodoListContaining(asList(initialTodos)));

        // When
        james.attemptsTo(
                CompleteItems.called(asList(itemsToComplete)),
                FilterItems.toShow(TodoStatusFilter.valueOf(filterToApply))
        );

        // Then
        then(james).should(
                seeThat(TheItems.displayed(), hasItems(expectedDisplayedItems)),
                seeThat(CurrentFilter.selected(), is(expectedFilter))
        );
    }

    private List<String> asList(String str) {
        if (str == null || str.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(str.split(";"));
    }

}
