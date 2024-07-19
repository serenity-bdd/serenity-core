package net.serenitybdd.cucumber.integration.steps.thucydides;

import net.serenitybdd.annotations.Step;

public class SomeNestedSeleniumSteps {

    StaticSitePage page;

    @Step
    public void enters_the_first_name(String firstname) {
        page.setFirstName(firstname);
    }

    @Step
    public void enters_the_last_name(String lastname) {
        page.setLastName(lastname);
    }
}
