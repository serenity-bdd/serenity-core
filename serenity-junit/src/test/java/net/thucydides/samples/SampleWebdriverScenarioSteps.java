package net.thucydides.samples;

import net.serenitybdd.annotations.*;
import net.serenitybdd.annotations.Steps;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import org.junit.Ignore;
import org.openqa.selenium.NoSuchElementException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SampleWebdriverScenarioSteps extends ScenarioSteps {

    public SampleWebdriverScenarioSteps(Pages pages) {
        super(pages);
    }

    @Steps
    public SampleScenarioNestedSteps nestedSteps;
    
    @Step
    @TestsRequirement("LOW_LEVEL_BUSINESS_RULE")
    public void stepThatSucceeds() {
    }

    @Step
    @TestsRequirements({"LOW_LEVEL_BUSINESS_RULE_1","LOW_LEVEL_BUSINESS_RULE_2"})
    public void anotherStepThatSucceeds() {
    }

    @Step
    public void stepThree(String option) {
    }

    @Step
    public void stepWithParameters(String option1, Integer option2) {
    }

    @Step
    public void stepThatFails() {
        assertThat(1, is(2));
    }

    @Step
    public void stepFour(String option) {
    }
    
    @Step
    public void stepThatShouldBeSkipped() {
    }

    @StepGroup("Nested group of steps")
    public void stepThatCallsNestedSteps() {
        nestedSteps.stepThatSucceeds();
        nestedSteps.anotherStepThatSucceeds();
    }

    @Step("A pending step")
    @Pending
    public void stepThatIsPending() {
    }

    @Step
    @Ignore
    public void stepThatIsIgnored() {
    }

    public void anUnannotatedMethod() {
    }

    @Step
    public void stepWithAParameter(String value) {
    }
    
    @Step
    public void stepWithTwoParameters(String value, int number) {
    }
    
    @StepGroup("Group of steps")
    public void groupOfStepsContainingAFailure() {
        stepThatSucceeds();
        stepThatFails();
        stepThatShouldBeSkipped();
        
    }

    @StepGroup("Another group of steps")
    public void anotherGroupOfSteps() {
        stepThatSucceeds();
        anotherStepThatSucceeds();
        stepThatIsPending();
        
    }

    @StepGroup("Group of steps")
    public void groupOfStepsContainingAnError() {
        stepThatSucceeds();
        anotherStepThatSucceeds();
        String nullString = null;
        int thisShouldFail = nullString.length();
        
    }

    @Step
    public void stepThatFailsWithWebdriverException() {
        throw new NoSuchElementException("No such element");
    }

    @Step
    public void stepThatFailsWithRuntimeException() {
        throw new IllegalArgumentException("Something nasty went wrong");
    }


}
