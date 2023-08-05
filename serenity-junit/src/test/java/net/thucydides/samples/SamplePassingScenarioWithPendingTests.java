package net.thucydides.samples;

import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.ManagedPages;
import net.serenitybdd.annotations.Pending;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.pages.Pages;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(SerenityRunner.class)
public class SamplePassingScenarioWithPendingTests {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SamplePassingScenarioWithPendingTests.class);

    @Managed
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "classpath:static-site/index.html")
    public Pages pages;
    
    @Steps
    public SampleScenarioSteps steps;

    @Test
    public void happy_day_scenario() throws Throwable {
        steps.stepThatSucceeds();
//        steps.stepThatIsIgnored();
        steps.anotherStepThatSucceeds();
    }

    @Pending @Test
    public void edge_case_1() {
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
    }

    @Pending @Test
    public void edge_case_2() {
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
    }
}
