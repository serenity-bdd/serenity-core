package net.serenitybdd.core.webdriver

import net.serenitybdd.core.pages.PageObject
import net.thucydides.core.annotations.DefaultUrl
import net.thucydides.core.webdriver.TimeoutStack
import net.thucydides.core.webdriver.WebDriverFacade
import net.thucydides.core.webdriver.WebDriverFactory
import org.openqa.selenium.WebDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import spock.lang.Specification

import java.time.Duration
import java.time.temporal.ChronoUnit

/**
 * Created by john on 12/03/15.
 */
class WhenKeepingTrackOfImplicitTimeouts extends Specification{

    WebDriver driver = Mock()
    WebDriver anotherDriver = Mock()
    WebDriver yetAnotherDriver = Mock()

    def "stack should be initially empty"() {
        when:
            def timeoutstack = new TimeoutStack();
        then:
            !timeoutstack.popTimeoutFor(driver).isPresent()
    }

    def "you can check whether a timeout value has been specified for a driver"() {
        given:
            def timeoutstack = new TimeoutStack();
            def timeout = Duration.ofSeconds(1) 
        when:
            timeoutstack.pushTimeoutFor(driver,timeout)
            timeoutstack.pushTimeoutFor(anotherDriver,timeout)
            timeoutstack.popTimeoutFor(anotherDriver)
        then:
            timeoutstack.containsTimeoutFor(driver)
        and:
            !timeoutstack.containsTimeoutFor(anotherDriver)
        and:
            !timeoutstack.containsTimeoutFor(yetAnotherDriver)

    }

    def "you can check the current timeout value for a driver"() {
        given:
            def timeoutstack = new TimeoutStack();
            def timeout = Duration.ofSeconds(1)
        when:
            timeoutstack.pushTimeoutFor(driver,timeout)
            def currentValue1 = timeoutstack.currentTimeoutValueFor(driver)
            def currentValue2 = timeoutstack.currentTimeoutValueFor(anotherDriver)
        then:
            currentValue1.isPresent() && currentValue1.get() == timeout
        and:
            !currentValue2.isPresent()

    }

    def "you can store a timeout duration for a particular driver"() {
        given:
            def timeoutstack = new TimeoutStack();
            def timeout = Duration.ofSeconds(1)
        when:
            timeoutstack.pushTimeoutFor(driver,timeout)
        and:
            Optional<Duration> storedDuration = timeoutstack.popTimeoutFor(driver)
        then:
            storedDuration.isPresent()
        and:
            storedDuration.get() == timeout
        and:
            !timeoutstack.popTimeoutFor(driver).isPresent()
    }

    def "you can store timeout durations for a multiple drivers"() {
        given:
            def timeoutstack = new TimeoutStack();
            def timeout1 = Duration.ofSeconds(1)
            def timeout2 = Duration.ofSeconds(2)
        when:
            timeoutstack.pushTimeoutFor(driver,timeout1)
            timeoutstack.pushTimeoutFor(anotherDriver,timeout2)
        and:
            Optional<Duration> storedDuration1 = timeoutstack.popTimeoutFor(driver)
            Optional<Duration> storedDuration2 = timeoutstack.popTimeoutFor(anotherDriver)
        then:
            storedDuration1.get() == timeout1
        and:
            storedDuration2.get() == timeout2
    }

    def "you can store multiple timeout durations for a driver"() {
        given:
            def timeoutstack = new TimeoutStack();
            def timeout1 = Duration.ofSeconds(1)
            def timeout2 = Duration.ofSeconds(2)
        when:
            timeoutstack.pushTimeoutFor(driver,timeout1)
            timeoutstack.pushTimeoutFor(driver,timeout2)
        and:
            Optional<Duration> storedDuration1 = timeoutstack.popTimeoutFor(driver)
            Optional<Duration> storedDuration2 = timeoutstack.popTimeoutFor(driver)
        then:
            storedDuration1.get() == timeout2
        and:
            storedDuration2.get() == timeout1
    }

    @DefaultUrl("classpath:static-site/index.html")
    static class PageObjectUsingImplicitTimeouts extends PageObject {

        PageObjectUsingImplicitTimeouts(WebDriver driver) {
            super(driver)
        }

        public void setImplicitTimeoutTo(int timeout) {
            setImplicitTimeout(timeout, ChronoUnit.SECONDS);
        }

        public long getImplicitTimoutMilliseconds() {
            return implicitTimoutMilliseconds();
        }
    }

    def "should be able to set the implicit timeout"() {
        given:
            WebDriverFacade driver = new WebDriverFacade(HtmlUnitDriver, new WebDriverFactory());
            def pageObject = new PageObjectUsingImplicitTimeouts(driver)
            pageObject.open()
        when:
            pageObject.setImplicitTimeoutTo(3)
        then:
            pageObject.getImplicitTimoutMilliseconds() == 3000
    }

    def "should be able to reset the implicit timeout"() {
        given:
            def driver = new WebDriverFacade(HtmlUnitDriver, new WebDriverFactory());
            def pageObject = new PageObjectUsingImplicitTimeouts(driver)
            def originalTimeout = pageObject.getImplicitTimoutMilliseconds()
        when:
            pageObject.setImplicitTimeoutTo(12)
        and:
            pageObject.resetImplicitTimeout()
        then:
            pageObject.getImplicitTimoutMilliseconds() == originalTimeout
    }

    def "should be able to set and reset the implicit timeout using nested calls"() {
        given:
            def driver = new WebDriverFacade(HtmlUnitDriver, new WebDriverFactory());
            def pageObject = new PageObjectUsingImplicitTimeouts(driver)
            def originalTimeout = pageObject.getImplicitTimoutMilliseconds()
        when:
            pageObject.setImplicitTimeoutTo(6)
            pageObject.setImplicitTimeoutTo(12)
        then:
            pageObject.getImplicitTimoutMilliseconds() == 12000
            pageObject.resetImplicitTimeout()
            pageObject.getImplicitTimoutMilliseconds() == 6000
            pageObject.resetImplicitTimeout()
            pageObject.getImplicitTimoutMilliseconds() == originalTimeout
    }

}

