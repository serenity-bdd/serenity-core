package net.serenitybdd.screenplay.webtests.ui.integration

import io.github.bonigarcia.wdm.WebDriverManager
import net.serenitybdd.screenplay.questions.SamplePage
import net.serenitybdd.screenplay.ui.Link
import net.serenitybdd.screenplay.ui.PageElement
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class WhenInteractingWithLinks extends Specification {

    static WebDriver driver
    static SamplePage samplePage;

    def setupSpec() {
        WebDriverManager.chromedriver().setup()
        ChromeOptions options = new ChromeOptions()
        options.setHeadless(true)
        driver = new ChromeDriver(options)
        samplePage = new SamplePage(driver)
    }

    def cleanupSpec() {
        driver.quit()
    }

    def setup() {
        samplePage.openUrl("classpath:/sample-web-site/screenplay/ui-elements/elements/links.html")
    }

    def "Find a link by name or id (#link)"() {
        expect:
        samplePage.find(link).click()
        this.result == expectedLink
        where:
        link                        | expectedLink
        Link.withNameOrId("Link 7") | "Link 7"
        Link.withNameOrId("link-8") | "Link 8"
        Link.withNameOrId("link 9") | "Link 9"
    }

    def "Find a link with a given title (#link)"() {
        expect:
        samplePage.find(link).click()
        this.result == expectedLink
        where:
        link                               | expectedLink
        Link.withTitle("Link Number 2")    | "Link 2"
        // Lower case
        Link.withTitle("link number 2")    | "Link 2"
        // Partial link
        Link.containing("ink 2")           | "Link 2"
        // A title with an apostrophe
        Link.withTitle("The link's title") | "Link 4"
    }

    def "Find a link starting or ending with a given text (#link)"() {
        expect:
        samplePage.find(link).click()
        this.result == expectedLink
        where:
        link                            | expectedLink
        Link.withText("Link 1")         | "Link 1"
        Link.startingWith("Lin")        | "Link 1"
        Link.withTitle("Link Number 2") | "Link 2"
        // Lower case
        Link.withTitle("link number 2") | "Link 2"
        // Partial link
        Link.containing("ink 2")        | "Link 2"
    }

    def "Find a link with an icon"() {
        expect:
        samplePage.find(link).click()
        this.result == expectedLink
        where:
        link                             | expectedLink
        Link.withIcon("glyphicon-cloud") | "Link 3"
    }

    def "Find a link (#link) by inside another element"() {
        expect:
        samplePage.find(link).click()
        this.result == expectedLink
        where:
        link                                                                                               | expectedLink
        Link.withText("Link 2").inside(PageElement.withCSSClass("section"))                                | "Link 2 bis"
        Link.withText("Link 3").inside(PageElement.withCSSClass("section").containingText("Link Section")) | "Link 3 bis"
        Link.withText("Link 3").inside(PageElement.locatedBy("#section3"))                                 | "Link 3 bis"
    }

    def "Find a link with a locator"() {
        expect:
        samplePage.find(link).click()
        this.result == expectedLink
        where:
        link                      | expectedLink
        Link.locatedBy("#link-8") | "Link 8"
    }

    def getResult() {
        return driver.findElement(By.id("result")).text
    }
}
