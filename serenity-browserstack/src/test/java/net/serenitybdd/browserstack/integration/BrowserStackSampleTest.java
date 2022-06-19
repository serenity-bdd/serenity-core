package net.serenitybdd.browserstack.integration;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.util.EnvironmentVariables;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Sample Browserstack interactions
 * This test needs the BROWSERSTACK_USER and BROWSERSTACK_KEY environment variables to be defined.
 */
@RunWith(SerenityRunner.class)
public class BrowserStackSampleTest {

    EnvironmentVariables environmentVariables;

    @Managed
    WebDriver driver;

    String browserstackUser;
    String browserstackKey;

    @Before
    public void fetchBrowserstackUsernameAndKey() {
        browserstackUser = environmentVariables.getValue("BROWSERSTACK_USER");
        browserstackKey = environmentVariables.getValue("BROWSERSTACK_KEY");

        Assume.assumeThat("BROWSERSTACK_USER must be defined", browserstackUser, notNullValue());
        Assume.assumeThat("BROWSERSTACK_KEY must be defined", browserstackKey, notNullValue());
    }

    @Test
//    @EnvironmentVariable("webdriver.driver","foo")
    public void shouldConnectToBrowserStack() {
//        driver.get("https://duckduckgo.com/");
//
//        assertThat(driver.getTitle()).contains("DuckDuckGo");
    }

    @Test
    public void shouldReportBrowserStackTests() {
//        driver.get("https://duckduckgo.com/");
//
//        assertThat(driver.getTitle()).contains("DuckDuckGo");
    }

}
