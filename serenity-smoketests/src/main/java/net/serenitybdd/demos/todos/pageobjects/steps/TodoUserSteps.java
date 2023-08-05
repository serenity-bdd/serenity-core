package net.serenitybdd.demos.todos.pageobjects.steps;

import net.serenitybdd.demos.todos.pageobjects.model.TodoStatusFilter;
import net.serenitybdd.demos.todos.pageobjects.pages.TodoListPage;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.annotations.Step;
import net.thucydides.model.util.EnvironmentVariables;

import static java.util.Arrays.asList;
import static net.serenitybdd.demos.todos.pageobjects.model.TodoStatus.Active;
import static net.serenitybdd.demos.todos.pageobjects.model.TodoStatus.Completed;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;


public class TodoUserSteps {

    TodoListPage todoListPage;

    // -----------------------------------------------------------------------------------------------------------------
    // STEPS (9 in total)

    @Step
    public void starts_with_an_empty_todo_list() {
        todoListPage.openAt("http://www.google.com");
        starts_with_a_todo_list_containing();
    }

    EnvironmentVariables environmentVariables;
    @Step
    public void starts_with_a_todo_list_containing(String... items) {
        todoListPage.openApplication();

        String baseUrl = ThucydidesSystemProperty.WEBDRIVER_BASE_URL.from(environmentVariables);


        adds_todo_items_called(items);
    }

    @Step
    public void adds_todo_items_called(String... items) {
        asList(items).forEach(this::adds_a_todo_item_called);
    }

    @Step
    public void adds_a_todo_item_called(String item) {
        todoListPage.addATodoItemCalled(item);
    }

    @Step
    public void completes(String item) {
        todoListPage.markAsComplete(item);
    }

    @Step
    public void filters_items_to_show(TodoStatusFilter filter) {
        todoListPage.filterByStatus(filter);
    }

    @Step
    public void deletes(String item) {
        todoListPage.delete(item);
    }

    @Step
    public void toggles_all_items() {
        todoListPage.toggleAll();
    }

    @Step
    public void clears_completed_items() {
        todoListPage.clearCompletedItems();
    }

    // -----------------------------------------------------------------------------------------------------------------
    // ASSERTIONS (13 in total)

    @Step
    public void should_see_that_the_placeholder_text_says(String expectedPlaceholderText) {
        assertThat(todoListPage.placeholderText(), is(expectedPlaceholderText));
    }

    @Step
    public void should_see_that_that_following_items_are_marked_as_complete(String... items) {
        asList(items).forEach(this::should_see_that_that_following_item_is_marked_as_complete);
    }

    @Step
    public void should_see_that_that_following_item_is_marked_as_complete(String item) {
        assertThat(todoListPage.statusOf(item), is(Completed));
    }

    @Step
    public void should_see_that_that_following_items_are_marked_as_active(String... items) {
        asList(items).forEach(this::should_see_that_that_following_item_is_marked_as_active);
    }

    @Step
    public void should_see_that_that_following_item_is_marked_as_active(String item) {
        assertThat(todoListPage.statusOf(item), is(Active));
    }

    @Step
    public void should_see_that_the_number_of_items_left_is(int expected) {

        assertThat(todoListPage.numberOfItemsLeft(), is(expected));
    }

    @Step
    public void should_see_that_the_clear_completed_items_option_is_not_visible() {
        assertThat(todoListPage.canClearCompletedItems(), is(false));
    }

    @Step
    public void should_see_that_the_currently_selected_filter_is(TodoStatusFilter filter) {
        assertThat(todoListPage.currentlySelectedFilter().equals(filter), is(true));
    }

    @Step
    public void should_see_that_displayed_items_contain(String... items) {
        assertThat(todoListPage.displayedItems(), hasItems(items));
    }

    @Step
    public void should_see_that_displayed_items_do_not_contain(String... items) {
        assertThat(todoListPage.displayedItems(), not(contains(items)));
    }

    @Step
    public void should_see_the_correct_website_title() {
        assertThat(todoListPage.getTitle(), is("AngularJS • TodoMVC"));
    }

    @Step
    public void should_see_the_correct_application_heading() {
        assertThat(todoListPage.heading(), is("todos"));
    }

    @Step
    public void should_see_the_about_section() {
        assertThat(todoListPage.footer(), containsString("Credits"));
    }

}
