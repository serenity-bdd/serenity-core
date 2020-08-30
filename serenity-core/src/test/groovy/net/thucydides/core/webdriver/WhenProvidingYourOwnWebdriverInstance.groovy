package net.thucydides.core.webdriver

import net.thucydides.core.steps.StepEventBus
import net.thucydides.core.util.MockEnvironmentVariables
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import spock.lang.Specification

class WhenProvidingYourOwnWebdriverInstance extends Specification {

    def environmentVariables = new MockEnvironmentVariables()

    def "should be able to use the 'PROVIDED' type to say that we want to provide our own driver"() {
        expect:
            SupportedWebDriver.listOfSupportedDrivers().contains "PROVIDED"
    }

    def driver

    def cleanup() {
        if (driver) {
            driver.close()
            driver.quit()
        }
    }

    def setup() {
        StepEventBus.eventBus.clear()
    }

    def "should be able to ask the WebDriverFactory to provide a custom driver"() {

        given:
            environmentVariables.setProperty("webdriver.driver","provided")
            environmentVariables.setProperty("webdriver.provided.type","mydriver")
            environmentVariables.setProperty("webdriver.provided.mydriver","net.thucydides.core.webdriver.MyDriverSource")
            def factory = new WebDriverFactory(environmentVariables)
        when:
            driver = factory.newWebdriverInstance(ProvidedDriver);
        then:
            driver.class == HtmlUnitDriver
    }

    def "should be able to know when a provided driver is provided"() {
        given:
            environmentVariables.setProperty("webdriver.driver","provided")
            environmentVariables.setProperty("webdriver.provided.type","mydriver")
            environmentVariables.setProperty("webdriver.provided.mydriver","net.thucydides.core.webdriver.MyDriverSource")
        when:
            def sourceConfig = new ProvidedDriverConfiguration(environmentVariables)
        then:
            sourceConfig.isProvided()
            sourceConfig.driverName == "mydriver"
    }

    def "should be able to know when a provided driver is not provided"() {
        given:
            environmentVariables.setProperty("webdriver.driver","firefox")
        when:
            def sourceConfig = new ProvidedDriverConfiguration(environmentVariables)
        then:
            !sourceConfig.isProvided()
    }

    def "should be able to know what provided driver is provided"() {
        given:
            environmentVariables.setProperty("webdriver.driver","provided")
            environmentVariables.setProperty("webdriver.provided.type","mydriver")
            environmentVariables.setProperty("webdriver.provided.mydriver","net.thucydides.core.webdriver.MyDriverSource")
        when:
            def sourceConfig = new ProvidedDriverConfiguration(environmentVariables)
            driver = sourceConfig.driverSource.newDriver()
        then:
            driver.class == HtmlUnitDriver
    }

    def "should be able to know if a provided driver can take screenshots"() {
        given:
            environmentVariables.setProperty("webdriver.driver","provided")
            environmentVariables.setProperty("webdriver.provided.type","mydriver")
            environmentVariables.setProperty("webdriver.provided.mydriver","net.thucydides.core.webdriver.MyDriverSource")
        when:
            def sourceConfig = new ProvidedDriverConfiguration(environmentVariables)
        then:
            sourceConfig.driverSource.takesScreenshots()
    }
}
