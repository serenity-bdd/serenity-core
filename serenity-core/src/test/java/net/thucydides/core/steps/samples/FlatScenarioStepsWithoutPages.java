package net.thucydides.core.steps.samples;

import net.serenitybdd.annotations.Pending;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.StepGroup;
import net.serenitybdd.annotations.Title;
import org.junit.Assume;
import org.junit.Ignore;

import static org.hamcrest.CoreMatchers.is;

public class FlatScenarioStepsWithoutPages {

    public FlatScenarioStepsWithoutPages() {}

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

    @Step
    public void failingAssumption() {
        Assume.assumeThat(true, is(false));
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
}
