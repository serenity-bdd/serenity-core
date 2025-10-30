package net.serenitybdd.core.webdriver

import net.thucydides.core.configuration.WebDriverConfiguration
import net.thucydides.core.webdriver.SerenityWebdriverManager
import net.thucydides.core.webdriver.WebDriverFactory
import net.thucydides.model.configuration.SystemPropertiesConfiguration
import net.thucydides.model.environment.MockEnvironmentVariables
import net.thucydides.model.util.EnvironmentVariables
import spock.lang.Specification

class WhenGettingNamedWebdriverInstances extends Specification {

    EnvironmentVariables environmentVariables = new MockEnvironmentVariables()
    SystemPropertiesConfiguration configuration = new WebDriverConfiguration(environmentVariables)

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
            driver1.driverClass.name.contains("Chrome")
            driver2.driverClass.name.contains("Chrome")
            driver1 != driver2

    }

    def "You can provide a driver type for named driver instances"() {
        given:
            def webdriverManager = new SerenityWebdriverManager(new WebDriverFactory(), configuration)
        when:
            def driver = webdriverManager.getWebdriverByName("Henrietta","chrome")
        then:
            driver.driverClass.name.contains("chrome")
    }
}
