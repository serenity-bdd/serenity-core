package net.serenitybdd.demos.todos.screenplay.features.accessing_the_application;

import net.serenitybdd.demos.todos.screenplay.questions.Application;
import net.serenitybdd.demos.todos.screenplay.questions.Placeholder;
import net.serenitybdd.demos.todos.screenplay.tasks.Start;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.thucydides.core.annotations.Managed;
import net.thucydides.model.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static net.serenitybdd.screenplay.GivenWhenThen.*;
import static net.serenitybdd.screenplay.matchers.ConsequenceMatchers.displays;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

/**
 * This example illustrates using Serenity Steps with JUnit.
 */
@RunWith(SerenityRunner.class)
@WithTags({
        @WithTag("Screenplay pattern"),
        @WithTag("version:RELEASE-1"),
})
public class LearnAboutTheApplication {

    private Actor james = Actor.named("James");

    @Managed
    private WebDriver hisBrowser;

    @Before
    public void jamesCanBrowseTheWeb() {
        james.can(BrowseTheWeb.with(hisBrowser));
    }

    @Test
    public void should_be_able_to_identify_the_application() {

        givenThat(james).wasAbleTo(Start.withAnEmptyTodoList());

        then(james).should(
            seeThat(Application.information(),
                displays("title",equalTo("AngularJS • TodoMVC")),
                displays("heading",equalTo("todos")),
                displays("about", containsString("Credits"))
            )
        );
    }

    @Test
    public void should_see_how_to_begin() {

        givenThat(james).wasAbleTo(Start.withAnEmptyTodoList());

        then(james).should(seeThat(Placeholder.text(), is("What needs to be done?")));
    }
}
