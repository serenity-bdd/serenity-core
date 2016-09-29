package net.thucydides.core.webdriver.strategies;

import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.support.InternetExplorerService;
import net.serenitybdd.core.support.ManagedDriverService;
import net.serenitybdd.core.time.InternalSystemClock;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityEnhancer;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class InternetExplorerDriverBuilder implements DriverBuilder {

    private final EnvironmentVariables environmentVariables;
    private final CapabilityEnhancer enhancer;
    private final DriverCapabilityRecord driverProperties;
    private static final Logger LOGGER = LoggerFactory.getLogger(InternetExplorerDriverBuilder.class);

    public InternetExplorerDriverBuilder(EnvironmentVariables environmentVariables, CapabilityEnhancer enhancer) {
        this.environmentVariables = environmentVariables;
        this.enhancer = enhancer;
        this.driverProperties = Injectors.getInjector().getInstance(DriverCapabilityRecord.class);
    }

    private ThreadLocal<ManagedDriverService> driverService = new ThreadLocal<>();

    private ManagedDriverService getDriverService() throws IOException {
        if (driverService.get() == null) {
            driverService.set(new InternetExplorerService(environmentVariables));
            driverService.get().start();
        }
        return driverService.get();
    }

    @Override
    public WebDriver newInstance() {
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }
        DesiredCapabilities browserCapabilities = DesiredCapabilities.internetExplorer();
        browserCapabilities.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
        browserCapabilities.setCapability(InternetExplorerDriver.NATIVE_EVENTS, false);
        browserCapabilities.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, false);
        browserCapabilities.setJavascriptEnabled(true);
        browserCapabilities.setCapability(CapabilityType.TAKES_SCREENSHOT, true);

        WebDriver driver;
        try {
            driver = getDriverService().newDriver(browserCapabilities);
        } catch (IOException e) {
            LOGGER.error(e.getClass().getCanonicalName() + " happened - retrying in 2 seconds");
            new InternalSystemClock().pauseFor(2000);
            try {
                driver = getDriverService().newDriver(browserCapabilities);
            } catch (IOException failedASecondTime) {
                driver = new InternetExplorerDriver(browserCapabilities);
            }
        }
        driverProperties.registerCapabilities("iexplorer", browserCapabilities);
        return driver;
    }
}
