package net.serenitybdd.screenplay.webtests.ui.integration


import io.github.bonigarcia.wdm.WebDriverManager
import net.serenitybdd.screenplay.questions.SamplePage
import net.serenitybdd.screenplay.ui.Button
import net.serenitybdd.screenplay.ui.PageElement
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class WhenInteractingWithButtons extends Specification {

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
        samplePage.openUrl("classpath:/sample-web-site/screenplay/ui-elements/forms/multi-button-form.html")
    }

    def "Clicking on a button with a given text (#button)"() {
        expect:
        samplePage.find(button).click()
        where:
        button                             | _
        Button.withText("A Simple Button") | _ // <Button type="Button">A Simple Button</Button>
        Button.withText("A Submit Input")  | _ // <input type="submit" value="A Submit Input"/>
        Button.withText("A Button Input")  | _ //  <input type="Button" value="A Button Input"/>,
        Button.withText("Material Button") | _
    }

    def "Clicking on a named button with a given text (#button)"() {
        expect:
        samplePage.find(button).click()
        button.getName() == label
        where:
        button                                                 | label
        Button.withText("A Simple Button").called("My Button") | "My Button"
        Button.containingText("Add to favorites")              | "button containing 'Add to favorites'"
    }

    def "Clicking on a named button located by a CSS expression or locator (#button)"() {
        expect:
        samplePage.find(button).click()
        button.getName() == label
        where:
        button                                             | label
        Button.locatedBy("#button-id").called("My Button") | "My Button"
        Button.located(By.id("button-id")).called("My Button") | "My Button"
    }

    def "Finding a button by name, id, test-data, aria-label or CSS class (#button)"() {
        expect:
        samplePage.find(button).click()
        where:
        button                                    | _
        Button.withNameOrId("button-name")        | _
        Button.withNameOrId("button-id")          | _
        Button.withNameOrId("button-data")        | _
        Button.withNameOrId("button-aria")        | _
        Button.withNameOrId("submit-button-name") | _
        Button.withNameOrId("submit-button-id")   | _
        Button.withNameOrId("submit-button-data") | _
        Button.withNameOrId("submit-button-aria") | _
        Button.withNameOrId("input-button-name")  | _
        Button.withNameOrId("input-button-id")    | _
        Button.withNameOrId("input-button-data")  | _
        Button.withNameOrId("input-button-aria")  | _
        Button.withCSSClass("icon-button-class")  | _
    }

    def "Finding a button with an icon (#button)"() {
        expect:
        samplePage.find(button).click()
        where:
        button                       | _
        // Finding Buttons with an icon
        Button.withIcon("glyphicon") | _

    }

    def "Finding a button with an ARIA label (#button)"() {
        expect:
        samplePage.find(button).click()
        where:
        button                                     | _
        Button.withAriaLabel("button-aria")        | _
        Button.withAriaLabel("submit-button-aria") | _
        Button.withAriaLabel("input-button-aria")  | _

        // CSS Class
        Button.withCSSClass("icon-button-class")   | _
    }

    def "Finding a button with a given label (#button)"() {
        expect:
        samplePage.find(button).click()
        where:
        button                             | _
        Button.withLabel("Transaction ID") | _
    }

    def "Finding a button nested in another element (#button)"() {
        expect:
        samplePage.find(button).click()
        where:
        button                                                                                                               | _
        // Finding Buttons inside other elements
        Button.withLabel("Transaction ID").inside(PageElement.withCSSClass("transfer-panel").containingText("Transfers"))    | _
        Button.withLabel("Transaction ID").inside(PageElement.withCSSClass("transfer-panel").containingTextIgnoringCase("transfers"))    | _
        Button.withLabel("Transaction ID").inside(PageElement.locatedBy(".transfer-panel"))                                  | _
        Button.withLabel("Transaction ID").inside(PageElement.locatedBy(".transfer-panel").called("Transfer Panel"))         | _
        Button.withCSSClass("nested-button").inside(PageElement.withCSSClass("transfer-panel").containingText("Transfers"))  | _
        Button.withAriaLabel("Nested Button").inside(PageElement.withCSSClass("transfer-panel").containingText("Transfers")) | _
    }

}
