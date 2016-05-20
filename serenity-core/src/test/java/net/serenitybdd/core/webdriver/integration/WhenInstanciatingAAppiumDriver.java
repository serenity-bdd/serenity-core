package net.serenitybdd.core.webdriver.integration;

import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.UnsupportedDriverException;
import net.thucydides.core.webdriver.WebDriverFactory;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenInstanciatingAAppiumDriver {

    private EnvironmentVariables environmentVariables;

    @Before
    public void createATestableDriverFactory() throws Exception {
        environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("serenity.timeout", "1000");
    }

    @Test
    public void should_verify_appium_config_params() {
        try {
            new WebDriverFactory(environmentVariables).newInstanceOf(SupportedWebDriver.APPIUM);
        } catch (UnsupportedDriverException couldNotFindDriver) {
            assertThat(couldNotFindDriver.getCause().getMessage()).contains("The browser under test or path to the app needs to be provided in the appium.app or appium.browserName property.");
        }
    }

    @Test
    public void should_verify_appium_platform() {
        try {
            environmentVariables.setProperty("appium.browserName", "abc");
            new WebDriverFactory(environmentVariables).newInstanceOf(SupportedWebDriver.APPIUM);
        } catch (UnsupportedDriverException couldNotFindDriver) {
            assertThat(couldNotFindDriver.getCause().getMessage())
                    .contains("The appium.platformName needs to be specified (either IOS or ANDROID)");
        }
    }

    @Test(expected = UnsupportedDriverException.class)
    public void should_start_mobile_browser_on_appium() {
        environmentVariables.setProperty("appium.browserName", "Browser");
        environmentVariables.setProperty("appium.platformName", "Android");
        environmentVariables.setProperty("appium.deviceName", "emulator-5554");
        new WebDriverFactory(environmentVariables).newInstanceOf(SupportedWebDriver.APPIUM);
    }

    @Test(expected = UnsupportedDriverException.class)
    public void should_ignoreWhenInstanciatingAAppiumDriver_browser_dimensions_on_appium() {
        environmentVariables.setProperty("appium.browserName", "Browser");
        environmentVariables.setProperty("appium.platformName", "Android");
        environmentVariables.setProperty("appium.deviceName", "emulator-5554");
        environmentVariables.setProperty("serenity.browser.width", "1280");
        environmentVariables.setProperty("serenity.browser.height", "1024");
        new WebDriverFactory(environmentVariables).newInstanceOf(SupportedWebDriver.APPIUM);
    }

}
