package net.serenitybdd.screenplay.webtests.ui.integration

import io.github.bonigarcia.wdm.WebDriverManager
import net.serenitybdd.screenplay.questions.SamplePage
import net.serenitybdd.screenplay.ui.*
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class WhenInteractingWithNestedElements extends Specification {

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
        samplePage.openUrl("classpath:/sample-web-site/screenplay/ui-elements/forms/nested-elements.html")
    }

    def "Elements nested inside another element located by CSS"() {
        expect:
        samplePage.find(element).click();
        samplePage.find("#result").getText() == expectedText
        where:
        element                                                 | expectedText
        Button.withText("Submit").inside("#section1")           | "Section 1"
        Button.withText("Submit").inside("#section2")           | "Section 2"
        InputField.withLabel("Input Field").inside("#section1") | "Section 1"
        InputField.withLabel("Input Field").inside("#section2") | "Section 2"
        Checkbox.withLabel("Checkbox").inside("#section1")      | "Section 1"
        Checkbox.withLabel("Checkbox").inside("#section2")      | "Section 2"
        Link.withText("Link").inside("#section1")               | "Section 1"
        Link.withText("Link").inside("#section2")               | "Section 2"
        RadioButton.withLabel("Radio").inside("#section1")      | "Section 1"
        RadioButton.withLabel("Radio").inside("#section2")      | "Section 2"
    }

    def "Nested elements from outer elements to inner ones"() {
        expect:
        samplePage.find(element).click();
        samplePage.find("#result").getText() == expectedText
        where:
        element                                                                          | expectedText
        PageElement.locatedBy("#section1").thenFind(Button.withText("Submit"))           | "Section 1"
        PageElement.locatedBy("#section2").thenFind(Button.withText("Submit"))           | "Section 2"
        PageElement.locatedBy("#section1").thenFind(InputField.withLabel("Input Field")) | "Section 1"
        PageElement.locatedBy("#section2").thenFind(InputField.withLabel("Input Field")) | "Section 2"
        PageElement.locatedBy("#section1").thenFind(Checkbox.withLabel("Checkbox"))      | "Section 1"
        PageElement.locatedBy("#section2").thenFind(Checkbox.withLabel("Checkbox"))      | "Section 2"
        PageElement.locatedBy("#section1").thenFind(Link.withText("Link"))               | "Section 1"
        PageElement.locatedBy("#section2").thenFind(Link.withText("Link"))               | "Section 2"
        PageElement.locatedBy("#section1").thenFind(RadioButton.withLabel("Radio"))      | "Section 1"
        PageElement.locatedBy("#section2").thenFind(RadioButton.withLabel("Radio"))      | "Section 2"
    }

    def "Multi-layer nested elements"() {
        when:
        samplePage.find(
                PageElement.locatedBy(".sections")
                        .thenFind(PageElement.locatedBy("#section1")
                                .thenFind(Button.withText("Submit"))))
                .click();
        then:
        samplePage.find("#result").getText() == "Section 1"

    }

    def "Elements nested inside another page element"() {
        expect:
        samplePage.find(element).click();
        samplePage.find("#result").getText() == expectedText
        where:
        element                                                                        | expectedText
        Button.withText("Submit").inside(PageElement.locatedBy("#section1"))           | "Section 1"
        Button.withText("Submit").inside(PageElement.locatedBy("#section2"))           | "Section 2"
        InputField.withLabel("Input Field").inside(PageElement.locatedBy("#section1")) | "Section 1"
        InputField.withLabel("Input Field").inside(PageElement.locatedBy("#section2")) | "Section 2"
        Checkbox.withLabel("Checkbox").inside(PageElement.locatedBy("#section1"))      | "Section 1"
        Checkbox.withLabel("Checkbox").inside(PageElement.locatedBy("#section2"))      | "Section 2"
        Link.withText("Link").inside(PageElement.locatedBy("#section1"))               | "Section 1"
        Link.withText("Link").inside(PageElement.locatedBy("#section2"))               | "Section 2"
        RadioButton.withLabel("Radio").inside(PageElement.locatedBy("#section1"))      | "Section 1"
        RadioButton.withLabel("Radio").inside(PageElement.locatedBy("#section2"))      | "Section 2"
    }

    def "Elements nested inside another page element containing a text"() {
        expect:
        samplePage.find(element).click();
        samplePage.find("#result").getText() == expectedText
        where:
        element                                                                                                     | expectedText
        Button.withText("Submit").inside(PageElement.withCSSClass("section").containingText("Section 1"))           | "Section 1"
        Button.withText("Submit").inside(PageElement.withCSSClass("section").containingText("Section 2"))           | "Section 2"
        InputField.withLabel("Input Field").inside(PageElement.withCSSClass("section").containingText("Section 1")) | "Section 1"
        InputField.withLabel("Input Field").inside(PageElement.withCSSClass("section").containingText("Section 2")) | "Section 2"
        Checkbox.withLabel("Checkbox").inside(PageElement.withCSSClass("section").containingText("Section 1"))      | "Section 1"
        Checkbox.withLabel("Checkbox").inside(PageElement.withCSSClass("section").containingText("Section 2"))      | "Section 2"
        Link.withText("Link").inside(PageElement.withCSSClass("section").containingText("Section 1"))               | "Section 1"
        Link.withText("Link").inside(PageElement.withCSSClass("section").containingText("Section 2"))               | "Section 2"
        RadioButton.withLabel("Radio").inside(PageElement.withCSSClass("section").containingText("Section 1"))      | "Section 1"
        RadioButton.withLabel("Radio").inside(PageElement.withCSSClass("section").containingText("Section 2"))      | "Section 2"
    }

    def "Elements nested inside another page element containing a text and a CSS class"() {
        expect:
        samplePage.find(element).click();
        samplePage.find("#result").getText() == expectedText
        where:
        element                                                                                           | expectedText
        Button.withText("Submit").inside(PageElement.containingText(".section", "Section 1"))             | "Section 1"
        Button.withText("Submit").inside(PageElement.containingTextIgnoringCase(".section", "section 1")) | "Section 1"
        Button.withText("Submit").inside(PageElement.containingText(".section", "Section 2"))             | "Section 2"
        InputField.withLabel("Input Field").inside(PageElement.containingText(".section", "Section 1"))   | "Section 1"
        InputField.withLabel("Input Field").inside(PageElement.containingText(".section", "Section 2"))   | "Section 2"
        Checkbox.withLabel("Checkbox").inside(PageElement.containingText(".section", "Section 1"))        | "Section 1"
        Checkbox.withLabel("Checkbox").inside(PageElement.containingText(".section", "Section 2"))        | "Section 2"
        Link.withText("Link").inside(PageElement.containingText(".section", "Section 1"))                 | "Section 1"
        Link.withText("Link").inside(PageElement.containingText(".section", "Section 2"))                 | "Section 2"
        RadioButton.withLabel("Radio").inside(PageElement.containingText(".section", "Section 1"))        | "Section 1"
        RadioButton.withLabel("Radio").inside(PageElement.containingText(".section", "Section 2"))        | "Section 2"
    }
}
