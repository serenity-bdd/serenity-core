package net.thucydides.samples;

import net.serenitybdd.junit.runners.SerenityRunner;
import serenitycore.net.thucydides.core.annotations.Managed;
import serenitycore.net.thucydides.core.annotations.ManagedPages;
import serenitycore.net.thucydides.core.annotations.Steps;
import serenitycore.net.thucydides.core.pages.Pages;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import serenitymodel.net.thucydides.core.annotations.WithTag;
import serenitymodel.net.thucydides.core.annotations.WithTagValuesOf;

@RunWith(SerenityRunner.class)
@WithTag("module:M1")
public class SampleSerenityPassingScenario {

    @Managed
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "classpath:static-site/index.html")
    public Pages pages;

    @Steps
    public SampleScenarioSteps steps;

    @Test
    @WithTagValuesOf({"story:simple scenario", "iteration:I1"})
    public void happy_day_scenario() throws Throwable {
        steps.stepThatSucceeds();
        steps.stepThatIsIgnored();
        steps.stepThatIsPending();
        steps.anotherStepThatSucceeds();
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
