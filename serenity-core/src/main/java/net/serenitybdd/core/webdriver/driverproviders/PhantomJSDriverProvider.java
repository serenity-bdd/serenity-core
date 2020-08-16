package net.serenitybdd.core.webdriver.driverproviders;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.di.WebDriverInjectors;
import net.serenitybdd.core.webdriver.servicepools.DriverServicePool;
import net.serenitybdd.core.webdriver.servicepools.PhantomJSServicePool;
import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityEnhancer;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.phantomjs.PhantomJSCapabilityEnhancer;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PhantomJSDriverProvider implements DriverProvider {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

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

        if(isDriverAutomaticallyDownloaded(environmentVariables)) {
            logger.info("Using automatically driver download");
            WebDriverManager.phantomjs().setup();
        } else {
            logger.info("Not using automatically driver download");
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
        SetProxyConfiguration.from(environmentVariables).in(capabilities);
        AddLoggingPreferences.from(environmentVariables).to(capabilities);

        PhantomJSCapabilityEnhancer phantomEnhancer = new PhantomJSCapabilityEnhancer(environmentVariables);
        phantomEnhancer.enhanceCapabilities(capabilities);

        CapabilityEnhancer enhancer = new CapabilityEnhancer(environmentVariables, fixtureProviderService);
        return enhancer.enhanced(capabilities, SupportedWebDriver.PHANTOMJS);
    }

}
