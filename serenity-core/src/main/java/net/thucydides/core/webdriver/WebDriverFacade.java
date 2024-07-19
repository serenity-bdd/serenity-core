package net.thucydides.core.webdriver;

import io.appium.java_client.android.AndroidDriver;
import net.serenitybdd.core.SystemTimeouts;
import net.serenitybdd.core.di.SerenityInfrastructure;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.webdriver.stubs.*;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.federatedcredentialmanagement.FederatedCredentialManagementDialog;
import org.openqa.selenium.federatedcredentialmanagement.HasFederatedCredentialManagement;
import org.openqa.selenium.interactions.Interactive;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.print.PrintOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.virtualauthenticator.HasVirtualAuthenticator;
import org.openqa.selenium.virtualauthenticator.VirtualAuthenticator;
import org.openqa.selenium.virtualauthenticator.VirtualAuthenticatorOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A proxy class for webdriver instances, designed to prevent the browser being opened unnecessarily.
 */
//public class RemoteWebDriver implements WebDriver, JavascriptExecutor, HasCapabilities, HasDownloads, HasFederatedCredentialManagement, HasVirtualAuthenticator, Interactive, PrintsPage, TakesScreenshot {
public class WebDriverFacade implements WebDriver, JavascriptExecutor, HasCapabilities,
        HasDownloads, HasFederatedCredentialManagement, HasVirtualAuthenticator,
        Interactive, PrintsPage, TakesScreenshot,
        ConfigurableTimeouts, HasAuthentication {

    private final Class<? extends WebDriver> driverClass;

    private final WebDriverFactory webDriverFactory;

    protected WebDriver proxiedWebDriver;

    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverFacade.class);

    private EnvironmentVariables environmentVariables;

    private String options = "";

    private EnvironmentVariables getEnvironmentVariables() {
        if (environmentVariables != null) {
            return environmentVariables;
        }
        return SystemEnvironmentVariables.currentEnvironmentVariables();
    }

    /**
     * Implicit timeout values recorded to that they can be restored after calling findElements()
     */
    Duration implicitTimeout;

    public WebDriverFacade(final Class<? extends WebDriver> driverClass,
                           final WebDriverFactory webDriverFactory) {
        this.driverClass = driverClass;
        this.webDriverFactory = webDriverFactory;
        this.environmentVariables = SerenityInfrastructure.getEnvironmentVariables();
        this.implicitTimeout = defaultImplicitWait();
    }

    public WebDriverFacade(final WebDriver driver,
                           final WebDriverFactory webDriverFactory) {
        this.driverClass = driver.getClass();
        this.proxiedWebDriver = driver;
        this.webDriverFactory = webDriverFactory;
        this.environmentVariables = SerenityInfrastructure.getEnvironmentVariables();
        this.implicitTimeout = defaultImplicitWait();
    }


    public WebDriverFacade(final WebDriver driver,
                           final WebDriverFactory webDriverFactory,
                           final EnvironmentVariables environmentVariables) {
        this.driverClass = driver.getClass();
        this.proxiedWebDriver = driver;
        this.webDriverFactory = webDriverFactory;
        this.environmentVariables = environmentVariables;
        this.implicitTimeout = defaultImplicitWait();
    }

    private Duration defaultImplicitWait() {
        long configuredWaitForTimeoutInMilliseconds = new SystemTimeouts(environmentVariables).getImplicitTimeout();
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

    public Class<? extends WebDriver> getDriverClass() {
        if (proxiedWebDriver != null) {
            return getProxiedDriver().getClass();
        }

        if (driverClass.isAssignableFrom(SupportedWebDriver.PROVIDED.getWebdriverClass())) {
            return new ProvidedDriverConfiguration(getEnvironmentVariables()).getDriverSource().driverType();
        }

        return driverClass;
    }

    public WebDriver getProxiedDriver() {
        if (StepEventBus.getParallelEventBus().isDryRun()) {
            return new WebDriverStub();
        }
        if (proxiedWebDriver == null) {
            proxiedWebDriver = newProxyDriver();
            WebdriverProxyFactory.getFactory().notifyListenersOfWebdriverCreationIn(this);
        }
        ThucydidesWebDriverSupport.initialize();
        ThucydidesWebDriverSupport.getWebdriverManager().setCurrentDriver(this);
        return proxiedWebDriver;
    }

    public boolean isEnabled() {
        return !StepEventBus.getParallelEventBus().webdriverCallsAreSuspended();
    }

    public void reset() {
        if (proxiedWebDriver != null) {
            forcedQuit();
        }
        proxiedWebDriver = null;
    }

    public void reinitializeRemoteWebDriver() {
        if ((proxiedWebDriver != null) && (proxiedWebDriver instanceof RemoteWebDriver)) {
            forcedQuit();
            proxiedWebDriver = null;
            initializeProxiedDriver();
        }
    }

    private void initializeProxiedDriver() {
        if (StepEventBus.getParallelEventBus().isDryRun()) {
            proxiedWebDriver = new WebDriverStub();
        }
        if (proxiedWebDriver == null) {
            proxiedWebDriver = newProxyDriver();
            WebdriverProxyFactory.getFactory().notifyListenersOfWebdriverCreationIn(this);
        }
        ThucydidesWebDriverSupport.initialize();
        ThucydidesWebDriverSupport.getWebdriverManager().setCurrentDriver(this);
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
            if (StepEventBus.getParallelEventBus().isDryRun()) {
                return new WebDriverStub();
            } else {
                webDriverFactory.setupFixtureServices();
                return webDriverFactory.newWebdriverInstance(driverClass, options, getEnvironmentVariables());
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
                // Out of memory errors can happen with big screens, and currently Selenium does
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
                    getDriverInstance().getWindowHandles() != null && getDriverInstance().getWindowHandles().size() == 1) {
                this.quit();
            } else {
                WebDriverInstanceEvents.bus().notifyOf(WebDriverLifecycleEvent.CLOSE).forDriver(getDriverInstance());
                getDriverInstance().close();
            }
        }
    }

    private boolean areWindowHandlesAllowed(final WebDriver driver) {
        return !(driver instanceof AndroidDriver);
    }

    public void quit() {
        if (proxyInstanciated()) {
            try {
                getDriverInstance().quit();
                webDriverFactory.shutdownFixtureServices();
                webDriverFactory.releaseTimoutFor(getDriverInstance());

            } catch (WebDriverException e) {
                LOGGER.warn("Error while quitting the driver (" + e.getMessage() + ")");
                LOGGER.debug("Caused by:" + e.getMessage(), e);
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

    /**
     * Registers a check for whether a set of Credentials should be used for a particular site, identified by its URI. If called multiple times, the credentials will be checked in the order they've been added and the first one to match will be used.
     */
    @Override
    public void register(Predicate<URI> whenThisMatches, Supplier<Credentials> useTheseCredentials) {
        ensureDriverSupportsTheHasAuthenticationInterface();
        ((HasAuthentication) getProxiedDriver()).register(whenThisMatches, useTheseCredentials);
    }

    /**
     * As register(Predicate, Supplier) but attempts to apply the credentials for any request for authorization.
     */
    @Override
    public void register(Supplier<Credentials> alwaysUseTheseCredentials) {
        ensureDriverSupportsTheHasAuthenticationInterface();
        ((HasAuthentication) getProxiedDriver()).register(alwaysUseTheseCredentials);
    }

    private void ensureDriverSupportsTheHasAuthenticationInterface() {
        if (!(getProxiedDriver() instanceof HasAuthentication)) {
            throw new HasAuthenticationNotSupportedException("HasAuthentication not supported for driver " + getProxiedDriver());
        }
    }

    @Override
    public List<String> getDownloadableFiles() {
        if (getProxiedDriver() instanceof HasDownloads) {
            return ((HasDownloads) getProxiedDriver()).getDownloadableFiles();
        } else {
            throw new IllegalStateException("Webdriver class " + getProxiedDriver().getClass() + " does not implement HasDownloads");
        }
    }

    @Override
    public void downloadFile(String fileName, Path targetLocation) throws IOException {
        if (getProxiedDriver() instanceof HasDownloads) {
            ((HasDownloads) getProxiedDriver()).downloadFile(fileName, targetLocation);
        }
    }

    @Override
    public void deleteDownloadableFiles() {
        if (getProxiedDriver() instanceof HasDownloads) {
            ((HasDownloads) getProxiedDriver()).deleteDownloadableFiles();
        }
    }

    @Override
    public Pdf print(PrintOptions printOptions) throws WebDriverException {
        if (getProxiedDriver() instanceof PrintsPage) {
            return ((PrintsPage) getProxiedDriver()).print(printOptions);
        } else {
            throw new IllegalStateException("Webdriver class " + getProxiedDriver().getClass() + " does not implement PrintsPage");
        }
    }

    @Override
    public void setDelayEnabled(boolean enabled) {
        if (getProxiedDriver() instanceof HasFederatedCredentialManagement) {
            ((HasFederatedCredentialManagement) getProxiedDriver()).setDelayEnabled(enabled);
        }
    }

    @Override
    public void resetCooldown() {
        if (getProxiedDriver() instanceof HasFederatedCredentialManagement) {
            ((HasFederatedCredentialManagement) getProxiedDriver()).resetCooldown();
        }
    }

    @Override
    public FederatedCredentialManagementDialog getFederatedCredentialManagementDialog() {
        if (getProxiedDriver() instanceof HasFederatedCredentialManagement) {
            return ((HasFederatedCredentialManagement) getProxiedDriver()).getFederatedCredentialManagementDialog();
        } else {
            throw new IllegalStateException("Webdriver class " + getProxiedDriver().getClass() + " does not implement FederatedCredentialManagementDialog");
        }
    }

    @Override
    public VirtualAuthenticator addVirtualAuthenticator(VirtualAuthenticatorOptions options) {
        if (getProxiedDriver() instanceof HasVirtualAuthenticator) {
            return ((HasVirtualAuthenticator) getProxiedDriver()).addVirtualAuthenticator(options);
        } else {
            throw new IllegalStateException("Webdriver class " + getProxiedDriver().getClass() + " does not implement VirtualAuthenticator");
        }
    }

    @Override
    public void removeVirtualAuthenticator(VirtualAuthenticator authenticator) {
        if (getProxiedDriver() instanceof HasVirtualAuthenticator) {
            ((HasVirtualAuthenticator) getProxiedDriver()).removeVirtualAuthenticator(authenticator);
        }
    }

//    @Override
//    public SessionId getSessionId() {
//        if (getProxiedDriver() instanceof RemoteWebDriver) {
//            return ((RemoteWebDriver) getProxiedDriver()).getSessionId();
//        } else {
//            throw new IllegalStateException("Webdriver class " + getProxiedDriver().getClass() + " does not implement RemoteWebDriver");
//        }
//    }
//
//    @Override
//    protected void setSessionId(String opaqueKey) {
//        // use the proxied driver if it is a RemoteWebDriver
//        try {
//            if (getProxiedDriver() instanceof RemoteWebDriver) {
//                Field sessionIdField = RemoteWebDriver.class.getDeclaredField("sessionId");
//                sessionIdField.setAccessible(true); // Make the field accessible
//                sessionIdField.set(getProxiedDriver(), new SessionId(opaqueKey)); // Set the new session ID
//            }
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            LOGGER.warn("Could not set the session ID for the proxied driver", e);
//        }
//    }
//
//    @Override
//    protected void startSession(Capabilities capabilities) {
//        // use the proxied driver if it is a RemoteWebDriver
//        try {
//            if (getProxiedDriver() instanceof RemoteWebDriver) {
//                // Invoke the startSession method on the proxied driver using reflection
//                Method startSessionMethod = RemoteWebDriver.class.getDeclaredMethod("startSession", Capabilities.class);
//                startSessionMethod.setAccessible(true); // Make the method accessible
//                startSessionMethod.invoke(getProxiedDriver(), capabilities);
//            }
//        } catch (NoSuchMethodException | IllegalAccessException e) {
//            LOGGER.warn("Could not set the session ID for the proxied driver", e);
//        } catch (InvocationTargetException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public ErrorHandler getErrorHandler() {
//        if (getProxiedDriver() instanceof RemoteWebDriver) {
//            return ((RemoteWebDriver) getProxiedDriver()).getErrorHandler();
//        } else {
//            throw new IllegalStateException("Webdriver class " + getProxiedDriver().getClass() + " does not implement RemoteWebDriver");
//        }
//    }
//
//    @Override
//    public void setErrorHandler(ErrorHandler handler) {
//        if (getProxiedDriver() instanceof RemoteWebDriver) {
//            ((RemoteWebDriver) getProxiedDriver()).setErrorHandler(handler);
//        } else {
//            throw new IllegalStateException("Webdriver class " + getProxiedDriver().getClass() + " does not implement RemoteWebDriver");
//        }
//    }
//
//    @Override
//    public CommandExecutor getCommandExecutor() {
//        if (getProxiedDriver() instanceof RemoteWebDriver) {
//            return ((RemoteWebDriver) getProxiedDriver()).getCommandExecutor();
//        } else {
//            throw new IllegalStateException("Webdriver class " + getProxiedDriver().getClass() + " does not implement RemoteWebDriver");
//        }
//    }
//
//    @Override
//    protected void setCommandExecutor(CommandExecutor executor) {
//        try {
//            if (getProxiedDriver() instanceof RemoteWebDriver) {
//                Method method = RemoteWebDriver.class.getDeclaredMethod("setCommandExecutor", CommandExecutor.class);
//                method.setAccessible(true); // Make the method accessible
//                method.invoke(getProxiedDriver(), executor);
//            }
//        } catch (NoSuchMethodException | IllegalAccessException e) {
//            LOGGER.warn("Could not call setCommandExecutor for the proxied driver", e);
//        } catch (InvocationTargetException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public List<WebElement> findElements(SearchContext context, BiFunction<String, Object, CommandPayload> findCommand, By locator) {
//        if (proxiedWebDriver instanceof RemoteWebDriver) {
//            return ((RemoteWebDriver) proxiedWebDriver).findElements(context, findCommand, locator);
//        } else {
//            LOGGER.warn("Could not call findElements() on " + proxiedWebDriver.getClass());
//            return new ArrayList<>();
//        }
//    }
//
//    @Override
//    protected void setFoundBy(SearchContext context, WebElement element, String by, String using) {
//        try {
//            if (proxiedWebDriver instanceof RemoteWebDriver) {
//                Method method = RemoteWebDriver.class.getDeclaredMethod("setFoundBy", SearchContext.class, WebElement.class, String.class, String.class);
//                method.setAccessible(true); // Make the method accessible
//                method.invoke(getProxiedDriver(), context, element, by, using);
//            } else {
//                LOGGER.warn("Could not call setFoundBy() on " + proxiedWebDriver.getClass());
//            }
//        } catch (NoSuchMethodException | IllegalAccessException e) {
//            LOGGER.warn("Could not call setFoundBy for the proxied driver", e);
//        } catch (InvocationTargetException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    protected JsonToWebElementConverter getElementConverter() {
//        try {
//            if (proxiedWebDriver instanceof RemoteWebDriver) {
//                Method method = RemoteWebDriver.class.getDeclaredMethod("getElementConverter");
//                method.setAccessible(true); // Make the method accessible
//                return (JsonToWebElementConverter) method.invoke(getProxiedDriver());
//            } else {
//                LOGGER.warn("Could not call getElementConverter() on " + proxiedWebDriver.getClass());
//                return null;
//            }
//        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
//            LOGGER.warn("Could not call getElementConverter for the proxied driver", e);
//            return null;
//        }
//    }
//
//    @Override
//    protected void setElementConverter(JsonToWebElementConverter converter) {
//        try {
//            if (proxiedWebDriver instanceof RemoteWebDriver) {
//                Method method = RemoteWebDriver.class.getDeclaredMethod("setElementConverter", JsonToWebElementConverter.class);
//                method.setAccessible(true); // Make the method accessible
//                method.invoke(getProxiedDriver(), converter);
//            } else {
//                LOGGER.warn("Could not call setElementConverter() on " + proxiedWebDriver.getClass());
//            }
//        } catch (NoSuchMethodException | IllegalAccessException e) {
//            LOGGER.warn("Could not call setFoundBy for the proxied driver", e);
//        } catch (InvocationTargetException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public void setLogLevel(Level level) {
//        if (proxiedWebDriver instanceof RemoteWebDriver) {
//            ((RemoteWebDriver) proxiedWebDriver).setLogLevel(level);
//        } else {
//            LOGGER.warn("Could not call setLogLevel() on " + proxiedWebDriver.getClass());
//        }
//    }
//
//    @Override
//    protected Response execute(CommandPayload payload) {
//        try {
//            if (proxiedWebDriver instanceof RemoteWebDriver) {
//                Method method = RemoteWebDriver.class.getDeclaredMethod("execute", CommandPayload.class);
//                method.setAccessible(true); // Make the method accessible
//                return (Response) method.invoke(getProxiedDriver(), payload);
//            } else {
//                LOGGER.warn("Could not call execute() on " + proxiedWebDriver.getClass());
//                return null;
//            }
//        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
//            LOGGER.warn("Could not call execute for the proxied driver", e);
//            return null;
//        }
//    }
//
//    @Override
//    protected Response execute(String driverCommand, Map<String, ?> parameters) {
//        try {
//            if (proxiedWebDriver instanceof RemoteWebDriver) {
//                Method method = RemoteWebDriver.class.getDeclaredMethod("execute", String.class, Map.class);
//                method.setAccessible(true); // Make the method accessible
//                return (Response) method.invoke(getProxiedDriver(), driverCommand, parameters);
//            } else {
//                LOGGER.warn("Could not call execute() on " + proxiedWebDriver.getClass());
//                return null;
//            }
//        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
//            LOGGER.warn("Could not call execute for the proxied driver", e);
//            return null;
//        }
//    }
//
//    @Override
//    protected Response execute(String command) {
//        try {
//            if (proxiedWebDriver instanceof RemoteWebDriver) {
//                Method method = RemoteWebDriver.class.getDeclaredMethod("execute", String.class);
//                method.setAccessible(true); // Make the method accessible
//                return (Response) method.invoke(getProxiedDriver(), command);
//            } else {
//                LOGGER.warn("Could not call execute() on " + proxiedWebDriver.getClass());
//                return null;
//            }
//        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
//            LOGGER.warn("Could not call execute for the proxied driver", e);
//            return null;
//        }
//    }
//
//    @Override
//    protected ExecuteMethod getExecuteMethod() {
//        try {
//            if (proxiedWebDriver instanceof RemoteWebDriver) {
//                Method method = RemoteWebDriver.class.getDeclaredMethod("getExecuteMethod");
//                method.setAccessible(true); // Make the method accessible
//                return (ExecuteMethod) method.invoke(getProxiedDriver());
//            } else {
//                LOGGER.warn("Could not call getExecuteMethod() on " + proxiedWebDriver.getClass());
//                return null;
//            }
//        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
//            LOGGER.warn("Could not call getExecuteMethod for the proxied driver", e);
//            return null;
//        }
//    }
//
//    @Override
//    protected void log(SessionId sessionId, String commandName, Object toLog, When when) {
//        try {
//            if (proxiedWebDriver instanceof RemoteWebDriver) {
//                Method method = RemoteWebDriver.class.getDeclaredMethod("log", SessionId.class, String.class, Object.class, When.class);
//                method.setAccessible(true); // Make the method accessible
//                method.invoke(getProxiedDriver(), sessionId, commandName, toLog, when);
//            } else {
//                LOGGER.warn("Could not call log() on " + proxiedWebDriver.getClass());
//            }
//        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
//            LOGGER.warn("Could not call log for the proxied driver", e);
//        }
//    }
//
//    @Override
//    public FileDetector getFileDetector() {
//        if (proxiedWebDriver instanceof RemoteWebDriver) {
//            return ((RemoteWebDriver) proxiedWebDriver).getFileDetector();
//        } else {
//            LOGGER.warn("Could not call getFileDetector() on " + proxiedWebDriver.getClass());
//            return null;
//        }
//    }
//
//    @Override
//    public void setFileDetector(FileDetector detector) {
//        if (proxiedWebDriver instanceof RemoteWebDriver) {
//            ((RemoteWebDriver) proxiedWebDriver).setFileDetector(detector);
//        } else {
//            LOGGER.warn("Could not call setFileDetector() on " + proxiedWebDriver.getClass());
//        }
//    }

    @Override
    public void requireDownloadsEnabled(Capabilities capabilities) {
        if (getProxiedDriver() instanceof HasDownloads) {
            ((HasDownloads) getProxiedDriver()).requireDownloadsEnabled(capabilities);
        }
    }

    @Override
    public ScriptKey pin(String script) {
        if (getProxiedDriver() instanceof JavascriptExecutor) {
            return ((JavascriptExecutor) getProxiedDriver()).pin(script);
        } else {
            return null;
        }
    }

    @Override
    public void unpin(ScriptKey key) {
        if (getProxiedDriver() instanceof JavascriptExecutor) {
            ((JavascriptExecutor) getProxiedDriver()).unpin(key);
        }
    }

    @Override
    public Set<ScriptKey> getPinnedScripts() {
        if (getProxiedDriver() instanceof JavascriptExecutor) {
            return ((JavascriptExecutor) getProxiedDriver()).getPinnedScripts();
        }
        return Collections.emptySet();
    }

    @Override
    public Object executeScript(ScriptKey key, Object... args) {
        if (getProxiedDriver() instanceof JavascriptExecutor) {
            return ((JavascriptExecutor) getProxiedDriver()).executeScript(key, args);
        }
        return null;
    }
}
