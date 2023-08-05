package net.thucydides.samples;

import net.serenitybdd.annotations.Pending;
import net.thucydides.core.annotations.Steps;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(SerenityRunner.class)
public class SamplePassingNonWebScenarioWithPendingTests {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SamplePassingNonWebScenarioWithPendingTests.class);

    @Steps
    public SampleNonWebSteps steps;

    @Test
    public void happy_day_scenario() throws Throwable {
        steps.stepThatSucceeds();
//        steps.stepThatIsIgnored();
        steps.anotherStepThatSucceeds();
    }

    @Pending @Test
    public void edge_case_1() {
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
    }

    @Pending @Test
    public void edge_case_2() {
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
    }
}
