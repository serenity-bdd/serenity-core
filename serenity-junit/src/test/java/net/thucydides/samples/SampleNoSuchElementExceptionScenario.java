package net.thucydides.samples;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.annotations.Managed;
import net.serenitybdd.annotations.ManagedPages;
import net.serenitybdd.annotations.Steps;
import net.thucydides.core.pages.Pages;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(SerenityRunner.class)
public class SampleNoSuchElementExceptionScenario {

    @Managed
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "classpath:static-site/index.html")
    public Pages pages;


    @Steps
    public SampleScenarioSteps steps;

    @Test
    public void failing_happy_day_scenario() {
        steps.stepThatSucceeds();
        steps.stepThatIsIgnored();
        steps.anotherStepThatSucceeds();
        steps.failsToFindElement();
        steps.stepThatShouldBeSkipped();
    }


    @Test
    public void successful_happy_day_scenario() {
        steps.stepThatSucceeds();
        steps.stepThatIsIgnored();
        steps.anotherStepThatSucceeds();
    }

    @Test
    public void other_test_case_1() {
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
        steps.stepThatIsPending();
    }

    @Test
    public void other_test_case_2() {
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
    }
}
