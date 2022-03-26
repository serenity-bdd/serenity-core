package net.serenitybdd.screenplay.webtests.ui.integration

import io.github.bonigarcia.wdm.WebDriverManager
import net.serenitybdd.screenplay.questions.SamplePage
import net.serenitybdd.screenplay.ui.Dropdown
import net.serenitybdd.screenplay.ui.PageElement
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class WhenInteractingWithDropdownLists extends Specification {

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
        samplePage.openUrl("classpath:/sample-web-site/screenplay/ui-elements/forms/selects.html")
    }

    def "Find a dropdown by name or id"() {
        expect:
        samplePage.find(dropdown).selectByIndex(1)
        samplePage.find(dropdown).selectedValue == selectedValue
        where:
        dropdown                                         | selectedValue
        Dropdown.withNameOrId("cars")                    | "volvo"
        Dropdown.withNameOrId("car-dropdown-id")         | "volvo"
        Dropdown.withNameOrId("Car Dropdown Aria Label") | "volvo"
        Dropdown.withNameOrId("car-dropdown-data")       | "volvo"
    }

    def "Find a dropdown by label"() {
        expect:
        samplePage.find(dropdown).selectByIndex(1)
        samplePage.find(dropdown).selectedValue == selectedValue
        where:
        dropdown                            | selectedValue
        Dropdown.withLabel("Choose a car:") | "volvo"
    }

    def "Find a dropdown by default option"() {
        expect:
        samplePage.find(dropdown).selectByIndex(1)
        samplePage.find(dropdown).selectedValue == selectedValue
        where:
        dropdown                                                                                                                           | selectedValue
        Dropdown.withDefaultOption("---Pick Your Car---")                                                                                  | "volvo"
        Dropdown.withDefaultOption("---Pick Your Plane---").inside(PageElement.locatedBy("#trainers"))                                     | "cessna172"
        Dropdown.withDefaultOption("---Pick Your Plane---").inside(PageElement.withCSSClass("planeset").containingText("Training Planes")) | "cessna172"
    }

    def "Find a dropdown by locator"() {
        expect:
        samplePage.find(dropdown).selectByIndex(1)
        samplePage.find(dropdown).selectedValue == selectedValue
        where:
        dropdown                               | selectedValue
        Dropdown.locatedBy("#car-dropdown-id") | "volvo"
    }

    def "find dropdown values in a list with grouped entries"() {
        expect:
        samplePage.find(dropdown).selectByVisibleText(selectedText)
        samplePage.find(dropdown).selectedValue == selectedValue
        where:
        dropdown                                  | selectedText | selectedValue
        Dropdown.withNameOrId("new_user_country") | "Sweden"     | "3"
        Dropdown.withNameOrId("new_user_country") | "USA"        | "5"

    }


    def "find dropdown values in a list with multiple selects"() {
        expect:
        selectedTexts.forEach(text -> samplePage.find(dropdown).selectByVisibleText(text))
        samplePage.find(dropdown).selectedValues == selectedValues
        where:
        dropdown                        | selectedTexts | selectedValues
        Dropdown.withLabel("Languages") | ["English"]        | ["2"]
        Dropdown.withLabel("Languages") | ["English", "Norwegian"]  | ["2", "3"]

    }


    def isSelected(String checkboxId) {
        return driver.findElement(By.id(checkboxId)).isSelected();
    }

}
