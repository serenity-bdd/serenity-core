package net.thucydides.samples;

import net.serenitybdd.annotations.Managed;
import net.serenitybdd.annotations.ManagedPages;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.pages.Pages;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * This is a very simple scenario of testing a single page.
 * @author johnsmart
 *
 */
@RunWith(SerenityRunner.class)
public class MockOpenStaticDemoPageWithFailureSample {

    @Managed(uniqueSession=true, driver = "chrome", options = "--headless")
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "classpath:static-site/index.html")
    public Pages pages;
    
    @Steps
    public MockDemoSiteSteps steps;
        
    @Test
    public void happy_day_scenario() {
        steps.enter_values("Label 1", true);
        steps.should_have_selected_value("Label 2");
        steps.do_something();
    }    
    
    @Test
    public void edge_case_1() {
        steps.enter_values("Label 2", true);
        steps.do_something_else();
    }

    @Test
    public void edge_case_2() {
        steps.enter_values("Label 3", true);
        steps.do_something_else();
    }
}
