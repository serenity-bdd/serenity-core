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
import org.openqa.selenium.edge.*;
import org.openqa.selenium.remote.*;
import org.slf4j.*;

import java.io.*;

public class EdgeDriverProvider implements DriverProvider {

    private final DriverCapabilityRecord driverProperties;
    private static final Logger LOGGER = LoggerFactory.getLogger(EdgeDriverProvider.class);

    private final DriverServicePool driverServicePool = new EdgeServicePool();

    private DriverServicePool getDriverServicePool() throws IOException {
        driverServicePool.ensureServiceIsRunning();
        return driverServicePool;
    }

    private final FixtureProviderService fixtureProviderService;

    public EdgeDriverProvider(FixtureProviderService fixtureProviderService) {
        this.fixtureProviderService = fixtureProviderService;
        this.driverProperties = WebDriverInjectors.getInjector().getInstance(DriverCapabilityRecord.class);
    }

    @Override
    public WebDriver newInstance(String options, EnvironmentVariables environmentVariables) {
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }

        CapabilityEnhancer enhancer = new CapabilityEnhancer(environmentVariables, fixtureProviderService);
        DesiredCapabilities desiredCapabilities = enhancer.enhanced(DesiredCapabilities.edge());
        driverProperties.registerCapabilities("edge", capabilitiesToProperties(desiredCapabilities));

        try {
            return getDriverServicePool().newDriver(desiredCapabilities);
        } catch (IOException couldNotStartServer) {
            LOGGER.warn("Failed to start the edge driver service, using a native driver instead",  couldNotStartServer.getMessage());
            return new EdgeDriver(desiredCapabilities);
        }
    }
}
