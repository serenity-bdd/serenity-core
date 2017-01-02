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
import net.serenitybdd.core.webdriver.driverproviders.*;
import net.thucydides.core.webdriver.redimension.RedimensionBrowser;
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

    private final CloseBrowser closeBrowser;

    private Map<SupportedWebDriver, DriverProvider> driverProvidersByDriverType;

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
        this.closeBrowser = Injectors.getInjector().getInstance(CloseBrowser.class);
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

    private Map<SupportedWebDriver, DriverProvider> driverProviders() {

        if (driverProvidersByDriverType == null) {
            driverProvidersByDriverType = Maps.newHashMap();

            CapabilityEnhancer enhancer = new CapabilityEnhancer(environmentVariables, fixtureProviderService);

            driverProvidersByDriverType.put(SupportedWebDriver.APPIUM, new AppiumDriverProvider(environmentVariables, enhancer));
            driverProvidersByDriverType.put(SupportedWebDriver.REMOTE, new RemoteDriverProvider(environmentVariables, enhancer));
            driverProvidersByDriverType.put(SupportedWebDriver.FIREFOX, new FirefoxDriverProvider(environmentVariables, enhancer));
            driverProvidersByDriverType.put(SupportedWebDriver.HTMLUNIT, new HtmlDriverProvider(environmentVariables, enhancer));
            driverProvidersByDriverType.put(SupportedWebDriver.PHANTOMJS, new PhantomJSDriverProvider(environmentVariables, enhancer));
            driverProvidersByDriverType.put(SupportedWebDriver.CHROME, new ChromeDriverProvider(environmentVariables, enhancer));
            driverProvidersByDriverType.put(SupportedWebDriver.SAFARI, new SafariDriverProvider(environmentVariables, enhancer));
            driverProvidersByDriverType.put(SupportedWebDriver.IEXPLORER, new InternetExplorerDriverProvider(environmentVariables, enhancer));
            driverProvidersByDriverType.put(SupportedWebDriver.EDGE, new EdgeDriverProvider(environmentVariables, enhancer));
            driverProvidersByDriverType.put(SupportedWebDriver.PROVIDED, new ProvidedDriverProvider(environmentVariables, enhancer));

        }
        return driverProvidersByDriverType;
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
            SupportedWebDriver supportedDriverType = inEnvironment(environmentVariables).forDriverClass(driverClass);
            WebDriver driver = driverProviders().get(supportedDriverType).newInstance();
            setImplicitTimeoutsIfSpecified(driver);
            redimensionBrowser.withDriver(driver);

            closeBrowser.closeWhenTheTestsAreFinished(driver);

            return driver;
        } catch (SerenityManagedException toPassThrough) {
            throw toPassThrough;
        } catch (Exception cause) {
             throw new UnsupportedDriverException("Could not instantiate new WebDriver instance of type " + driverClass + " (" + cause.getMessage(), cause);
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

    public static String getBrowserStackDriverFrom(EnvironmentVariables environmentVariables) {
        String driver = ThucydidesSystemProperty.BROWSERSTACK_BROWSER.from(environmentVariables);
        if (driver == null) {
            driver = ThucydidesSystemProperty.BROWSERSTACK_BROWSERNAME.from(environmentVariables);
        }
        if (driver == null) {
            driver = getDriverFrom(environmentVariables);
        }
        return driver;
    }

    public static String getSaucelabsDriverFrom(EnvironmentVariables environmentVariables) {
        String driver = ThucydidesSystemProperty.SAUCELABS_BROWSERNAME.from(environmentVariables);
        if (driver == null) {
            driver = getDriverFrom(environmentVariables);
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
