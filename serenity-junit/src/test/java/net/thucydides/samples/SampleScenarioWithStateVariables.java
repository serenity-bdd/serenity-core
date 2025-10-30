package net.thucydides.samples;

import net.serenitybdd.annotations.Managed;
import net.serenitybdd.annotations.ManagedPages;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.pages.Pages;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SerenityRunner.class)
public class SampleScenarioWithStateVariables {
    
    @Managed
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "classpath:static-site/index.html")
    public Pages pages;
    
    @Steps
    public SampleScenarioSteps steps;

    @Test
    public void joes_test() throws Throwable {
        steps.store_name("joe");
        assertThat(steps.get_name(), is("joe"));
    }

    @Test
    public void jills_test() throws Throwable {
        steps.store_name("jill");
        assertThat(steps.get_name(), is("jill"));
    }

    @Test
    public void no_ones_test() throws Throwable {
        assertThat(steps.hasName(), is(false));
    }
}
