package net.thucydides.samples;

import net.serenitybdd.annotations.Steps;
import net.serenitybdd.core.steps.Instrumented;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SerenityJUnit5Extension.class)
public class SamplePassingNonWebScenario {
    
    @Steps
    public SampleNonWebSteps steps;

    public SamplePassingNonWebScenario() {
        this.steps = Instrumented.instanceOf(SampleNonWebSteps.class).newInstance();
    }

    @Test
    public void happy_day_scenario() throws Throwable {
        steps.stepThatSucceeds();
        steps.stepThatIsIgnored();
        steps.stepThatIsPending();
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
