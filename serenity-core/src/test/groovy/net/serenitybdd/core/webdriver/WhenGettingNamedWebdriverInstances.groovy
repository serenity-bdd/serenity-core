package net.serenitybdd.core.webdriver

import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import net.thucydides.core.webdriver.SerenityWebdriverManager
import net.thucydides.core.configuration.SystemPropertiesConfiguration
import net.thucydides.core.webdriver.WebDriverFactory
import spock.lang.Specification

class WhenGettingNamedWebdriverInstances extends Specification {

    EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
    SystemPropertiesConfiguration configuration = new SystemPropertiesConfiguration(environmentVariables);

    def "should be able to request explicitly several named driver instances"() {
        given:
            def webdriverManager = new SerenityWebdriverManager(new WebDriverFactory(), configuration)
        when:
            def driver1 = webdriverManager.getWebdriverByName("James")
            def driver2 = webdriverManager.getWebdriverByName("Jane")
        then:
            driver1.driverClass.name.contains("Firefox")
            driver2.driverClass.name.contains("Firefox")
            driver1 != driver2

    }

    def "Named driver instances should respect the default configured browser if provided"() {
        given:
            environmentVariables.setProperty("webdriver.driver", "chrome")
            def webdriverManager = new SerenityWebdriverManager(new WebDriverFactory(), configuration)
        when:
            def driver = webdriverManager.getWebdriverByName("James")
        then:
            driver.driverClass.name.contains("Chrome")
    }

    def "You can provide a driver type for named driver instances"() {
        given:
            def webdriverManager = new SerenityWebdriverManager(new WebDriverFactory(), configuration)
        when:
            def driver = webdriverManager.getWebdriverByName("James","htmlunit")
        then:
            driver.driverClass.name.contains("HtmlUnit")
    }
}
