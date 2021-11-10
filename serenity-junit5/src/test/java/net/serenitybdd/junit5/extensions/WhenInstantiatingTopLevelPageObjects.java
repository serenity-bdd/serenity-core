package net.serenitybdd.junit5.extensions;

import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.pages.Pages;
import net.thucydides.samples.IndexPage;
import net.thucydides.samples.SampleScenarioSteps;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;


public class WhenInstantiatingTopLevelPageObjects {
    
    @Managed(driver = "chrome", options="--headless")
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "classpath:static-site/index.html")
    public Pages pages;
    
    @Steps
    public SampleScenarioSteps steps;

    IndexPage page;

    /*@Test //TODO clarify
    public void shouldInstantiateDeclaredPageObjects() throws Throwable {
        assertThat(page).isNotNull();
    }

    @Test
    public void shouldInstantiateNestedPageObjects() throws Throwable {
        assertThat(page).isNotNull();
        assertThat(page.nestedIndexPage).isNotNull();
    }*/

}
