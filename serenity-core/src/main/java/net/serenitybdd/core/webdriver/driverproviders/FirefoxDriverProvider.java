package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.di.SerenityInfrastructure;
import net.serenitybdd.core.webdriver.FirefoxOptionsEnhancer;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.TestContext;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.capabilities.W3CCapabilities;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;

public class FirefoxDriverProvider extends DownloadableDriverProvider implements DriverProvider {

    private final DriverCapabilityRecord driverProperties;

    private final FixtureProviderService fixtureProviderService;

    public FirefoxDriverProvider(FixtureProviderService fixtureProviderService) {
        this.fixtureProviderService = fixtureProviderService;
        this.driverProperties = SerenityInfrastructure.getDriverCapabilityRecord();
    }

    @Override
    public WebDriver newInstance(String options, EnvironmentVariables environmentVariables) {
        // If webdriver calls are suspended no need to create a new driver
        if (StepEventBus.getParallelEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }
        // Download the driver using WebDriverManager if required
//        downloadDriverIfRequired("firefox", environmentVariables);
        //
        // Update the binary path if necessary
        //
        UpdateDriverEnvironmentProperty.forDriverProperty(GeckoDriverService.GECKO_DRIVER_EXE_PROPERTY);
        //
        // Load the FirefoxDriver capabilities from the serenity.conf file
        //
        FirefoxOptions firefoxOptions = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").firefoxOptions();
        FirefoxOptionsEnhancer.enhanceOptions(firefoxOptions).using(environmentVariables);
        //
        // Add any arguments passed from the test itself
        //
        firefoxOptions.addArguments(argumentsIn(options));
        if (ThucydidesSystemProperty.HEADLESS_MODE.booleanFrom(environmentVariables)) {
            firefoxOptions.addArguments("-headless");
        }
        //
        // Check for extended classes to add extra ChromeOptions configuration
        //
        final FirefoxOptions enhancedOptions = EnhanceCapabilitiesWithFixtures.using(fixtureProviderService).into(firefoxOptions);
        //
        // Record browser and platform
        //
        TestContext.forTheCurrentTest().recordBrowserConfiguration(enhancedOptions);
        TestContext.forTheCurrentTest().recordCurrentPlatform();

        //
        // Record the driver capabilities for reporting
        //
        driverProperties.registerCapabilities("firefox", capabilitiesToProperties(enhancedOptions));

        return new FirefoxDriver(enhancedOptions);
    }
}
