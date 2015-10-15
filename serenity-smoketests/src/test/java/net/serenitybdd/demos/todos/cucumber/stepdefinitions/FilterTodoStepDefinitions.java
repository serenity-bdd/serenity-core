package net.serenitybdd.demos.todos.cucumber.stepdefinitions;

import cucumber.api.java.en.When;
import net.serenitybdd.demos.todos.model.TodoStatusFilter;
import net.serenitybdd.demos.todos.tasks.FilterItems;

import static net.serenitybdd.demos.todos.model.Actors.theActorNamed;

public class FilterTodoStepDefinitions {

    @When("^(.*) consults(?: the)? (.*) tasks$")
    public void i_delete_the_todo_action(String name, TodoStatusFilter status) throws Throwable {
        theActorNamed(name).attemptsTo(FilterItems.byStatus(status));
    }
}
