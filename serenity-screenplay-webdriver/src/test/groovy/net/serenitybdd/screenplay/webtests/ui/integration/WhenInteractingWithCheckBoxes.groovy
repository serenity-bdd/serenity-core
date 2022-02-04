package net.serenitybdd.screenplay.webtests.ui.integration

import io.github.bonigarcia.wdm.WebDriverManager
import net.serenitybdd.screenplay.questions.SamplePage
import net.serenitybdd.screenplay.ui.Checkbox
import net.serenitybdd.screenplay.ui.PageElement
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class WhenInteractingWithCheckBoxes extends Specification {

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
        samplePage.openUrl("classpath:/sample-web-site/screenplay/ui-elements/forms/checkboxes.html")
    }

    def "Clicking on a checkbox (#checkbox)"() {
        expect:
        samplePage.find(checkbox).click()
        isSelected(selectedOption)

        where:
        checkbox                                 | selectedOption
        Checkbox.withNameOrId("vehicle1")        | "vehicle1"
        Checkbox.withAriaLabel("A Bike")         | "vehicle1"
        Checkbox.withLabel("I have a bike")      | "vehicle1"
        Checkbox.withLabel("I have a motorbike") | "vehicle4"
        Checkbox.locatedBy(".field-style")       | "vehicle2"
        Checkbox.withValue("Car")                | "vehicle2"
    }


    def "Clicking on a checkbox inside another element (#checkbox)"() {
        expect:
        samplePage.find(checkbox).click()
        isSelected(selectedOption)

        where:
        checkbox                                                                         | selectedOption
        Checkbox.withLabel("I have a scooter").inside(PageElement.locatedBy(".section")) | "vehicle7"
    }

    def isSelected(String checkboxId) {
        return driver.findElement(By.id(checkboxId)).isSelected();
    }

}
