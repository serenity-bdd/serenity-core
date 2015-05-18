package net.thucydides.core.webdriver

import com.opera.core.systems.OperaDriver
import net.thucydides.core.util.MockEnvironmentVariables
import org.openqa.selenium.Capabilities
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.ie.InternetExplorerDriver
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.remote.RemoteWebDriver
import spock.lang.Specification

class WhenConfiguringTheWebdriverInstance extends Specification {

    def environmentVariables = new MockEnvironmentVariables()

    def firefox = Mock(FirefoxDriver)
    def chrome = Mock(ChromeDriver)
    def htmlunit = Mock(HtmlUnitDriver)
    def iexplore = Mock(InternetExplorerDriver)
    def opera = Mock(PhantomJSDriver)   // Currently there is a bug in the opera driver
    def remote = Mock(RemoteWebDriver)
    def phantomdriver = Mock(PhantomJSDriver)
    Capabilities configuredRemoteCapabilities

    def webdriverInstanceFactory = new WebdriverInstanceFactory() {
        @Override
        WebDriver newFirefoxDriver(Capabilities capabilities) { return firefox }

        @Override
        WebDriver newChromeDriver(Capabilities capabilities) { return chrome }

        @Override
        WebDriver newHtmlUnitDriver(Capabilities caps) { return htmlunit }

        @Override
        WebDriver newPhantomDriver(Capabilities caps) { return phantomdriver }

        @Override
        WebDriver newRemoteDriver(URL remoteUrl, Capabilities capabilities) {
            configuredRemoteCapabilities = capabilities;
            return remote;
        }

        @Override
        WebDriver newInternetExplorerDriver(Capabilities capabilities) {
            return iexplore;
        }

        @Override
        WebDriver newInstanceOf(Class<? extends WebDriver> webdriverClass) {
            switch (webdriverClass) {
                case OperaDriver : return opera
            }
        }
    }

    WebDriverFactory webDriverFactory

    Capabilities capabilities = Mock(Capabilities)
    def setup() {
        capabilities.asMap() >> [:]
        remote.getCapabilities() >> capabilities
        webDriverFactory = new WebDriverFactory(webdriverInstanceFactory, environmentVariables)
    }

    def "Should create firefox driver proxy when required"() {
        when:
            def webdriver = webDriverFactory.newInstanceOf(SupportedWebDriver.FIREFOX)
        then:
            webdriver == firefox
    }

    def "Should create chrome driver proxy when required"() {
        when:
            def webdriver = webDriverFactory.newInstanceOf(SupportedWebDriver.CHROME)
        then:
            webdriver == chrome
    }

    def "Should create htmlunit driver proxy when required"() {
        when:
            def webdriver = webDriverFactory.newInstanceOf(SupportedWebDriver.HTMLUNIT)
        then:
            webdriver == htmlunit

    }

    def "Should create phantomjs driver proxy when required"() {
        when:
        def webdriver = webDriverFactory.newInstanceOf(SupportedWebDriver.PHANTOMJS)
        then:
        webdriver == phantomdriver

    }

    def "Should create IE driver proxy when required"() {
        when:
            def webdriver = webDriverFactory.newInstanceOf(SupportedWebDriver.IEXPLORER)
        then:
            webdriver == iexplore

    }

//    @Ignore("Opera driver needs updating")
    def "Should create opera driver proxy when required"() {
        when:
            def webdriver = webDriverFactory.newInstanceOf(SupportedWebDriver.OPERA)
        then:
            webdriver == opera

    }


    def "Should create remote driver proxy when required"() {
        when:
            environmentVariables.setProperty("webdriver.remote.url", "http://some.remote.server")
            def webdriver = webDriverFactory.newInstanceOf(SupportedWebDriver.REMOTE)
        then:
            webdriver == remote
    }

    def "Should create remote driver with the correct browser when required"() {
        when:
            environmentVariables.setProperty("webdriver.remote.url", "http://some.remote.server")
            environmentVariables.setProperty("webdriver.remote.driver", "iexplorer")
            def webdriver = webDriverFactory.newInstanceOf(SupportedWebDriver.REMOTE)
        then:
            webdriver == remote && configuredRemoteCapabilities.getCapability("browserName") == "internet explorer"
    }

    def "Should create remote driver using the browser defined in webdriver.driver if webdriver.remote.driver is not defined"() {
        when:
            environmentVariables.setProperty("webdriver.remote.url", "http://some.remote.server")
            environmentVariables.setProperty("webdriver.driver", "iexplorer")
            def webdriver = webDriverFactory.newInstanceOf(SupportedWebDriver.REMOTE)
        then:
            webdriver == remote && configuredRemoteCapabilities.getCapability("browserName") == "internet explorer"
    }

    def "Should throw meaningful error message if the driver is invalid"() {
        when:
            environmentVariables.setProperty("webdriver.remote.url", "http://some.remote.server")
            environmentVariables.setProperty("webdriver.driver", "iexploror")
            webDriverFactory.newInstanceOf(SupportedWebDriver.REMOTE)
        then:
            AssertionError error = thrown()
            error.message == "Unsupported driver for webdriver.driver or webdriver.remote.driver: iexploror. Did you mean iexplorer?"
    }


    def "Should create saucelabs remote driver proxy when required"() {
        when:
            environmentVariables.setProperty("saucelabs.url", "http://some.saucelabs.server")
            def webdriver = webDriverFactory.newInstanceOf(SupportedWebDriver.FIREFOX)
        then:
            webdriver == remote

    }

    def "should know what driver to use"() {
        when:
            def driverClass = webDriverFactory.getClassFor(driverType)
        then:
            driverClass == expectedClass
        where:
            driverType                      | expectedClass
            SupportedWebDriver.FIREFOX      | FirefoxDriver
            SupportedWebDriver.IEXPLORER    | InternetExplorerDriver
            SupportedWebDriver.CHROME       | ChromeDriver
            SupportedWebDriver.OPERA        | OperaDriver
            SupportedWebDriver.HTMLUNIT     | HtmlUnitDriver
            SupportedWebDriver.REMOTE       | RemoteWebDriver
    }

    def "should be able to list supported drivers"() {
        when:
            def supportedDrivers = SupportedWebDriver.listOfSupportedDrivers()
        then:
            supportedDrivers.contains "FIREFOX"
            supportedDrivers.contains "IEXPLORER"
            supportedDrivers.contains "CHROME"
            supportedDrivers.contains "OPERA"
            supportedDrivers.contains "HTMLUNIT"
    }

}
