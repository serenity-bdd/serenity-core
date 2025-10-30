package net.serenitybdd.junit5.extensions;

import net.serenitybdd.annotations.Managed;
import net.serenitybdd.annotations.ManagedPages;
import net.serenitybdd.annotations.Steps;
import net.thucydides.core.pages.Pages;
import net.thucydides.samples.IndexPage;
import net.thucydides.samples.SampleScenarioSteps;
import org.openqa.selenium.WebDriver;


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
