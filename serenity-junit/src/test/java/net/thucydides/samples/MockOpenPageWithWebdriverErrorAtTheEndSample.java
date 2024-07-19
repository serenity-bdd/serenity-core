package net.thucydides.samples;

import net.serenitybdd.annotations.Managed;
import net.serenitybdd.annotations.ManagedPages;
import net.serenitybdd.annotations.Steps;
import net.thucydides.core.pages.Pages;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * This is a very simple scenario of testing a single page.
 * @author johnsmart
 *
 */
@RunWith(SerenityRunner.class)
public class MockOpenPageWithWebdriverErrorAtTheEndSample {

    @Managed(uniqueSession=true)
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "classpath:static-site/index.html")
    public Pages pages;
    
    @Steps
    public MockDemoSiteStepsWithWebdriverError steps;
        
    @Test
    public void the_user_opens_the_page() {
        steps.enter_values("Label 1", true);
        steps.do_something();
    }
    
    @Test
    public void the_user_opens_another_page() {
        steps.enter_values("Label 2", true);
        steps.do_something_else();
    }

    @Test
    public void the_user_opens_a_third_page() {
        steps.enter_values("Label 3", true);
        steps.do_something_else();
        steps.should_have_selected_value("Label 2");
    }


}
