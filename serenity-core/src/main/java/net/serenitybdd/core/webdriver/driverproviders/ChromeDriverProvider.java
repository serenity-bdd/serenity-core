package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.buildinfo.*;
import net.serenitybdd.core.di.*;
import net.serenitybdd.core.webdriver.servicepools.*;
import net.thucydides.core.fixtureservices.*;
import net.thucydides.core.steps.*;
import net.thucydides.core.util.*;
import net.thucydides.core.webdriver.*;
import net.thucydides.core.webdriver.stubs.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.remote.*;
import org.slf4j.*;

import java.io.*;

public class ChromeDriverProvider implements DriverProvider {

    private final DriverCapabilityRecord driverProperties;
    private static final Logger LOGGER = LoggerFactory.getLogger(ChromeDriverProvider.class);

    private final DriverServicePool driverServicePool = new ChromeServicePool();

    private DriverServicePool getDriverServicePool() throws IOException {
        driverServicePool.ensureServiceIsRunning();
        return driverServicePool;
    }

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
        DesiredCapabilities capabilities = requestedChromeCapabilities(options, environmentVariables);
        driverProperties.registerCapabilities("chrome", capabilitiesToProperties(capabilities));

        try {
            return getDriverServicePool().newDriver(capabilities);
        } catch (IOException couldNotStartChromeServer) {
            LOGGER.warn("Failed to start the chrome driver service, using a native driver instead",  couldNotStartChromeServer.getMessage());
            CapabilityEnhancer enhancer = new CapabilityEnhancer(environmentVariables, fixtureProviderService);
            return new ChromeDriver(enhancer.enhanced(capabilities));
        }
    }

    private DesiredCapabilities requestedChromeCapabilities(String options, EnvironmentVariables environmentVariables) {
        DesiredCapabilities capabilities = new ChromeDriverCapabilities(environmentVariables, options).getCapabilities();
        CapabilityEnhancer enhancer = new CapabilityEnhancer(environmentVariables, fixtureProviderService);
        return enhancer.enhanced(capabilities);
    }
}
