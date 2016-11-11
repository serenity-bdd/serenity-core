package net.serenitybdd.demos.todos.features.record_todos;

import net.serenitybdd.demos.todos.serenity.ATodoUser;
import net.serenitybdd.junit.runners.SerenityParameterizedRunner;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.Steps;
import net.thucydides.junit.annotations.TestData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.Collection;

/**
 * This example illustrates using Serenity Steps with JUnit.
 */
@RunWith(SerenityParameterizedRunner.class)
//@Concurrent
public class AddDifferentSortsOfTodos {

    @Managed
    WebDriver janesBrowser;

    @TestData
    public static Collection<Object[]> todoItems(){
        return Arrays.asList(new Object[][]{
                {"walk the lion"},
                {"wash the dishes"},
                {"feed the ferrets"},
                {"count the rabbits"},
        });
    }

    @Steps
    ATodoUser jane;

    private final String todo;


    public AddDifferentSortsOfTodos(String todo) {
        this.todo = todo;
    }


    @Before
    public void openTheApplication() {
        jane.opens_the_todo_application();
    }

    @Test
    public void shouldBeAbleToAddANewTodoItem() {

        // WHEN
        jane.adds_an_action_called(todo);

        // THEN
        jane.should_see_the_todo_actions(todo);
    }

}
