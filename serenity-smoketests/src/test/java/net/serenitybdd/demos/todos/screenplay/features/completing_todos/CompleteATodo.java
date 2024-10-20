package net.serenitybdd.demos.todos.screenplay.features.completing_todos;

import net.serenitybdd.annotations.Managed;
import net.serenitybdd.demos.todos.screenplay.questions.TheItemStatus;
import net.serenitybdd.demos.todos.screenplay.questions.TheItems;
import net.serenitybdd.demos.todos.screenplay.tasks.CompleteItem;
import net.serenitybdd.demos.todos.screenplay.tasks.Start;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

import static net.serenitybdd.demos.todos.screenplay.model.TodoStatus.Completed;
import static net.serenitybdd.screenplay.GivenWhenThen.*;
import static org.hamcrest.CoreMatchers.is;

@ExtendWith(SerenityJUnit5Extension.class)
public class CompleteATodo {

    private Actor james = Actor.named("James");

    @Managed
    private WebDriver hisBrowser;

    @BeforeEach
    public void jamesCanBrowseTheWeb() {
        james.can(BrowseTheWeb.with(hisBrowser));
    }

    @Test
    public void should_be_able_to_complete_a_todo() {

        givenThat(james).wasAbleTo(
                Start.withATodoListContaining("Walk the dog", "Put out the garbage")
        );

        when(james).attemptsTo(
                CompleteItem.called("Walk the dog")
        );

        then(james).should(
                seeThat(TheItemStatus.forTheItemCalled("Walk the dog"), is(Completed)),
                seeThat(TheItems.leftCount(), is(1))
        );
    }

    @Test
    public void should_see_the_number_of_todos_decrease_when_an_item_is_completed() {

        givenThat(james).wasAbleTo(
                Start.withATodoListContaining("Walk the dog", "Put out the garbage")
        );

        when(james).attemptsTo(
                CompleteItem.called("Walk the dog")
        );

        then(james).should(seeThat(TheItems.leftCount(), is(1)));
    }
}
