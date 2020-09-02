package net.serenitybdd.core.webdriver.driverproviders;

import com.google.common.base.Preconditions;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityEnhancer;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Platform;
import org.openqa.selenium.edge.EdgeOptions;
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

    private final Integer EXTRA_TIME_TO_TAKE_SCREENSHOTS = 180;

    public DriverCapabilities(EnvironmentVariables environmentVariables, CapabilityEnhancer enhancer) {
        this.environmentVariables = environmentVariables;
        this.enhancer = enhancer;
    }

    public DesiredCapabilities forDriver(String driverName, String options) {
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
        String normalizedDriverName = driverComponentof(driver).toUpperCase();
        if (!SupportedWebDriver.listOfSupportedDrivers().contains(normalizedDriverName)) {
            SupportedWebDriver closestDriver = SupportedWebDriver.getClosestDriverValueTo(normalizedDriverName);
            throw new AssertionError("Unsupported driver for webdriver.driver or webdriver.remote.driver: " + driver
                    + ". Did you mean " + closestDriver.toString().toLowerCase() + "?");
        }
        return SupportedWebDriver.valueOrSynonymOf(normalizedDriverName);
    }

    private Map<SupportedWebDriver, DriverCapabilitiesProvider> driverCapabilitiesSelector(String options) {
        Map<SupportedWebDriver, DriverCapabilitiesProvider> selectors = new HashMap<>();

        selectors.put(CHROME, new ChromeDriverCapabilities(environmentVariables, options));
        selectors.put(FIREFOX, new FirefoxDriverCapabilities(environmentVariables));
        selectors.put(APPIUM, new AppiumDriverCapabilities(environmentVariables, options));
        selectors.put(PROVIDED, new ProvidedDriverCapabilities(environmentVariables));
        selectors.put(SAFARI, DesiredCapabilities::safari);
        selectors.put(HTMLUNIT, DesiredCapabilities::htmlUnit);
        selectors.put(OPERA, DesiredCapabilities::operaBlink);
        selectors.put(IEXPLORER, DesiredCapabilities::internetExplorer);
        selectors.put(EDGE, () -> new DesiredCapabilities(new EdgeOptions()));
        selectors.put(PHANTOMJS, DesiredCapabilities::phantomjs);
        selectors.put(IPHONE, DesiredCapabilities::iphone);
        selectors.put(ANDROID, DesiredCapabilities::android);
        return selectors;
    }

    public DesiredCapabilities realBrowserCapabilities(SupportedWebDriver driverType, String options) {

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

    private DesiredCapabilities remoteCapabilities(String options) {

        DesiredCapabilities capabilities;

        String remoteBrowser = ThucydidesSystemProperty.WEBDRIVER_REMOTE_DRIVER.from(environmentVariables, getDriverFrom(environmentVariables));
        if (!isUndefined(remoteBrowser)) {
            capabilities = realBrowserCapabilities(driverTypeFor(remoteBrowser), options);;
        } else {
            capabilities = new DesiredCapabilities();
        }

        capabilities.setCapability("idle-timeout", EXTRA_TIME_TO_TAKE_SCREENSHOTS);

        Boolean recordScreenshotsInSaucelabs = ThucydidesSystemProperty.SAUCELABS_RECORD_SCREENSHOTS.booleanFrom(environmentVariables);
        capabilities.setCapability("record-screenshots", recordScreenshotsInSaucelabs);


        if (WEBDRIVER_REMOTE_OS.from(environmentVariables) != null) {
            capabilities.setCapability("platform", Platform.valueOf(WEBDRIVER_REMOTE_OS.from(environmentVariables)));
        }

        if (WEBDRIVER_REMOTE_BROWSER_VERSION.from(environmentVariables) != null) {
            capabilities.setCapability("version", WEBDRIVER_REMOTE_BROWSER_VERSION.from(environmentVariables));
        }

        AddCustomDriverCapabilities.from(environmentVariables)
                .withTestDetails(SupportedWebDriver.REMOTE, StepEventBus.getEventBus().getBaseStepListener().getCurrentTestOutcome())
                .to(capabilities);

        return capabilities;
    }


    private boolean shouldUseARemoteDriver() {
        return StringUtils.isNotEmpty(WEBDRIVER_REMOTE_URL.from(environmentVariables));
    }

}
