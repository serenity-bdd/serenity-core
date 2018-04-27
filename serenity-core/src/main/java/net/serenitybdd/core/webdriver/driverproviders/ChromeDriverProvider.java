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
            return new ChromeDriver(enhancer.enhanced(capabilities, SupportedWebDriver.CHROME));
        }
    }

    private DesiredCapabilities requestedChromeCapabilities(String options, EnvironmentVariables environmentVariables) {
        DesiredCapabilities capabilities = new ChromeDriverCapabilities(environmentVariables, options).getCapabilities();
        CapabilityEnhancer enhancer = new CapabilityEnhancer(environmentVariables, fixtureProviderService);
        return enhancer.enhanced(capabilities, SupportedWebDriver.CHROME);
    }
}
