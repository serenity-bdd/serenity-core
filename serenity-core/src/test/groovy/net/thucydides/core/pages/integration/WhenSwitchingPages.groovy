package net.thucydides.core.pages.integration

import net.serenitybdd.core.pages.PageObject
import net.thucydides.core.pages.Pages
import org.openqa.selenium.WebDriver
import spock.lang.Specification

class WhenSwitchingPages extends Specification {

    static class PageObjectA extends PageObject {

        PageObjectA(WebDriver driver) {
            super(driver)
        }

        PageObjectB switchToPageB() {
            switchToPage(PageObjectB.class)
        }
    }

    static class PageObjectB extends PageObject {

        PageObjectB(WebDriver driver) {
            super(driver)
        }
    }

    def driver = Mock(WebDriver)

    def "should be able to switch between pages from withing a page"() {
        given:
        Pages pageFactory = new Pages(driver);
            def pageA = pageFactory[PageObjectA]
        when:
            def pageB = pageA.switchToPageB()
        then:
            pageB instanceof PageObjectB
            pageB.getDriver() != null
    }
}