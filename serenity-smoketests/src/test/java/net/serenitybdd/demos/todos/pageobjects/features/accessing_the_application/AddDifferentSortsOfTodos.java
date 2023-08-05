package net.serenitybdd.demos.todos.pageobjects.features.accessing_the_application;

import net.serenitybdd.demos.todos.pageobjects.steps.TodoUserSteps;
import net.serenitybdd.junit.runners.SerenityParameterizedRunner;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.Steps;
import net.thucydides.model.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import net.thucydides.junit.annotations.Concurrent;
import net.thucydides.junit.annotations.TestData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.Collection;

@RunWith(SerenityParameterizedRunner.class)
@Concurrent(threads = "4")
@WithTags({
        @WithTag("PageObjects pattern"),
        @WithTag("version:RELEASE-3"),
})
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
    TodoUserSteps james;

    private final String todo;


    public AddDifferentSortsOfTodos(String todo) {
        this.todo = todo;
    }


    @Before
    public void openTheApplication() {
        james.starts_with_an_empty_todo_list();

    }

    @Test
//    @Manual
    public void shouldBeAbleToAddANewTodoItem() {
        james.should_see_the_correct_website_title();
        james.should_see_the_correct_application_heading();
        james.should_see_the_about_section();
    }

}
