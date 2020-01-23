package net.serenitybdd.core.annotations.findby.integration

import net.serenitybdd.core.webdriver.servicepools.ChromeServicePool
import net.serenitybdd.core.webdriver.servicepools.DriverServicePool
import net.thucydides.core.steps.StepEventBus
import net.thucydides.core.webdriver.SerenityWebdriverManager
import net.thucydides.core.webdriver.integration.PageWithFindBys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.DesiredCapabilities
import spock.lang.Shared
import spock.lang.Specification

class WhenLocatingWebElementsUsingEnhancedFindBys extends Specification {


    @Shared DriverServicePool chromeService;
    WebDriver driver

    def setupSpec() {
        chromeService = new ChromeServicePool()
        chromeService.start()
        StepEventBus.eventBus.clear()

    }

    def cleanupSpec() {
        chromeService.shutdown()
    }

    def setup() {
        StepEventBus.eventBus.clear()

        def desiredCapabilities = DesiredCapabilities.chrome();
        def chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);

        driver = chromeService.newDriver(desiredCapabilities);
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
