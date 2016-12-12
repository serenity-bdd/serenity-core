package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.webdriver.servicepools.DriverServicePool;
import net.serenitybdd.core.webdriver.servicepools.GeckoServicePool;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityEnhancer;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static net.thucydides.core.ThucydidesSystemProperty.USE_GECKO_DRIVER;

public class FirefoxDriverProvider implements DriverProvider {

    private final EnvironmentVariables environmentVariables;
    private final CapabilityEnhancer enhancer;
    private final DriverCapabilityRecord driverProperties;

    private static final Logger LOGGER = LoggerFactory.getLogger(FirefoxDriverProvider.class);

    private final DriverServicePool driverServicePool = new GeckoServicePool();

    private DriverServicePool getDriverServicePool() throws IOException {
        driverServicePool.ensureServiceIsRunning();
        return driverServicePool;
    }

    protected String serviceName(){ return "firefox"; }

    public FirefoxDriverProvider(EnvironmentVariables environmentVariables, CapabilityEnhancer enhancer) {
        this.environmentVariables = environmentVariables;
        this.enhancer = enhancer;
        this.driverProperties = Injectors.getInjector().getInstance(DriverCapabilityRecord.class);
    }

    @Override
    public WebDriver newInstance() {
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }
        DesiredCapabilities capabilities = new FirefoxDriverCapabilities(environmentVariables).getCapabilities();

        WebDriver driver =  (shouldUseGeckoDriver()) ? newMarionetteDriver(capabilities) : newFirefoxDriver(capabilities);

        driverProperties.registerCapabilities("firefox", capabilities);

        return driver;
    }

    private boolean shouldUseGeckoDriver() {
        return geckoDriverIsOnTheClasspath() && geckoIsNotDisabled();
    }

    private boolean geckoIsNotDisabled() {
        return USE_GECKO_DRIVER.booleanFrom(environmentVariables, true) && geckoDriverIsOnTheClasspath();
    }

    private WebDriver newFirefoxDriver(DesiredCapabilities capabilities) {
        return new FirefoxDriver(enhancer.enhanced(capabilities));
    }

    private WebDriver newMarionetteDriver(DesiredCapabilities capabilities) {
        capabilities.setCapability("marionette", true);

        try {
            return getDriverServicePool().newDriver(enhancer.enhanced(capabilities));
        } catch (IOException couldNotStartGeckoDriverService) {
            LOGGER.warn("Failed to start the gecko driver service, using a native driver instead",  couldNotStartGeckoDriverService.getMessage());
            return newFirefoxDriver(capabilities);
        }
    }

    private boolean geckoDriverIsOnTheClasspath() {
        try {
            Runtime.getRuntime().exec("geckodriver --help");
            return true;
        } catch (Exception geckodriverBinaryNotFound) {
            try {
                Runtime.getRuntime().exec("wires --help");
                return true;
            } catch (Exception wiresBinaryNotFound) {
                return false;
            }
        }
    }

}
