package net.serenitybdd.core.webdriver.driverproviders;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.di.WebDriverInjectors;
import net.serenitybdd.core.webdriver.appium.AppiumDevicePool;
import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.*;
import net.thucydides.core.webdriver.appium.AppiumConfiguration;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.MutableCapabilities;

import java.net.URL;

import static net.thucydides.core.webdriver.SupportedWebDriver.ANDROID;
import static net.thucydides.core.webdriver.SupportedWebDriver.IPHONE;

public class AppiumDriverProvider implements DriverProvider {

    private final DriverCapabilityRecord driverProperties;

    private final FixtureProviderService fixtureProviderService;

    public AppiumDriverProvider(FixtureProviderService fixtureProviderService) {
        this.fixtureProviderService = fixtureProviderService;
        this.driverProperties = WebDriverInjectors.getInjector().getInstance(DriverCapabilityRecord.class);
    }

    @Override
    public WebDriver newInstance(String options, EnvironmentVariables environmentVariables) {

        EnvironmentVariables testEnvironmentVariables = environmentVariables.copy();
        String deviceName = AppiumDevicePool.instance().requestDevice();
        testEnvironmentVariables.setProperty("deviceName", deviceName);

        CapabilityEnhancer enhancer = new CapabilityEnhancer(environmentVariables, fixtureProviderService);

        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }
        switch (appiumTargetPlatform(environmentVariables)) {
            case ANDROID:
                AndroidDriver androidDriver = new AndroidDriver(appiumUrl(testEnvironmentVariables), enhancer.enhanced(appiumCapabilities(options,testEnvironmentVariables), ANDROID) );
                driverProperties.registerCapabilities("appium", capabilitiesToProperties(androidDriver.getCapabilities()));
                WebDriverInstanceEvents.bus().register(listenerFor(androidDriver, deviceName));
                return androidDriver;
            case IOS:
                IOSDriver iosDriver = new IOSDriver(appiumUrl(testEnvironmentVariables), enhancer.enhanced(appiumCapabilities(options,testEnvironmentVariables), IPHONE));
                driverProperties.registerCapabilities("appium", capabilitiesToProperties(iosDriver.getCapabilities()));
                WebDriverInstanceEvents.bus().register(listenerFor(iosDriver, deviceName));
                return iosDriver;
        }
        throw new UnsupportedDriverException(appiumTargetPlatform(environmentVariables).name());

    }

    private MutableCapabilities appiumCapabilities(String options, EnvironmentVariables environmentVariables) {
        return AppiumConfiguration.from(environmentVariables).getCapabilities(options);
    }

    private MobilePlatform appiumTargetPlatform(EnvironmentVariables environmentVariables) {
        return AppiumConfiguration.from(environmentVariables).getTargetPlatform();
    }

    private URL appiumUrl(EnvironmentVariables environmentVariables) {
        return AppiumConfiguration.from(environmentVariables).getUrl();
    }

    private WebDriverInstanceEventListener listenerFor(WebDriver driver, String deviceName) {
        return new AppiumEventListener(driver, deviceName);
    }

    private static class AppiumEventListener implements WebDriverInstanceEventListener {
        private final String deviceName;
        private WebDriver appiumDriver;
        private AppiumDevicePool devicePool;

        private AppiumEventListener(WebDriver appiumDriver, String deviceName, AppiumDevicePool devicePool) {
            this.appiumDriver = appiumDriver;
            this.deviceName = deviceName;
            this.devicePool = devicePool;
        }

        private AppiumEventListener(WebDriver appiumDriver, String deviceName) {
            this(appiumDriver, deviceName, AppiumDevicePool.instance());
        }

        @Override
        public void close(WebDriver driver) {
            quit(driver);
        }

        @Override
        public void quit(WebDriver driver) {
            if (appiumDriver == driver) {
                devicePool.freeDevice(deviceName);
                appiumDriver = null;
            }
        }

    }
}
