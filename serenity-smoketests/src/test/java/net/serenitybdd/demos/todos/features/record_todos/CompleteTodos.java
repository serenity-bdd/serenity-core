package net.serenitybdd.demos.todos.features.record_todos;

import net.serenitybdd.demos.todos.serenity.ATodoUser;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.Steps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * This example illustrates using Serenity Steps with JUnit.
 */
@RunWith(SerenityRunner.class)
public class CompleteTodos {

    @Managed
    WebDriver joesBrowser;

    @Steps
    ATodoUser joe;

    @Before
    public void openTheApplication() {
        joe.opens_the_todo_application();
    }

    @Test
    public void shouldBeAbleToClearCompletedActionsFromTheTodoList() {

        // GIVEN
        joe.has_added_actions_called("Walk the dog", "Put out the garbage");

        // WHEN
        joe.completes_the_action_called("Walk the dog");
        joe.clears_the_completed_actions();

        // THEN
        joe.should_see_the_todo_actions("Put out the garbage");
    }

}
