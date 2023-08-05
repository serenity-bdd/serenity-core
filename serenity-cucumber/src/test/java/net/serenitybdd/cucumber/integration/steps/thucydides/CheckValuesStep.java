package net.serenitybdd.cucumber.integration.steps.thucydides;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;

public class CheckValuesStep {
    String firstname;
    String lastname;
    String expectedFirstname;
    String expectedLastname;

    @Steps
    SomeNormalSteps normalSteps;

    @Step
    public void checkValues() {
        normalSteps.processFirstName(firstname);
        normalSteps.processSecondName(lastname);
        normalSteps.checkResults(expectedFirstname, expectedLastname);
    }
}
