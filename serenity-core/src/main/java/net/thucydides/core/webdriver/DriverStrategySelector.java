package net.thucydides.core.webdriver;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.capabilities.BrowserStackRemoteDriverCapabilities;
import net.thucydides.core.webdriver.capabilities.SauceRemoteDriverCapabilities;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;


/**
 * Created by john on 25/06/2016.
 */
public class DriverStrategySelector {

    private final EnvironmentVariables environmentVariables;
    private final SauceRemoteDriverCapabilities sauceRemoteDriverCapabilities;
    private final BrowserStackRemoteDriverCapabilities browserStackRemoteDriverCapabilities;

    public DriverStrategySelector(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.browserStackRemoteDriverCapabilities = new BrowserStackRemoteDriverCapabilities(environmentVariables);
        this.sauceRemoteDriverCapabilities = new SauceRemoteDriverCapabilities(environmentVariables);
    }

    public static DriverStrategySelector inEnvironment(EnvironmentVariables environmentVariables) {
        return new DriverStrategySelector(environmentVariables);
    }

    public SupportedWebDriver forDriverClass(Class<? extends WebDriver> driverClass) {

        // Driver type defined as 'remote'
        if (isARemoteDriver(driverClass) || shouldUseARemoteDriver() || saucelabsUrlIsDefined() || browserStackUrlIsDefined()) {
            return SupportedWebDriver.REMOTE;
        }

        // A named browser, like 'firefox' or 'chrome'
        return SupportedWebDriver.forClass(driverClass);
    }

    public boolean isARemoteDriver(Class<? extends WebDriver> driverClass) {
        return (RemoteWebDriver.class == driverClass);
    }

    public boolean shouldUseARemoteDriver() {
        return ThucydidesSystemProperty.WEBDRIVER_REMOTE_URL.isDefinedIn(environmentVariables);
    }

    public boolean saucelabsUrlIsDefined() {
        return StringUtils.isNotEmpty(sauceRemoteDriverCapabilities.getUrl());
    }

    public boolean browserStackUrlIsDefined() {
        return StringUtils.isNotEmpty(browserStackRemoteDriverCapabilities.getUrl());
    }
}
