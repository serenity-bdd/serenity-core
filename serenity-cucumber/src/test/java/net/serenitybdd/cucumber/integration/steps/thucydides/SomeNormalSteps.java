package net.serenitybdd.cucumber.integration.steps.thucydides;

import net.thucydides.core.annotations.Step;

public class SomeNormalSteps {

    @Step
    public void processFirstName(String firstname) {
    }

    @Step
    public void processSecondName(String lastname) {
    }

    @Step
    public void checkResults(String expectedFirstname, String expectedLastname) {
    }
}
