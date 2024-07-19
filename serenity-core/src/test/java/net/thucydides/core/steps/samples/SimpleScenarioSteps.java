package net.thucydides.core.steps.samples;

import net.serenitybdd.annotations.Pending;
import net.serenitybdd.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

/**
 * Some sample test steps, used for demonstration and testing purposes.
 */
public class SimpleScenarioSteps extends ScenarioSteps {
    
    public SimpleScenarioSteps(Pages pages) {
        super(pages);
    }

    @Step
    public void clickOnProjects() {
    }
    
    @Step
    public void clickOnCategories() {
    }

    @Step
    public void clickOnInexistantLink() {
    }

    @Step
    public void clickOnProjectAndCheckTitle() {
    }

    @Step @Pending
    public void notImplementedYet() {}
}
