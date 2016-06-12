package net.serenitybdd.core.annotations.findby.integration

import net.thucydides.core.webdriver.integration.PageWithFindBys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.phantomjs.PhantomJSDriver
import spock.lang.Specification

class WhenLocatingWebElementsUsingEnhancedFindBys extends Specification {

    static WebDriver driver


    def "should load simple @FindBy fields"() {
        given:
            PageWithFindBys page = new PageWithFindBys(driver)
            page.open()
        when:
            def options = page.allTheOptions
        then:
            options.size() == 8
    }

    def "should load @FindBy fields even when elements aren't visible yet"() {
        given:
            PageWithFindBys page = new PageWithFindBys(driver)
            page.open()
        when:
            def options = page.allTheInputFields
        then:
            options.size() == 20
    }

    def "should load @FindAll fields even when elements aren't visible yet"() {
        given:
            PageWithFindBys page = new PageWithFindBys(driver)
            page.open()
        when:
            def options = page.allTheInputAndOptionsFields
        then:
            options.size() == 28
    }

    def setupSpec() {
        driver = new PhantomJSDriver()
    }

    def cleanupSpec() {
        driver.quit()
    }
}
