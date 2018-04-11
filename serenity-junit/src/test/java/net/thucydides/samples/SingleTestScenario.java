package net.thucydides.samples;

import net.serenitybdd.junit.runners.*;
import net.thucydides.core.annotations.*;
import net.thucydides.core.pages.*;
import org.junit.*;
import org.junit.runner.*;
import org.openqa.selenium.*;

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