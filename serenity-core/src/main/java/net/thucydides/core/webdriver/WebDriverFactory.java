package net.thucydides.core.webdriver;

import com.beust.jcommander.internal.Maps;
import com.google.common.base.Optional;
import io.appium.java_client.AppiumDriver;
import net.serenitybdd.core.exceptions.SerenityManagedException;
import net.serenitybdd.core.pages.DefaultTimeouts;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.fixtureservices.FixtureException;
import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.core.fixtureservices.FixtureService;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.capabilities.SauceRemoteDriverCapabilities;
import net.thucydides.core.webdriver.strategies.*;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static net.thucydides.core.webdriver.DriverStrategySelector.inEnvironment;

/**
 * Provides an instance of a supported WebDriver.
 * When you instanciate a Webdriver instance for Firefox or Chrome, it opens a new browser.
 *
 * @author johnsmart
 */
public class WebDriverFactory {
    public static final String DEFAULT_DRIVER = "firefox";
    public static final String REMOTE_DRIVER = "remote";

    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverFactory.class);


    private final EnvironmentVariables environmentVariables;
    private final FixtureProviderService fixtureProviderService;
    private final SauceRemoteDriverCapabilities sauceRemoteDriverCapabilities;

    private final TimeoutStack timeoutStack;

    public WebDriverFactory() {
        this(Injectors.getInjector().getProvider(EnvironmentVariables.class).get() );
    }


    public WebDriverFactory(EnvironmentVariables environmentVariables) {
        this(environmentVariables,
            Injectors.getInjector().getInstance(FixtureProviderService.class));
    }

    public WebDriverFactory(EnvironmentVariables environmentVariables,
                            FixtureProviderService fixtureProviderService) {
        this.environmentVariables = environmentVariables;
        this.fixtureProviderService = fixtureProviderService;
        this.sauceRemoteDriverCapabilities = new SauceRemoteDriverCapabilities(environmentVariables);
        this.timeoutStack = new TimeoutStack();
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
        if (usesSauceLabs() && (driverType != SupportedWebDriver.HTMLUNIT)) {
            return RemoteWebDriver.class;
        } else {
            return driverType.getWebdriverClass();
        }
    }

    public boolean usesSauceLabs() {
        return StringUtils.isNotEmpty(sauceRemoteDriverCapabilities.getUrl());
    }

    private Map<DriverStrategy, DriverBuilder> driverBuilders() {
        Map<DriverStrategy, DriverBuilder> builderMap = Maps.newHashMap();
        CapabilityEnhancer enhancer = new CapabilityEnhancer(environmentVariables, fixtureProviderService);

        builderMap.put(DriverStrategy.Appium, new AppiumDriverBuilder(environmentVariables, enhancer));
        builderMap.put(DriverStrategy.Remote, new RemoteDriverBuilder(environmentVariables, enhancer));
        builderMap.put(DriverStrategy.Firefox, new FirefoxDriverBuilder(environmentVariables, enhancer));
        builderMap.put(DriverStrategy.HtmlUnit, new HtmlDriverBuilder(environmentVariables, enhancer));
        builderMap.put(DriverStrategy.PhantomJS, new PhantomJSDriverBuilder(environmentVariables, enhancer));
        builderMap.put(DriverStrategy.Chrome, new ChromeDriverBuilder(environmentVariables, enhancer));
        builderMap.put(DriverStrategy.Safari, new SafariDriverBuilder(environmentVariables, enhancer));
        builderMap.put(DriverStrategy.IE, new InternetExplorerDriverBuilder(environmentVariables, enhancer));
        builderMap.put(DriverStrategy.Edge, new EdgeDriverBuilder(environmentVariables, enhancer));
        builderMap.put(DriverStrategy.Provided, new ProvidedDriverBuilder(environmentVariables, enhancer));

        return builderMap;
    }

    /**
     * This method is synchronized because multiple webdriver instances can be created in parallel.
     * However, they may use common system resources such as ports, so may potentially interfere
     * with each other.
     *
     * @param driverClass
     */
    protected synchronized WebDriver newWebdriverInstance(final Class<? extends WebDriver> driverClass) {
        RedimensionBrowser redimensionBrowser = new RedimensionBrowser(environmentVariables, driverClass);
        try {
            DriverStrategy strategy = inEnvironment(environmentVariables).forDriverClass(driverClass);
            WebDriver driver = driverBuilders().get(strategy).newInstance();

            if (driver == null) {
                throw new UnsupportedDriverException("Failed to instantiate " + driverClass);
            }
            setImplicitTimeoutsIfSpecified(driver);
            redimensionBrowser.resizeDriver(driver);

            return driver;
        } catch (SerenityManagedException toPassThrough) {
            throw toPassThrough;
        } catch (Exception cause) {
             throw new UnsupportedDriverException("Could not instantiate " + driverClass, cause);
        }
    }


    private void setImplicitTimeoutsIfSpecified(WebDriver driver) {
        if (ThucydidesSystemProperty.WEBDRIVER_TIMEOUTS_IMPLICITLYWAIT.isDefinedIn(environmentVariables)) {
            int timeout = environmentVariables.getPropertyAsInteger(ThucydidesSystemProperty.WEBDRIVER_TIMEOUTS_IMPLICITLYWAIT
                    .getPropertyName(),0);

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
        for(FixtureService fixtureService : fixtureProviderService.getFixtureServices()) {
            fixtureService.setup();
        }
    }

    public void shutdownFixtureServices() {
        for(FixtureService fixtureService : fixtureProviderService.getFixtureServices()) {
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
            proxiedDriver.manage().timeouts().implicitlyWait(implicitTimeout.in(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
        }
    }

    public Duration currentTimeoutFor(WebDriver proxiedDriver) {
        Optional<Duration> storedTimeoutValue = timeoutStack.currentTimeoutValueFor(proxiedDriver);
        return storedTimeoutValue.or(getDefaultImplicitTimeout());
    }

    public Duration resetTimeouts(WebDriver proxiedDriver) {
        Duration currentTimeout = currentTimeoutFor(proxiedDriver);
        if (!timeoutStack.containsTimeoutFor(proxiedDriver)) {
            return currentTimeout;
        }

        timeoutStack.popTimeoutFor(proxiedDriver);
        Duration previousTimeout = currentTimeoutFor(proxiedDriver);//timeoutStack.popTimeoutFor(proxiedDriver).or(getDefaultImplicitTimeout());
        if ((currentTimeout != previousTimeout)  && isNotAMocked(proxiedDriver)) {
            proxiedDriver.manage().timeouts().implicitlyWait(previousTimeout.in(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
        }
        return previousTimeout;
    }

    public Duration getDefaultImplicitTimeout() {
        String configuredTimeoutValue = ThucydidesSystemProperty.WEBDRIVER_TIMEOUTS_IMPLICITLYWAIT.from(environmentVariables);
        return (configuredTimeoutValue != null) ? new Duration(Integer.parseInt(configuredTimeoutValue), TimeUnit.MILLISECONDS)
                : DefaultTimeouts.DEFAULT_IMPLICIT_WAIT_TIMEOUT;

    }

    public static boolean isAlive(final WebDriver driver) {
        try {
            WebDriver local = driver;
            if(driver instanceof WebDriverFacade){
                local = ((WebDriverFacade)driver).getDriverInstance();
            }
            if(!(local instanceof AppiumDriver)){
                local.getCurrentUrl();
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean isNotAlive(final WebDriver driver){
        return !isAlive(driver);
    }
}
