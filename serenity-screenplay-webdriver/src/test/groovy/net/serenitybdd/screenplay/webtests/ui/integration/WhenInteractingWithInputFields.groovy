package net.serenitybdd.screenplay.webtests.ui.integration

import io.github.bonigarcia.wdm.WebDriverManager
import net.serenitybdd.screenplay.questions.SamplePage
import net.serenitybdd.screenplay.ui.InputField
import net.serenitybdd.screenplay.ui.PageElement
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class WhenInteractingWithInputFields extends Specification {

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
        samplePage.openUrl("classpath:/sample-web-site/screenplay/ui-elements/forms/input-fields.html")
    }

    def "Find an input field by name or id"() {
        expect:
        samplePage.find(field).isVisible()
        where:
        field                                 | _
        InputField.withNameOrId("field")      | _
        InputField.withNameOrId("fieldname")  | _
        InputField.withNameOrId("field-data") | _
        InputField.withNameOrId("Aria Label") | _
    }

    def "Find a text area by name or id"() {
        expect:
        samplePage.find(field).isVisible()
        where:
        field                                           | _
        InputField.withNameOrId("textarea-field")       | _
        InputField.withNameOrId("textarea-fieldname")   | _
        InputField.withNameOrId("textarea-field-data")  | _
        InputField.withNameOrId("Text Area Aria Label") | _
    }

    def "Find an input field or text area by placeholder value"() {
        expect:
        samplePage.find(field).isVisible()
        where:
        field                                                   | _
        InputField.withPlaceholder("value goes here")           | _
        InputField.withPlaceholder("text area value goes here") | _
    }

    def "Find an input field or text area by class"() {
        expect:
        samplePage.find(field).isVisible()
        where:
        field                                           | _
        InputField.withCSSClass("field-style")          | _
        InputField.withCSSClass("textarea-field-style") | _
    }

    def "Find an input field or text area by ARIA label"() {
        expect:
        samplePage.find(field).isVisible()
        where:
        field                                            | _
        InputField.withAriaLabel("ARIA Label")           | _
        InputField.withAriaLabel("Text Area ARIA Label") | _
    }

    def "Find an input field or text area by locator"() {
        expect:
        samplePage.find(field).isVisible()
        where:
        field                                   | _
        InputField.locatedBy("#field")      | _
        InputField.locatedBy("#textarea-field") | _
    }

    def "Find an input field or text area by label"() {
        expect:
        samplePage.find(field).isVisible()
        where:
        field                                    | _
        InputField.withLabel("Customer Name")    | _
        InputField.withLabel("Customer Address") | _
    }

    def "Find an input field or text area in another element"() {
        expect:
        samplePage.find(field).getAttribute("id") == id
        where:
        field                                                                                                                   | id
        InputField.withCSSClass("input-field").inside(PageElement.withCSSClass("section").containingText("Section 2"))          | "section2-field"
        InputField.withCSSClass("textarea-field-style").inside(PageElement.withCSSClass("section").containingText("Section 2")) | "section2-textarea-field"
    }
}
