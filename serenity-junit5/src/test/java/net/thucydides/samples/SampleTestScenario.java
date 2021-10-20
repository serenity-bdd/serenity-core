package net.thucydides.samples;

import net.serenitybdd.junit5.StepsInjectorTestInstancePostProcessor;
import net.thucydides.core.annotations.Steps;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(StepsInjectorTestInstancePostProcessor.class)
public class SampleTestScenario {
    
    @Steps
    public SampleNonWebSteps steps;
        
    @Test
    public void happy_day_scenario() {
        steps.stepThatSucceeds();
        steps.stepThatIsIgnored();
        steps.stepThatIsPending();
        steps.anotherStepThatSucceeds();
        steps.stepThatFails();
        steps.stepThatSucceeds();
    }

    @Test
    public void failing_scenario() {
        steps.stepThatFails();
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
    }
}
