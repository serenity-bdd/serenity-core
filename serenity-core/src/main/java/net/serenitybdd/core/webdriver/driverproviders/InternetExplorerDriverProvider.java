package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.buildinfo.*;
import net.serenitybdd.core.di.*;
import net.serenitybdd.core.time.*;
import net.serenitybdd.core.webdriver.servicepools.*;
import net.thucydides.core.fixtureservices.*;
import net.thucydides.core.steps.*;
import net.thucydides.core.util.*;
import net.thucydides.core.webdriver.*;
import net.thucydides.core.webdriver.stubs.*;
import org.openqa.selenium.*;
import org.openqa.selenium.ie.*;
import org.openqa.selenium.remote.*;
import org.slf4j.*;

import java.io.*;

public class InternetExplorerDriverProvider implements DriverProvider {

    private final DriverCapabilityRecord driverProperties;
    private static final Logger LOGGER = LoggerFactory.getLogger(InternetExplorerDriverProvider.class);

    private final DriverServicePool driverServicePool = new InternetExplorerServicePool();

    private DriverServicePool getDriverServicePool() throws IOException {
        driverServicePool.ensureServiceIsRunning();
        return driverServicePool;
    }

    private final FixtureProviderService fixtureProviderService;

    public InternetExplorerDriverProvider(FixtureProviderService fixtureProviderService) {
        this.fixtureProviderService = fixtureProviderService;
        this.driverProperties = WebDriverInjectors.getInjector().getInstance(DriverCapabilityRecord.class);
    }

    @Override
    public WebDriver newInstance(String options, EnvironmentVariables environmentVariables) {
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }

        CapabilityEnhancer enhancer = new CapabilityEnhancer(environmentVariables, fixtureProviderService);
        DesiredCapabilities desiredCapabilities = enhancer.enhanced(recommendedDefaultInternetExplorerCapabilities());
        driverProperties.registerCapabilities("iexplorer", capabilitiesToProperties(desiredCapabilities));

        try {
            return retryCreateDriverOnNoSuchSession(desiredCapabilities);
        } catch (Exception couldNotStartServer) {
            LOGGER.warn("Failed to start the Internet driver service, using a native driver instead - " + couldNotStartServer.getMessage());
            return new InternetExplorerDriver(desiredCapabilities);
        }
    }

    private WebDriver retryCreateDriverOnNoSuchSession(DesiredCapabilities desiredCapabilities) throws IOException {
        return new TryAtMost(3).toStartNewDriverWith(desiredCapabilities);
    }

    private class TryAtMost {
        private final int maxTries;

        private TryAtMost(int maxTries) {
            this.maxTries = maxTries;
        }

        public WebDriver toStartNewDriverWith(DesiredCapabilities desiredCapabilities) throws IOException {
            try {
                return getDriverServicePool().newDriver(desiredCapabilities);
            } catch (NoSuchSessionException e) {
                if (maxTries == 0) { throw e; }

                LOGGER.error(e.getClass().getCanonicalName() + " happened - retrying in 2 seconds");
                new InternalSystemClock().pauseFor(2000);
                return new TryAtMost(maxTries - 1).toStartNewDriverWith(desiredCapabilities);
            }
        }
    }

    private DesiredCapabilities recommendedDefaultInternetExplorerCapabilities() {
        DesiredCapabilities defaults = DesiredCapabilities.internetExplorer();
        defaults.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
        defaults.setCapability(InternetExplorerDriver.NATIVE_EVENTS, false);
        defaults.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, false);
        defaults.setCapability(CapabilityType.TAKES_SCREENSHOT, true);
        defaults.setJavascriptEnabled(true);
        return defaults;
    }
}
