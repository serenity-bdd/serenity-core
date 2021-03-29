package serenitycore.net.serenitybdd.core.webdriver.driverproviders;

import serenitymodel.net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import serenitycore.net.serenitybdd.core.di.WebDriverInjectors;
import serenitymodel.net.thucydides.core.ThucydidesSystemProperty;
import serenitycore.net.thucydides.core.fixtureservices.FixtureProviderService;
import serenitycore.net.thucydides.core.steps.StepEventBus;
import serenitymodel.net.thucydides.core.util.EnvironmentVariables;
import serenitycore.net.thucydides.core.webdriver.CapabilityEnhancer;
import serenitycore.net.thucydides.core.webdriver.SupportedWebDriver;
import serenitycore.net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

public class SafariDriverProvider implements DriverProvider {

    private final DriverCapabilityRecord driverProperties;

    private final FixtureProviderService fixtureProviderService;

    public SafariDriverProvider(FixtureProviderService fixtureProviderService) {
        this.fixtureProviderService = fixtureProviderService;
        this.driverProperties = WebDriverInjectors.getInjector().getInstance(DriverCapabilityRecord.class);
    }

    @Override
    public WebDriver newInstance(String options, EnvironmentVariables environmentVariables) {
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }

        CapabilityEnhancer enhancer = new CapabilityEnhancer(environmentVariables, fixtureProviderService);
        SafariOptions safariOptions = SafariOptions.fromCapabilities(enhancer.enhanced(DesiredCapabilities.safari(), SupportedWebDriver.SAFARI));

        boolean useCleanSession = ThucydidesSystemProperty.SAFARI_USE_CLEAN_SESSION.booleanFrom(environmentVariables, false);

        SafariDriver driver = new SafariDriver(safariOptions);
        driverProperties.registerCapabilities("safari", capabilitiesToProperties(driver.getCapabilities()));
        return driver;
    }
}
