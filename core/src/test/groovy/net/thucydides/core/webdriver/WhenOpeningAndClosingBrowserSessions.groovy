package net.thucydides.core.webdriver

import net.thucydides.core.steps.StepEventBus
import net.thucydides.core.util.MockEnvironmentVariables
import org.openqa.selenium.Capabilities
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.ie.InternetExplorerDriver
import org.openqa.selenium.remote.RemoteWebDriver
import spock.lang.Specification

class WhenOpeningAndClosingBrowserSessions extends Specification {

    def environmentVariables = new MockEnvironmentVariables()

    def firefox = Mock(FirefoxDriver)
    def chrome = Mock(ChromeDriver)
    def htmlunit = Mock(HtmlUnitDriver)
    def iexplore = Mock(InternetExplorerDriver)
    def remote = Mock(RemoteWebDriver)

    def webdriverInstanceFactory = new WebdriverInstanceFactory() {
        WebDriver newFirefoxDriver(Capabilities capabilities) { return firefox }

        @Override
        WebDriver newChromeDriver(Capabilities capabilities) { return chrome }

        @Override
        WebDriver newHtmlUnitDriver(Capabilities capabilities){ return htmlunit }

        @Override
        WebDriver newRemoteDriver(URL remoteUrl, Capabilities capabilities) { return remote }

        @Override
        WebDriver newInstanceOf(Class<? extends WebDriver> webdriverClass) {
            switch (webdriverClass) {
                case InternetExplorerDriver : return iexplore
            }
        }
    }

    WebDriverFactory webDriverFactory
    WebdriverManager webdriverManager;

    Capabilities capabilities = Mock(Capabilities)

    def setup() {
        capabilities.asMap() >> [:]
        remote.getCapabilities() >> capabilities
        environmentVariables.setProperty("webdriver.driver","htmlunit")
        webDriverFactory = new WebDriverFactory(webdriverInstanceFactory, environmentVariables)
        webdriverManager = new ThucydidesWebdriverManager(webDriverFactory, new SystemPropertiesConfiguration(environmentVariables));
        StepEventBus.eventBus.clear()
    }

    def "should open a new browser when a page is opened"() {
        given:
            def webdriver = webdriverManager.getWebdriver("htmlunit")
        when:
            webdriver.get("about:blank")
        then:
            webdriver.proxiedWebDriver != null
    }

    def "should shutdown browser when requested"() {
        given:
            def webdriver = webdriverManager.getWebdriver("htmlunit")
        when:
            webdriver.get("about:blank")
            webdriver.quit()
        then:
            webdriver.proxiedWebDriver == null
    }

    def "should open a new browser after shutdown browser when requested"() {
        given:
            def webdriver = webdriverManager.getWebdriver("htmlunit")
        when:
            webdriver.get("about:blank")
            webdriver.quit()
        webdriver.get("about:blank")
        then:
            webdriver.proxiedWebDriver != null
    }

    def "quitting a shut browser should have no effect"() {
        given:
            def webdriver = webdriverManager.getWebdriver("htmlunit")
            webdriver.get("about:blank")
            webdriver.quit()
        when:
            webdriver.quit()
        then:
            webdriver.proxiedWebDriver == null
    }

    def "resetting the proxy should close the current browser"() {
        given:
            def webdriver = webdriverManager.getWebdriver("htmlunit")
            webdriver.get("about:blank")
        when:
            webdriver.reset()
        then:
            webdriver.proxiedWebDriver == null
    }

}
