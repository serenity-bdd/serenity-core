package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.webdriver.servicepools.DriverServicePool;
import net.serenitybdd.core.webdriver.servicepools.InternetExplorerServicePool;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityEnhancer;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ThreadGuard;
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

        DesiredCapabilities desiredCapabilities = enhancer.enhanced(DesiredCapabilities.internetExplorer());
        addInternetExplorerCapabilitiesTo(desiredCapabilities);

        driverProperties.registerCapabilities("iexplorer", desiredCapabilities);

        try {
            return getDriverServicePool().newDriver(desiredCapabilities);
        } catch (IOException couldNotStartServer) {
            LOGGER.warn("Failed to start the edge driver service, using a native driver instead", couldNotStartServer.getMessage());
            return ThreadGuard.protect(new InternetExplorerDriver(desiredCapabilities));
        }
    }

    private void addInternetExplorerCapabilitiesTo(DesiredCapabilities browserCapabilities) {
        browserCapabilities.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
        browserCapabilities.setCapability(InternetExplorerDriver.NATIVE_EVENTS, false);
        browserCapabilities.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, false);
        browserCapabilities.setJavascriptEnabled(true);
        browserCapabilities.setCapability(CapabilityType.TAKES_SCREENSHOT, true);
    }
}
