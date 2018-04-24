package net.serenitybdd.core.webdriver.driverproviders;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.di.WebDriverInjectors;
import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityEnhancer;
import net.thucydides.core.webdriver.MobilePlatform;
import net.thucydides.core.webdriver.UnsupportedDriverException;
import net.thucydides.core.webdriver.appium.AppiumConfiguration;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

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
        CapabilityEnhancer enhancer = new CapabilityEnhancer(environmentVariables, fixtureProviderService);

        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }
        switch (appiumTargetPlatform(environmentVariables)) {
            case ANDROID:
                AndroidDriver androidDriver = new AndroidDriver(appiumUrl(environmentVariables), enhancer.enhanced(appiumCapabilities(options,environmentVariables), ANDROID) );
                driverProperties.registerCapabilities("appium", capabilitiesToProperties(androidDriver.getCapabilities()));
                return androidDriver;
            case IOS:
                IOSDriver iosDriver = new IOSDriver(appiumUrl(environmentVariables), enhancer.enhanced(appiumCapabilities(options,environmentVariables), IPHONE));
                driverProperties.registerCapabilities("appium", capabilitiesToProperties(iosDriver.getCapabilities()));
                return iosDriver;
        }
        throw new UnsupportedDriverException(appiumTargetPlatform(environmentVariables).name());

    }

    private DesiredCapabilities appiumCapabilities(String options, EnvironmentVariables environmentVariables) {
        return AppiumConfiguration.from(environmentVariables).getCapabilities(options);
    }

    private MobilePlatform appiumTargetPlatform(EnvironmentVariables environmentVariables) {
        return AppiumConfiguration.from(environmentVariables).getTargetPlatform();
    }

    private URL appiumUrl(EnvironmentVariables environmentVariables) {
        return AppiumConfiguration.from(environmentVariables).getUrl();
    }

}
