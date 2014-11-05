package net.thucydides.core.webdriver;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Manage WebDriver instances.
 * It instantiates browser drivers, based on the test configuration, and manages them for the
 * duration of the tests.
 * A webdriver manager needs to be thread-safe. Tests can potentially be run in parallel, and different
 * tests can use different drivers.
 *
 * @author johnsmart
 *
 */
public class ThucydidesWebdriverManager implements WebdriverManager {

    private static final ThreadLocal<WebdriverInstances> webdriverInstancesThreadLocal = new ThreadLocal<WebdriverInstances>();

    private final WebDriverFactory webDriverFactory;

    private final Configuration configuration;

    private final Set<WebDriver> allWebdriverInstances;

    @Inject
    public ThucydidesWebdriverManager(final WebDriverFactory webDriverFactory, final Configuration configuration) {
        this.webDriverFactory = webDriverFactory;
        this.configuration = configuration;
        this.allWebdriverInstances =  Collections.synchronizedSet(new HashSet<WebDriver>());
    }

    /**
     * Create a new driver instance based on system property values. You can
     * override this method to use a custom driver if you really know what you
     * are doing.
     *
     * @throws net.thucydides.core.webdriver.UnsupportedDriverException
     *             if the driver type is not supported.
     */
    private static WebDriver newDriver(final Configuration configuration,
                                       final WebDriverFactory webDriverFactory,
                                       final String driver) {
        SupportedWebDriver supportedDriverType = getConfiguredWebDriverWithOverride(configuration, driver);
        Class<? extends WebDriver> webDriverType = webDriverFactory.getClassFor(supportedDriverType);
        return WebdriverProxyFactory.getFactory().proxyFor(webDriverType, webDriverFactory);
    }

    private static SupportedWebDriver getConfiguredWebDriverWithOverride(final Configuration configuration,
                                                                         final String driver) {
        if (StringUtils.isEmpty(driver)) {
            return configuration.getDriverType();
        }  else {
            return SupportedWebDriver.getDriverTypeFor(driver);
        }
    }

    public void closeDriver() {
        WebDriver driver = inThisTestThread().closeCurrentDriver();
        if (driver != null) {
            allWebdriverInstances.remove(driver);
        }
    }

    public void closeAllCurrentDrivers() {
        Set<WebDriver> closedDrivers = inThisTestThread().closeAllDrivers();
        allWebdriverInstances.removeAll(closedDrivers);
    }

    public void closeAllDrivers() {
        synchronized (allWebdriverInstances) {
            for(WebDriver driver : allWebdriverInstances) {
                closeSafely(driver);
            }
            allWebdriverInstances.clear();
        }
    }

    private void closeSafely(WebDriver driver) {
        try {
            driver.close();
            driver.quit();
        } catch(Throwable ignored) {}
    }

    public void resetDriver() {
        inThisTestThread().resetCurrentDriver();
    }

    public WebDriver getWebdriver() {
        return getThreadLocalWebDriver(configuration, webDriverFactory, inThisTestThread().getCurrentDriverName());
    }

    public SessionId getSessionId() {
        WebDriver driver = getThreadLocalWebDriver(configuration, webDriverFactory,
                                                   inThisTestThread().getCurrentDriverName());
        if (driver instanceof RemoteWebDriver) {
            return ((RemoteWebDriver) driver).getSessionId();
        }
        return null;
    }

    public WebDriver getWebdriver(final String driverName) {
        WebDriver activeDriver;
        if (StringUtils.isEmpty(driverName)) {
            activeDriver = getWebdriver();
        } else {
            activeDriver = getThreadLocalWebDriver(configuration, webDriverFactory, driverName);
        }
        registerDriverInGlobalDrivers(activeDriver);
        return activeDriver;
    }

    private void registerDriverInGlobalDrivers(WebDriver activeDriver) {
        allWebdriverInstances.add(activeDriver);
    }

    private static WebDriver getThreadLocalWebDriver(final Configuration configuration,
                                                     final WebDriverFactory webDriverFactory,
                                                     final String driver) {


        if (!inThisTestThread().driverIsRegisteredFor(driver)) {
            inThisTestThread().registerDriverCalled(driver)
                              .forDriver(newDriver(configuration, webDriverFactory, driver));

        }
        return inThisTestThread().useDriver(driver);
    }

    private static WebdriverInstances inThisTestThread() {
        if (webdriverInstancesThreadLocal.get() == null) {
            webdriverInstancesThreadLocal.set(new WebdriverInstances());
        }
        return webdriverInstancesThreadLocal.get();
    }

    public int getCurrentActiveWebdriverCount() {
        return inThisTestThread().getActiveWebdriverCount();
    }

    public int getActiveWebdriverCount() {
        return allWebdriverInstances.size();
    }

}