package net.serenitybdd.core.webdriver.driverproviders;

import com.google.common.base.Preconditions;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityEnhancer;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.capabilities.W3CCapabilities;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.HashMap;
import java.util.Map;

import static net.thucydides.core.ThucydidesSystemProperty.*;
import static net.thucydides.core.webdriver.SupportedWebDriver.*;
import static net.thucydides.core.webdriver.WebDriverFactory.REMOTE_DRIVER;
import static net.thucydides.core.webdriver.WebDriverFactory.getDriverFrom;

public class DriverCapabilities {

    public static final DriverCapabilitiesProvider DEFAULT_CAPABILITIES = DesiredCapabilities::new;
    private final EnvironmentVariables environmentVariables;
    private final CapabilityEnhancer enhancer;

    private final static Integer EXTRA_TIME_TO_TAKE_SCREENSHOTS = 180;

    public DriverCapabilities(EnvironmentVariables environmentVariables, CapabilityEnhancer enhancer) {
        this.environmentVariables = environmentVariables;
        this.enhancer = enhancer;
    }

    public MutableCapabilities forDriver(String driverName, String options) {
        if (driverName == null || driverName.startsWith(":")) {
            driverName = REMOTE_DRIVER;
        }
        SupportedWebDriver driverType = driverTypeFor(driverComponentof(driverName));

        Preconditions.checkNotNull(driverType, "Unsupported remote driver type: " + driverName);

        if (shouldUseARemoteDriver()) {
            return enhancer.enhanced(remoteCapabilities(options), driverType);
        } else {
            return enhancer.enhanced(realBrowserCapabilities(driverType, options), driverType);
        }
    }

    private String driverComponentof(String driverName) {
        return driverName.contains(":") ? driverName.split(":")[0] : driverName;
    }

    private SupportedWebDriver driverTypeFor(String driver) {
        if (!SupportedWebDriver.isSupported(driver)) {
            SupportedWebDriver closestDriver = SupportedWebDriver.getClosestDriverValueTo(driver);
            throw new AssertionError("Unsupported driver for webdriver.driver or webdriver.remote.driver: " + driver
                                     + ". Did you mean " + closestDriver.toString().toLowerCase() + "?");
        }
        return SupportedWebDriver.valueOrSynonymOf(driver);
    }

    private Map<SupportedWebDriver, DriverCapabilitiesProvider> driverCapabilitiesSelector(String options) {
        Map<SupportedWebDriver, DriverCapabilitiesProvider> selectors = new HashMap<>();

        selectors.put(CHROME, new ChromeDriverCapabilities(environmentVariables, options));
        selectors.put(FIREFOX, new FirefoxDriverCapabilities(environmentVariables));
        selectors.put(APPIUM, new AppiumDriverCapabilities(environmentVariables, options));
        selectors.put(PROVIDED, new ProvidedDriverCapabilities(environmentVariables));
        selectors.put(EDGE, new EdgeDriverCapabilities(environmentVariables, options));
        return selectors;
    }

    public MutableCapabilities realBrowserCapabilities(SupportedWebDriver driverType, String options) {

        return enhancer.enhanced(
                driverCapabilitiesSelector(options)
                        .getOrDefault(driverType, DEFAULT_CAPABILITIES)
                        .getCapabilities(),
                driverType
        );
    }

    private boolean isUndefined(String browser) {
        return (browser == null || browser.startsWith(":"));
    }

    private MutableCapabilities remoteCapabilities(String options) {

        MutableCapabilities capabilities;

        String remoteBrowser = ThucydidesSystemProperty.WEBDRIVER_REMOTE_DRIVER.from(environmentVariables, getDriverFrom(environmentVariables));
        if (!isUndefined(remoteBrowser)) {
            capabilities = realBrowserCapabilities(driverTypeFor(remoteBrowser), options);
        } else {
            capabilities = new DesiredCapabilities();
        }

        capabilities.setCapability("idle-timeout", EXTRA_TIME_TO_TAKE_SCREENSHOTS);

        if (WEBDRIVER_REMOTE_OS.from(environmentVariables) != null) {
            capabilities.setCapability("platform", Platform.valueOf(WEBDRIVER_REMOTE_OS.from(environmentVariables)));
        }

        if (WEBDRIVER_REMOTE_BROWSER_VERSION.from(environmentVariables) != null) {
            capabilities.setCapability("version", WEBDRIVER_REMOTE_BROWSER_VERSION.from(environmentVariables));
        }

        if (StepEventBus.getEventBus().isBaseStepListenerRegistered()) {
            AddCustomDriverCapabilities.from(environmentVariables)
                    .withTestDetails(SupportedWebDriver.REMOTE, StepEventBus.getEventBus().getBaseStepListener().getCurrentTestOutcome())
                    .to(capabilities);
        }

        capabilities.merge(W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver"));
        return capabilities;
    }

    private boolean shouldUseARemoteDriver() {
        return StringUtils.isNotEmpty(WEBDRIVER_REMOTE_URL.from(environmentVariables));
    }
}
