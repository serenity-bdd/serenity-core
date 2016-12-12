package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.webdriver.servicepools.DriverServicePool;
import net.serenitybdd.core.webdriver.servicepools.PhantomJSServicePool;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityEnhancer;
import net.thucydides.core.webdriver.phantomjs.PhantomJSCapabilityEnhancer;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PhantomJSDriverProvider implements DriverProvider {

    private final EnvironmentVariables environmentVariables;
    private final CapabilityEnhancer enhancer;
    private final DriverCapabilityRecord driverProperties;
    private static final Logger LOGGER = LoggerFactory.getLogger(PhantomJSDriverProvider.class);

    private final DriverServicePool driverServicePool = new PhantomJSServicePool();

    private DriverServicePool getDriverServicePool() throws IOException {
        driverServicePool.ensureServiceIsRunning();
        return driverServicePool;
    }

    public PhantomJSDriverProvider(EnvironmentVariables environmentVariables, CapabilityEnhancer enhancer) {
        this.environmentVariables = environmentVariables;
        this.enhancer = enhancer;
        this.driverProperties = Injectors.getInjector().getInstance(DriverCapabilityRecord.class);
    }

    @Override
    public WebDriver newInstance() {
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }

        DesiredCapabilities enhancedCapabilities = requestedPhantomJSCapabilities();
        driverProperties.registerCapabilities("phantomjs", enhancedCapabilities);

        try {
            return getDriverServicePool().newDriver(enhancedCapabilities);
        } catch (IOException couldNotStartChromeServer) {
            LOGGER.warn("Failed to start the phantomJS driver service, using a native driver instead",  couldNotStartChromeServer.getMessage());
            return new PhantomJSDriver(enhancedCapabilities);
        }
    }

    private DesiredCapabilities requestedPhantomJSCapabilities() {
        DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
        PhantomJSCapabilityEnhancer phantomEnhancer = new PhantomJSCapabilityEnhancer(environmentVariables);
        phantomEnhancer.enhanceCapabilities(capabilities);
        return enhancer.enhanced(capabilities);
    }

}
