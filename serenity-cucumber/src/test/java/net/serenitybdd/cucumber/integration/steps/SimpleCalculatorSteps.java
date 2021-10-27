package net.serenitybdd.cucumber.integration.steps;

import io.cucumber.java.PendingException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleCalculatorSteps {

    int a;
    int b;
    @Given("^the number ([0-9]*) and the number ([0-9]*)$")
    public void theNumberAAndTheNumberB(Integer a, Integer b) throws Throwable {
        this.a = a;
        this.b = b;
    }

    int result;
    @When("^([0-9]*) plus ([0-9]*)$")
    public void aPlusB(Integer a, Integer b) throws Throwable {
        result = a + b;
    }

    @Then("^the result is equals to ([0-9]*)$")
    public void theResultIsEqualsToC(Integer expectedResult) throws Throwable {
        assertThat(result).isEqualTo(expectedResult);
    }

    @Then("a PendingException should be thrown")
    public void throwPending() {
        throw new PendingException();
    }

    @Given("^the amount ([0-9]*) and the amount ([0-9]*)$")
    public void theAmounts(Integer a, Integer b) throws Throwable {
    }

    @When("^([0-9]*) minus ([0-9]*)$")
    public void aMinusB(Integer a, Integer b) throws Throwable {
    }

    @Then("^the result should be ([0-9]*)$")
    public void theResultShouldBe(Integer b) throws Throwable {
    }
}