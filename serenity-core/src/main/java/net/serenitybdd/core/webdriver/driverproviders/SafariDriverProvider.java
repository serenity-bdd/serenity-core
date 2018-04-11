package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.buildinfo.*;
import net.serenitybdd.core.di.*;
import net.thucydides.core.*;
import net.thucydides.core.fixtureservices.*;
import net.thucydides.core.steps.*;
import net.thucydides.core.util.*;
import net.thucydides.core.webdriver.*;
import net.thucydides.core.webdriver.stubs.*;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.*;
import org.openqa.selenium.safari.*;

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
        SafariOptions safariOptions = SafariOptions.fromCapabilities(enhancer.enhanced(DesiredCapabilities.safari()));

        boolean useCleanSession = ThucydidesSystemProperty.SAFARI_USE_CLEAN_SESSION.booleanFrom(environmentVariables, false);
        safariOptions.useCleanSession(useCleanSession);

        SafariDriver driver = new SafariDriver(safariOptions);
        driverProperties.registerCapabilities("safari", capabilitiesToProperties(driver.getCapabilities()));
        return driver;
    }
}
