package serenitycore.net.thucydides.core.steps.samples;

import serenitymodel.net.thucydides.core.annotations.Pending;
import serenitymodel.net.thucydides.core.annotations.Step;
import serenitycore.net.thucydides.core.pages.Pages;
import serenitycore.net.thucydides.core.steps.ScenarioSteps;

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
