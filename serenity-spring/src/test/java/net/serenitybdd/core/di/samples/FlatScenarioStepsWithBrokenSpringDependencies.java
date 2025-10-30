package net.serenitybdd.core.di.samples;

import net.serenitybdd.annotations.Pending;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.StepGroup;
import net.serenitybdd.annotations.Title;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;

@ContextConfiguration(locations = "/spring/config.xml")
public class FlatScenarioStepsWithBrokenSpringDependencies extends ScenarioSteps {

    @Autowired
    public UnconfiguredService widgetService;


    @Resource
    public CatalogService catalogService;

    public FlatScenarioStepsWithBrokenSpringDependencies(Pages pages) {
        super(pages);
    }

    @Step
    public void step_one(){
    }

    @Step
    public void step_two() {
    }

    @Step
    public void step_three() {
    }

    @Step
    public void nested_step_one(){
    }

    @Step
    public void nested_step_two() {
    }

    @Step

    public void nested_step_three() {
    }

    @Step
    public void failingStep() {
        throw new AssertionError("Step failed");
    }

    @Ignore
    @Step
    public void ignoredStep() {}

    @Pending
    @Step
    public void pendingStep() {}

    @Pending
    @Step
    public void pending_group() {
        step_three();
        step_two();
        step_one();
    }

    @Title("A step with a title")
    @Step
    public void step_with_title() {}

    @Ignore
    @Step
    public void ignored_group() {
        step_three();
        step_two();
        step_one();
    }

    @Step
    public void grouped_steps() {
        nested_step_one();
        nested_step_two();
        nested_step_one();
        nested_step_two();
    }

    @Step
    public void deeply_grouped_steps() {
        step_one();
        step_two();
        grouped_steps();
        step_two();
        step_one();
    }

    @Step
    public void stepWithLongName() {}

    @Step
    public void stepWithParameters(String name) {}

    @Step
    public void step_with_long_name_and_underscores() {}

    @StepGroup("Annotated step group title")
    public void a_step_group() {
        stepWithLongName();
        step_with_long_name_and_underscores();
    }

    @Step
    public void a_plain_step_group() {
        stepWithLongName();
        step_with_long_name_and_underscores();
    }

    @Step
    public void stepCausingANullPointerException() {
        String nullValue = null;
        nullValue.length();
    }

    public void unannotatedStepCausingANullPointerException() {
        String nullValue = null;
        nullValue.length();
    }

    @Step
    public void programmaticallyIgnoredStep() {
        Serenity.ignoredStep("This test should be skipped");

    }

    @Step
    public void programmaticallyPendingStep() {
        Serenity.pendingStep("This test should be skipped");

    }

}
