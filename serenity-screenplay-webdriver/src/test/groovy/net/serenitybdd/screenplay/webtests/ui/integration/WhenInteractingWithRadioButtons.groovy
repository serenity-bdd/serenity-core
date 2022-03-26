package net.serenitybdd.screenplay.webtests.ui.integration


import io.github.bonigarcia.wdm.WebDriverManager
import net.serenitybdd.screenplay.questions.SamplePage
import net.serenitybdd.screenplay.ui.PageElement
import net.serenitybdd.screenplay.ui.RadioButton
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.locators.RelativeLocator
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class WhenInteractingWithRadioButtons extends Specification {

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
        samplePage.openUrl("classpath:/sample-web-site/screenplay/ui-elements/forms/radio-buttons.html")
    }

    def "Clicking on a radio button with a given id (#button)"() {
        expect:
        samplePage.find(button).click()
        isSelected(selectedValue)
        where:
        button                                | selectedValue
        RadioButton.withId("html")            | "html"
        RadioButton.withId("html-radio-data") | "html"
    }

    def "Clicking on a radio button with a given value (#button)"() {
        expect:
        samplePage.find(button).click()
        isSelected(selectedValue)
        where:
        button                              | selectedValue
        RadioButton.withValue("HTML")       | "html"
        RadioButton.withValue("JavaScript") | "JavaScript"
    }

    def "Clicking on a radio button with a given label (#button)"() {
        expect:
        samplePage.find(button).click()
        isSelected(selectedValue)
        where:
        button                               | selectedValue
        RadioButton.withLabel("Choose HTML") | "html"
        RadioButton.withLabel("Choose CSS")  | "CSS"
    }

    def "Clicking on a radio button inside another section (#button)"() {
        expect:
        samplePage.find(button).click()
        isSelected(selectedValue)
        where:
        button                               | selectedValue
        RadioButton.withName("fav_language").inside(PageElement.withCSSClass("section").containingText("Web")) | "html"
        RadioButton.withName("fav_language").inside(PageElement.withCSSClass("section").containingText("Coding")) | "javascript"
    }

    def isSelected(String radioId) {
        return driver.findElement(By.id(radioId)).isSelected();
    }

}
