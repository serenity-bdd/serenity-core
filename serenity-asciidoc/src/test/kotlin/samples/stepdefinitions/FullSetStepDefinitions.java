package samples.stepdefinitions;

import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.serenitybdd.core.exceptions.TestCompromisedException;
import net.thucydides.core.steps.StepEventBus;

import static org.assertj.core.api.Assertions.assertThat;

public class FullSetStepDefinitions {

    @Given("^some precondition$")
    public void some_precondition() {
    }

    @When("^something good happens$")
    public void something_good_happens() {
    }

    @Then("^we should be happy$")
    public void we_should_be_happy() {
    }

    @Then("^we should not be happy$")
    public void we_should_not_be_happy() {
    }

//    @When("something is not ready")
//    public void something_is_not_ready() {
//        throw new PendingException();
//    }


    @When("something should be ignored")
    public void something_should_be_ignored() {
    }

    @When("something is wrong")
    public void something_is_wrong() {
        assertThat(true).isFalse();
    }

    @When("something is broken")
    public void something_is_brokem() {
        throw new IllegalArgumentException("Broken test");
    }


    @When("something is compromised")
    public void something_is_compromised() {
        throw new TestCompromisedException("Test compromised");
    }

}
