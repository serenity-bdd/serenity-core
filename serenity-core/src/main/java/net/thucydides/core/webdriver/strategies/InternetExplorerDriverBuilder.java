package net.thucydides.core.webdriver.strategies;

import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.time.InternalSystemClock;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityEnhancer;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

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

    @Override
    public WebDriver newInstance() {
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }

        InternetExplorerDriverService.Builder builder = new InternetExplorerDriverService.Builder().usingAnyFreePort();
        String environmentDefinedIEDriverPath = environmentVariables.getProperty(ThucydidesSystemProperty.WEBDRIVER_IE_DRIVER);
        if (StringUtils.isNotEmpty(environmentDefinedIEDriverPath)) {
            builder.usingDriverExecutable(new File(environmentDefinedIEDriverPath));
        }
        final InternetExplorerDriverService service = builder.build();

        try {
            service.start();
        } catch (Exception e) {
            throw new RuntimeException("InternetExplorerDriverService could not be started", e);
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                service.stop();
            }
        });
        DesiredCapabilities browserCapabilities = DesiredCapabilities.internetExplorer();
        browserCapabilities.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
        browserCapabilities.setCapability(InternetExplorerDriver.NATIVE_EVENTS, false);
        browserCapabilities.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, false);
        browserCapabilities.setJavascriptEnabled(true);
        browserCapabilities.setCapability(CapabilityType.TAKES_SCREENSHOT, true);

        InternetExplorerDriver driver = driver(service, browserCapabilities);
        driverProperties.registerCapabilities("iexplorer", driver.getCapabilities());
        return driver;
    }

    private InternetExplorerDriver driver(InternetExplorerDriverService service, DesiredCapabilities browserCapabilities) {
        try {
            return new InternetExplorerDriver(service, browserCapabilities);
        } catch (NoSuchSessionException e) {
            LOGGER.error(e.getClass().getCanonicalName() + " happened - retrying in 2 seconds");
            new InternalSystemClock().pauseFor(2000);
            return new InternetExplorerDriver(service, browserCapabilities);
        }
    }
}
