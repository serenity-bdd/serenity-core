package net.serenitybdd.cucumber.integration.steps.thucydides;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

/**
 * Created by john on 15/07/2014.
 */
public class SampleWebSteps extends ScenarioSteps {


    public SampleWebSteps(Pages pages) {
        super(pages);
    }

    @Step
    public void aSimpleStep() { }

    @Step
    public void anotherSimpleStep() {}
}
