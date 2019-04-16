package net.serenitybdd.core.webdriver.driverproviders;

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
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.thucydides.core.ThucydidesSystemProperty.WEBDRIVER_USE_DRIVER_SERVICE_POOL;

public class ChromeDriverProvider implements DriverProvider {

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

        DesiredCapabilities enhancedCapabilities = enhancedCapabilitiesConfiguredIn(environmentVariables, options);
        driverProperties.registerCapabilities("chrome", capabilitiesToProperties(enhancedCapabilities));

        return ProvideNewDriver.withConfiguration(environmentVariables,
                enhancedCapabilities,
                driverServicePool,
                DriverServicePool::newDriver,
                (pool, capabilities) -> new ChromeDriver(capabilities)
        );
    }

    private DesiredCapabilities enhancedCapabilitiesConfiguredIn(EnvironmentVariables environmentVariables, String options) {
        DesiredCapabilities capabilities = new ChromeDriverCapabilities(environmentVariables, options).getCapabilities();
        CapabilityEnhancer enhancer = new CapabilityEnhancer(environmentVariables, fixtureProviderService);
        return enhancer.enhanced(capabilities, SupportedWebDriver.CHROME);
    }
}
