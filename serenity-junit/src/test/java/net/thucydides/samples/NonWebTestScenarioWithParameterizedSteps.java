package net.thucydides.samples;

import net.serenitybdd.annotations.Steps;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.NoSuchElementException;

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

    @Test(expected = NoSuchElementException.class)
    public void should_throw_element_not_found() {
        steps.throw_element_not_found_exception();
    }


    @Test
    public void should_handle_nested_object_parameters() {
        steps.a_customized_step_with_object_parameters(new SampleNonWebSteps.CurrencyIn$(100));
    }

    @Test
    public void should_be_correct_customized_title_for_parameter_with_comma() {
        steps.a_customized_step_with_two_parameters("Joe, Smith", "20");
    }
}
