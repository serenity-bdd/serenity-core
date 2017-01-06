package net.serenitybdd.core.webdriver

import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import net.thucydides.core.webdriver.SerenityWebdriverManager
import net.thucydides.core.webdriver.SupportedWebDriver
import net.thucydides.core.configuration.SystemPropertiesConfiguration
import net.thucydides.core.webdriver.WebDriverFactory
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.remote.SessionId
import spock.lang.Specification

import static org.mockito.Matchers.any
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

    def configuration

    @Mock
    def WebDriverFactory webDriverFactory

    def setup() {
        MockitoAnnotations.initMocks(this);
        environmentVariables = new MockEnvironmentVariables()
        configuration = new SystemPropertiesConfiguration(environmentVariables)
    }

    def "should be possible get session id using webdriverManager"() {
        given:
            when(remote.getSessionId()).thenReturn(session)
            when(webDriverFactory.newInstanceOf(any(SupportedWebDriver.class))).thenReturn(remote)
            def manager = new SerenityWebdriverManager(webDriverFactory, configuration);
        when:
            manager.registerDriver(remote)
            manager.getSessionId()
        then:
            session == manager.getSessionId()
            null == verify(remote, atLeast(2)).getSessionId();

    }
}