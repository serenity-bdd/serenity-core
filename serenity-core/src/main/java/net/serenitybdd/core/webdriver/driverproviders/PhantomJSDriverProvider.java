package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.buildinfo.*;
import net.serenitybdd.core.di.*;
import net.serenitybdd.core.webdriver.servicepools.*;
import net.thucydides.core.fixtureservices.*;
import net.thucydides.core.steps.*;
import net.thucydides.core.util.*;
import net.thucydides.core.webdriver.*;
import net.thucydides.core.webdriver.phantomjs.*;
import net.thucydides.core.webdriver.stubs.*;
import org.openqa.selenium.*;
import org.openqa.selenium.phantomjs.*;
import org.openqa.selenium.remote.*;
import org.slf4j.*;

import java.io.*;

public class PhantomJSDriverProvider implements DriverProvider {

    private final DriverCapabilityRecord driverProperties;
    private static final Logger LOGGER = LoggerFactory.getLogger(PhantomJSDriverProvider.class);

    private final DriverServicePool driverServicePool = new PhantomJSServicePool();

    private DriverServicePool getDriverServicePool() throws IOException {
        driverServicePool.ensureServiceIsRunning();
        return driverServicePool;
    }

    private final FixtureProviderService fixtureProviderService;

    public PhantomJSDriverProvider(FixtureProviderService fixtureProviderService) {
        this.fixtureProviderService = fixtureProviderService;
        this.driverProperties = WebDriverInjectors.getInjector().getInstance(DriverCapabilityRecord.class);
    }

    @Override
    public WebDriver newInstance(String options, EnvironmentVariables environmentVariables) {
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }

        DesiredCapabilities enhancedCapabilities = requestedPhantomJSCapabilities(environmentVariables);
        driverProperties.registerCapabilities("phantomjs", capabilitiesToProperties(enhancedCapabilities));

        try {
            return getDriverServicePool().newDriver(enhancedCapabilities);
        } catch (IOException couldNotStartChromeServer) {
            LOGGER.warn("Failed to start the phantomJS driver service, using a native driver instead",  couldNotStartChromeServer.getMessage());
            return new PhantomJSDriver(enhancedCapabilities);
        }
    }

    private DesiredCapabilities requestedPhantomJSCapabilities(EnvironmentVariables environmentVariables) {
        DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
        PhantomJSCapabilityEnhancer phantomEnhancer = new PhantomJSCapabilityEnhancer(environmentVariables);
        phantomEnhancer.enhanceCapabilities(capabilities);

        CapabilityEnhancer enhancer = new CapabilityEnhancer(environmentVariables, fixtureProviderService);
        return enhancer.enhanced(capabilities);
    }

}
