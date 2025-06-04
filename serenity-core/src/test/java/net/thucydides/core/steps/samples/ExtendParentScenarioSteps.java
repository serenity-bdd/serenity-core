package net.thucydides.core.steps.samples;

import net.serenitybdd.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

public class ExtendParentScenarioSteps extends ScenarioSteps {

    public ExtendParentScenarioSteps(Pages pages) {
        super(pages);
    }

    @Step("Add parent scenario step")
    public void addChildScenarioStep() {
    }

    @Step("Get parent scenario step")
    public void getChildScenarioStep() {
    }
}
