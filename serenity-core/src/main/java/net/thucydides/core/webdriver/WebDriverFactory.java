package net.thucydides.core.webdriver;

import com.google.common.base.Splitter;
import io.appium.java_client.AppiumDriver;
import net.serenitybdd.core.di.WebDriverInjectors;
import net.serenitybdd.core.exceptions.SerenityManagedException;
import net.serenitybdd.core.pages.DefaultTimeouts;
import net.serenitybdd.core.webdriver.driverproviders.*;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.fixtureservices.FixtureException;
import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.core.fixtureservices.FixtureService;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.redimension.RedimensionBrowser;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static net.thucydides.core.ThucydidesSystemProperty.*;
import static net.thucydides.core.webdriver.DriverStrategySelector.inEnvironment;

/**
 * Provides an instance of a supported WebDriver.
 * When you instantiate a WebDriver instance for Firefox or Chrome, it opens a new browser.
 *
 * @author johnsmart
 */
public class WebDriverFactory {
    public static final String DEFAULT_DRIVER = "chrome";
    public static final String REMOTE_DRIVER = "remote";

    private final EnvironmentVariables environmentVariables;
    private final FixtureProviderService fixtureProviderService;

    private final CloseBrowser closeBrowser;

    private Map<SupportedWebDriver, DriverProvider> driverProvidersByDriverType;

