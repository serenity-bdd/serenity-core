package net.serenitybdd.cucumber.integration.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import net.serenitybdd.cucumber.integration.steps.thucydides.SampleSteps;
import net.thucydides.core.annotations.Steps;

import static org.assertj.core.api.Assertions.assertThat;

;

/**
 * Created by john on 15/07/2014.
 */
public class IllegalStepInstantiationSteps {

    @Steps
    SampleSteps sampleSteps;

    public IllegalStepInstantiationSteps(SampleSteps sampleSteps) {
        this.sampleSteps = sampleSteps;
    }

    @Given("I have a step library without a default constructor")
    public void featureFileContainsStepsFields() {
    }

    @Then("the tests should fail with an exception")
    public void thePageObjectsShouldBeInstantiated() {
        assertThat(sampleSteps.pageObject).isNotNull();
    }

}
