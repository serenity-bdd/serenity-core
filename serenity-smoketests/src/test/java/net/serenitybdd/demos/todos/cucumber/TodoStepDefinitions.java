package net.serenitybdd.demos.todos.cucumber;

import io.cucumber.java.Before;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.serenitybdd.screenplay.questions.Text;
import org.openqa.selenium.Keys;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TodoStepDefinitions {

    @Before
    public void setTheStage() {
        OnStage.setTheStage(new OnlineCast());
    }

    @ParameterType(".*")
    public Actor actor(String actorName) {
        return OnStage.theActorCalled(actorName);
    }

    @Given("{actor} starts with an empty list")
    public void stats_with_an_empty_list(Actor actor) {
        actor.attemptsTo(
                Open.url("https://todomvc.com/examples/react/dist/")
        );
    }

    @When("{actor} adds {string} to his list")
    public void he_adds_to_his_list(Actor actor, String item) {
        actor.attemptsTo(
                Enter.theValue(item).into(".new-todo").thenHit(Keys.RETURN)
        );
    }

    @Then("the todo list should contain the following items:")
    public void the_todo_list_should_contain(List<String> expectedItems) {
        Actor currentActor = OnStage.theActorInTheSpotlight();
        var todos = currentActor.asksFor(Text.ofEach(".todo-list li"));
        assertThat(todos).containsExactlyElementsOf(expectedItems);
    }

}
