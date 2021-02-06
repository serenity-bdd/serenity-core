package net.thucydides.core.webdriver;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

@RunWith(MockitoJUnitRunner.class)
public class WhenUsingAProvidedDriver {

    MockWebDriverFacade facade;

    static class MyDriverSource implements DriverSource {

        @Override
        public WebDriver newDriver() {
            return new HtmlUnitDriver();
        }

        @Override
        public boolean takesScreenshots() {
            return false;
        }

        public Class<? extends WebDriver> driverType() { return HtmlUnitDriver.class; }

    }

    class MockWebDriverFacade extends WebDriverFacade {
        MockWebDriverFacade(EnvironmentVariables environmentVariables) {
            super(ProvidedDriver.class, new WebDriverFactory(environmentVariables), environmentVariables);
        }
    }

    @Before
    public void setup() {
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("webdriver.provided.type", "mydriver");
        environmentVariables.setProperty("webdriver.provided.mydriver", MyDriverSource.class.getName());

        facade = new MockWebDriverFacade(environmentVariables);
    }

    @Test
    public void the_web_driver_facade_should_expose_the_proxied_driver_class_for_an_uninstantiated_driver() {
        Assert.assertEquals(facade.getDriverClass(), HtmlUnitDriver.class);
    }

    @Test
    public void the_web_driver_facade_should_expose_the_proxied_driver_class_for_an_instantiated_driver() {
        facade.getProxiedDriver();
        Assert.assertEquals(facade.getDriverClass(), HtmlUnitDriver.class);
    }

}
