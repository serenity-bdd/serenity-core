package net.thucydides.samples;

import net.serenitybdd.annotations.Managed;
import net.serenitybdd.annotations.ManagedPages;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.pages.Pages;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static org.hamcrest.core.Is.is;

@RunWith(SerenityRunner.class)
public class SampleTestWithAssumption {
 
    @Managed
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "classpath:static-site/index.html")
    public Pages pages;
    
    @Steps
    public SampleScenarioSteps steps;

    @Test
    public void call_step_with_failing_assumption() {
        steps.stepWithFailedAssumption();
    }

    @Test
    public void failing_assumption_in_test() {
        Assume.assumeThat(true, is(false));
    }
}
