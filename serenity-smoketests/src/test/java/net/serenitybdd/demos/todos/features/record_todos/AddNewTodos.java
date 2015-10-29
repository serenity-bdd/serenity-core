package net.serenitybdd.demos.todos.features.record_todos;

import net.serenitybdd.demos.todos.serenity.ATodoUser;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.Manual;
import net.thucydides.core.annotations.Pending;
import net.thucydides.core.annotations.Steps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * This example illustrates using Serenity Steps with JUnit.
 */
@RunWith(SerenityRunner.class)
public class AddNewTodos {

    @Managed
    WebDriver janesBrowser;

    @Steps
    ATodoUser jane;

    @Before
    public void openTheApplication() {
        jane.opens_the_todo_application();
    }

    @Test
    public void shouldBeAbleToAddANewTodoItem() {

        // WHEN
        jane.adds_an_action_called("Walk the dog");

        // AND
        jane.adds_an_action_called("Put out the garbage");

        // THEN
        jane.should_see_the_todo_actions("Walk the dog", "Put out the garbage");
    }

    @Test
    @Manual
    public void shouldBeAbleToAddANewTodoItemReallyFast() {}

    @Test
    @Pending
    public void shouldBeAbleToAddANewTodoItemEasily() {}
}
