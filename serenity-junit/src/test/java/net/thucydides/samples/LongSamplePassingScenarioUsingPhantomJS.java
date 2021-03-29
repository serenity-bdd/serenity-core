package net.thucydides.samples;

import net.serenitybdd.junit.runners.SerenityRunner;
import serenitycore.net.thucydides.core.annotations.Managed;
import serenitycore.net.thucydides.core.annotations.ManagedPages;
import serenitycore.net.thucydides.core.annotations.Steps;
import serenitycore.net.thucydides.core.pages.Pages;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(SerenityRunner.class)
public class LongSamplePassingScenarioUsingPhantomJS {

    @Managed(uniqueSession = true, driver="phantomjs")
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
