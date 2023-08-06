package net.serenitybdd.demos.todos.screenplay.features.maintain_my_todo_list;

import net.serenitybdd.demos.todos.screenplay.questions.TheItems;
import net.serenitybdd.demos.todos.screenplay.tasks.Clear;
import net.serenitybdd.demos.todos.screenplay.tasks.CompleteItem;
import net.serenitybdd.demos.todos.screenplay.tasks.Start;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.annotations.Managed;
import net.serenitybdd.annotations.WithTag;
import net.serenitybdd.annotations.WithTags;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static net.serenitybdd.screenplay.GivenWhenThen.*;
import static org.hamcrest.Matchers.hasItems;

@RunWith(SerenityRunner.class)
@WithTag("Screenplay pattern")
@WithTags({
        @WithTag("Screenplay pattern"),
        @WithTag("version:RELEASE-3"),
})
public class TodosBelongToAUser {

    private Actor james = Actor.named("James");
    private Actor jane = Actor.named("Jane");

    @Managed private WebDriver hisBrowser;
    @Managed private WebDriver herBrowser;

    @Before
    public void jamesCanBrowseTheWeb() {
        james.can(BrowseTheWeb.with(hisBrowser));
        jane.can(BrowseTheWeb.with(herBrowser));
    }

    @Test
    public void should_not_affect_todos_belonging_to_another_user() {
        givenThat(james).wasAbleTo(Start.withATodoListContaining("Walk the dog", "Put out the garbage"));
        andThat(jane).wasAbleTo(Start.withATodoListContaining("Walk the dog", "Feed the cat"));

        when(james).attemptsTo(
                CompleteItem.called("Walk the dog"),
                Clear.completedItems()
        );

        then(jane).should(seeThat(TheItems.displayed(), hasItems("Walk the dog", "Feed the cat")));
    }
}
