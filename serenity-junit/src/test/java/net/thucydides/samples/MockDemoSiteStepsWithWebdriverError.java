package net.thucydides.samples;

import net.serenitybdd.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

public class MockDemoSiteStepsWithWebdriverError extends ScenarioSteps {

    public MockDemoSiteStepsWithWebdriverError(Pages pages) {
        super(pages);
    }

    @Step
    public void enter_values(String selectValue, boolean checkboxValue) {
    }

    @Step
    public void fields_should_be_displayed() {
    }

    @Step
    public void should_display(String selectValue) {
    }
    
    @Step
    public void should_have_selected_value(String selectValue) {
        throw new RuntimeException("This is not the element you're looking for");
    }

    @Step
    public void should_not_have_selected_value(String selectValue) {
    }

    @Step
    public void should_have_no_selected_value() {
    }

    @Step
    public void do_something() {}

    @Step
    public void do_something_else() {}
}
