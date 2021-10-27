package net.serenitybdd.cucumber.integration.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import net.serenitybdd.cucumber.integration.steps.thucydides.SampleWebSteps;
import net.thucydides.core.annotations.Steps;

import static org.assertj.core.api.Assertions.assertThat;

;

/**
 * Created by john on 15/07/2014.
 */
public class WebEnabledStepInstantiationSteps {

    @Steps
    SampleWebSteps sampleSteps;

    @Given("I have a Cucumber feature file containing a web-enabled Thucydides @Steps field")
    public void featureFileContainsStepsFields() {
    }

    @Then("the web-enabled step fields should be instantiated")
    public void theStepFieldsShouldBeInstantiated() {
        assertThat(sampleSteps).isNotNull();
    }

}
