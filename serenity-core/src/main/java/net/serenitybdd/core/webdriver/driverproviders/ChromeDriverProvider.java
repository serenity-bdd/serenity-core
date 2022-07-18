package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.di.WebDriverInjectors;
import net.serenitybdd.core.webdriver.servicepools.ChromeServicePool;
import net.serenitybdd.core.webdriver.servicepools.DriverServicePool;
import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.capabilities.W3CCapabilities;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.service.DriverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create a new instance of a ChromeDriver driver, using the configuration options defined in serenity.conf
 * and/or in the custom fixture classes.
 */
public class ChromeDriverProvider extends DownloadableDriverProvider implements DriverProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChromeDriverProvider.class);

    private final DriverCapabilityRecord driverProperties;

    private final FixtureProviderService fixtureProviderService;

    public ChromeDriverProvider(FixtureProviderService fixtureProviderService) {
        this.fixtureProviderService = fixtureProviderService;
        this.driverProperties = WebDriverInjectors.getInjector().getInstance(DriverCapabilityRecord.class);
    }

    @Override
    public WebDriver newInstance(String options, EnvironmentVariables environmentVariables) {
        // If webdriver calls are suspended no need to create a new driver
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }
        // Download the driver using WebDriverManager if required
        downloadDriverIfRequired("chrome", environmentVariables);
        //
        // Load the ChromeDriver capabilities from the serenity.conf file
        //
        ChromeOptions chromeOptions = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").chromeOptions();
        //
        // Check for extended classes to add extra ChromeOptions configuration
        //
        ChromeOptions enhancedOptions = ConfigureChromiumOptions.from(environmentVariables).into(chromeOptions);
        EnhanceCapabilitiesWithFixtures.using(fixtureProviderService).into(enhancedOptions);

        //
        // Find the DriverService for the base config options
        //
        ChromeDriverService driverService = DriverServices.getChromeDriverService(enhancedOptions);

        //
        // Add any arguments passed from the test itself
        //
        enhancedOptions.addArguments(argumentsIn(options));
        //
        // Record the driver capabilities for reporting
        //
        driverProperties.registerCapabilities("chrome", capabilitiesToProperties(enhancedOptions));

        LOGGER.info("Starting Chrome driver instance with capabilities:");
        LOGGER.info(enhancedOptions.toString());

        return new ChromeDriver(driverService, enhancedOptions);
    }

}
