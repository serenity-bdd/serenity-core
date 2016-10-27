package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.webdriver.servicepools.DriverServicePool;
import net.serenitybdd.core.webdriver.servicepools.PhantomJSServicePool;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityEnhancer;
import net.thucydides.core.webdriver.phantomjs.PhantomJSCapabilityEnhancer;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ThreadGuard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PhantomJSDriverProvider implements DriverProvider {

    private final EnvironmentVariables environmentVariables;
    private final CapabilityEnhancer defaultCapabilitiyEnhancer;
    private final DriverCapabilityRecord driverProperties;

    private static final Logger LOGGER = LoggerFactory.getLogger(PhantomJSDriverProvider.class);

    private static DriverServicePool driverService = null;

    private DriverServicePool getDriverService() throws IOException {
        if (driverService == null) {
            driverService = new PhantomJSServicePool();
            driverService.start();
        }
        return driverService;
    }


    public PhantomJSDriverProvider(EnvironmentVariables environmentVariables, CapabilityEnhancer enhancer) {
        this.environmentVariables = environmentVariables;
        this.defaultCapabilitiyEnhancer = enhancer;
        this.driverProperties = Injectors.getInjector().getInstance(DriverCapabilityRecord.class);
    }

    @Override
    public WebDriver newInstance() {
        DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
        PhantomJSCapabilityEnhancer enhancer = new PhantomJSCapabilityEnhancer(environmentVariables);
        enhancer.enhanceCapabilities(capabilities);

        WebDriver driver;

        DesiredCapabilities enhancedCapabilities = defaultCapabilitiyEnhancer.enhanced(capabilities);

        try {
            driver = getDriverService().newDriver(enhancedCapabilities);
        } catch (IOException couldNotStartChromeServer) {
            LOGGER.warn("Failed to start the chrome driver service, using a native driver instead",  couldNotStartChromeServer.getMessage());
            driver = new PhantomJSDriver(enhancedCapabilities);
        }

        driverProperties.registerCapabilities("phantomjs", enhancedCapabilities);
        return ThreadGuard.protect(driver);
    }
}
