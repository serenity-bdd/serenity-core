package net.thucydides.core.pages.integration

import net.thucydides.core.pages.WebElementFacadeImpl
import net.thucydides.core.webdriver.WebDriverFacade
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import spock.lang.Specification

class WhenCheckingElementState extends Specification {

    def driver = Mock(WebDriverFacade)
    def webElement = Mock(WebElement)


    def "should display custom error message for web state checks"() {
        given:
            webElement.isDisplayed() >> false
        and:
            def fieldElement = WebElementFacadeImpl.wrapWebElement(driver, webElement, 1000, 1000)
        when:
            fieldElement.expect("My field should be visible").shouldBeVisible();
        then:
            def e = thrown(AssertionError)
            e.message == "My field should be visible"
    }

    def "should delegate to findElements methods"() {
        given:
            def webElementFacade = net.serenitybdd.core.pages.WebElementFacadeImpl.wrapWebElement(driver, webElement, 1000, 1000)
        when:
            webElementFacade.findElements(By.id("someId"))
        then:
            1 * webElement.findElements(By.id("someId"))
    }

    def "should delegate to isDisplayed methods"() {
        given:
            def webElementFacade = net.serenitybdd.core.pages.WebElementFacadeImpl.wrapWebElement(driver, webElement, 1000, 1000)
        when:
            webElementFacade.isDisplayed()
        then:
            1 * webElement.isDisplayed()
    }

    def "should delegate to getSize methods"() {
        given:
        def webElementFacade = net.serenitybdd.core.pages.WebElementFacadeImpl.wrapWebElement(driver, webElement, 1000, 1000)
        when:
        webElementFacade.getSize()
        then:
        1 * webElement.getSize()
    }

    def "should delegate to submit methods"() {
        given:
        def webElementFacade = net.serenitybdd.core.pages.WebElementFacadeImpl.wrapWebElement(driver, webElement, 1000, 1000)
        when:
        webElementFacade.submit()
        then:
        1 * webElement.submit()
    }

    def "should delegate to sendKeys methods"() {
        given:
        def webElementFacade = net.serenitybdd.core.pages.WebElementFacadeImpl.wrapWebElement(driver, webElement, 1000, 1000)
        when:
        webElementFacade.sendKeys("keys")
        then:
        1 * webElement.sendKeys("keys")
    }

    def "should delegate to getTagName methods"() {
        given:
        def webElementFacade = net.serenitybdd.core.pages.WebElementFacadeImpl.wrapWebElement(driver, webElement, 1000, 1000)
        when:
        webElement.getTagName() >> "someTag"
        then:
        webElementFacade.getTagName() == "someTag"
    }


    def "text value of an invisible element should be empty"() {
        given:
        def webElementFacade = net.serenitybdd.core.pages.WebElementFacadeImpl.wrapWebElement(driver, webElement, 1000, 1000)
        when:
        webElement.isDisplayed() >> false
        then:
        webElementFacade.getTextValue() == ""
    }

    def "should delegate to getCssValue methods"() {
        given:
        def webElementFacade = net.serenitybdd.core.pages.WebElementFacadeImpl.wrapWebElement(driver, webElement, 1000, 1000)
        when:
        webElementFacade.getCssValue("value")
        then:
        1 * webElement.getCssValue("value")
    }

    def "should delegate to getAttribute methods"() {
        given:
        def webElementFacade = net.serenitybdd.core.pages.WebElementFacadeImpl.wrapWebElement(driver, webElement, 1000, 1000)
        when:
        webElementFacade.getAttribute("name")
        then:
        1 * webElement.getAttribute("name")
    }

//
//    def "should return immediately if called after  "() {
//        given:
//        def webElementFacade = WebElementFacadeImpl.wrapWebElement(driver, webElement, 1000, 1000)
//        when:
//        webElementFacade.getAttribute("name")
//        then:
//        1 * webElement.getAttribute("name")
//    }
}
