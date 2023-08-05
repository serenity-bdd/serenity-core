package net.serenitybdd.demos.todos.screenplay.features.maintain_my_todo_list;

import net.serenitybdd.demos.todos.screenplay.questions.TheItems;
import net.serenitybdd.demos.todos.screenplay.tasks.DeleteAnItem;
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
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;

@RunWith(SerenityRunner.class)
@WithTags({
        @WithTag("Screenplay pattern"),
        @WithTag("version:RELEASE-1"),
})
public class DeleteTodos {

    @Managed private WebDriver hisBrowser;
    private Actor james = Actor.named("James");
    @Before public void jamesCanBrowseTheWeb() {
        james.can(BrowseTheWeb.with(hisBrowser));
    }

    @Test
    public void should_be_able_to_delete_todos() {

        givenThat(james).wasAbleTo(Start.withATodoListContaining("Walk the dog", "Put out the garbage"));

        when(james).attemptsTo(
                DeleteAnItem.called("Walk the dog")
        );

        then(james).should(seeThat(TheItems.displayed(), hasItem("Put out the garbage")));
    }

    @Test
    public void should_see_deleting_a_todo_decreases_the_remaining_items_count() {

        givenThat(james).wasAbleTo(Start.withATodoListContaining("Walk the dog", "Put out the garbage"));

        when(james).attemptsTo(
                DeleteAnItem.called("Walk the dog")
        );

        then(james).should(seeThat(TheItems.leftCount(), is(1)));
    }
}
