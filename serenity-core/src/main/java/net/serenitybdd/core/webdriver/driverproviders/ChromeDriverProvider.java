package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.di.WebDriverInjectors;
import net.serenitybdd.core.webdriver.driverproviders.webdrivermanager.WebDriverManagerSetup;
import net.serenitybdd.core.webdriver.servicepools.ChromeServicePool;
import net.serenitybdd.core.webdriver.servicepools.DriverServicePool;
import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityEnhancer;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ChromeDriverProvider implements DriverProvider {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DriverCapabilityRecord driverProperties;

    private final DriverServicePool<ChromeDriverService> driverServicePool = new ChromeServicePool();

    private final FixtureProviderService fixtureProviderService;

    public ChromeDriverProvider(FixtureProviderService fixtureProviderService) {
        this.fixtureProviderService = fixtureProviderService;
        this.driverProperties = WebDriverInjectors.getInjector().getInstance(DriverCapabilityRecord.class);
    }

    @Override
    public WebDriver newInstance(String options, EnvironmentVariables environmentVariables) {
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }

        if(isDriverAutomaticallyDownloaded(environmentVariables)) {
            logger.info("Using automatically driver download");
            WebDriverManagerSetup.usingEnvironmentVariables(environmentVariables).forChrome();
        } else {
            logger.info("Not using automatically driver download");
        }
        MutableCapabilities enhancedCapabilities = enhancedCapabilitiesConfiguredIn(environmentVariables, options);
        driverProperties.registerCapabilities("chrome", capabilitiesToProperties(enhancedCapabilities));

        ChromeOptions chromeOptions = chromeDriverCapabilitiesDefinedIn(environmentVariables,options).configuredOptions();
        enhancedCapabilities.asMap().forEach(
                chromeOptions::setCapability
        );

        //
        // Check for extended classes to add extra ChromeOptions configuration
        //
        final ChromeOptions enhancedChromeOptions = ConfigureChromeOptions.from(environmentVariables).to(chromeOptions);

        WebDriver newDriver = ProvideNewDriver.withConfiguration(environmentVariables,
                enhancedCapabilities,
                driverServicePool,
                DriverServicePool::newDriver,
                (pool, capabilities) -> new ChromeDriver(enhancedChromeOptions)
        );
        return newDriver;
    }

    private MutableCapabilities enhancedCapabilitiesConfiguredIn(EnvironmentVariables environmentVariables, String options) {
        MutableCapabilities capabilities = chromeDriverCapabilitiesDefinedIn(environmentVariables,options).getCapabilities();
        CapabilityEnhancer enhancer = new CapabilityEnhancer(environmentVariables, fixtureProviderService);
        return enhancer.enhanced(capabilities, SupportedWebDriver.CHROME);
    }

    private ChromeDriverCapabilities chromeDriverCapabilitiesDefinedIn(EnvironmentVariables environmentVariables, String options) {
        return new ChromeDriverCapabilities(environmentVariables, options);
    }
}
