package net.serenitybdd.core.webdriver

import net.serenitybdd.core.annotations.findby.FindBy
import net.serenitybdd.core.pages.PageObject
import net.thucydides.core.pages.WebElementFacade
import net.thucydides.model.util.EnvironmentVariables
import net.thucydides.model.environment.MockEnvironmentVariables
import net.thucydides.core.webdriver.DefaultPageObjectInitialiser
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.RemoteWebDriver
import spock.lang.Specification

class WhenInitializingPageObjectFields extends Specification {

    EnvironmentVariables environmentVariables = new MockEnvironmentVariables()
    def driver = Mock(RemoteWebDriver)

    class SamplePageObject extends PageObject {

        WebElement someField

        WebElementFacade someFieldFacade

        @FindBy(css="li")
        List<WebElement> someFieldList

        @FindBy(css="li")
        List<WebElementFacade> someFieldFacadeList

        SamplePageObject(WebDriver driver) {
            super(driver)
        }
    }

	def "should initialize WebElement page object fields"() {
        when:
            def page = new SamplePageObject(driver)
        then:
            page.someField != null
    }

    def "should initialize WebElementFacade page object fields"() {
        when:
            def page = new SamplePageObject(driver)
        then:
            page.someFieldFacade != null
    }

    def "should initialize WebElement list fields"() {
       when:
            def page = new SamplePageObject(driver)
        then:
            page.someFieldList != null
    }

    def "should initialize WebElementFacade list fields"() {
        when:
            def page = new SamplePageObject(driver)
        then:
            page.someFieldFacadeList != null
    }

    def "should default to at least 1 second AJAX timeout"() {
        when:
            def pageObjectInitialiser = new DefaultPageObjectInitialiser(driver, 5)
        then:
            pageObjectInitialiser.ajaxTimeoutInSecondsWithAtLeast1Second() == 1
    }

    def "should convert timeout to seconds"() {
        when:
           def pageObjectInitialiser = new DefaultPageObjectInitialiser(driver, 5000)
        then:
            pageObjectInitialiser.ajaxTimeoutInSecondsWithAtLeast1Second() == 5
    }

}
