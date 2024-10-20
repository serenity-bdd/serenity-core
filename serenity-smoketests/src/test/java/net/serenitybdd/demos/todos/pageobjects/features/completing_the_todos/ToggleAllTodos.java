package net.serenitybdd.demos.todos.pageobjects.features.completing_the_todos;

import net.serenitybdd.demos.todos.pageobjects.steps.TodoUserSteps;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.annotations.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(SerenityRunner.class)
@WithTags({
        @WithTag("PageObjects pattern"),
        @WithTag("version:RELEASE-1"),
        @WithTag("smoke"),
})
public class ToggleAllTodos {

    @Managed//(driver = "chrome", options = "--headless")
    WebDriver driver;

    @Steps
    TodoUserSteps james;

    @Test
    @Issues({"PROJ-12", "PROJ-43"})
    public void should_be_able_to_quickly_complete_all_todos_with_page_objects() {

        james.starts_with_a_todo_list_containing("Walk the dog", "Put out the garbage");

        james.toggles_all_items();

        james.should_see_that_that_following_items_are_marked_as_complete("Walk the dog", "Put out the garbage");
    }

    @Test
    public void should_be_able_to_toggle_status_of_all_todos_with_page_objects() {

        james.starts_with_a_todo_list_containing("Walk the dog", "Put out the garbage");

        james.toggles_all_items();

        james.toggles_all_items();

        james.should_see_that_that_following_items_are_marked_as_active("Walk the dog", "Put out the garbage");
    }


    @Test
    public void should_see_that_there_are_zero_items_todo_when_all_are_toggled_complete_with_page_objects() {

        james.starts_with_a_todo_list_containing("Walk the dog", "Put out the garbage");

        james.toggles_all_items();

        james.should_see_that_the_number_of_items_left_is(0);
    }

    @Test
    public void should_see_how_many_items_todo_when_all_are_toggled_to_incomplete_with_page_objects() {

        james.starts_with_a_todo_list_containing("Walk the dog", "Put out the garbage");

        james.toggles_all_items();

        james.toggles_all_items();

        james.should_see_that_the_number_of_items_left_is(2);
    }
}
