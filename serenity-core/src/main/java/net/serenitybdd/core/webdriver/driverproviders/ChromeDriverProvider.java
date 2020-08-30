package net.serenitybdd.core.webdriver.driverproviders;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.di.WebDriverInjectors;
import net.serenitybdd.core.webdriver.servicepools.ChromeServicePool;
import net.serenitybdd.core.webdriver.servicepools.DriverServicePool;
import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityEnhancer;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ChromeDriverProvider implements DriverProvider {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DriverCapabilityRecord driverProperties;

    private final DriverServicePool driverServicePool = new ChromeServicePool();

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
            WebDriverManager.chromedriver().setup();
        } else {
            logger.info("Not using automatically driver download");
        }
        DesiredCapabilities enhancedCapabilities = enhancedCapabilitiesConfiguredIn(environmentVariables, options);
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

        //
        // Perform any custom configuration to the new driver
        //
        EnhanceDriver.from(environmentVariables).to(newDriver);

        return newDriver;
    }

    private DesiredCapabilities enhancedCapabilitiesConfiguredIn(EnvironmentVariables environmentVariables, String options) {
        DesiredCapabilities capabilities = chromeDriverCapabilitiesDefinedIn(environmentVariables,options).getCapabilities();
        CapabilityEnhancer enhancer = new CapabilityEnhancer(environmentVariables, fixtureProviderService);
        return enhancer.enhanced(capabilities, SupportedWebDriver.CHROME);
    }

    private ChromeDriverCapabilities chromeDriverCapabilitiesDefinedIn(EnvironmentVariables environmentVariables, String options) {
        return new ChromeDriverCapabilities(environmentVariables, options);
    }
}
