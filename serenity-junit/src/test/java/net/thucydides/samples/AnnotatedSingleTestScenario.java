package net.thucydides.samples;

import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.annotations.Title;
import net.thucydides.core.pages.Pages;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(SerenityRunner.class)
public class AnnotatedSingleTestScenario {
    
    @Managed(driver = "chrome", options="--headless")
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "classpath:static-site/index.html")
    public Pages pages;
    
    @Steps
    public AnnotatedSampleScenarioSteps steps;
        
    @Test
    @Title("Oh happy days!")
    public void happy_day_scenario() {
        steps.stepThatSucceeds();
        steps.stepWithParameter(AnnotatedSampleScenarioSteps.Color.DARK_GREEN);
        steps.stepThatIsIgnored();
        steps.stepThatIsPending();
        steps.anotherStepThatSucceeds();
    }

}
