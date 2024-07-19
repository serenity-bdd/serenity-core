package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.model.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.di.SerenityInfrastructure;
import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.TestContext;
import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.core.webdriver.capabilities.W3CCapabilities;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.ie.InternetExplorerOptions;

public class InternetExplorerDriverProvider extends DownloadableDriverProvider implements DriverProvider {

    private final DriverCapabilityRecord driverProperties;

    private final FixtureProviderService fixtureProviderService;

    public InternetExplorerDriverProvider(FixtureProviderService fixtureProviderService) {
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
//        downloadDriverIfRequired("ie", environmentVariables);
        //
        // Update the binary path if necessary
        //
        UpdateDriverEnvironmentProperty.forDriverProperty(InternetExplorerDriverService.IE_DRIVER_EXE_PROPERTY);
        //
        // Load the capabilities from the serenity.conf file
        //
        InternetExplorerOptions ieOptions = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").internetExplorerOptions();
        //
        // Check for extended classes to add extra configuration

        EnhanceCapabilitiesWithFixtures.using(fixtureProviderService).into(ieOptions);
        //
        // Record browser and platform
        //
        TestContext.forTheCurrentTest().recordBrowserConfiguration(ieOptions);
        TestContext.forTheCurrentTest().recordCurrentPlatform();
        //
        // Record the driver capabilities for reporting
        //
        driverProperties.registerCapabilities("ie", capabilitiesToProperties(ieOptions));

        return new InternetExplorerDriver(ieOptions);
    }
}
