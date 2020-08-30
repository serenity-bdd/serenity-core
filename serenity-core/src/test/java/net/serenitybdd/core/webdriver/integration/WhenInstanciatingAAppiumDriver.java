package net.serenitybdd.core.webdriver.integration;

import net.serenitybdd.core.webdriver.appium.AppiumDevicePool;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.DriverConfigurationError;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.WebDriverFactory;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenInstanciatingAAppiumDriver {

    private EnvironmentVariables environmentVariables;

    @Before
    public void createATestableDriverFactory(){
        environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("serenity.timeout", "1000");
        AppiumDevicePool.clear();
    }

    @Test
    public void should_verify_appium_device_name() {
        try {
            environmentVariables.setProperty("manage.appium.servers","true");
            new WebDriverFactory(environmentVariables).newInstanceOf(SupportedWebDriver.APPIUM);
        } catch (DriverConfigurationError couldNotFindDriver) {
            assertThat(couldNotFindDriver.getCause().getMessage()).contains("No available Appium device found - have you specified a device in appium.deviceName or a list of available devices in appium.deviceNames?");
        }
    }

    @Test
    public void should_verify_appium_platform_name() {
        try {
            environmentVariables.setProperty("appium.deviceName", "abc");
            new WebDriverFactory(environmentVariables).newInstanceOf(SupportedWebDriver.APPIUM);
        } catch (DriverConfigurationError couldNotFindDriver) {
            assertThat(couldNotFindDriver.getCause().getMessage())
                    .contains("The appium.platformName needs to be specified (either IOS or ANDROID)");
        }
    }

    @Test
    public void should_verify_appium_platform() {
        try {
            environmentVariables.setProperty("appium.deviceName", "abc");
            environmentVariables.setProperty("appium.browserName", "chrome");
            new WebDriverFactory(environmentVariables).newInstanceOf(SupportedWebDriver.APPIUM);
        } catch (DriverConfigurationError couldNotFindDriver) {
            assertThat(couldNotFindDriver.getCause().getMessage())
                    .contains("The appium.platformName needs to be specified (either IOS or ANDROID)");
        }
    }

//    @Test(expected = DriverConfigurationError.class)
//    public void should_start_mobile_browser_on_appium() {
//        environmentVariables.setProperty("appium.browserName", "Browser");
//        environmentVariables.setProperty("appium.platformName", "Android");
//        environmentVariables.setProperty("appium.deviceName", "emulator-5554");
//        new WebDriverFactory(environmentVariables).newInstanceOf(SupportedWebDriver.APPIUM);
//    }
//
//    @Test(expected = DriverConfigurationError.class)
//    public void should_ignoreWhenInstanciatingAAppiumDriver_browser_dimensions_on_appium() {
//        environmentVariables.setProperty("appium.browserName", "Browser");
//        environmentVariables.setProperty("appium.platformName", "Android");
//        environmentVariables.setProperty("appium.deviceName", "emulator-5554");
//        environmentVariables.setProperty("serenity.browser.width", "1280");
//        environmentVariables.setProperty("serenity.browser.height", "1024");
//        new WebDriverFactory(environmentVariables).newInstanceOf(SupportedWebDriver.APPIUM);
//    }

}
