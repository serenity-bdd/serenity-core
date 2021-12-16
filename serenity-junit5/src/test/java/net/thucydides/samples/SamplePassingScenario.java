package net.thucydides.samples;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTagValuesOf;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

@WithTag("module:M1")
@ExtendWith(SerenityJUnit5Extension.class)
public class SamplePassingScenario {
    
    @Managed(driver = "firefox",options = "headless")
    public WebDriver webdriver;

    @Steps
    public SampleScenarioSteps steps;

    @Test
    @WithTagValuesOf({"story:simple scenario", "iteration:I1"})
    public void happy_day_scenario() throws Throwable {
        steps.stepThatSucceeds();
        steps.stepThatOpensWikipedia();
//        steps.stepThatIsIgnored();
//        steps.stepThatIsPending();
//        steps.anotherStepThatSucceeds();
    }

    @Test
    @WithTagValuesOf({"story:simple scenario", "iteration:I1"})
    public void edge_case_1() {
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
        steps.stepThatIsPending();
    }

    @Test
    @WithTagValuesOf({"story:simple scenario", "iteration:I2"})
    public void edge_case_2() {
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
    }
}
