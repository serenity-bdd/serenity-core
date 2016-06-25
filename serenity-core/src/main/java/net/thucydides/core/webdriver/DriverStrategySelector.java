package net.thucydides.core.webdriver;

import io.appium.java_client.AppiumDriver;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.capabilities.BrowserStackRemoteDriverCapabilities;
import net.thucydides.core.webdriver.capabilities.SauceRemoteDriverCapabilities;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

import static net.thucydides.core.webdriver.DriverStrategy.*;
import static net.thucydides.core.webdriver.DriverStrategy.Default;


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

    public DriverStrategy forDriverClass(Class<? extends WebDriver> driverClass) {
        if (isAnAppiumDriver(driverClass)) {
            return Appium;
        }
        if (isARemoteDriver(driverClass) || shouldUseARemoteDriver() || saucelabsUrlIsDefined() || browserStackUrlIsDefined()) {
            return Remote;
        }
        if (isAFirefoxDriver(driverClass)) {
            return Firefox;
        }
        if (isAnHtmlUnitDriver(driverClass)) {
            return HtmlUnit;
        }
        if (isAPhantomJSDriver(driverClass)) {
            return PhantomJS;
        } else if (isAChromeDriver(driverClass)) {
            return Chrome;
        }
        if (isASafariDriver(driverClass)) {
            return Safari;
        }
        if (isAnInternetExplorerDriver(driverClass)) {
            return IE;
        } else if (IsAMicrosoftEdgeDriver(driverClass)) {
            return Edge;
        } else if (isAProvidedDriver(driverClass)) {
            return Provided;
        } else {
            return Default;
        }
    }

    public boolean isARemoteDriver(Class<? extends WebDriver> driverClass) {
        return (RemoteWebDriver.class == driverClass);
    }

    public boolean isAFirefoxDriver(Class<? extends WebDriver> driverClass) {
        return (FirefoxDriver.class.isAssignableFrom(driverClass));
    }

    public boolean isAChromeDriver(Class<? extends WebDriver> driverClass) {
        return (ChromeDriver.class.isAssignableFrom(driverClass));
    }

    public boolean isASafariDriver(Class<? extends WebDriver> driverClass) {
        return (SafariDriver.class.isAssignableFrom(driverClass));
    }

    public boolean isAnInternetExplorerDriver(Class<? extends WebDriver> driverClass) {
        return (InternetExplorerDriver.class.isAssignableFrom(driverClass));
    }

    public boolean IsAMicrosoftEdgeDriver(Class<? extends WebDriver> driverClass) {
        return (EdgeDriver.class.isAssignableFrom(driverClass));
    }

    public boolean isAnAppiumDriver(Class<? extends WebDriver> driverClass) {
        return (AppiumDriver.class.isAssignableFrom(driverClass));
    }

    public boolean isAnHtmlUnitDriver(Class<? extends WebDriver> driverClass) {
        return (HtmlUnitDriver.class.isAssignableFrom(driverClass));
    }

    public boolean isAPhantomJSDriver(Class<? extends WebDriver> driverClass) {
        return (PhantomJSDriver.class.isAssignableFrom(driverClass));
    }

    public boolean isAProvidedDriver(Class<? extends WebDriver> driverClass) {
        return ProvidedDriver.class.isAssignableFrom(driverClass);
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
