package net.serenitybdd.screenplay.webtests.ui.integration

import io.github.bonigarcia.wdm.WebDriverManager
import net.serenitybdd.screenplay.questions.SamplePage
import net.serenitybdd.screenplay.ui.PageElement
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class WhenInteractingWithPageElements extends Specification {

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
        samplePage.openUrl("classpath:/sample-web-site/screenplay/ui-elements/elements/elements.html")
    }

    def "Find an element by id or name (#element)"() {
        expect:
        samplePage.find(element).shouldBeVisible()
        where:
        element                                          | _
        PageElement.withNameOrId("container-id")         | _
        PageElement.withNameOrId("container-data")       | _
        PageElement.withNameOrId("Container Aria Label") | _
        PageElement.withNameOrId("Container Name")       | _
    }

    def "Find an element by text contents"() {
        expect:
        samplePage.find(element).shouldBeVisible()
        where:
        element                                                               | _
        PageElement.containingText("Item 1 description")                      | _
        PageElement.withCSSClass("item").containingText("Item 2 description") | _
    }

    def "Find an element with a CSS or XPath locator"() {
        expect:
        samplePage.find(element).shouldBeVisible()
        where:
        element                                | _
        PageElement.locatedBy("#container-id") | _
        PageElement.locatedBy(".item")         | _
    }

    def "Find an element inside another element"() {
        expect:
        samplePage.find(element).shouldBeVisible()
        where:
        element                                                 | _
        PageElement.locatedBy(".item").containingText("Item 1") | _
    }

}
