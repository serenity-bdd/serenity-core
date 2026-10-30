package demo;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import static org.assertj.core.api.Assertions.assertThat;

public class MathSteps {
    int sum;

    @Given("I have {int} and {int}")
    public void have(int a, int b) { sum = a + b; }

    @Then("the sum should be {int}")
    public void sumIs(int expected) { assertThat(sum).isEqualTo(expected); }
}
