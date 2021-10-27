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
public class StepInstantiationSteps {

    @Steps
    SampleSteps sampleSteps;

    @Given("I have a Cucumber feature file containing Thucydides @Steps fields")
    public void featureFileContainsStepsFields() {
    }

    @Then("the step fields should be instantiated")
    public void theStepFieldsShouldBeInstantiated() {
        assertThat(sampleSteps).isNotNull();
    }

    @Then("the nested pages objects should be instantiated")
    public void thePageObjectsShouldBeInstantiated() {
        assertThat(sampleSteps.pageObject).isNotNull();
    }

}