    private final TimeoutStack timeoutStack;

    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverFactory.class);

    public WebDriverFactory() {
        this(Injectors.getInjector().getProvider(EnvironmentVariables.class).get());
    }

    public WebDriverFactory(EnvironmentVariables environmentVariables) {
        this(environmentVariables, WebDriverInjectors.getInjector().getInstance(FixtureProviderService.class));
    }

    public WebDriverFactory(EnvironmentVariables environmentVariables,
                            FixtureProviderService fixtureProviderService) {
        this.environmentVariables = environmentVariables;
        this.fixtureProviderService = fixtureProviderService;
        this.timeoutStack = new TimeoutStack();
        this.closeBrowser = WebDriverInjectors.getInjector().getInstance(CloseBrowser.class);
    }

    public WebDriverFactory(EnvironmentVariables environmentVariables,
                            FixtureProviderService fixtureProviderService,
                            TimeoutStack timeoutStack,
                            CloseBrowser closeBrowser) {
        this.environmentVariables = environmentVariables;
        this.fixtureProviderService = fixtureProviderService;
        this.timeoutStack = timeoutStack;
        this.closeBrowser = closeBrowser;
    }

    public WebDriverFactory withEnvironmentVariables(EnvironmentVariables environmentVariables) {
        return new WebDriverFactory(environmentVariables, fixtureProviderService, timeoutStack, closeBrowser);
    }

    /**
     * Create a new WebDriver instance of a given type.
     */
    public WebDriver newInstanceOf(final SupportedWebDriver driverType) {
        if (driverType == null) {
            throw new IllegalArgumentException("Driver type cannot be null");
        }
        return newWebdriverInstance(driverType.getWebdriverClass());
    }

    public Class<? extends WebDriver> getClassFor(final SupportedWebDriver driverType) {
        return driverType.getWebdriverClass();
    }

    public boolean usesSauceLabs() {
        return StringUtils.isNotEmpty(SAUCELABS_URL.from(environmentVariables));
    }

    private Map<SupportedWebDriver, DriverProvider> driverProviders() {

        if (driverProvidersByDriverType == null) {
            driverProvidersByDriverType = new HashMap<>();

            driverProvidersByDriverType.put(SupportedWebDriver.APPIUM, new AppiumDriverProvider(fixtureProviderService));
            driverProvidersByDriverType.put(SupportedWebDriver.REMOTE, new RemoteDriverProvider(fixtureProviderService));
            driverProvidersByDriverType.put(SupportedWebDriver.FIREFOX, new FirefoxDriverProvider(fixtureProviderService));
            driverProvidersByDriverType.put(SupportedWebDriver.CHROME, new ChromeDriverProvider(fixtureProviderService));
            driverProvidersByDriverType.put(SupportedWebDriver.SAFARI, new SafariDriverProvider(fixtureProviderService));
            driverProvidersByDriverType.put(SupportedWebDriver.IEXPLORER, new InternetExplorerDriverProvider(fixtureProviderService));
            driverProvidersByDriverType.put(SupportedWebDriver.EDGE, new EdgeDriverProvider(fixtureProviderService));
            driverProvidersByDriverType.put(SupportedWebDriver.PROVIDED, new ProvidedDriverProvider());
        }
        return driverProvidersByDriverType;
    }

    /**
     * This method is synchronized because multiple webdriver instances can be created in parallel.
     * However, they may use common system resources such as ports, so may potentially interfere
     * with each other.
     */
    protected synchronized WebDriver newWebdriverInstance(final Class<? extends WebDriver> driverClass) {
        String driverOptions = DRIVER_OPTIONS.from(environmentVariables, "");
        return newWebdriverInstance(driverClass, driverOptions);
    }

    private synchronized WebDriver newWebdriverInstance(final Class<? extends WebDriver> driverClass, String options) {
        return newWebdriverInstance(driverClass, options, environmentVariables);
    }

    protected synchronized WebDriver newWebdriverInstance(final Class<? extends WebDriver> driverClass,
                                                          String options,
                                                          EnvironmentVariables environmentVariables) {
        try {
            return createWebDriver(driverClass, options, environmentVariables);
        } catch (SerenityManagedException toPassThrough) {
            throw toPassThrough;
        } catch (Exception cause) {
            if (shouldRetry(cause)) {
                LOGGER.debug("Waiting to retry: " + cause.getMessage() + ")");
                return waitThenRetry(driverClass, options, environmentVariables);
            } else {
                throw new DriverConfigurationError(
                        "Could not instantiate new WebDriver instance of type " + driverClass + " (" +
                        cause.getMessage() + "). See below for more details.", cause);
            }
        }
    }

    private WebDriver createWebDriver(Class<? extends WebDriver> driverClass, String options, EnvironmentVariables environmentVariables) throws MalformedURLException {
        RedimensionBrowser redimensionBrowser = new RedimensionBrowser(environmentVariables);
        SupportedWebDriver supportedDriverType = inEnvironment(environmentVariables).forDriverClass(driverClass);

        String resolvedOptions = (options.isEmpty()) ? ThucydidesWebDriverSupport.getDefaultDriverOptions().orElse(options) : options;

        WebDriver driver = driverProviders().get(supportedDriverType).newInstance(resolvedOptions, environmentVariables);
        setImplicitTimeoutsIfSpecified(driver);
        redimensionBrowser.withDriver(driver);
        //
        // Perform any custom configuration to the new driver
        //
        EnhanceDriver.from(environmentVariables).to(driver);

        closeBrowser.closeWhenTheTestsAreFinished(driver);
        return driver;
    }

    private WebDriver waitThenRetry(Class<? extends WebDriver> driverClass,
                                    String options,
                                    EnvironmentVariables environmentVariables) {
        int maxRetryCount = WEBDRIVER_CREATION_RETRY_MAX_TIME.integerFrom(environmentVariables, 30);
        return waitThenRetry(maxRetryCount, driverClass, options, environmentVariables, null);
    }

    private WebDriver waitThenRetry(int remainingTries,
                                    Class<? extends WebDriver> driverClass,
                                    String options,
                                    EnvironmentVariables environmentVariables,
                                    Exception cause) {
        LOGGER.debug("Remaining tries: " + remainingTries);

        if (remainingTries == 0) {
            throw new DriverConfigurationError(
                    "After several attempts, could not instantiate new WebDriver instance of type " + driverClass +
                    " (" + cause.getMessage() + "). See below for more details.", cause);
        }

        PauseTestExecution.forADelayOf(30).seconds();

        try {
            return createWebDriver(driverClass, options, environmentVariables);
        } catch (SerenityManagedException toPassThrough) {
            throw toPassThrough;
        } catch (Exception latestCause) {
            return waitThenRetry(remainingTries - 1, driverClass, options, environmentVariables, latestCause);
        }
    }

    private boolean shouldRetry(Exception cause) {
        List<String> RETRY_CAUSES = Splitter.on(";")
                .trimResults()
                .omitEmptyStrings()
                .splitToList(WEBDRIVER_CREATION_RETRY_CAUSES
                        .from(environmentVariables, "All parallel tests are currently in use"));
        return RETRY_CAUSES.stream().anyMatch(
                partialErrorMessage -> (cause != null) && (cause.getMessage() != null)
                                       && (cause.getMessage().contains(partialErrorMessage))
        );
    }

    private void setImplicitTimeoutsIfSpecified(WebDriver driver) {
        if (ThucydidesSystemProperty.WEBDRIVER_TIMEOUTS_IMPLICITLYWAIT.isDefinedIn(environmentVariables)) {
            int timeout = WEBDRIVER_TIMEOUTS_IMPLICITLYWAIT.integerFrom(environmentVariables, 0);
//            int timeout = environmentVariables.getPropertyAsInteger(ThucydidesSystemProperty.WEBDRIVER_TIMEOUTS_IMPLICITLYWAIT
//                    .getPropertyName(),0);

            driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.MILLISECONDS);
        }
    }

    public static String getDriverFrom(EnvironmentVariables environmentVariables, String defaultDriver) {
        String driver = getDriverFrom(environmentVariables);
        return (driver != null) ? driver : defaultDriver;
    }

    public static String getDriverFrom(EnvironmentVariables environmentVariables) {
        String driver = ThucydidesSystemProperty.WEBDRIVER_DRIVER.from(environmentVariables);
        if (driver == null) {
            driver = ThucydidesSystemProperty.DRIVER.from(environmentVariables);
        }
        return driver;
    }

    public void setupFixtureServices() throws FixtureException {
        for (FixtureService fixtureService : fixtureProviderService.getFixtureServices()) {
            fixtureService.setup();
        }
    }

    public void shutdownFixtureServices() {
        for (FixtureService fixtureService : fixtureProviderService.getFixtureServices()) {
            fixtureService.shutdown();
        }
    }

    private boolean isNotAMocked(WebDriver driver) {
        return (!(driver.getClass().getName().contains("Mock") || driver.toString().contains("Mock for")));
    }

    public void setTimeouts(WebDriver proxiedDriver, Duration implicitTimeout) {
        Duration currentTimeout = currentTimeoutFor(proxiedDriver);
        timeoutStack.pushTimeoutFor(proxiedDriver, implicitTimeout);
        if ((implicitTimeout != currentTimeout) && isNotAMocked(proxiedDriver)) {
            proxiedDriver.manage().timeouts().implicitlyWait(implicitTimeout.toMillis(), TimeUnit.MILLISECONDS);
        }
    }

    public Duration currentTimeoutFor(WebDriver proxiedDriver) {
        Optional<Duration> storedTimeoutValue = timeoutStack.currentTimeoutValueFor(proxiedDriver);
        return storedTimeoutValue.orElse(getDefaultImplicitTimeout());
    }

    public Duration resetTimeouts(WebDriver proxiedDriver) {
        Duration currentTimeout = currentTimeoutFor(proxiedDriver);
        if (!timeoutStack.containsTimeoutFor(proxiedDriver)) {
            return currentTimeout;
        }

        timeoutStack.popTimeoutFor(proxiedDriver);
        Duration previousTimeout = currentTimeoutFor(proxiedDriver);//timeoutStack.popTimeoutFor(proxiedDriver).or(getDefaultImplicitTimeout());
        if ((currentTimeout != previousTimeout) && isNotAMocked(proxiedDriver)) {
            proxiedDriver.manage().timeouts().implicitlyWait(previousTimeout.toMillis(), TimeUnit.MILLISECONDS);
        }
        return previousTimeout;
    }

    public Duration getDefaultImplicitTimeout() {
        String configuredTimeoutValue = ThucydidesSystemProperty.WEBDRIVER_TIMEOUTS_IMPLICITLYWAIT.from(environmentVariables);
        return (configuredTimeoutValue != null) ? Duration.ofMillis(Integer.parseInt(configuredTimeoutValue))
                : DefaultTimeouts.DEFAULT_IMPLICIT_WAIT_TIMEOUT;
    }

    public static boolean isAlive(final WebDriver driver) {
        try {
            WebDriver local = driver;
            if (driver instanceof WebDriverFacade) {
                local = ((WebDriverFacade) driver).getDriverInstance();
            }
            if (!(local instanceof AppiumDriver)) {
                local.getCurrentUrl();
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean isNotAlive(final WebDriver driver) {
        return !isAlive(driver);
    }

    public void releaseTimoutFor(WebDriver driverInstance) {
        timeoutStack.releaseTimeoutFor(driverInstance);
    }
}
