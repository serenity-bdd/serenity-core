package net.thucydides.samples;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.annotations.Pending;
import net.thucydides.core.annotations.Steps;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(SerenityJUnit5Extension.class)
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
