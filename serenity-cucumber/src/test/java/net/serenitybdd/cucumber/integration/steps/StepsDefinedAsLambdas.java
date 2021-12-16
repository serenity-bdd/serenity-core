package net.serenitybdd.cucumber.integration.steps;

import io.cucumber.java8.En;

public class StepsDefinedAsLambdas implements En {

    public StepsDefinedAsLambdas() {
        Given("I want to use a lambda step", () -> { });
        When("I run the test", () -> { });
        Then("I should see the output", () -> { });
    }
}
