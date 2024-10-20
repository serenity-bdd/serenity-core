package net.serenitybdd.demos.todos.screenplay.features.maintain_my_todo_list;

import net.serenitybdd.annotations.Managed;
import net.serenitybdd.demos.todos.screenplay.model.TodoStatusFilter;
import net.serenitybdd.demos.todos.screenplay.questions.CurrentFilter;
import net.serenitybdd.demos.todos.screenplay.questions.TheItems;
import net.serenitybdd.demos.todos.screenplay.tasks.CompleteItem;
import net.serenitybdd.demos.todos.screenplay.tasks.CompleteItems;
import net.serenitybdd.demos.todos.screenplay.tasks.FilterItems;
import net.serenitybdd.demos.todos.screenplay.tasks.Start;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.serenitybdd.demos.todos.screenplay.model.TodoStatusFilter.*;
import static net.serenitybdd.screenplay.GivenWhenThen.*;
import static org.hamcrest.Matchers.*;

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
            // initialTodos, itemsToComplete, filtersToApply, expectedDisplayedItems, expectedNotDisplayedItems, expectedSelectedFilter
            "'Walk the dog;Put out the garbage', 'Walk the dog', 'Completed', 'Walk the dog', 'Put out the garbage', 'Completed'",
            "'Walk the dog;Put out the garbage', 'Walk the dog', 'Active', 'Put out the garbage', 'Walk the dog', 'Active'",
            "'Walk the dog;Put out the garbage', 'Walk the dog', 'All', 'Walk the dog;Put out the garbage', '', 'All'"
    })
    public void should_be_able_to_filter_todos(
            String initialTodosStr,
            String itemsToCompleteStr,
            String filterToApply,
            String expectedDisplayedItemsStr,
            String expectedNotDisplayedItemsStr,
            String expectedSelectedFilterStr
    ) {
        // Parse the CSV string parameters into lists
        List<String> initialTodos = parseStringToList(initialTodosStr);
        List<String> itemsToComplete = parseStringToList(itemsToCompleteStr);
        List<String> expectedDisplayedItems = parseStringToList(expectedDisplayedItemsStr);
        List<String> expectedNotDisplayedItems = parseStringToList(expectedNotDisplayedItemsStr);
        TodoStatusFilter expectedSelectedFilter = TodoStatusFilter.valueOf(expectedSelectedFilterStr);

        // Given
        givenThat(james).wasAbleTo(Start.withATodoListContaining(initialTodos));

        // When
        james.attemptsTo(
                CompleteItems.called(itemsToComplete),
                FilterItems.toShow(TodoStatusFilter.valueOf(filterToApply))
        );

        // Then
        then(james).should(
                seeThat(TheItems.displayed(), hasItems(expectedDisplayedItems.toArray(new String[0]))),
                seeThat(CurrentFilter.selected(), is(expectedSelectedFilter))
        );
        if (!expectedNotDisplayedItems.isEmpty()) {
            and(james).should(seeThat(TheItems.displayed(), not(hasItems(expectedNotDisplayedItems.toArray(new String[0])))));
        }
    }

    // Helper method to parse semicolon-separated strings into lists
    private List<String> parseStringToList(String str) {
        if (str == null || str.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(str.split(";"));
    }

    // Helper method to parse filter names into TodoStatusFilter enum instances
    private List<TodoStatusFilter> parseFilters(List<String> filterNames) {
        List<TodoStatusFilter> filters = new ArrayList<>();
        for (String name : filterNames) {
            filters.add(TodoStatusFilter.valueOf(name));
        }
        return filters;
    }
}
