package net.serenitybdd.core.pages

import net.serenitybdd.core.annotations.findby.By
import net.thucydides.core.pages.integration.StaticSitePage
import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import net.thucydides.core.webdriver.StaticTestSite
import org.openqa.selenium.support.ui.Duration
import spock.lang.Specification

import static java.util.concurrent.TimeUnit.MILLISECONDS
import static java.util.concurrent.TimeUnit.SECONDS

/**
 * Created by john on 20/01/15.
 */
class WhenManagingWebdriverTimeouts extends Specification {

    StaticTestSite staticTestSite
    StaticSitePage page
    def driver
    EnvironmentVariables environmentVariables

    def setup() {
        environmentVariables = new MockEnvironmentVariables();
    }

    def cleanup() {
        if (driver) {
            driver.close()
        }
    }

    def StaticSitePage openTestPageUsing(String browser) {
        staticTestSite = new StaticTestSite(environmentVariables)
        driver = staticTestSite.open(browser)
        page = new StaticSitePage(driver);
        page.open()
        return page
    }

    def "WebDriver Implicit Waits are defined using the webdriver.timeouts.implicitlywait system property"() {
        // This uses the driver.manage().timeouts().implicitlyWait() method under the hood
        // (See http://docs.seleniumhq.org/docs/04_webdriver_advanced.jsp#implicit-waits)
        // It applies by default to all web elements
        given: "The #city field takes 500 milliseconds to load"
        and: "We configure the WebDriver implicit wait to be 100 milliseconds"
            environmentVariables.setProperty("webdriver.timeouts.implicitlywait","100")
        when: "We access the field"
            page = openTestPageUsing("firefox")
            page.city.isDisplayed()
        then: "An error should be thrown"
            thrown(Exception)

    }

    def "should be able to tailor the waitfor timeout"() {
        when:
            page = openTestPageUsing("firefox")
        then:
            page.withTimeoutOf(2, SECONDS).elementIsDisplayed(By.name("city"));
    }

    def "should fail when waitfor timeout"() {
        when:
            page = openTestPageUsing("firefox")
        then:
            !page.withTimeoutOf(500, MILLISECONDS).elementIsDisplayed(By.id("invisible"));
    }

    def "waitfor timeout should work for lists"() {
        when:
            page = openTestPageUsing("firefox")
            def elements = page.findAll(By.cssSelector("#elements option"))
        then:
            elements.size() == 4
    }
    def "waitfor timeout should be configurable for lists"() {
        when:
            page = openTestPageUsing("firefox")
            def elements = page.usingTimeoutOf(new Duration(100,MILLISECONDS)).findAll(By.cssSelector("#elements option"))
        then:
            elements.size() == 0
    }

//
//    def "waitfor timeout should be configurable for lists"() {
//        when:
//            def elements = page.usingTimeoutOf(new Dur).findAll(By.cssSelector("#elements option"))
//        then:
//            elements.size() == 0
//    }
}
