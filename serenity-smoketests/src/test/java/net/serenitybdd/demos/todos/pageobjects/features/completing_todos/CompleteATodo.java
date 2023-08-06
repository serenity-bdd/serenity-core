package net.serenitybdd.demos.todos.pageobjects.features.completing_todos;

import net.serenitybdd.demos.todos.pageobjects.steps.TodoUserSteps;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.annotations.Managed;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.annotations.WithTag;
import net.serenitybdd.annotations.WithTags;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(SerenityRunner.class)
@WithTags({
        @WithTag("PageObjects pattern"),
        @WithTag("version:RELEASE-2"),
})

public class CompleteATodo {

    @Managed(uniqueSession = true)
    WebDriver driver;

    @Steps   TodoUserSteps james;

    @Before
    public void setup()
    {
        james.starts_with_a_todo_list_containing("Walk the dog", "Put out the garbage");
    }

    @Test
    public void should_be_able_to_complete_a_todo_with_page_objects() {

        james.completes("Walk the dog");

        james.should_see_that_that_following_item_is_marked_as_complete("Walk the dog");

        james.should_see_that_the_number_of_items_left_is(1);

    }

}
