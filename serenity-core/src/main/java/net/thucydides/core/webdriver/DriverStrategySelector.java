package net.thucydides.core.webdriver;

import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.core.webdriver.capabilities.BrowserStackRemoteDriverCapabilities;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import static net.thucydides.model.ThucydidesSystemProperty.SAUCELABS_URL;

/**
 * Created by john on 25/06/2016.
 */
public class DriverStrategySelector {

    private final EnvironmentVariables environmentVariables;
    private final BrowserStackRemoteDriverCapabilities browserStackRemoteDriverCapabilities;

    public DriverStrategySelector(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.browserStackRemoteDriverCapabilities = new BrowserStackRemoteDriverCapabilities(environmentVariables);
    }

    public static DriverStrategySelector inEnvironment(EnvironmentVariables environmentVariables) {
        return new DriverStrategySelector(environmentVariables);
    }

    public SupportedWebDriver forDriverClass(Class<? extends WebDriver> driverClass) {
        // Driver type defined as 'remote'
        if (isARemoteDriver(driverClass) || shouldUseARemoteDriver()) {
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

    public boolean sauceLabsIsUsed() {
        return StringUtils.isNotEmpty(SAUCELABS_URL.from(environmentVariables));
    }

    public boolean browserStackUrlIsDefined() {
        return StringUtils.isNotEmpty(browserStackRemoteDriverCapabilities.getUrl());
    }
}
