package net.thucydides.samples;

import net.serenitybdd.annotations.Steps;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SamplePassingNonWebScenarioWithIgnoredTests {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SamplePassingNonWebScenarioWithIgnoredTests.class);

    @Steps
    public SampleNonWebSteps steps;

    @Test
    public void happy_day_scenario() throws Throwable {
        steps.stepThatSucceeds();
//        steps.stepThatIsIgnored();
        steps.anotherStepThatSucceeds();
    }

    @Ignore
    @Test
    public void edge_case_1() {
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
    }

    @Ignore
    @Test
    public void edge_case_2() {
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
    }
}
