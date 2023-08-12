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
public class LongSamplePassingScenarioUsingChrome {

    @Managed(uniqueSession = true, driver="chrome", options = "--headless")
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "classpath:static-site/index.html")
    public Pages pages;

    @Steps
    public SampleScenarioSteps steps;

    @Test
    public void happy_day_scenario() throws Throwable {
        steps.stepThatUsesABrowser();
        steps.anotherStepThatUsesABrowser();
        steps.aStepThatAlsoUsesABrowser();
    }
}
