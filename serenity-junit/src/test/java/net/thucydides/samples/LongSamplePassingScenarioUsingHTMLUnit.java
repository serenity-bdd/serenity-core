package net.thucydides.samples;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.pages.Pages;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(SerenityRunner.class)
public class LongSamplePassingScenarioUsingHTMLUnit {

    @Managed(uniqueSession = true, driver="htmlunit")
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "http://www.wikipedia.org")
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
