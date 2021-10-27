package net.serenitybdd.cucumber.integration.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;


public class CoreSteps {

    @When("^I run it using Thucydides$")
    public void I_run_it_using_Thucydides() throws Throwable {}

    @Then("^Thucydides should record a test outcome in the target directory$")
    public void should_record_test_outcome() throws Throwable {
    }
}
