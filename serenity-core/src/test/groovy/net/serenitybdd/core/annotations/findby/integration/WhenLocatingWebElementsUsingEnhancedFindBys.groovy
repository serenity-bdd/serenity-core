package net.serenitybdd.core.annotations.findby.integration

import net.thucydides.core.steps.StepEventBus
import net.thucydides.core.webdriver.SerenityWebdriverManager
import net.thucydides.core.webdriver.integration.PageWithFindBys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriverService
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
import spock.lang.Shared
import spock.lang.Specification

class WhenLocatingWebElementsUsingEnhancedFindBys extends Specification {
    
    @Shared ChromeDriverService chromeDriverService;

    WebDriver driver

    def setupSpec() {
        chromeDriverService = new ChromeDriverService.Builder()
                .usingAnyFreePort()
                .build();
        chromeDriverService.start()

        StepEventBus.eventBus.clear()

    }

    def cleanupSpec() {
        chromeDriverService.stop()
    }

    def WebDriver newDriver() {
        driver = new RemoteWebDriver(chromeDriverService.getUrl(), DesiredCapabilities.chrome());
        return driver
    }

    def setup() {
        StepEventBus.eventBus.clear()
        driver = newDriver()
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
