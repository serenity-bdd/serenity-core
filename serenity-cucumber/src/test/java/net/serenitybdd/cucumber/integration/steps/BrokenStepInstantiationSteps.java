package net.serenitybdd.cucumber.integration.steps;

import io.cucumber.java.en.Given;


/**
 * Created by john on 15/07/2014.
 */
public class BrokenStepInstantiationSteps {

    public BrokenStepInstantiationSteps() throws InstantiationException {
        throw new InstantiationException("Oh crap!");
    }

    @Given("I have a step library that fails to instantiate")
    public void featureFileContainsStepsFields() {
    }

}
