package net.thucydides.samples;

import net.serenitybdd.annotations.Managed;
import net.serenitybdd.annotations.ManagedPages;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.pages.Pages;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(SerenityRunner.class)
public class SampleScenarioWithoutSteps {
    
    @Managed
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "classpath:static-site/index.html")
    public Pages pages;

    @Test
    public void happy_day_scenario() {
    }
    
    @Test
    public void edge_case_1() {
    }
    
    @Test
    public void edge_case_2() {
    }
}
