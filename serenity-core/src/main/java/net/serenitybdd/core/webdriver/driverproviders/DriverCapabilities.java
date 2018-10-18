package net.serenitybdd.core.webdriver.driverproviders;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityEnhancer;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.openqa.selenium.Platform;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.opera.OperaOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariOptions;
import org.openqa.selenium.MutableCapabilities;

import java.util.Map;

import static net.thucydides.core.webdriver.SupportedWebDriver.*;
import static net.thucydides.core.webdriver.WebDriverFactory.REMOTE_DRIVER;
import static net.thucydides.core.webdriver.WebDriverFactory.getDriverFrom;

public class DriverCapabilities {

    public static final DriverCapabilitiesProvider DEFAULT_CAPABILITIES = FirefoxOptions::new;
    private final EnvironmentVariables environmentVariables;
    private final CapabilityEnhancer enhancer;

    private final Integer EXTRA_TIME_TO_TAKE_SCREENSHOTS = 180;

    public DriverCapabilities(EnvironmentVariables environmentVariables, CapabilityEnhancer enhancer) {
        this.environmentVariables = environmentVariables;
        this.enhancer = enhancer;
    }

    public MutableCapabilities forDriver(String driverName, String options) {
        if (driverName == null) {
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

    @SuppressWarnings("deprecation")
	private Map<SupportedWebDriver, DriverCapabilitiesProvider> driverCapabilitiesSelector(String options) {
        Map<SupportedWebDriver, DriverCapabilitiesProvider> selectors = new HashMap<>();

        selectors.put(CHROME,new ChromeDriverCapabilities(environmentVariables, options));
        selectors.put(FIREFOX,new FirefoxDriverCapabilities(environmentVariables));
        selectors.put(APPIUM,new AppiumDriverCapabilities(environmentVariables, options));
        selectors.put(PROVIDED,new ProvidedDriverCapabilities(environmentVariables));
        selectors.put(SAFARI, SafariOptions::new);
        selectors.put(HTMLUNIT, DesiredCapabilities::htmlUnit);
        selectors.put(OPERA, OperaOptions::new);
        selectors.put(IEXPLORER, DesiredCapabilities::internetExplorer);
        selectors.put(EDGE, EdgeOptions::new);
        selectors.put(PHANTOMJS, DesiredCapabilities::phantomjs);
        selectors.put(IPHONE, DesiredCapabilities::iphone);
        selectors.put(ANDROID, DesiredCapabilities::android);
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

    private MutableCapabilities remoteCapabilities(String options) {
        String remoteBrowser = ThucydidesSystemProperty.WEBDRIVER_REMOTE_DRIVER.from(environmentVariables, getDriverFrom(environmentVariables));
        if (remoteBrowser == null) {
            remoteBrowser = "firefox";
        }

        MutableCapabilities capabilities = realBrowserCapabilities(driverTypeFor(remoteBrowser), options);
        capabilities.setCapability("idle-timeout",EXTRA_TIME_TO_TAKE_SCREENSHOTS);

        Boolean recordScreenshotsInSaucelabs = ThucydidesSystemProperty.SAUCELABS_RECORD_SCREENSHOTS.booleanFrom(environmentVariables);
        capabilities.setCapability("record-screenshots", recordScreenshotsInSaucelabs);


        if (environmentVariables.getProperty(ThucydidesSystemProperty.WEBDRIVER_REMOTE_OS) != null) {
            capabilities.setCapability("platform", Platform.valueOf(environmentVariables.getProperty(ThucydidesSystemProperty.WEBDRIVER_REMOTE_OS)));
        }

        if (environmentVariables.getProperty(ThucydidesSystemProperty.WEBDRIVER_REMOTE_BROWSER_VERSION) != null) {
            capabilities.setCapability("version", environmentVariables.getProperty(ThucydidesSystemProperty.WEBDRIVER_REMOTE_BROWSER_VERSION));
        }

        return capabilities;
    }

    private boolean shouldUseARemoteDriver() {
        return ThucydidesSystemProperty.WEBDRIVER_REMOTE_URL.isDefinedIn(environmentVariables);
    }

}
