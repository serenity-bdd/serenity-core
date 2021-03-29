package net.thucydides.samples;

import net.serenitybdd.junit.runners.SerenityRunner;
import serenitycore.net.thucydides.core.annotations.Managed;
import serenitycore.net.thucydides.core.annotations.ManagedPages;
import serenitycore.net.thucydides.core.annotations.Steps;
import serenitycore.net.thucydides.core.annotations.UserStoryCode;
import serenitycore.net.thucydides.core.pages.Pages;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import serenitymodel.net.thucydides.core.annotations.TestsRequirement;

@RunWith(SerenityRunner.class)
@UserStoryCode("US01")
public class SingleTestScenario {
    
    @Managed
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "classpath:static-site/index.html")
    public Pages pages;

    @Steps
    public SampleScenarioSteps steps;
        
    @Test
    @TestsRequirement("SOME_BUSINESS_RULE")
    public void happy_day_scenario() {
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
        steps.stepThatIsIgnored();
        steps.anotherStepThatSucceeds();
        steps.stepThatFails();
        steps.stepThatShouldBeSkipped();
    }
}