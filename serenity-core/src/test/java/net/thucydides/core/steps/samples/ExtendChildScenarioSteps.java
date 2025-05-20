package net.thucydides.core.steps.samples;

import net.serenitybdd.annotations.Step;
import net.thucydides.core.pages.Pages;

public class ExtendChildScenarioSteps extends ExtendParentScenarioSteps {

    public ExtendChildScenarioSteps(Pages pages) {
        super(pages);
    }

    @Override
    @Step("Add child scenario step")
    public void addChildScenarioStep() {
    }

    @Override
    @Step("Get child scenario step")
    public void getChildScenarioStep() {
    }
}
