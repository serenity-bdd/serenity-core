package net.thucydides.core.webdriver;

import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.safari.SafariDriver;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WhenInstanciatingANewDriver {

    private WebDriverFactory webDriverFactory;

    @Mock
    WebdriverInstanceFactory webdriverInstanceFactory;

    @Mock
    ChromeDriver chromeDriver;

    @Mock
    SafariDriver safariDriver;

    EnvironmentVariables environmentVariables;

    @Before
    public void createATestableDriverFactory() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(webdriverInstanceFactory.newChromeDriver(any(Capabilities.class))).thenReturn(chromeDriver);
        when(webdriverInstanceFactory.newSafariDriver(any(Capabilities.class))).thenReturn(safariDriver);

        environmentVariables = new MockEnvironmentVariables();
        webDriverFactory = new WebDriverFactory(webdriverInstanceFactory, environmentVariables);
    }

    @Test
    public void should_create_safari_driver_instance() throws Exception {
        webDriverFactory.newInstanceOf(SupportedWebDriver.SAFARI);
        verify(webdriverInstanceFactory).newSafariDriver(any(Capabilities.class));
    }


}
