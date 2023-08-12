package net.thucydides.samples;

import net.serenitybdd.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

public class DemoSiteSteps extends ScenarioSteps {

    public DemoSiteSteps(Pages pages) {
        super(pages);
    }

    IndexPage page;

    @Step
    public void opensPage() {
        page.open();
    }

    @Step
    public void enter_values(String selectValue, boolean checkboxValue) {
        page.selectItem(selectValue);
        page.setCheckboxOption(checkboxValue);
    }

    @Step
    public void enter_values(String selectValue, boolean checkboxValue, String textValue) {
        page.selectItem(selectValue);
        page.setCheckboxOption(checkboxValue);
        page.enterValue(textValue);
    }


    @Step
    public void fields_should_be_displayed() {
        page.shouldBeVisible(page.multiselect);
    }

    @Step
    public void should_display(String selectValue) {
        page.shouldContainText(selectValue);
    }

    @Step
    public void should_have_selected_value(String selectValue) {
        if (!page.getSelectedValues().contains(selectValue)) {
            throw new AssertionError("Value " + selectValue + " not in " + page.getSelectedValues());
        }
    }

    @Step
    public void should_not_have_selected_value(String selectValue) {
        if (page.getSelectedValues().contains(selectValue)) {
            throw new AssertionError();
        }
    }

    @Step
    public void should_have_no_selected_value() {
        if (page.getSelectedValues().size() > 0) {
            throw new AssertionError("Should have no selected value but got " + page.getSelectedValues());
        }
    }

    @Step
    public void do_something() {}

    @Step
    public void do_something_else() {}

    @Step
    public void throw_exception() {
        throw new IllegalArgumentException();
    }
}
