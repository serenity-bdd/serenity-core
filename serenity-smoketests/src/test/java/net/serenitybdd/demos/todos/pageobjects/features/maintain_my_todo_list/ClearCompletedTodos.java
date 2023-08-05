package net.serenitybdd.demos.todos.pageobjects.features.maintain_my_todo_list;

import net.serenitybdd.demos.todos.pageobjects.steps.TodoUserSteps;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.Steps;
import net.thucydides.model.annotations.WithTag;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(SerenityRunner.class)
@WithTag("PageObjects pattern")
public class ClearCompletedTodos {

    @Managed WebDriver driver;

    @Steps   TodoUserSteps james;

    @Test
    public void should_be_able_to_clear_completed_todos_with_page_objects() {

        james.starts_with_a_todo_list_containing("Walk the dog", "Put out the garbage");

        james.completes("Walk the dog");

        james.clears_completed_items();

        james.should_see_that_displayed_items_contain("Put out the garbage");
    }

    @Test
    public void should_not_be_able_to_clear_completed_todos_if_none_are_complete_with_page_objects() {

        james.starts_with_a_todo_list_containing("Walk the dog", "Put out the garbage");

        james.should_see_that_the_clear_completed_items_option_is_not_visible();
    }
}
