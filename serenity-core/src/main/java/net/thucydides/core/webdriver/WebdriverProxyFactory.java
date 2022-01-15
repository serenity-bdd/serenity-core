package net.thucydides.core.webdriver;

import net.serenitybdd.core.collect.NewList;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.serenitybdd.core.environment.WebDriverConfiguredEnvironment;
import net.thucydides.core.steps.StepEventBus;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.devtools.HasDevTools;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.synchronizedList;

/**
 * Provides a proxy for a WebDriver instance.
 * The proxy lets you delay opening the browser until you really know you are going to use it.
 */
public class WebdriverProxyFactory implements Serializable {

    private static final long serialVersionUID = 1L;

    private static ThreadLocal<WebdriverProxyFactory> factory = new ThreadLocal<WebdriverProxyFactory>();

    private static List<ThucydidesWebDriverEventListener> eventListeners
                                              = synchronizedList(new ArrayList<ThucydidesWebDriverEventListener>());

    private WebDriverFactory webDriverFactory;
    private WebDriverFacade mockDriver;
    private final DriverConfiguration configuration;

    private WebdriverProxyFactory() {
        webDriverFactory = new WebDriverFactory();
        this.configuration = WebDriverConfiguredEnvironment.getDriverConfiguration();
    }

    public static WebdriverProxyFactory getFactory() {
        if (factory.get() == null) {
            factory.set(new WebdriverProxyFactory());
        }
        return factory.get();
    }

    public static List<ThucydidesWebDriverEventListener> getEventListeners() {
        return NewList.copyOf(eventListeners);
    }
    public WebDriverFacade proxyFor(final Class<? extends WebDriver> driverClass) {
       return proxyFor(driverClass,
                       new WebDriverFactory(),
                       ConfiguredEnvironment.getConfiguration());
    }

    public WebDriverFacade proxyFor(final Class<? extends WebDriver> driverClass,
                                    final WebDriverFactory webDriverFactory,
                                    Configuration configuration) {
        return proxyFor(driverClass,webDriverFactory,configuration,"");
    }

    public WebDriverFacade proxyFor(final Class<? extends WebDriver> driverClass,
                                    final WebDriverFactory webDriverFactory,
                                    Configuration configuration,
                                    String options) {
        if (mockDriver != null) {
            return mockDriver;
        } else if (driverClass != null && HasDevTools.class.isAssignableFrom(driverClass)) {
            return new DevToolsWebDriverFacade(driverClass, webDriverFactory, configuration.getEnvironmentVariables()).withOptions(options);
        } else {
            return new WebDriverFacade(driverClass, webDriverFactory, configuration.getEnvironmentVariables()).withOptions(options);
        }
    }

    public WebDriverFacade proxyFor(WebDriver driver) {
        if (driver instanceof HasDevTools) {
            return new DevToolsWebDriverFacade(driver, webDriverFactory, configuration.getEnvironmentVariables());
        } else {
            return new WebDriverFacade(driver, webDriverFactory, configuration.getEnvironmentVariables());
        }
    }


    public void registerListener(final ThucydidesWebDriverEventListener eventListener) {
        eventListeners.add(eventListener);
    }

    public void notifyListenersOfWebdriverCreationIn(final WebDriverFacade webDriverFacade) {
        for(ThucydidesWebDriverEventListener listener : getEventListeners()) {
            listener.driverCreatedIn(webDriverFacade);
        }
    }

    public WebDriver proxyDriver() {
        Class<? extends WebDriver> driverClass = webDriverFactory.getClassFor(configuration.getDriverType());
        return proxyFor(driverClass,
                        webDriverFactory,
                        ConfiguredEnvironment.getConfiguration());
    }

    public static void resetDriver(final WebDriver driver) {
        if (driver instanceof WebDriverFacade) {
            ((WebDriverFacade) driver).reset();
        }
    }

    public void useMockDriver(final WebDriverFacade mockDriver) {
        this.mockDriver = mockDriver;
    }

    public void clearMockDriver() {
        this.mockDriver = null;
    }

    public static void clearBrowserSession(WebDriver driver) {

        if (StepEventBus.getEventBus().isDryRun()) { return; }

        if ((driver instanceof WebDriverFacade)
                && ((WebDriverFacade) driver).isInstantiated()
                && !((WebDriverFacade) driver).getProxiedDriver().getCurrentUrl().isEmpty()) {

            WebDriver proxiedDriver = ((WebDriverFacade) driver).getProxiedDriver();
            proxiedDriver.manage().deleteAllCookies();
            try {
                ((JavascriptExecutor) proxiedDriver).executeScript("window.sessionStorage.clear();");
                ((JavascriptExecutor) proxiedDriver).executeScript("window.localStorage.clear();");
            } catch (WebDriverException driverDoesntSupportJavascriptTooBad) {}
        }
    }
}
