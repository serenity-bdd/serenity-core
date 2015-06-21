package net.thucydides.samples;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
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

    @Test
    public void should_handle_nested_object_parameters() {
        steps.a_customized_step_with_object_parameters(new SampleNonWebSteps.CurrencyIn$(100));
    }


}
