package net.thucydides.junit.integration;

import net.thucydides.core.annotations.*;
import net.thucydides.core.pages.Pages;
import net.thucydides.junit.runners.ThucydidesRunner;
import net.thucydides.samples.DemoSiteSteps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * This is a very simple scenario of testing a single page.
 * @author johnsmart
 *
 */
@RunWith(ThucydidesRunner.class)
public class WhenOpeningStaticDemoPageWithDifferentDrivers {

    @Managed(driver = "htmlunit")
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "classpath:static-site/index.html")
    public Pages pages;
    
    @Steps
    public DemoSiteSteps steps;
        
    @Test
    @Title("The user opens the index page")
    public void the_user_opens_the_page() {
        steps.should_display("A visible title");
    }    
    
    @Test
    @Title("The user selects a value")
    @WithDriver("htmlunit")
    public void the_user_selects_a_value() {
        steps.enter_values("Label 2", true);
        steps.onSamePage(DemoSiteSteps.class).should_have_selected_value("2");
    }

    @Test
    @Title("The user selects a value")
    @WithDriver("phantomjs")
    public void the_user_selects_a_value_with_phantomjs() {
        steps.enter_values("Label 2", true);
        steps.onSamePage(DemoSiteSteps.class).should_have_selected_value("2");
    }

    @Test
    @Title("The user enters different values.")
    public void the_user_opens_another_page() {
        steps.enter_values("Label 3", true);
        steps.onSamePage(DemoSiteSteps.class).do_something();
        steps.onSamePage(DemoSiteSteps.class).should_have_selected_value("3");
    }
}
