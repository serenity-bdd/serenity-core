package net.thucydides.samples;

import net.thucydides.core.annotations.Steps;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SerenityRunner.class)
public class SampleNonWebScenarioWithErrorThatEventuallyWorks {

    @Steps
    public SampleNonWebSteps steps;

    @Test
    public void happy_day_scenario() {
        steps.stepThatSucceeds();
        //steps.stepThatIsIgnored();
        steps.anotherStepThatSucceeds();
        steps.aStepThatFailsOnOddTries();
    }
}
