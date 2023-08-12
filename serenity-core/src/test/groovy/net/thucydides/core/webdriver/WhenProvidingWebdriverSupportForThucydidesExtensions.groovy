package net.thucydides.core.webdriver

import net.serenitybdd.annotations.Managed
import net.serenitybdd.annotations.ManagedPages
import net.thucydides.core.pages.Pages
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import spock.lang.Specification

class WhenProvidingWebdriverSupportForThucydidesExtensions extends Specification {

    def setup() {
        ThucydidesWebDriverSupport.reset()
    }

    def "should allow plugins to instantiate a webdriver manager"() {
        when: "we initialize Thucydides support"
        ThucydidesWebDriverSupport.initialize()
        then: "we can obtain a webdriver manager instance"
        ThucydidesWebDriverSupport.getWebdriverManager() != null
    }

    def "should allow plugins to obtain a webdriver driver instance"() {
        when: "we initialize Thucydides support"
        ThucydidesWebDriverSupport.initialize()
        then: "we can obtain a webdriver driver"
        ThucydidesWebDriverSupport.getDriver() != null
    }

    def "should allow plugins to obtain a page factory instance"() {
        when: "we initialize Thucydides support"
        ThucydidesWebDriverSupport.initialize()
        then: "we can obtain a page factory"
        ThucydidesWebDriverSupport.getPages() != null
    }

    class ObjectWithManagedWebdriver {
        @Managed
        WebDriver driver;
    }

    def "should inject the webdriver instance into the managed field of a specified object"() {
        given: "we initialize Thucydides support"
        ThucydidesWebDriverSupport.initialize()
        and: "we have an object with a @Managed WebDriver field"
        def testCase = new ObjectWithManagedWebdriver()
        when: "we inject the webdriver fields into this class"
        ThucydidesWebDriverSupport.initializeFieldsIn(testCase)
        then: "the webdriver field should be initialized"
        testCase.driver != null
    }

    class ObjectWithManagedPageFactory {
        @ManagedPages
        Pages pages;
    }

    def "should inject the pages instance into the managed field of a specified object"() {
        given: "we initialize Thucydides support"
        ThucydidesWebDriverSupport.initialize()
        and: "we have an object with a @ManagedPages page factory field"
        def testCase = new ObjectWithManagedPageFactory()
        when: "we inject the webdriver fields into this class"
        ThucydidesWebDriverSupport.initializeFieldsIn(testCase)
        then: "the webdriver field should be initialized"
        testCase.pages != null
    }

    def "should prepare a step factory"() {
        given: "we initialize Thucydides support"
        ThucydidesWebDriverSupport.initialize()
        when: "we need a step factory"
        def stepFactory = ThucydidesWebDriverSupport.getStepFactory()
        then: "the support class should provide one"
        stepFactory != null
        and: "the step factory page factory should have been set"
        stepFactory.pages == ThucydidesWebDriverSupport.getPages()
    }

    def "should provide access to the webdriver instance"() {
        when: "we initialize Thucydides support"
        ThucydidesWebDriverSupport.initialize()
        then: "we should be able to access the current driver"
        ThucydidesWebDriverSupport.getDriver() != null
    }

    def "should not initialize again if already initialized with a given driver"() {
        when: "we initialize Thucydides support"
        ThucydidesWebDriverSupport.initialize()
        def firstDriver = ThucydidesWebDriverSupport.driver;
        and: "we initialize support again"
        ThucydidesWebDriverSupport.initialize()
        def secondDriver = ThucydidesWebDriverSupport.driver;
        then: "the driver should be unchanged"
        secondDriver == firstDriver
    }

    def "should be able to define the driver"() {
        when: "we initialize Thucydides support with a specified driver"
        ThucydidesWebDriverSupport.initialize("chrome")
        then: "the provided driver should be of the specified type"
        ThucydidesWebDriverSupport.driver.driverClass == ChromeDriver.class
    }


}
