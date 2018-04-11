package net.serenitybdd.core.webdriver.driverproviders;

import io.appium.java_client.android.*;
import io.appium.java_client.ios.*;
import net.serenitybdd.core.buildinfo.*;
import net.serenitybdd.core.di.*;
import net.thucydides.core.fixtureservices.*;
import net.thucydides.core.steps.*;
import net.thucydides.core.util.*;
import net.thucydides.core.webdriver.*;
import net.thucydides.core.webdriver.appium.*;
import net.thucydides.core.webdriver.stubs.*;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.*;

import java.net.*;

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
                AndroidDriver androidDriver = new AndroidDriver(appiumUrl(environmentVariables), enhancer.enhanced(appiumCapabilities(options,environmentVariables)) );
                driverProperties.registerCapabilities("appium", capabilitiesToProperties(androidDriver.getCapabilities()));
                return androidDriver;
            case IOS:
                IOSDriver iosDriver = new IOSDriver(appiumUrl(environmentVariables), enhancer.enhanced(appiumCapabilities(options,environmentVariables)));
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
