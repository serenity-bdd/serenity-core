package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.webdriver.servicepools.ChromeServicePool;
import net.serenitybdd.core.webdriver.servicepools.DriverServicePool;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityEnhancer;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ChromeDriverProvider implements DriverProvider {

    private final EnvironmentVariables environmentVariables;
    private final CapabilityEnhancer enhancer;
    private final DriverCapabilityRecord driverProperties;
    private static final Logger LOGGER = LoggerFactory.getLogger(ChromeDriverProvider.class);

    private final DriverServicePool driverServicePool = new ChromeServicePool();

    private DriverServicePool getDriverServicePool() throws IOException {
        driverServicePool.ensureServiceIsRunning();
        return driverServicePool;
    }

    public ChromeDriverProvider(EnvironmentVariables environmentVariables, CapabilityEnhancer enhancer) {
        this.environmentVariables = environmentVariables;
        this.enhancer = enhancer;
        this.driverProperties = Injectors.getInjector().getInstance(DriverCapabilityRecord.class);
    }

    @Override
    public WebDriver newInstance() {
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }
        DesiredCapabilities capabilities = requestedChromeCapabilities();
        driverProperties.registerCapabilities("chrome", capabilities);

        try {
            return getDriverServicePool().newDriver(capabilities);
        } catch (IOException couldNotStartChromeServer) {
            LOGGER.warn("Failed to start the chrome driver service, using a native driver instead",  couldNotStartChromeServer.getMessage());
            return new ChromeDriver(enhancer.enhanced(capabilities));
        }
    }

    private DesiredCapabilities requestedChromeCapabilities() {
        DesiredCapabilities capabilities = new ChromeDriverCapabilities(environmentVariables).getCapabilities();
        return enhancer.enhanced(capabilities);
    }
}
