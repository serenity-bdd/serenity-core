package net.serenitybdd.screenplay.webtests.ui.integration

import io.github.bonigarcia.wdm.WebDriverManager
import net.serenitybdd.screenplay.questions.SamplePage
import net.serenitybdd.screenplay.ui.InputField
import net.serenitybdd.screenplay.ui.PageElement
import net.serenitybdd.screenplay.ui.TextArea
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class WhenInteractingWithTextAreas extends Specification {

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

    def "Find a text area by name or id"() {
        expect:
        samplePage.find(field).isVisible()
        where:
        field                                         | _
        TextArea.withNameOrId("textarea-field")       | _
        TextArea.withNameOrId("textarea-fieldname")   | _
        TextArea.withNameOrId("textarea-field-data")  | _
        TextArea.withNameOrId("Text Area Aria Label") | _
    }

    def "Input fields and text areas can be given readable names"() {
        expect:
        field.getName() == name
        where:
        field                                                            | name
        InputField.withNameOrId("fieldname").called("The Text Field")    | "The Text Field"
        TextArea.withNameOrId("textarea-field").called("The Text Field") | "The Text Field"
    }

    def "Find a text area by placeholder value"() {
        expect:
        samplePage.find(field).isVisible()
        where:
        field                                                 | _
        TextArea.withPlaceholder("text area value goes here") | _
    }

    def "Find a text area by class"() {
        expect:
        samplePage.find(field).isVisible()
        where:
        field                                         | _
        TextArea.withCSSClass("textarea-field-style") | _
    }

    def "Find a text area by ARIA label"() {
        expect:
        samplePage.find(field).isVisible()
        where:
        field                                          | _
        TextArea.withAriaLabel("Text Area ARIA Label") | _
    }

    def "Find a text area by locator"() {
        expect:
        samplePage.find(field).isVisible()
        where:
        field                                 | _
        TextArea.locatedBy("#textarea-field") | _
    }

    def "Find a text area by label"() {
        expect:
        samplePage.find(field).isVisible()
        where:
        field                                  | _
        TextArea.withLabel("Customer Address") | _
    }

    def "Find an input field or text area in another element"() {
        expect:
        samplePage.find(field).getAttribute("id") == id
        where:
        field                                                                                                                 | id
        TextArea.withCSSClass("textarea-field-style").inside(PageElement.withCSSClass("section").containingText("Section 2")) | "section2-textarea-field"
    }
}
