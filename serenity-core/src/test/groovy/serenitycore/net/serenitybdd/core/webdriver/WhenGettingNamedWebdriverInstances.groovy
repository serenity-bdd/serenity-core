package serenitycore.net.serenitybdd.core.webdriver

import serenitymodel.net.thucydides.core.configuration.SystemPropertiesConfiguration
import serenitycore.net.thucydides.core.configuration.WebDriverConfiguration
import serenitymodel.net.thucydides.core.util.EnvironmentVariables
import serenitymodel.net.thucydides.core.util.MockEnvironmentVariables
import serenitycore.net.thucydides.core.webdriver.SerenityWebdriverManager
import serenitycore.net.thucydides.core.webdriver.WebDriverFactory
import spock.lang.Ignore
import spock.lang.Specification

class WhenGettingNamedWebdriverInstances extends Specification {

    EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
    SystemPropertiesConfiguration configuration = new WebDriverConfiguration(environmentVariables);

    def setup() {
        SerenityWebdriverManager.inThisTestThread().closeAllDrivers()
        SerenityWebdriverManager.resetThisThread()
    }

    def "should be able to request explicitly several named driver instances"() {
        given:
            def webdriverManager = new SerenityWebdriverManager(new WebDriverFactory(), configuration)
        when:
            def driver1 = webdriverManager.getWebdriverByName("Fred")
            def driver2 = webdriverManager.getWebdriverByName("Freda")
        then:
            driver1.driverClass.name.contains("Firefox")
            driver2.driverClass.name.contains("Firefox")
            driver1 != driver2

    }

    @Ignore("Fails on Github for some reason - pending investigation")
    def "Named driver instances should respect the default configured browser if provided"() {
        given:
            environmentVariables.setProperty("webdriver.driver", "chrome")
            def webdriverManager = new SerenityWebdriverManager(new WebDriverFactory(environmentVariables), configuration)
        when:
            def driver = webdriverManager.getWebdriverByName("Charlie")
        then:
            driver.driverClass.name.contains("Chrome")
    }

    def "You can provide a driver type for named driver instances"() {
        given:
            def webdriverManager = new SerenityWebdriverManager(new WebDriverFactory(), configuration)
        when:
            def driver = webdriverManager.getWebdriverByName("Henrietta","htmlunit")
        then:
            driver.driverClass.name.contains("HtmlUnit")
    }
}
