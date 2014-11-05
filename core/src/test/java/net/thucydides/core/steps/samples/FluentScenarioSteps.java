package net.thucydides.core.steps.samples;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

public class FluentScenarioSteps extends ScenarioSteps {

    public FluentScenarioSteps(Pages pages) {
        super(pages);
    }

    @Step(fluent=true)
    public FluentScenarioSteps when(){
        return this;
    }
    
    @Step(fluent=true)
    public FluentScenarioSteps someone() {
        return this;
    }

    @Step(fluent=true)
    public FluentScenarioSteps does() {
        return this;
    }

    @Step
    public void this_sort_of_thing(){
    }

    @Step
    public void step1(){
    }

    @Step
    public void step2(){
    }

    @Step
    public void parent_step(){
        when().someone().does().this_sort_of_thing();
    }

}
