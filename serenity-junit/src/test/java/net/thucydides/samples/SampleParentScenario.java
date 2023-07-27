package net.thucydides.samples;

import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.pages.Pages;
import net.serenitybdd.junit.runners.SerenityRunner;
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
