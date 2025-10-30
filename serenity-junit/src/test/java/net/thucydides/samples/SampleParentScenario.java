package net.thucydides.samples;

import net.serenitybdd.annotations.Managed;
import net.serenitybdd.annotations.ManagedPages;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.pages.Pages;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(SerenityRunner.class)
public class SampleParentScenario {
    
    @Managed
    private WebDriver webdriver;

    @ManagedPages(defaultUrl = "classpath:static-site/index.html")
    private Pages pages;
    
    @Steps
    private SampleScenarioSteps steps;

    protected SampleScenarioSteps getSteps() {
        return steps;
    }

}
