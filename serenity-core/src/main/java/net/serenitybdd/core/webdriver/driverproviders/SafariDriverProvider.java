package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.di.WebDriverInjectors;
import net.serenitybdd.core.webdriver.driverproviders.webdrivermanager.WebDriverManagerSetup;
import net.serenitybdd.core.webdriver.servicepools.DriverServicePool;
import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityEnhancer;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.capabilities.W3CCapabilities;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

public class SafariDriverProvider extends DownloadableDriverProvider implements DriverProvider {

    private final DriverCapabilityRecord driverProperties;

    private final FixtureProviderService fixtureProviderService;

    public SafariDriverProvider(FixtureProviderService fixtureProviderService) {
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
        downloadDriverIfRequired("safari", environmentVariables);
        //
        // Load the capabilities from the serenity.conf file
        //
        SafariOptions safariOptions = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").safariOptions();
        //
        // Check for extended classes to add extra ChromeOptions configuration

        final SafariOptions enhancedOptions = EnhanceCapabilitiesWithFixtures.using(fixtureProviderService).into(safariOptions);
        //
        // Record the driver capabilities for reporting
        //
        driverProperties.registerCapabilities("safari", capabilitiesToProperties(enhancedOptions));

        return new SafariDriver(safariOptions);
    }
}
