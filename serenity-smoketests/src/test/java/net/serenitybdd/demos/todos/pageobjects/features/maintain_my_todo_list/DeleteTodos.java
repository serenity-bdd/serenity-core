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
public class DeleteTodos {

    @Managed WebDriver driver;

    @Steps   TodoUserSteps james;

    @Test
    public void should_be_able_to_delete_todos_with_page_objects() {

        james.starts_with_a_todo_list_containing("Walk the dog", "Put out the garbage");

        james.deletes("Walk the dog");

        james.should_see_that_displayed_items_contain("Put out the garbage");
    }

    @Test
    public void should_see_deleting_a_todo_decreases_the_remaining_items_count_with_page_objects() {

        james.starts_with_a_todo_list_containing("Walk the dog", "Put out the garbage");

        james.deletes("Walk the dog");

        james.should_see_that_the_number_of_items_left_is(1);
    }
}
