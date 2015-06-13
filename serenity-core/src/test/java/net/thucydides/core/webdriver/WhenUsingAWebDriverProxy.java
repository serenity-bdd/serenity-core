package net.thucydides.core.webdriver;

import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.MockEnvironmentVariables;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import java.lang.reflect.InvocationTargetException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class WhenUsingAWebDriverProxy {

    @Mock
    WebdriverInstanceFactory webdriverInstanceFactory;

    @Mock
    PhantomJSDriver driver;

    @Mock
    WebDriver.Options options;

    @Mock
    WebDriver.Timeouts timeouts;

    WebdriverManager webdriverManager;

    WebDriverFactory factory;

    WebDriverFacade webDriverFacade;

    class MockFirefoxWebDriverFacade extends WebDriverFacade {
        MockFirefoxWebDriverFacade() {
            super(FirefoxDriver.class, factory);
        }

        @Override
        public WebDriver getProxiedDriver() {
            return driver;
        }

        @Override
        public Options manage() {
            return options;
        }
    }

    private void initWendriverManager() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        when(webdriverInstanceFactory.newFirefoxDriver(any(Capabilities.class))).thenReturn(driver);
        when(options.timeouts()).thenReturn(timeouts);
        MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        factory = new WebDriverFactory(webdriverInstanceFactory, environmentVariables);

        webdriverManager = new ThucydidesWebdriverManager(factory, new SystemPropertiesConfiguration(environmentVariables));

    }


    MockFirefoxWebDriverFacade facade;

    @Before
    public void createATestableDriverFactory() throws Exception {
        MockitoAnnotations.initMocks(this);
        initWendriverManager();
        StepEventBus.getEventBus().clear();
        webDriverFacade = (WebDriverFacade) webdriverManager.getWebdriver();
        WebdriverProxyFactory.getFactory().clearMockDriver();
        webdriverManager.closeAllCurrentDrivers();

        facade = new MockFirefoxWebDriverFacade();

    }

    @After
    public void clearMocks() {
        WebdriverProxyFactory.getFactory().clearMockDriver();
        webdriverManager.closeDriver();
    }

    @Test
    public void the_webdriver_proxy_should_handle_get() {
        facade.get("http://www.google.com");
        verify(driver).get("http://www.google.com");
    }

    @Test
    public void the_webdriver_proxy_should_ignore_get_when_webdriver_calls_are_disabled() {
        StepEventBus.getEventBus().temporarilySuspendWebdriverCalls();
        facade.get("http://www.google.com");

        verify(driver, never()).get("http://www.google.com");
        StepEventBus.getEventBus().reenableWebdriverCalls();
    }


    @Test
    public void the_webdriver_proxy_should_quit_driver_when_reset() {

        webDriverFacade.get("http://www.google.com");

        webDriverFacade.reset();

        verify(driver).quit();
    }

    @Test
    public void the_webdriver_proxy_should_remove_proxied_driver_when_reset() {

        webDriverFacade.get("http://www.google.com");

        webDriverFacade.reset();

        TransparentWebDriverFacade facade = new TransparentWebDriverFacade(webDriverFacade);
        assertThat(facade.getProxied(), is(nullValue()));
    }

    @Test
    public void the_webdriver_proxy_should_handle_find_element() {
        facade.findElement(By.id("q"));

        verify(driver).findElement(By.id("q"));
    }

    @Test
    public void the_webdriver_proxy_should_handle_find_elements() {
        facade.findElements(By.id("q"));
        verify(driver).findElements(By.id("q"));
    }

    @Test
    public void the_webdriver_proxy_should_ignore_find_elements_when_webdriver_calls_are_disabled() {
        StepEventBus.getEventBus().temporarilySuspendWebdriverCalls();

        facade.findElements(By.id("q"));
        verify(driver, never()).findElements(By.id("q"));
    }


    @Test
    public void the_webdriver_proxy_should_handle_get_screenshot() {
        webDriverFacade.get("http://www.google.com");
        webDriverFacade.getScreenshotAs(OutputType.FILE);
        verify(driver).getScreenshotAs(OutputType.FILE);
    }

    @Test
    public void the_webdriver_proxy_should_handle_get_current_url() {
        facade.getCurrentUrl();
        verify(driver, atLeast(1)).getCurrentUrl();
    }

    @Test
    public void the_webdriver_proxy_should_ignore_get_current_when_webdriver_calls_are_disabled() {
        StepEventBus.getEventBus().temporarilySuspendWebdriverCalls();

        facade.getCurrentUrl();
        verify(driver, never()).getCurrentUrl();
    }


    @Test
    public void the_webdriver_proxy_should_handle_get_page_source() {
        facade.getPageSource();
        verify(driver).getPageSource();
    }

    @Test
    public void the_webdriver_proxy_should_ignore_get_page_source_when_webdriver_calls_are_disabled() {
        StepEventBus.getEventBus().temporarilySuspendWebdriverCalls();

        facade.getPageSource();
        verify(driver, never()).getPageSource();
    }

    @Test
    public void the_webdriver_proxy_should_handle_get_title() {
        facade.getTitle();
        verify(driver).getTitle();
    }

    @Test
    public void the_webdriver_proxy_should_ignore_get_title_when_webdriver_calls_are_disabled() {
        StepEventBus.getEventBus().temporarilySuspendWebdriverCalls();

        facade.getTitle();
        verify(driver, never()).getTitle();
    }


    @Test
    public void the_webdriver_proxy_should_handle_get_window_handle() {
        facade.getWindowHandle();
        verify(driver).getWindowHandle();
    }

    @Test
    public void the_webdriver_proxy_should_ignore_get_window_handle_when_webdriver_calls_are_disabled() {
        StepEventBus.getEventBus().temporarilySuspendWebdriverCalls();

        facade.getWindowHandle();
        verify(driver, never()).getWindowHandle();
    }

    @Test
    public void the_webdriver_proxy_should_handle_get_window_handles() {
        facade.getWindowHandles();
        verify(driver).getWindowHandles();
    }

    @Test
    public void the_webdriver_proxy_should_ignore_get_window_handles_when_webdriver_calls_are_disabled() {
        StepEventBus.getEventBus().temporarilySuspendWebdriverCalls();

        facade.getWindowHandles();
        verify(driver, never()).getWindowHandles();
    }

    @Test
    public void the_webdriver_proxy_should_handle_navigate() {
        facade.navigate();
        verify(driver).navigate();
    }

    @Test
    public void the_webdriver_proxy_should_ignore_navigate_when_webdriver_calls_are_disabled() {
        StepEventBus.getEventBus().temporarilySuspendWebdriverCalls();

        facade.navigate();
        verify(driver, never()).navigate();
    }

    @Test
    public void the_webdriver_proxy_should_handle_switchTo() {
        facade.switchTo();
        verify(driver).switchTo();
    }

    @Test
    public void the_webdriver_proxy_should_ignore_switchTo_when_webdriver_calls_are_disabled() {
        StepEventBus.getEventBus().temporarilySuspendWebdriverCalls();

        facade.switchTo();
        verify(driver, never()).switchTo();
    }

    @Test
    public void the_webdriver_proxy_should_handle_quit_if_a_proxied_driver_exists() {
        webDriverFacade.get("http://www.google.com");
        webDriverFacade.quit();
        verify(driver).quit();
    }

    @Test
    public void the_webdriver_proxy_should_handle_close_if_a_proxied_driver_exists() {
        webDriverFacade.get("http://www.google.com");
        webDriverFacade.close();
        verify(driver).close();
    }

    @Test
    public void the_webdriver_proxy_should_ignore_managed_when_webdriver_calls_are_disabled() {
        StepEventBus.getEventBus().temporarilySuspendWebdriverCalls();

        facade.manage();
        verify(driver, never()).manage();
    }

    @Test
    public void the_webdriver_proxy_should_not_call_quit_if_a_proxied_driver_doesnt_exist() {
        facade.quit();
        verify(driver, never()).quit();
    }

    @Test
    public void the_webdriver_proxy_should_not_call_close_if_a_proxied_driver_doesnt_exist() {
        facade.close();
        verify(driver, never()).close();
    }

    @Test
    public void the_webdriver_proxy_should_not_instanciate_the_webdriver_instance_until_a_method_is_invoked() {

        webDriverFacade.get("http://www.google.com");
        webDriverFacade.findElement(By.id("q"));
        webDriverFacade.getScreenshotAs(OutputType.FILE);

        verify(driver).get("http://www.google.com");
        verify(driver).findElement(By.id("q"));
        verify(driver).getScreenshotAs(OutputType.FILE);
    }

    @Mock
    ThucydidesWebDriverEventListener eventListener;

    @Test
    public void when_a_listener_is_registered_the_webdriver_proxy_should_notify_the_listener_when_the_browser_is_opened() {

        WebdriverProxyFactory.getFactory().registerListener(eventListener);

        webDriverFacade.get("http://www.google.com");

        verify(eventListener).driverCreatedIn(any(WebDriver.class));
    }

    @Test
    public void the_webdriver_proxy_looks_and_feels_like_a_webdriver() {
        WebDriver driver = WebdriverProxyFactory.getFactory().proxyFor(HtmlUnitDriver.class);

        assertThat(driver, is(notNullValue()));
        assertThat(WebDriver.class.isAssignableFrom(driver.getClass()), is(true));
    }

    @Test
    public void the_proxied_webdriver_should_be_accessible_if_required() {
        WebDriver driver = WebdriverProxyFactory.getFactory().proxyFor(HtmlUnitDriver.class);

        HtmlUnitDriver proxiedDriver = (HtmlUnitDriver) ((WebDriverFacade) driver).getProxiedDriver();

        assertThat(proxiedDriver, is(notNullValue()));
        assertThat(HtmlUnitDriver.class.isAssignableFrom(proxiedDriver.getClass()), is(true));
    }

    @Test
    public void should_return_proxied_driver_without_lots_of_ugly_casts() {
        ThucydidesWebDriverSupport.initialize("htmlunit");
        HtmlUnitDriver proxiedDriver = ThucydidesWebDriverSupport.getProxiedDriver();
        assertThat(proxiedDriver, is(instanceOf(HtmlUnitDriver.class)));

    }

}
