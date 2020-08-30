package net.serenitybdd.core.webdriver.driverproviders;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.di.WebDriverInjectors;
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

public class FirefoxDriverProvider implements DriverProvider {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DriverCapabilityRecord driverProperties;

    private final DriverServicePool driverServicePool = new GeckoServicePool();

    protected String serviceName(){ return "firefox"; }

    private final FixtureProviderService fixtureProviderService;

    public FirefoxDriverProvider(FixtureProviderService fixtureProviderService) {
        this.fixtureProviderService = fixtureProviderService;
        this.driverProperties = WebDriverInjectors.getInjector().getInstance(DriverCapabilityRecord.class);
    }

    @Override
    // TODO: Add support for runtime options
    public WebDriver newInstance(String options, EnvironmentVariables environmentVariables) {
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }

        if(isDriverAutomaticallyDownloaded(environmentVariables)) {
            logger.info("Using automatically driver download");
            WebDriverManager.firefoxdriver().setup();
        } else {
            logger.info("Not using automatically driver download");
        }

        DesiredCapabilities capabilities = new FirefoxDriverCapabilities(environmentVariables, options).getCapabilities();
        SetProxyConfiguration.from(environmentVariables).in(capabilities);
        AddLoggingPreferences.from(environmentVariables).to(capabilities);

        WebDriver driver = newMarionetteDriver(capabilities,environmentVariables);

        driverProperties.registerCapabilities("firefox", capabilitiesToProperties(capabilities));

        return driver;
    }

    private WebDriver newMarionetteDriver(DesiredCapabilities capabilities, EnvironmentVariables environmentVariables) {
        capabilities.setCapability("marionette", true);
        capabilities.setCapability("headless", ThucydidesSystemProperty.HEADLESS_MODE.booleanFrom(environmentVariables, false));
        CapabilityEnhancer enhancer = new CapabilityEnhancer(environmentVariables, fixtureProviderService);
        FirefoxOptions options = new FirefoxOptions(enhancer.enhanced(capabilities, SupportedWebDriver.FIREFOX));
        DesiredCapabilities enhancedCapabilities = enhancer.enhanced(capabilities, SupportedWebDriver.FIREFOX);

        return ProvideNewDriver.withConfiguration(environmentVariables,
                enhancedCapabilities,
                driverServicePool,
                DriverServicePool::newDriver,
                (pool, caps) -> new FirefoxDriver(options)
        );
    }
}
