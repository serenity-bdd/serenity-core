package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.webdriver.servicepools.EdgeServicePool;
import net.serenitybdd.core.webdriver.servicepools.DriverServicePool;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityEnhancer;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ThreadGuard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class EdgeDriverProvider implements DriverProvider {

    private final EnvironmentVariables environmentVariables;
    private final CapabilityEnhancer enhancer;
    private final DriverCapabilityRecord driverProperties;

    private static final Logger LOGGER = LoggerFactory.getLogger(EdgeDriverProvider.class);

    public EdgeDriverProvider(EnvironmentVariables environmentVariables, CapabilityEnhancer enhancer) {
        this.environmentVariables = environmentVariables;
        this.enhancer = enhancer;
        this.driverProperties = Injectors.getInjector().getInstance(DriverCapabilityRecord.class);
    }
    private ThreadLocal<DriverServicePool> driverService = new ThreadLocal<>();

    private DriverServicePool getDriverService() throws IOException {
        if (driverService.get() == null) {
            driverService.set(new EdgeServicePool());
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
        return ThreadGuard.protect(driver);
    }
}
