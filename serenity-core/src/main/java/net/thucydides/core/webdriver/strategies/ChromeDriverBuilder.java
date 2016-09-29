package net.thucydides.core.webdriver.strategies;

import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.support.ChromeService;
import net.serenitybdd.core.support.ManagedDriverService;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityEnhancer;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ChromeDriverBuilder implements DriverBuilder {

    private final EnvironmentVariables environmentVariables;
    private final CapabilityEnhancer enhancer;
    private final DriverCapabilityRecord driverProperties;
    private static final Logger LOGGER = LoggerFactory.getLogger(ChromeDriverBuilder.class);

    private ThreadLocal<ManagedDriverService> driverService = new ThreadLocal<>();

    private ManagedDriverService getDriverService() throws IOException {
        if (driverService.get() == null) {
            driverService.set(new ChromeService());
            driverService.get().start();
        }
        return driverService.get();
    }

    public ChromeDriverBuilder(EnvironmentVariables environmentVariables, CapabilityEnhancer enhancer) {
        this.environmentVariables = environmentVariables;
        this.enhancer = enhancer;
        this.driverProperties = Injectors.getInjector().getInstance(DriverCapabilityRecord.class);
    }

    @Override
    public WebDriver newInstance() {
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }
        DesiredCapabilities capabilities = new ChromeDriverCapabilities(environmentVariables).getCapabilities();
        updateChromePathIfSpecifiedIn(environmentVariables);

        DesiredCapabilities desiredCapabilities = enhancer.enhanced(capabilities);
        WebDriver driver;
        try {
            driver = getDriverService().newDriver(desiredCapabilities);
        } catch (IOException couldNotStartChromeServer) {
            LOGGER.warn("Failed to start the chrome driver service, using a native driver instead",  couldNotStartChromeServer.getMessage());
            driver = new ChromeDriver(enhancer.enhanced(capabilities));
        }

        driverProperties.registerCapabilities("chrome", desiredCapabilities);
        return driver;
    }

    private void updateChromePathIfSpecifiedIn(EnvironmentVariables environmentVariables) {
        String environmentDefinedChromeDriverPath = environmentVariables.getProperty(ThucydidesSystemProperty.WEBDRIVER_CHROME_DRIVER);
        if (StringUtils.isNotEmpty(environmentDefinedChromeDriverPath)) {
            System.setProperty(ThucydidesSystemProperty.WEBDRIVER_CHROME_DRIVER.toString(), environmentDefinedChromeDriverPath);
        }
    }
}
