package net.thucydides.samples;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.annotations.Steps;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SerenityJUnit5Extension.class)
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

    @Test
    public void should_throw_correct_exception() {
        assertThrows(IllegalArgumentException.class,()-> steps.throw_exception());
    }

    @Test
    public void should_throw_element_not_found() {
        assertThrows(NoSuchElementException.class,()-> steps.throw_element_not_found_exception());

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
