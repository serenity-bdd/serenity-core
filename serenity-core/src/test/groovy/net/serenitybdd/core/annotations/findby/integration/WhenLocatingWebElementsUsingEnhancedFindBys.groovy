package net.serenitybdd.core.annotations.findby.integration

import net.serenitybdd.core.support.ChromeService
import net.thucydides.core.steps.StepEventBus
import net.thucydides.core.webdriver.SerenityWebdriverManager
import net.thucydides.core.webdriver.integration.PageWithFindBys
import org.openqa.selenium.WebDriver
import spock.lang.Shared
import spock.lang.Specification

class WhenLocatingWebElementsUsingEnhancedFindBys extends Specification {


    @Shared ChromeService chromeService;
    WebDriver driver

    def setupSpec() {
        chromeService = new ChromeService()
        chromeService.start()
        StepEventBus.eventBus.clear()

    }

    def cleanupSpec() {
        chromeService.stop()
    }

    def setup() {
        StepEventBus.eventBus.clear()
        driver = chromeService.newDriver()
    }

    def cleanup() {
        SerenityWebdriverManager.inThisTestThread().closeAllDrivers();
        if (driver) {
            driver.quit();
        }
    }


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
            options.size() == (20 + 8)
    }
}
