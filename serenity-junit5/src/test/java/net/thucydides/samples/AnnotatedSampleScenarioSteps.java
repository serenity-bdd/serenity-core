package net.thucydides.samples;

import net.serenitybdd.annotations.Pending;
import net.serenitybdd.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import org.junit.Ignore;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class AnnotatedSampleScenarioSteps extends ScenarioSteps {
    
    public AnnotatedSampleScenarioSteps(Pages pages) {
        super(pages);
    }

    @Step("A step that succeeds indeed!")
    public void stepThatSucceeds() {
    }

    public static enum Color {
        RED, BLUE, DARK_GREEN
    }

    @Step("A step that has a parameter of {0} and stuff")
    public void stepWithParameter(Color color) {
    }

    @Step
    public void anotherStepThatSucceeds() {
    }

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
    
}
