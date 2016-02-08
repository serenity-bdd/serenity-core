package net.serenitybdd.core.webdriver

import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import net.thucydides.core.webdriver.SupportedWebDriver
import net.thucydides.core.webdriver.SystemPropertiesConfiguration
import net.thucydides.core.webdriver.ThucydidesWebdriverManager
import net.thucydides.core.webdriver.TimeoutStack
import net.thucydides.core.webdriver.WebDriverFacade
import net.thucydides.core.webdriver.WebDriverFactory
import net.thucydides.core.webdriver.WebdriverInstanceFactory
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.remote.SessionId
import spock.lang.Specification
import static org.mockito.Mockito.*

/**
 * User: YamStranger
 * Date: 2/8/16
 * Time: 12:58 PM
 */
class WhenUsingWebDriverFacade extends Specification {

    @Mock
    def RemoteWebDriver remote

    @Mock
    SessionId session

    def EnvironmentVariables environmentVariables

    @Mock
    def WebdriverInstanceFactory webdriverInstanceFactory

    def configuration

    @Mock
    def WebDriverFactory webDriverFactory

    def setup() {
        MockitoAnnotations.initMocks(this);
        environmentVariables = new MockEnvironmentVariables()
        configuration = new SystemPropertiesConfiguration(environmentVariables)
    }

    def "stack should be initially empty"() {
        given:
            when(remote.getSessionId()).thenReturn(session)
            when(webDriverFactory.newInstanceOf(any(SupportedWebDriver.class))).thenReturn(remote)
            def manager = new ThucydidesWebdriverManager(webDriverFactory, configuration);
        when:
            manager.registerDriver(remote)
            manager.getSessionId()
        then:
            session == manager.getSessionId()
            null == verify(remote, atLeast(2)).getSessionId();

    }
}