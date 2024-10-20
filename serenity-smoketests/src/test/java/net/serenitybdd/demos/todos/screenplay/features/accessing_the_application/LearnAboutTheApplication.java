package net.serenitybdd.demos.todos.screenplay.features.accessing_the_application;

import net.serenitybdd.annotations.Managed;
import net.serenitybdd.demos.todos.screenplay.questions.Application;
import net.serenitybdd.demos.todos.screenplay.questions.Placeholder;
import net.serenitybdd.demos.todos.screenplay.tasks.Start;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

import static net.serenitybdd.screenplay.GivenWhenThen.*;
import static net.serenitybdd.screenplay.matchers.ConsequenceMatchers.displays;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

/**
 * This example illustrates using Serenity Steps with JUnit.
 */
@ExtendWith(SerenityJUnit5Extension.class)
public class LearnAboutTheApplication {

    private Actor james = Actor.named("James");

    @Managed
    private WebDriver hisBrowser;

    @BeforeEach
    public void jamesCanBrowseTheWeb() {
        james.can(BrowseTheWeb.with(hisBrowser));
    }

    @Test
    public void should_be_able_to_identify_the_application() {

        givenThat(james).wasAbleTo(Start.withAnEmptyTodoList());

        then(james).should(
            seeThat(Application.information(),
                displays("title",equalTo("Serenity/JS TodoApp")),
                displays("heading",equalTo("todos")),
                displays("about", containsString("Serenity/JS TodoApp, based on TodoMVC"))
            )
        );
    }

    @Test
    public void should_see_how_to_begin() {

        givenThat(james).wasAbleTo(Start.withAnEmptyTodoList());

        then(james).should(seeThat(Placeholder.text(), is("What needs to be done?")));
    }
}
