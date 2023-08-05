package net.serenitybdd.core.webdriver

import net.serenitybdd.model.environment.ConfiguredEnvironment
import net.serenitybdd.core.environment.WebDriverConfiguredEnvironment
import net.serenitybdd.annotations.Managed
import net.thucydides.core.annotations.TestCaseAnnotations
import net.thucydides.core.configuration.WebDriverConfiguration
import net.thucydides.model.util.EnvironmentVariables
import net.thucydides.model.environment.MockEnvironmentVariables
import net.thucydides.core.webdriver.DriverConfiguration
import net.thucydides.core.webdriver.SerenityWebdriverManager
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport
import net.thucydides.core.webdriver.WebDriverFactory
import spock.lang.Specification

class WhenInjectingWebdriverInstancesIntoATestCase extends Specification {

    EnvironmentVariables environmentVariables
    DriverConfiguration configuration
    WebDriverFactory webdriverFactory

    static class WithADefaultDriver {
        @Managed driver
    }

    def setup() {
        environmentVariables = new MockEnvironmentVariables()
        configuration = new WebDriverConfiguration(environmentVariables)
        webdriverFactory = new WebDriverFactory(environmentVariables)
        ThucydidesWebDriverSupport.initialize()
        ThucydidesWebDriverSupport.reset()
        ConfiguredEnvironment.reset()
    }

    def "should inject @Managed driver field with the configured browser type by default if defined"() {
        given:
            WebDriverConfiguredEnvironment.setTestEnvironmentVariables(environmentVariables)
        when:
            environmentVariables.setProperty("webdriver.driver", "chrome")
            def testCase = new WithADefaultDriver()
            def webdriverManager = new SerenityWebdriverManager(webdriverFactory, configuration)
            new TestCaseAnnotations(testCase, configuration).injectDrivers(webdriverManager)
        then:
            testCase.driver && testCase.driver.driverClass.name.contains("Chrome")
    }

    def "should inject @Managed driver field with a chrome instance by default"() {
        given:
            WebDriverConfiguredEnvironment.setTestEnvironmentVariables(environmentVariables)
            def testCase = new WithADefaultDriver()
            def webdriverManager = new SerenityWebdriverManager(webdriverFactory, configuration)
        when:
            TestCaseAnnotations.forTestCase(testCase).injectDrivers(webdriverManager)
        then:
        testCase.driver && testCase.driver.driverClass.name.contains("Chrome")
    }

    static class WithADriverOfASpecifiedType {
        @Managed(driver="chrome") driver
    }

    def "should inject @Managed driver field with a specified type if requested"() {
        given:
            def testCase = new WithADriverOfASpecifiedType()
            def webdriverManager = new SerenityWebdriverManager(webdriverFactory, configuration)
        when:
            TestCaseAnnotations.forTestCase(testCase).injectDrivers(webdriverManager)
        then:
            testCase.driver && testCase.driver.driverClass.name.contains("Chrome")
    }

    static class WithMultipleDrivers {
        @Managed driver1
        @Managed driver2
    }

    def "should inject a different driver for each @Managed field"() {
        given:
            def testCase = new WithMultipleDrivers()
            def webdriverManager = new SerenityWebdriverManager(webdriverFactory, configuration)
        when:
            TestCaseAnnotations.forTestCase(testCase).injectDrivers(webdriverManager)
        then:
            testCase.driver1.driverClass.name.contains("Chrome")
        and:
            testCase.driver2.driverClass.name.contains("Chrome")
        and:
            testCase.driver1 != testCase.driver2
    }


    static class WithMultipleDriversOfDifferentTypes {
        @Managed(driver = "firefox") driver1
        @Managed(driver = "chrome") driver2
    }

    def "should inject a different driver for each @Managed field with different types"() {
        given:
            def testCase = new WithMultipleDriversOfDifferentTypes()
            def webdriverManager = new SerenityWebdriverManager(webdriverFactory, configuration)
        when:
            TestCaseAnnotations.forTestCase(testCase).injectDrivers(webdriverManager)
        then:
            testCase.driver1 && testCase.driver1.driverClass.name.contains("Firefox")
        and:
            testCase.driver2 && testCase.driver2.driverClass.name.contains("Chrome")
    }

    static class WithMultipleDriversOfDifferentTypeWithADefaultValueFirst {
        @Managed driver1
        @Managed(driver = "firefox") driver2
    }

    def "should inject a different driver for each @Managed field with a mixture of types and defaults"() {
        given:
        def testCase = new WithMultipleDriversOfDifferentTypeWithADefaultValueFirst()
        def webdriverManager = new SerenityWebdriverManager(webdriverFactory, configuration)
        when:
        TestCaseAnnotations.forTestCase(testCase).injectDrivers(webdriverManager)
        then:
        testCase.driver1 && testCase.driver1.driverClass.name.contains("Chrome")
        and:
        testCase.driver2 && testCase.driver2.driverClass.name.contains("Firefox")
    }

    static class WithMultipleDriversOfDifferentTypeWithADefaultValueLast {
        @Managed driver1
        @Managed(driver = "chrome") driver2
        @Managed(driver = "firefox") driver3
        @Managed driver4
    }

    def "should inject a different driver for each @Managed field with a mixture of types and defaults with the default last"() {
        given:
            def testCase = new WithMultipleDriversOfDifferentTypeWithADefaultValueLast()
            def webdriverManager = new SerenityWebdriverManager(webdriverFactory, configuration)
        when:
            TestCaseAnnotations.forTestCase(testCase).injectDrivers(webdriverManager)
        then:
            testCase.driver1 && testCase.driver1.driverClass.name.contains("Chrome")
        and:
            testCase.driver2 && testCase.driver2.driverClass.name.contains("Chrome")
        and:
            testCase.driver3 && testCase.driver3.driverClass.name.contains("Firefox")
        and:
            testCase.driver4 && testCase.driver4.driverClass.name.contains("Chrome")
        and:
           testCase.driver3 != testCase.driver2
    }
}
