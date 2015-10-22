package net.thucydides.core.webdriver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.WebDriver;

@RunWith(MockitoJUnitRunner.class)
public class WhenUsingAProvidedDriver {


    @Mock
    WebDriver driver;

    @Mock
    WebDriverFactory factory;

    MockWebDriverFacade facade;

    class MockWebDriverFacade extends WebDriverFacade {
        MockWebDriverFacade() {
            super(ProvidedDriver.class, factory);
        }

        @Override
        public WebDriver getProxiedDriver() {
            return driver;
        }

    }

    @Before
    public void setup() {
        facade = new MockWebDriverFacade();
    }

    @Test
    public void the_web_driver_facade_should_expose_the_proxied_driver_class() {
        Assert.assertEquals(facade.getDriverClass(), driver.getClass());
    }
}
