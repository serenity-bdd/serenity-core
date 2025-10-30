package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.di.SerenityInfrastructure;
import net.serenitybdd.core.webdriver.FirefoxOptionsEnhancer;
import net.serenitybdd.model.buildinfo.DriverCapabilityRecord;
import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.TestContext;
import net.thucydides.core.webdriver.capabilities.W3CCapabilities;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.util.EnvironmentVariables;
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
        if (StepEventBus.getParallelEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }

        UpdateDriverEnvironmentProperty.forDriverProperty(GeckoDriverService.GECKO_DRIVER_EXE_PROPERTY);

        FirefoxOptions firefoxOptions = W3CCapabilities.definedIn(environmentVariables)
                .withPrefix("webdriver.capabilities")
                .firefoxOptions();

        // Set BiBi Firefox preferences
        firefoxOptions.addPreference("remote.active-protocols", 1);
        firefoxOptions.addPreference("devtools.chrome.enabled", false);
        firefoxOptions.addPreference("devtools.debugger.remote-enabled", true);

        // Configure base capabilities
        firefoxOptions.setCapability("webSocketUrl", true);
        firefoxOptions.setCapability("moz:debuggerAddress", true);

        FirefoxOptionsEnhancer.enhanceOptions(firefoxOptions).using(environmentVariables);

        firefoxOptions.addArguments(argumentsIn(options));
        if (ThucydidesSystemProperty.HEADLESS_MODE.booleanFrom(environmentVariables)) {
            firefoxOptions.addArguments("-headless");
        }

        final FirefoxOptions enhancedOptions = EnhanceCapabilitiesWithFixtures.using(fixtureProviderService)
                .into(firefoxOptions);

        TestContext.forTheCurrentTest().recordBrowserConfiguration(enhancedOptions);
        TestContext.forTheCurrentTest().recordCurrentPlatform();

        driverProperties.registerCapabilities("firefox", capabilitiesToProperties(enhancedOptions));

        return new FirefoxDriver(enhancedOptions);
    }
}
