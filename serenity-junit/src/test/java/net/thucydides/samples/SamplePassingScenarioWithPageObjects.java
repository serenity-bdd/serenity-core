package net.thucydides.samples;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.annotations.Managed;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class SamplePassingScenarioWithPageObjects {
    
    @Managed(driver = "chrome", options="--headless")
    public WebDriver webdriver;

    IndexPage indexPage;

    @Test
    public void happy_day_scenario() throws Throwable {
        indexPage.open();
        assertThat(indexPage.getTitle()).isNotEmpty();
    }
}
