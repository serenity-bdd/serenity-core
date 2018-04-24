package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.di.WebDriverInjectors;
import net.serenitybdd.core.webdriver.FirefoxOptionsEnhancer;
import net.serenitybdd.core.webdriver.servicepools.DriverServicePool;
import net.serenitybdd.core.webdriver.servicepools.GeckoServicePool;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityEnhancer;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static net.thucydides.core.ThucydidesSystemProperty.USE_GECKO_DRIVER;
import static net.thucydides.core.ThucydidesSystemProperty.WEBDRIVER_GECKO_DRIVER;

public class FirefoxDriverProvider implements DriverProvider {

    private final DriverCapabilityRecord driverProperties;

    private static final Logger LOGGER = LoggerFactory.getLogger(FirefoxDriverProvider.class);

    private final DriverServicePool driverServicePool = new GeckoServicePool();

    private DriverServicePool getDriverServicePool() throws IOException {
        driverServicePool.ensureServiceIsRunning();
        return driverServicePool;
    }

    protected String serviceName(){ return "firefox"; }

    private final FixtureProviderService fixtureProviderService;

    public FirefoxDriverProvider(FixtureProviderService fixtureProviderService) {
        this.fixtureProviderService = fixtureProviderService;
        this.driverProperties = WebDriverInjectors.getInjector().getInstance(DriverCapabilityRecord.class);
    }

    @Override
    public WebDriver newInstance(String options, EnvironmentVariables environmentVariables) {
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }
        DesiredCapabilities capabilities = new FirefoxDriverCapabilities(environmentVariables).getCapabilities();

        WebDriver driver =  (shouldUseGeckoDriver(environmentVariables)) ?
                newMarionetteDriver(capabilities,environmentVariables) :
                newFirefoxDriver(capabilities,environmentVariables);

        driverProperties.registerCapabilities("firefox", capabilitiesToProperties(capabilities));

        return driver;
    }

    private boolean shouldUseGeckoDriver(EnvironmentVariables environmentVariables) {
        return (geckoDriverIsInEnvironmentVariable(environmentVariables)
                || geckoDriverIsOnTheClasspath()) && geckoIsNotDisabled(environmentVariables);
    }

    private boolean geckoIsNotDisabled(EnvironmentVariables environmentVariables) {
        return USE_GECKO_DRIVER.booleanFrom(environmentVariables, true);
    }

    private WebDriver newFirefoxDriver(DesiredCapabilities capabilities, EnvironmentVariables environmentVariables) {
        capabilities.setCapability("marionette", false);

        CapabilityEnhancer enhancer = new CapabilityEnhancer(environmentVariables, fixtureProviderService);

        FirefoxOptions options = new FirefoxOptions(enhancer.enhanced(capabilities, SupportedWebDriver.FIREFOX));

        FirefoxOptionsEnhancer.enhanceOptions(options).using(environmentVariables);

        return new FirefoxDriver(options);
    }

    private WebDriver newMarionetteDriver(DesiredCapabilities capabilities, EnvironmentVariables environmentVariables) {
        capabilities.setCapability("marionette", true);
        capabilities.setCapability("headless", ThucydidesSystemProperty.HEADLESS_MODE.booleanFrom(environmentVariables, false));
        CapabilityEnhancer enhancer = new CapabilityEnhancer(environmentVariables, fixtureProviderService);

        try {
            return getDriverServicePool().newDriver(enhancer.enhanced(capabilities, SupportedWebDriver.FIREFOX));
        } catch (IOException couldNotStartGeckoDriverService) {
            LOGGER.warn("Failed to start the gecko driver service, using a native driver instead",  couldNotStartGeckoDriverService.getMessage());
            return newFirefoxDriver(capabilities, environmentVariables);
        }
    }

    private boolean geckoDriverIsOnTheClasspath() {
        try {
            Runtime.getRuntime().exec("geckodriver --help");
            return true;
        } catch (Exception geckodriverBinaryNotFound) {
            try {
                Runtime.getRuntime().exec("wires --help");
                return true;
            } catch (Exception wiresBinaryNotFound) {
                return false;
            }
        }
    }

    private boolean geckoDriverIsInEnvironmentVariable(EnvironmentVariables environmentVariables) {
        try {
            new ProcessBuilder().command(WEBDRIVER_GECKO_DRIVER.from(environmentVariables), "--help").start();
            return true;
        } catch (Exception geckodriverBinaryNotFound) {
            return false;
        }
    }

}
