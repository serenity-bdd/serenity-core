package net.thucydides.core.webdriver

import org.openqa.selenium.WebDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.phantomjs.PhantomJSDriver
import spock.lang.Ignore
import spock.lang.Specification

import java.util.concurrent.TimeUnit

/**
 * Created by john on 30/01/15.
 */
class WhenMonitoringWebDriverTimeoutOptions extends Specification {

    def webDriverFactory = new WebDriverFactory()

    @Ignore
    def "should record the driver implicit timeout when it is set"() {
        given:
            def driver = new WebDriverFacade(PhantomJSDriver.class, webDriverFactory)
        when:
            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS)
        then:
            driver.implicitTimeoutValue == 30
            driver.implicitTimeoutUnit == TimeUnit.SECONDS
    }
}
