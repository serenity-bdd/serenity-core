package net.thucydides.core.fixtureservices

import net.thucydides.core.util.MockEnvironmentVariables
import net.thucydides.core.webdriver.SupportedWebDriver
import net.thucydides.core.webdriver.WebDriverFacade
import net.thucydides.core.webdriver.WebDriverFactory
import net.thucydides.core.webdriver.WebdriverInstanceFactory
import net.thucydides.core.webdriver.firefox.FirefoxProfileEnhancer
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import spock.lang.Specification

class WhenUsingFixtureServices extends Specification {
    def "should load fixture services from the classpath"() {
        given:
            def fixtureServiceLoader = new ClasspathFixtureProviderService()
        when:
            def fixtureServices = fixtureServiceLoader.getFixtureServices()
        then:
            fixtureServices.find { it.getClass() == SampleFixtureService }
    }

    def webdriverInstanceFactory = Mock(WebdriverInstanceFactory)
    def environmentVariables = new MockEnvironmentVariables()
    def firefoxProfileEnhancer = Mock(FirefoxProfileEnhancer)
    def fixtureProviderService = Mock(FixtureProviderService)
    def fixtureService = Mock(FixtureService)
    def driver = Mock(WebDriver)

    def "fixture services should be invoked when creating driver instances"() {
        given:
            fixtureProviderService.getFixtureServices() >> [fixtureService]
            webdriverInstanceFactory.newFirefoxDriver(_) >> driver
            def webdriverFactory = new WebDriverFactory(webdriverInstanceFactory, environmentVariables,
                                                        firefoxProfileEnhancer,fixtureProviderService)
        when:
            webdriverFactory.newInstanceOf(SupportedWebDriver.FIREFOX)
        then:
            1 * fixtureService.addCapabilitiesTo(_)

    }

    def "fixture service should be setup whenever a driver instance is created"() {
        given:
            fixtureProviderService.getFixtureServices() >> [fixtureService]
            webdriverInstanceFactory.newFirefoxDriver(_) >> driver
            webdriverInstanceFactory.newWebdriverInstance(_) >> driver
            def webdriverFactory = new WebDriverFactory(webdriverInstanceFactory, environmentVariables,
                                                        firefoxProfileEnhancer,fixtureProviderService)
        and:
            def webdriver = new WebDriverFacade(FirefoxDriver, webdriverFactory)
        when:
            webdriver.get("http://some.site")
            webdriver.get("http://some.other.site")
        then:
            1 * fixtureService.setup()
    }

    def "fixture service should be shut down whenever a driver instance is closed"() {
        given:
            fixtureProviderService.getFixtureServices() >> [fixtureService]
            webdriverInstanceFactory.newFirefoxDriver(_) >> driver
            webdriverInstanceFactory.newWebdriverInstance(_) >> driver
            def webdriverFactory = new WebDriverFactory(webdriverInstanceFactory, environmentVariables,
                    firefoxProfileEnhancer,fixtureProviderService)
        and:
            def webdriver = new WebDriverFacade(FirefoxDriver, webdriverFactory)
        when:
            webdriver.get("http://some.site")
            webdriver.close()
        then:
            1 * fixtureService.shutdown()
    }
}
