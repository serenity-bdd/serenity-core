package net.thucydides.core.webdriver;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isEmpty;

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
public class SerenityWebdriverManager implements WebdriverManager {

    private static final ThreadLocal<WebdriverInstances> webdriverInstancesThreadLocal = new ThreadLocal<>();

    private final WebDriverFactory webDriverFactory;

    private final Configuration configuration;

    private final Set<WebDriver> allWebdriverInstances;

    private Optional<String> overridenDefaultDriverType = Optional.absent();

    @Inject
    public SerenityWebdriverManager(final WebDriverFactory webDriverFactory, final Configuration configuration) {
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
        return WebdriverProxyFactory.getFactory().proxyFor(webDriverType,
                                                           webDriverFactory,
                                                           configuration);
    }

    private static SupportedWebDriver getConfiguredWebDriverWithOverride(final Configuration configuration,
                                                                         final String driver) {
        if (isEmpty(driver)) {
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
                safelyClose(driver);
            }
            allWebdriverInstances.clear();
        }
    }

    private void safelyClose(WebDriver driver) {
        try {
            driver.close();
            driver.quit();
        } catch(Throwable ignored) {}
    }

    public void resetDriver() {
        inThisTestThread().resetCurrentDriver();
    }

    public WebDriver getWebdriver() {
        return instantiatedThreadLocalWebDriver(configuration,
                                                webDriverFactory,
                                                getDefaultDriverType()
                                                /*inThisTestThread().getCurrentDriverName()*/);
    }

    @Override
    public WebdriverContext inContext(String context) {
        return new WebdriverContext(this, context);
    }

    @Override
    public void setCurrentDriver(WebDriver driver) {
        inThisTestThread().setCurrentDriverTo(driver);
    }

    @Override
    public void clearCurrentDriver() {

    }

    @Override
    public void registerDriver(WebDriver driver) {
        inThisTestThread().registerDriverCalled(driver.toString()).forDriver(driver);
        inThisTestThread().setCurrentDriverTo(driver);
    }

    @Override
    public List<WebDriver> getRegisteredDrivers() {
        return inThisTestThread().getActiveDrivers();
    }

    @Override
    public List<String> getActiveDriverTypes() {
        return inThisTestThread().getActiveDriverTypes();
    }

    public String getCurrentDriverType() {
        return inThisTestThread().getCurrentDriverType();
    }

    @Override
    public String getDefaultDriverType() {
        return overridenDefaultDriverType.or(configuration.getDriverType().name());
    }

    @Override
    public void overrideDefaultDriverType(String driverType) {
        overridenDefaultDriverType = Optional.fromNullable(isEmpty(driverType) ? null : driverType);
    }


    public SessionId getSessionId() {

        WebDriver driver = inThisTestThread().getCurrentDriver();

        if(driver instanceof WebDriverFacade){
            driver = ((WebDriverFacade) driver).getDriverInstance();
        }
        if (driver instanceof RemoteWebDriver) {
            return ((RemoteWebDriver) driver).getSessionId();
        }
        return null;
    }

    public WebDriver getWebdriver(final String driverName) {

        String name = (isEmpty(driverName)) ?  inThisTestThread().getCurrentDriverName() : driverName;

        WebDriver activeDriver = instantiatedThreadLocalWebDriver(configuration, webDriverFactory, name);

        registerDriverInGlobalDrivers(activeDriver);

        return activeDriver;
    }

    public WebDriver getWebdriverByName(String name, String driver) {
        return getWebdriver(driver + ":" + name);
    }

    public WebDriver getWebdriverByName(String name) {
        return getWebdriverByName(name,configuration.getDriverType().name());
    }

    private void registerDriverInGlobalDrivers(WebDriver activeDriver) {
        allWebdriverInstances.add(activeDriver);
    }

    private static WebDriver instantiatedThreadLocalWebDriver(final Configuration configuration,
                                                              final WebDriverFactory webDriverFactory,
                                                              final String driver) {


        if (!inThisTestThread().driverIsRegisteredFor(driver)) {
            inThisTestThread().registerDriverCalled(driver)
                              .forDriver(newDriver(configuration, webDriverFactory, driverTypeOf(driver)));

        }
        return inThisTestThread().useDriver(driver);
    }

    private static String driverTypeOf(String driverName) {
//        if (driverName.trim().startsWith(":")) {
//            return driverName.trim().substring(1);
//        }
        if (driverName.contains(":")) {
            return driverName.substring(0, driverName.indexOf(":"));
        }
        return driverName;
    }

    public static WebdriverInstances inThisTestThread() {
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

    public boolean isDriverInstantiated() {
        return inThisTestThread().isDriverInstantiated();
    }

}