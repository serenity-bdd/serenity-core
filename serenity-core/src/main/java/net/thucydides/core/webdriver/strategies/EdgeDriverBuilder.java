package net.thucydides.core.webdriver.strategies;

import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.support.EdgeService;
import net.serenitybdd.core.support.ManagedDriverService;
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

public class EdgeDriverBuilder implements DriverBuilder {

    private final EnvironmentVariables environmentVariables;
    private final CapabilityEnhancer enhancer;
    private final DriverCapabilityRecord driverProperties;

    private static final Logger LOGGER = LoggerFactory.getLogger(EdgeDriverBuilder.class);

    public EdgeDriverBuilder(EnvironmentVariables environmentVariables, CapabilityEnhancer enhancer) {
        this.environmentVariables = environmentVariables;
        this.enhancer = enhancer;
        this.driverProperties = Injectors.getInjector().getInstance(DriverCapabilityRecord.class);
    }
    private ThreadLocal<ManagedDriverService> driverService = new ThreadLocal<>();

    private ManagedDriverService getDriverService() throws IOException {
        if (driverService.get() == null) {
            driverService.set(new EdgeService(environmentVariables));
            driverService.get().start();
        }
        return driverService.get();
    }


    @Override
    public WebDriver newInstance() {
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }
        DesiredCapabilities desiredCapabilities = enhancer.enhanced(DesiredCapabilities.edge());
        WebDriver driver;
        try {
            driver = getDriverService().newDriver(desiredCapabilities);
        } catch (IOException couldNotStartChromeServer) {
            LOGGER.warn("Failed to start the edge driver service, using a native driver instead",  couldNotStartChromeServer.getMessage());
            driver = new ChromeDriver(desiredCapabilities);
        }
        driverProperties.registerCapabilities("edge", desiredCapabilities);
        return driver;
    }
}
