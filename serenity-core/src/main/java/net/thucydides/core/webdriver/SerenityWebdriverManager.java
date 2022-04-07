package net.thucydides.core.webdriver;

import io.appium.java_client.AppiumDriver;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

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

    private final DriverConfiguration<DriverConfiguration> configuration;

    private final String options;

    private String overridenDefaultDriverType = null;


    public SerenityWebdriverManager(final WebDriverFactory webDriverFactory, final DriverConfiguration configuration) {
        this(webDriverFactory, configuration, "");
    }

    public SerenityWebdriverManager(final WebDriverFactory webDriverFactory,
                                    final DriverConfiguration configuration,
                                    final String options) {
        this.webDriverFactory = webDriverFactory;
        this.configuration = configuration;
        this.options = options;
    }

    /**
     * Create a new driver instance based on system property values. You can
     * override this method to use a custom driver if you really know what you
     * are doing.
     *
     * @throws DriverConfigurationError
     *             if the driver type is not supported.
     */
    private static WebDriver newDriver(final DriverConfiguration configuration,
                                       final WebDriverFactory webDriverFactory,
                                       final String driver,
                                       final String options) {
        SupportedWebDriver supportedDriverType = getConfiguredWebDriverWithOverride(configuration, driver);
        Class<? extends WebDriver> webDriverType = webDriverFactory.getClassFor(supportedDriverType);
        return WebdriverProxyFactory.getFactory().proxyFor(webDriverType,
                                                           webDriverFactory,
                                                           configuration,
                                                           options);
    }

    private static SupportedWebDriver getConfiguredWebDriverWithOverride(final DriverConfiguration configuration,
                                                                         final String driver) {
        if (isEmpty(driver)) {
            return configuration.getDriverType();
        }  else {
            return SupportedWebDriver.getDriverTypeFor(driver);
        }
    }

    public void closeDriver() {
        inThisTestThread().closeCurrentDriver();
    }

    public void closeCurrentDrivers() {
        inThisTestThread().closeCurrentDrivers();
    }

    public void closeAllDrivers() {
        inThisTestThread().closeAllDrivers();
    }

    public void reset() {
        inThisTestThread().closeAllDrivers();

    }

    @Override
    public WebdriverManager withOptions(String driverOptions) {
        return new SerenityWebdriverManager(webDriverFactory, configuration, driverOptions);
    }

    @Override
    public void overrideProperties(Map<String, String> propertyValues) {
        configuration.getEnvironmentVariables().setProperties(propertyValues);
    }

    @Override
    public WebdriverManager withProperty(String property, String value) {
        EnvironmentVariables updatedEnvironmentVariables = configuration.getEnvironmentVariables().copy();
        updatedEnvironmentVariables.setProperty(property, value);
        return new SerenityWebdriverManager(webDriverFactory,
                                            configuration.withEnvironmentVariables(updatedEnvironmentVariables),
                                            options);
    }
    public void resetDriver() {
        inThisTestThread().resetCurrentDriver();
    }

    public WebDriver getWebdriver() {
        String currentDriverName = (isNotEmpty(inThisTestThread().getCurrentDriverName())) ?
            inThisTestThread().getCurrentDriverName() : getDefaultDriverType();


        return instantiatedThreadLocalWebDriver(configuration,
                                                webDriverFactory,
                                                currentDriverName,
                                                options);
    }

    @Override
    public WebdriverContext inContext(String context) {
        return new WebdriverContext(this, context);
    }

    @Override
    public void setCurrentDriver(WebDriver driver) {

        inThisTestThread().setCurrentDriverTo(driver);
    }

    public void clearCurrentDriver() {
        inThisTestThread().resetCurrentDriver();
    }

    public static void resetThisThread() {
        webdriverInstancesThreadLocal.remove();
    }

    @Override
    public void registerDriver(WebDriver driver) {
        if (driver != null) {
            inThisTestThread().registerDriverCalled(nameOf(driver)).forDriver(driver);
            inThisTestThread().setCurrentDriverTo(driver);
        }
    }

    private String nameOf(WebDriver driver) {
        if (driver instanceof WebDriverFacade) {
            return ((WebDriverFacade) driver).getDriverName();
        }
        if(driver instanceof AppiumDriver){
            return "appium";
        }
        if ((driver instanceof RemoteWebDriver) && ((RemoteWebDriver) driver).getCapabilities() != null) {
            return ((RemoteWebDriver) driver).getCapabilities().getBrowserName();
        }
        return driver.toString();
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
        return Optional.ofNullable(overridenDefaultDriverType).orElse(configuration.getDriverType().name());
    }

    @Override
    public void overrideDefaultDriverType(String driverType) {
        overridenDefaultDriverType = isEmpty(driverType) ? null : driverType;
    }

    public SessionId getSessionId() {

        WebDriver driver = inThisTestThread().getCurrentDriver();

        if((driver instanceof WebDriverFacade) && (((WebDriverFacade) driver).isInstantiated())){
            WebDriver proxiedDriver = ((WebDriverFacade) driver).getDriverInstance();
            return sessionIdOf(proxiedDriver);
        }
        return sessionIdOf(driver);
    }

    private SessionId sessionIdOf(WebDriver driver) {
        if (driver instanceof RemoteWebDriver) {
            return ((RemoteWebDriver) driver).getSessionId();
        }
        return null;
    }

    public WebDriver getWebdriver(final String driverName, String options) {

        String name = (isEmpty(driverName)) ?  inThisTestThread().getCurrentDriverName() : driverName;

        return instantiatedThreadLocalWebDriver(configuration, webDriverFactory, name, options);
    }

    public WebDriver getWebdriver(final String driverName) {
        String defaultDriverOptions = ThucydidesWebDriverSupport.getDefaultDriverOptions().orElse("");
        String activeOptions = (StringUtils.isEmpty(options)) ? defaultDriverOptions : options;
        return getWebdriver(driverName, activeOptions);
    }

    public WebDriver getCurrentDriver() {
        return inThisTestThread().getCurrentDriver();
    }

    public WebDriver getWebdriverByName(String name, String driver) {
        return getWebdriver(driver + ":" + name);
    }

    public WebDriver getWebdriverByName(String name) {
        return getWebdriverByName(name,configuration.getDriverType().name());
    }

    private static WebDriver instantiatedThreadLocalWebDriver(final DriverConfiguration configuration,
                                                              final WebDriverFactory webDriverFactory,
                                                              final String driver,
                                                              final String options) {

        String uniqueDriverName = uniqueDriverNameFor(driver, options);

        if (!inThisTestThread().driverIsRegisteredFor(uniqueDriverName)) {
            inThisTestThread().registerDriverCalled(uniqueDriverName)
                              .forDriver(newDriver(configuration,
                                         webDriverFactory,
                                         driverTypeOf(driver),
                                         options));

        }
        return inThisTestThread().useDriver(uniqueDriverName);
    }

    private static String uniqueDriverNameFor(String driver, String options) {
        return driver + ((isEmpty(options)) ? "" : ":" + options);
    }

    private static String driverTypeOf(String driverName) {
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
        return inThisTestThread().getActiveWebdriverCount();
    }

    public boolean hasAnInstantiatedDriver() {
        return inThisTestThread().hasAnInstantiatedDriver();
    }

}