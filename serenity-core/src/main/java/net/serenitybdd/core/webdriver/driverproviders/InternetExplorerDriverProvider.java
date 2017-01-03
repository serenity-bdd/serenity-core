package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.time.InternalSystemClock;
import net.serenitybdd.core.webdriver.servicepools.DriverServicePool;
import net.serenitybdd.core.webdriver.servicepools.InternetExplorerServicePool;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityEnhancer;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class InternetExplorerDriverProvider implements DriverProvider {

    private final EnvironmentVariables environmentVariables;
    private final CapabilityEnhancer enhancer;
    private final DriverCapabilityRecord driverProperties;
    private static final Logger LOGGER = LoggerFactory.getLogger(InternetExplorerDriverProvider.class);

    private final DriverServicePool driverServicePool = new InternetExplorerServicePool();

    private DriverServicePool getDriverServicePool() throws IOException {
        driverServicePool.ensureServiceIsRunning();
        return driverServicePool;
    }

    public InternetExplorerDriverProvider(EnvironmentVariables environmentVariables, CapabilityEnhancer enhancer) {
        this.environmentVariables = environmentVariables;
        this.enhancer = enhancer;
        this.driverProperties = Injectors.getInjector().getInstance(DriverCapabilityRecord.class);
    }

    @Override
    public WebDriver newInstance() {
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }

        DesiredCapabilities desiredCapabilities = enhancer.enhanced(recommendedDefaultInternetExplorerCapabilities());
        driverProperties.registerCapabilities("iexplorer", desiredCapabilities);

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
