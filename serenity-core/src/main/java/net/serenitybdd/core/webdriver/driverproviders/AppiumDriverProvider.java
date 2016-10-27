package net.serenitybdd.core.webdriver.driverproviders;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.thucydides.core.guice.Injectors;
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

public class AppiumDriverProvider implements DriverProvider {

    private final EnvironmentVariables environmentVariables;
    private final CapabilityEnhancer enhancer;
    private final DriverCapabilityRecord driverProperties;

    public AppiumDriverProvider(EnvironmentVariables environmentVariables, CapabilityEnhancer enhancer) {
        this.environmentVariables = environmentVariables;
        this.enhancer = enhancer;
        this.driverProperties = Injectors.getInjector().getInstance(DriverCapabilityRecord.class);
    }

    @Override
    public WebDriver newInstance() {
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }
        switch (appiumTargetPlatform()) {
            case ANDROID:
                AndroidDriver androidDriver = new AndroidDriver(appiumUrl(), enhancer.enhanced(appiumCapabilities()) );
                driverProperties.registerCapabilities("appium", androidDriver.getCapabilities());
                return androidDriver;
            case IOS:
                IOSDriver iosDriver = new IOSDriver(appiumUrl(), enhancer.enhanced(appiumCapabilities()));
                driverProperties.registerCapabilities("appium", iosDriver.getCapabilities());
                return iosDriver;
        }
        throw new UnsupportedDriverException(appiumTargetPlatform().name());

    }

    private DesiredCapabilities appiumCapabilities() {
        return AppiumConfiguration.from(environmentVariables).getCapabilities();
    }

    private MobilePlatform appiumTargetPlatform() {
        return AppiumConfiguration.from(environmentVariables).getTargetPlatform();
    }

    private URL appiumUrl() {
        return AppiumConfiguration.from(environmentVariables).getUrl();
    }

}
