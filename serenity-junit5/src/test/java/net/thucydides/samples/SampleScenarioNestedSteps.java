package net.thucydides.samples;

import net.serenitybdd.annotations.Pending;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.StepGroup;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import org.junit.Ignore;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SampleScenarioNestedSteps extends ScenarioSteps {
    
    public SampleScenarioNestedSteps(Pages pages) {
        super(pages);
    }

    @Pending
    @Step
    public void stepThatSucceeds() {
    }

    @Pending
    @Step
    public void anotherStepThatSucceeds() {
    }

    @Pending
    @Step
    public void stepThree(String option) {
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

    @Step
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
    public void groupOfSteps() {
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
    public void step1() {
    }

    @Step
    public void step2() {
    }

}
