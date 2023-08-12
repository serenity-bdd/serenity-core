package net.thucydides.core.pages.integration;


import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.model.environment.MockEnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.WebDriverFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class WhenSettingScreenDimensions {

    private StaticSitePage page;
    private EnvironmentVariables environmentVariables;
    private WebDriver driver;
    WebDriverFactory factory;

    @Before
    public void setupFactory() {
        environmentVariables = new MockEnvironmentVariables();
        factory = new WebDriverFactory(environmentVariables);
    }

    class StubbedWebDriverFactory extends WebDriverFactory {
        StubbedWebDriverFactory(EnvironmentVariables environmentVariables) {
            super(environmentVariables);
        }

        public boolean screenWasResized;
    }

    @Test
    public void should_remove_timeout_on_close() {
        driver = factory.newInstanceOf(SupportedWebDriver.CHROME);
        Duration implicitTimeout = Duration.ofMillis(1000L);
        factory.setTimeouts(driver, implicitTimeout);

        assertThat(factory.currentTimeoutFor(driver),is(sameInstance(implicitTimeout)));
        factory.releaseTimoutFor(driver);
        assertThat(factory.currentTimeoutFor(driver),is(sameInstance(factory.getDefaultImplicitTimeout())));
    }

    @Test
    public void should_not_resize_browser_if_dimension_are_not_provided() {
        StubbedWebDriverFactory stubbedFactory = new StubbedWebDriverFactory(environmentVariables);
        driver = stubbedFactory.newInstanceOf(SupportedWebDriver.CHROME);
        page = new StaticSitePage(driver, 1000);
        page.open();
        assertThat(stubbedFactory.screenWasResized, is(false));
    }

    @Test
    public void should_resize_browser_automatically() {
        environmentVariables.setProperty("thucydides.browser.height", "500");
        environmentVariables.setProperty("thucydides.browser.width", "500");
        environmentVariables.setProperty("headless.mode", "true");

        driver = factory.withEnvironmentVariables(environmentVariables).newInstanceOf(SupportedWebDriver.CHROME);
        page = new StaticSitePage(driver, 1024);

        int width = ((Long)(((JavascriptExecutor)driver).executeScript("return window.innerWidth"))).intValue();
        assertThat(width, equalTo(500));
    }

    @After
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }
}
