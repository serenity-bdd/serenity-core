package net.thucydides.samples;

import net.thucydides.core.annotations.TestsRequirement;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(SerenityRunner.class)
public class SamplePassingScenarioWithFieldsInParent extends SampleParentScenario{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SamplePassingScenarioWithFieldsInParent.class);

    @Test
    @TestsRequirement("ABC")
    public void happy_day_scenario() throws Throwable {
        getSteps().stepThatSucceeds();
        getSteps().stepThatIsIgnored();
        getSteps().stepThatIsPending();
        getSteps().anotherStepThatSucceeds();
    }

    @Test
    @TestsRequirement("DEF")
    public void edge_case_1() {
        getSteps().stepThatSucceeds();
        getSteps().anotherStepThatSucceeds();
        getSteps().stepThatIsPending();
    }

    @Test
    public void edge_case_2() {
        getSteps().stepThatSucceeds();
        getSteps().anotherStepThatSucceeds();
    }
}
