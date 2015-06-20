package net.thucydides.samples;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import net.thucydides.junit.runners.ThucydidesRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SerenityRunner.class)
public class NonWebTestScenarioWithParameterizedSteps {
    
    @Steps
    public SampleNonWebSteps steps;
        
    @Test
    public void happy_day_scenario() {
        steps.stepWithAParameter("proportionOf");
        steps.stepWithTwoParameters("proportionOf", 2);
        steps.stepThatSucceeds();
        steps.stepThatIsIgnored();
        steps.stepThatIsPending();
        steps.anotherStepThatSucceeds();
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_correct_exception() {
        steps.throw_exception();
    }

}
