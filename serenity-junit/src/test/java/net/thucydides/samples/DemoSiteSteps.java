package net.thucydides.samples;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

public class DemoSiteSteps extends ScenarioSteps {

    public DemoSiteSteps(Pages pages) {
        super(pages);
    }

    @Step
    public void enter_values(String selectValue, boolean checkboxValue) {
        IndexPage page = (IndexPage) getPages().currentPageAt(IndexPage.class);
        page.selectItem(selectValue);
        page.setCheckboxOption(checkboxValue);
    }

    @Step
    public void enter_values(String selectValue, boolean checkboxValue, String textValue) {
        IndexPage page = (IndexPage) getPages().currentPageAt(IndexPage.class);
        page.selectItem(selectValue);
        page.setCheckboxOption(checkboxValue);
        page.enterValue(textValue);
    }


    @Step
    public void fields_should_be_displayed() {
        IndexPage page = (IndexPage) getPages().currentPageAt(IndexPage.class);
        page.shouldBeVisible(page.multiselect);
    }

    @Step
    public void should_display(String selectValue) {
        IndexPage page = (IndexPage) getPages().currentPageAt(IndexPage.class);
        page.shouldContainText(selectValue);
    }

    @Step
    public void should_have_selected_value(String selectValue) {
        IndexPage page = (IndexPage) getPages().currentPageAt(IndexPage.class);
        if (!page.getSelectedValues().contains(selectValue)) {
            throw new AssertionError("Value " + selectValue + " not in " + page.getSelectedValues());
        }
    }

    @Step
    public void should_not_have_selected_value(String selectValue) {
        IndexPage page = (IndexPage) getPages().currentPageAt(IndexPage.class);
        if (page.getSelectedValues().contains(selectValue)) {
            throw new AssertionError();
        }
    }

    @Step
    public void should_have_no_selected_value() {
        IndexPage page = (IndexPage) getPages().currentPageAt(IndexPage.class);
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