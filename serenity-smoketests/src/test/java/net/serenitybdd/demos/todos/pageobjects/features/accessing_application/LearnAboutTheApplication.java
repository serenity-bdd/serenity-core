package net.serenitybdd.demos.todos.pageobjects.features.accessing_application;

import net.serenitybdd.demos.todos.pageobjects.steps.TodoUserSteps;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.annotations.Managed;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.annotations.WithTag;
import net.serenitybdd.annotations.WithTags;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(SerenityRunner.class)
@WithTags({
        @WithTag("PageObjects pattern"),
        @WithTag("version:RELEASE-1"),
        @WithTag("smoke"),
})
public class LearnAboutTheApplication {

    @Managed WebDriver driver;

    @Steps   TodoUserSteps james;

    @Test
    public void should_be_able_to_identify_the_application_with_page_objects() {

        james.starts_with_an_empty_todo_list();

        james.should_see_the_correct_website_title();
        james.should_see_the_correct_application_heading();
        james.should_see_the_about_section();
    }

    @Test
    public void should_see_how_to_begin_with_page_objects() {

        james.starts_with_an_empty_todo_list();

        james.should_see_that_the_placeholder_text_says("What needs to be done?");
    }
}
