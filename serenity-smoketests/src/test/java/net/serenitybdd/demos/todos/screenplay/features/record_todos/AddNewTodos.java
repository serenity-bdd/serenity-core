package net.serenitybdd.demos.todos.screenplay.features.record_todos;
import net.serenitybdd.demos.todos.screenplay.questions.TheItems;
import net.serenitybdd.demos.todos.screenplay.tasks.AddATodoItem;
import net.serenitybdd.demos.todos.screenplay.tasks.Start;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.annotations.Managed;
import net.serenitybdd.annotations.WithTag;
import net.serenitybdd.annotations.WithTags;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static net.serenitybdd.screenplay.GivenWhenThen.*;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;

/**
 * This example illustrates using Serenity Steps with JUnit.
 */
@ExtendWith(SerenityJUnit5Extension.class)
public class AddNewTodos {

    private Actor james = Actor.named("James");
    @Managed private WebDriver hisBrowser;
    @BeforeEach
    public void jamesCanBrowseTheWeb() {
        james.can(BrowseTheWeb.with(hisBrowser));
    }

    @Test
    public void should_be_able_to_add_the_first_todo_item() {

        givenThat(james).wasAbleTo(Start.withAnEmptyTodoList());

        when(james).attemptsTo(AddATodoItem.called("Buy some milk"));

        then(james).should(seeThat(TheItems.displayed(), hasItem("Buy some milk")));
    }

    @Test
    public void should_be_able_to_add_additional_todo_items() {

        givenThat(james).wasAbleTo(Start.withATodoListContaining("Walk the dog", "Put out the garbage"));

        when(james).attemptsTo(AddATodoItem.called("Buy some milk"));

        then(james).should(seeThat(TheItems.displayed(),
                                   hasItems("Walk the dog", "Put out the garbage", "Buy some milk")));
    }
}
