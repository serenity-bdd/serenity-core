package net.thucydides.samples;

import net.thucydides.core.annotations.Steps;
import net.thucydides.junit.runners.ThucydidesRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(ThucydidesRunner.class)
public class SampleNonWebScenarioWithErrorThatEventuallyWorks {

    static int testCount = 1;

    @Steps
    public SampleNonWebSteps steps;

    @Test
    public void happy_day_scenario() {
        steps.stepThatSucceeds();
        steps.stepThatIsIgnored();
        steps.anotherStepThatSucceeds();

        boolean shouldPass = testCount++ % 2 == 0;
        assertThat(shouldPass).isTrue();
    }
}
