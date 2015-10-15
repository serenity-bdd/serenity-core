package net.serenitybdd.demos.todos.cucumber.stepdefinitions;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.serenitybdd.demos.todos.model.TodoStatus;
import net.serenitybdd.demos.todos.pages.TodoPage;

import static org.assertj.core.api.Assertions.assertThat;

public class CompleteTodoStepDefinitions {

    TodoPage todoPage;

    @When("^I mark the '(.*)' action as complete$")
    public void i_mark_the_action_as_complete(String action) throws Throwable {
        todoPage.markComplete(action);
    }

    @Then("^'(.*)' should appear as completed$")
    public void should_appear_as_completed(String action) throws Throwable {
        assertThat(todoPage.getStatusFor(action)).isEqualTo(TodoStatus.Completed);
    }
}
