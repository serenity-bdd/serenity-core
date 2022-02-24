package net.thucydides.core.webdriver;

import io.appium.java_client.android.AndroidDriver;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.serenitybdd.core.pages.DefaultTimeouts;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.stubs.*;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.interactions.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;

/**
 * A proxy class for webdriver instances, designed to prevent the browser being opened unnecessarily.
 */
public class WebDriverFacade implements WebDriver, TakesScreenshot, HasInputDevices, JavascriptExecutor, HasCapabilities, ConfigurableTimeouts, Interactive {

    private final Class<? extends WebDriver> driverClass;

    private final WebDriverFactory webDriverFactory;

    protected WebDriver proxiedWebDriver;

    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverFacade.class);

    private EnvironmentVariables environmentVariables;

    private String options = "";

    /**
     * Implicit timeout values recorded to that they can be restored after calling findElements()
     */
    Duration implicitTimeout;

    public WebDriverFacade(final Class<? extends WebDriver> driverClass,
                           final WebDriverFactory webDriverFactory) {
        this(driverClass, webDriverFactory, ConfiguredEnvironment.getEnvironmentVariables());
    }

    public WebDriverFacade(final Class<? extends WebDriver> driverClass,
                           final WebDriverFactory webDriverFactory,
                           final EnvironmentVariables environmentVariables ) {
        this.driverClass = driverClass;
        this.webDriverFactory = webDriverFactory;
        this.environmentVariables = environmentVariables;
        this.implicitTimeout = defaultImplicitWait();
    }

    public WebDriverFacade(final WebDriver driver,
                           final WebDriverFactory webDriverFactory,
                           final EnvironmentVariables environmentVariables ) {
        this.driverClass = driver.getClass();
        this.proxiedWebDriver = driver;
        this.webDriverFactory = webDriverFactory;
        this.environmentVariables = environmentVariables;
        this.implicitTimeout = defaultImplicitWait();
    }

    private Duration defaultImplicitWait() {
        int configuredWaitForTimeoutInMilliseconds = ThucydidesSystemProperty.WEBDRIVER_TIMEOUTS_IMPLICITLYWAIT
                        .integerFrom(environmentVariables, (int) DefaultTimeouts.DEFAULT_IMPLICIT_WAIT_TIMEOUT.toMillis());

       return Duration.ofMillis(configuredWaitForTimeoutInMilliseconds);
    }

    public WebDriverFacade(final Class<? extends WebDriver> driverClass,
                           final WebDriverFactory webDriverFactory,
                           WebDriver proxiedWebDriver,
                           Duration implicitTimeout) {
        this.driverClass = driverClass;
        this.webDriverFactory = webDriverFactory;
        this.proxiedWebDriver = proxiedWebDriver;
        this.implicitTimeout = implicitTimeout;
    }


    public WebDriverFacade withTimeoutOf(Duration implicitTimeout) {
        return new WebDriverFacade(driverClass, webDriverFactory, proxiedWebDriver, implicitTimeout);
    }

    public Class<? extends WebDriver>  getDriverClass() {
        if (proxiedWebDriver != null) {
            return getProxiedDriver().getClass();
        }

        if (driverClass.isAssignableFrom(SupportedWebDriver.PROVIDED.getWebdriverClass())) {
            return new ProvidedDriverConfiguration(environmentVariables).getDriverSource().driverType();
        }

        return driverClass;
    }

    public WebDriver getProxiedDriver() {
        if (proxiedWebDriver == null) {
            proxiedWebDriver = newProxyDriver();
            WebdriverProxyFactory.getFactory().notifyListenersOfWebdriverCreationIn(this);
        }
        ThucydidesWebDriverSupport.initialize();
        ThucydidesWebDriverSupport.getWebdriverManager().setCurrentDriver(this);
        return proxiedWebDriver;
    }

    public boolean isEnabled() {
        return !StepEventBus.getEventBus().webdriverCallsAreSuspended();
    }

    public void reset() {
        if (proxiedWebDriver != null) {
            forcedQuit();
        }
        proxiedWebDriver = null;

    }

    private void forcedQuit() {
        try {
            getDriverInstance().quit();
            proxiedWebDriver = null;
        } catch (WebDriverException e) {
            LOGGER.warn("Closing a driver that was already closed: " + e.getMessage());
        }
    }

    private WebDriver newProxyDriver() {
        return newDriverInstance();
    }

    private WebDriver newDriverInstance() {
        try {
            if (StepEventBus.getEventBus().isDryRun()) {
                return new WebDriverStub();
            } else {
                webDriverFactory.setupFixtureServices();
                return webDriverFactory.newWebdriverInstance(driverClass, options, environmentVariables);
            }
        } catch (DriverConfigurationError e) {
            throw new DriverConfigurationError("Could not instantiate " + driverClass, e);
        }
    }

    public <X> X getScreenshotAs(final OutputType<X> target) {
        if (proxyInstanciated() && driverCanTakeScreenshots()) {
            try {
                return ((TakesScreenshot) getProxiedDriver()).getScreenshotAs(target);
            } catch (OutOfMemoryError outOfMemoryError) {
                // Out of memory errors can happen with extremely big screens, and currently Selenium does
                // not handle them correctly/at all.
                LOGGER.error("Failed to take screenshot - out of memory", outOfMemoryError);
            } catch (RuntimeException e) {
                LOGGER.warn("Failed to take screenshot (" + e.getMessage() + ")");
            }
        }
        return null;
    }

    private boolean driverCanTakeScreenshots() {
        return (TakesScreenshot.class.isAssignableFrom(getDriverClass()));
    }

    public void get(final String url) {
        if (!isEnabled()) {
            return;
        }

        getProxiedDriver().get(url);
        setTimeouts();
    }


    private void setTimeouts() {
        webDriverFactory.setTimeouts(getProxiedDriver(), implicitTimeout);
    }

    public String getCurrentUrl() {
        if (!isEnabled() || !isInstantiated()) {
            return StringUtils.EMPTY;
        }

        return getProxiedDriver().getCurrentUrl();
    }

    public String getTitle() {
        if (!isEnabled() || !isInstantiated()) {
            return StringUtils.EMPTY;
        }

        return getProxiedDriver().getTitle();
    }

    @Override
    public List<WebElement> findElements(By by) {
        if (!isEnabled() || !isInstantiated()) {
            return Collections.emptyList();
        }
        List<WebElement> elements;
        try {
            webDriverFactory.setTimeouts(getProxiedDriver(), getCurrentImplicitTimeout());
            elements = getProxiedDriver().findElements(by);
        } finally {
            webDriverFactory.resetTimeouts(getProxiedDriver());
        }
        return elements;
    }

    @Override
    public WebElement findElement(By by) {
        if (!isEnabled() || !isInstantiated()) {
            return new WebElementFacadeStub();
        }

        WebElement element;

        try {
            webDriverFactory.setTimeouts(getProxiedDriver(), getCurrentImplicitTimeout());
            element = getProxiedDriver().findElement(by);
        } finally {
            webDriverFactory.resetTimeouts(getProxiedDriver());
        }
        return element;
   }

    public String getPageSource() {
        if (!isEnabled() || !isInstantiated()) {
            return StringUtils.EMPTY;
        }
        try {
            return getProxiedDriver().getPageSource();
        } catch (WebDriverException pageSourceNotSupported) {
            return StringUtils.EMPTY;
        } catch (RuntimeException pageSourceFailedForSomeReason) {
            LOGGER.warn("Failed to get the page source code (" + pageSourceFailedForSomeReason.getMessage() + ")");
            return StringUtils.EMPTY;
        }
    }

    public void setImplicitTimeout(Duration implicitTimeout) {
        webDriverFactory.setTimeouts(getProxiedDriver(), implicitTimeout);
    }

    public Duration getCurrentImplicitTimeout() {
        return webDriverFactory.currentTimeoutFor(getProxiedDriver());
    }

    public Duration resetTimeouts() {
        return webDriverFactory.resetTimeouts(getProxiedDriver());
    }


    protected WebDriver getDriverInstance() {
        return proxiedWebDriver;
    }

    public void close() {
        if (proxyInstanciated()) {
        	//if there is only one window closing it means quitting the web driver
        	if (areWindowHandlesAllowed(getDriverInstance()) &&
                    getDriverInstance().getWindowHandles() != null && getDriverInstance().getWindowHandles().size() == 1){
                this.quit();
            } else{
        	    WebDriverInstanceEvents.bus().notifyOf(WebDriverLifecycleEvent.CLOSE).forDriver(getDriverInstance());
                getDriverInstance().close();
            }
        }
    }

    private boolean areWindowHandlesAllowed(final WebDriver driver){
        return !(driver instanceof AndroidDriver);
    }

    public void quit() {
        if (proxyInstanciated()) {
            try {
                getDriverInstance().quit();
                webDriverFactory.shutdownFixtureServices();
                webDriverFactory.releaseTimoutFor(getDriverInstance());

            } catch (WebDriverException e) {
                LOGGER.warn("Error while quitting the driver (" + e.getMessage() + ")", e);
            }
            proxiedWebDriver = null;
        }
    }

    protected boolean proxyInstanciated() {
        return (getDriverInstance() != null);
    }

    public Set<String> getWindowHandles() {
        if (!isEnabled() || !isInstantiated()) {
            return new HashSet<>();
        }

        return getProxiedDriver().getWindowHandles();
    }

    public String getWindowHandle() {
        if (!isEnabled() || !isInstantiated()) {
            return StringUtils.EMPTY;
        }

        return getProxiedDriver().getWindowHandle();
    }

    public TargetLocator switchTo() {
        if (!isEnabled() || !isInstantiated()) {
            return new TargetLocatorStub(this);
        }

        return getProxiedDriver().switchTo();
    }

    public Navigation navigate() {
        if (!isEnabled()) {
            return new NavigationStub();
        }

        return getProxiedDriver().navigate();
    }

    public Options manage() {
        if (!isEnabled()) {
            return new OptionsStub();
        }

        return new OptionsFacade(getProxiedDriver().manage(), this);
    }

    public boolean canTakeScreenshots() {
    	if (driverClass != null) {
    		if (driverClass == ProvidedDriver.class) {
    			return TakesScreenshot.class.isAssignableFrom(getDriverClass()) || (getDriverClass() == RemoteWebDriver.class);
    		} else {
    			return TakesScreenshot.class.isAssignableFrom(driverClass)
    					|| (driverClass == RemoteWebDriver.class);
    		}
    	} else {
    		return false;
    	}
    }

    public boolean isInstantiated() {
        return (driverClass != null) && (proxiedWebDriver != null);
    }

    public Keyboard getKeyboard() {
        if (!isEnabled() || !isInstantiated()) {
            return new KeyboardStub();
        }

        return ((HasInputDevices) getProxiedDriver()).getKeyboard();
    }

    public Mouse getMouse() {
        if (!isEnabled() || !isInstantiated()) {
            return new MouseStub();
        }

        return ((HasInputDevices) getProxiedDriver()).getMouse();
    }

    public Object executeScript(String script, Object... parameters) {
        if (!isEnabled() || !isInstantiated()) {
            return null;
        }
        return ((JavascriptExecutor) getProxiedDriver()).executeScript(script, parameters);
    }

    public Object executeAsyncScript(String script, Object... parameters) {
        if (!isEnabled() || !isInstantiated()) {
            return null;
        }
        return ((JavascriptExecutor) getProxiedDriver()).executeAsyncScript(script, parameters);
    }

    @Override
    public Capabilities getCapabilities() {
        if (!isEnabled()) {
            return new CapabilitiesStub();
        }
        return ((HasCapabilities) getProxiedDriver()).getCapabilities();
    }

    public String getDriverName() {
        return SupportedWebDriver.forClass(driverClass).name().toLowerCase();
    }

    @Override
    public String toString() {
        return (proxiedWebDriver == null) ? "Uninitialised WebDriverFacade" : proxiedWebDriver.toString();
    }

    public WebDriverFacade withOptions(String options) {
        this.options = options;
        return this;
    }

    public boolean isAProxyFor(Class<? extends WebDriver> somedriverClass) {
        return somedriverClass.isAssignableFrom(getDriverClass());
    }

    public boolean isDisabled() {
        return (proxyInstanciated() && proxiedWebDriver.getClass().getName().endsWith("Stub"));
    }

    @Override
    public void perform(Collection<Sequence> actions) {
        if (proxiedWebDriver instanceof Interactive) {
            ((Interactive) proxiedWebDriver).perform(actions);
            return;
        }
        throw new UnsupportedOperationException("Underlying driver does not implement advanced"
                + " user interactions yet.");
    }

    @Override
    public void resetInputState() {
        if (proxiedWebDriver instanceof Interactive) {
            ((Interactive) proxiedWebDriver).resetInputState();
            return;
        }
        throw new UnsupportedOperationException("Underlying driver does not implement advanced"
                + " user interactions yet.");
    }

    /**
     * Check whether the underlying driver supports DevTools
     * @return
     */
    public boolean hasDevTools() {
        return (getProxiedDriver() instanceof HasDevTools);
    }

    public DevTools getDevTools() {
        if (hasDevTools()) {
            return ((HasDevTools) getProxiedDriver()).getDevTools();
        }
        throw new DevToolsNotSupportedException("DevTools not supported for driver " + getProxiedDriver());
    }

}
