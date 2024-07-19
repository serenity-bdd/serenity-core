package net.thucydides.core.steps.samples;

import net.serenitybdd.annotations.Step;
import net.thucydides.core.pages.Pages;
import some.other.place.BaseScenarioInSomeOtherPackage;

public class StepsDerivedFromADifferentDomain extends BaseScenarioInSomeOtherPackage {

    public StepsDerivedFromADifferentDomain(final Pages pages) {
        super(pages);
    }

    @Step
    public void step_one() {}

    @Step
    public void step_two() {}
}
