package net.thucydides.samples;

import net.thucydides.core.annotations.Steps;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SerenityRunner.class)
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
