package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.di.WebDriverInjectors;
import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.capabilities.W3CCapabilities;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class FirefoxDriverProvider extends DownloadableDriverProvider implements DriverProvider {

    private final DriverCapabilityRecord driverProperties;

    private final FixtureProviderService fixtureProviderService;

    public FirefoxDriverProvider(FixtureProviderService fixtureProviderService) {
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
        downloadDriverIfRequired("firefox", environmentVariables);
        //
        // Load the FirefoxDriver capabilities from the serenity.conf file
        //
        FirefoxOptions firefoxOptions = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").firefoxOptions();
        //
        // Add any arguments passed from the test itself
        //
        firefoxOptions.addArguments(argumentsIn(options));
        //
        // Check for extended classes to add extra ChromeOptions configuration
        //
        final FirefoxOptions enhancedOptions = EnhanceCapabilitiesWithFixtures.using(fixtureProviderService).into(firefoxOptions);
        //
        // Record the driver capabilities for reporting
        //
        driverProperties.registerCapabilities("firefox", capabilitiesToProperties(enhancedOptions));

        return new FirefoxDriver(enhancedOptions);
//        return ProvideNewDriver.withConfiguration(environmentVariables,
//                enhancedOptions,
//                driverServicePool,
//                DriverServicePool::newDriver,
//                (pool, capabilities) -> new FirefoxDriver(enhancedOptions)
//        );
    }
}
