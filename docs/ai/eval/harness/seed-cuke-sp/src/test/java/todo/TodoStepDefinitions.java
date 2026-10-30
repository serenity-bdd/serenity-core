package todo;

import io.cucumber.java.Before;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.Matchers.hasItem;

public class TodoStepDefinitions {

    @Before
    public void setTheStage() {
        OnStage.setTheStage(new OnlineCast());
    }

    @ParameterType(".*")
    public Actor actor(String name) {
        return OnStage.theActorCalled(name);
    }

    @Given("{actor} has opened the todo app")
    public void opensTheApp(Actor actor) {
        actor.attemptsTo(Open.url("https://todo-app.serenity-js.org"));
    }

    @When("{actor} adds {string} to the list")
    public void addsATodo(Actor actor, String item) {
        actor.attemptsTo(AddATodo.called(item));
    }

    @Then("{actor} should see {string} in the list")
    public void seesTheTodo(Actor actor, String item) {
        actor.should(seeThat(TheTodos.displayed(), hasItem(item)));
    }
}
