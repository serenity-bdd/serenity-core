package net.thucydides.samples;

import net.serenitybdd.junit5.SerenityBDD;
import net.serenitybdd.junit5.StepsInjectorTestInstancePostProcessor;
import net.thucydides.core.annotations.Steps;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;

@ExtendWith(StepsInjectorTestInstancePostProcessor.class)
@SerenityBDD
public class SampleScenarioWithFailingAssumption {
    
    @Steps
    public SampleNonWebSteps steps;

    @Test
    public void happy_day_scenario() throws Throwable {
        steps.stepWithFailingAssumption();
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
    }

    @Test
    public void edge_case_1() {
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
        steps.stepThatIsPending();
    }

    @Test
    public void edge_case_2() {
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
    }
}
