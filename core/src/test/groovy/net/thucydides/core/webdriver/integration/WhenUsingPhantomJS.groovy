package net.thucydides.core.webdriver.integration

import net.thucydides.core.pages.PageObject
import net.thucydides.core.pages.WebElementFacade
import net.thucydides.core.util.MockEnvironmentVariables
import net.thucydides.core.webdriver.SupportedWebDriver
import net.thucydides.core.webdriver.WebDriverFactory
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import spock.lang.Specification

class WhenUsingPhantomJS extends Specification {

    public static class IndexPage extends PageObject {

        public WebElementFacade firstname;

        public IndexPage(WebDriver driver) {
            super(driver);
        }
    }

    def environmentVariables = new MockEnvironmentVariables()

    WebDriver driver

    def setup() {
        def factory = new WebDriverFactory(environmentVariables)
        driver = factory.newInstanceOf(SupportedWebDriver.PHANTOMJS)
    }
    def cleanup() {
        driver.quit()
    }

    def "should be able to use the PhantomJS driver"() {
        given: "we want to use PhantomJS"
            environmentVariables.setProperty("webdriver.driver","phantomjs")
        when: "we use webdriver"
            IndexPage indexPage = openPageWithPhantomJS()
        then: "we should be able to use the phantom JS driver"
            indexPage.firstname.type("Joe")
            indexPage.firstname.getAttribute("value") == "Joe"
    }

    def "should be able to check visibility with PhantomJS"() {
        given: "we want to use PhantomJS"
            environmentVariables.setProperty("webdriver.driver","phantomjs")
        when: "we use webdriver"
            IndexPage indexPage = openPageWithPhantomJS()
        then: "we should be able to wait for a text to appear"
            indexPage.waitForTextToAppear("A visible title");
        and: "we should be able to see if a text is not visible"
            indexPage.shouldNotBeVisible(By.xpath("//h2[.='An invisible title']"));
    }

    def openPageWithPhantomJS() {
        openStaticTestSite(driver)
        return new IndexPage(driver)
    }

    def openStaticTestSite(driver) {
		URL siteURL = getClass().getClassLoader().getResource("static-site/index.html")
		driver.get(siteURL.toString())
    }
}