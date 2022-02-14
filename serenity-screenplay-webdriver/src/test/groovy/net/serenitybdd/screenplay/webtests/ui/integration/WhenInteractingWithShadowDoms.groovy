package net.serenitybdd.screenplay.webtests.ui.integration

import io.github.bonigarcia.wdm.WebDriverManager
import net.serenitybdd.core.annotations.findby.By
import net.serenitybdd.screenplay.questions.SamplePage
import net.serenitybdd.screenplay.ui.*
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class WhenInteractingWithShadowDoms extends Specification {

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
        samplePage.openUrl("classpath:/sample-web-site/screenplay/ui-elements/forms/shadow-doms.html")
    }

    def "Find shadow dom elements using a ByShadow locator"() {
        expect:
        samplePage.find(field).isVisible()
        where:
        field                                                                               | _
        InputField.located(By.shadowCSS("#shadow-text").inHost("#shadow_host"))             | _
        Checkbox.located(By.shadowCSS("#shadow-checkbox").inHost("#shadow_host"))           | _
        Link.located(By.shadowLocator(By.linkText("Shadow Link")).inHost("#shadow_host"))   | _
        Image.located(By.shadowLocator(By.id("shadow-img")).inHost("#shadow_host"))         | _
        Dropdown.located(By.shadowLocator(By.name("shadow-select")).inHost("#shadow_host")) | _
    }

    def "Find nested shadow dom elements using a ByShadow locator"() {
        expect:
        samplePage.find(field).isVisible() == isVisible
        where:
        field                                                                                                                                             | isVisible
        PageElement.located(By.shadowCSS("#nested_shadow_content").inHost("#shadow_host").thenInHost("#nested_shadow_host"))                              | true
        InputField.located(By.shadowCSS("#nested_shadow_content").inHost("#shadow_host").thenInHost("#nested_shadow_host")).containingText("nested text") | true
    }

    def "Interact with an input field inside a shadow dom using a ByShadow locator"() {
        expect:
        samplePage.find(field).isVisible()
        samplePage.find(field).type("Some text")
        samplePage.find(field).getValue() == "Some text"
        where:
        field                                                                   | _
        InputField.located(By.shadowCSS("#shadow-text").inHost("#shadow_host")) | _
    }

    def "Interact with a checkbox inside a shadow dom using a ByShadow locator"() {
        expect:
        samplePage.find(field).isVisible()
        samplePage.find(field).click()
        samplePage.find(field).isSelected()
        where:
        field                                                                     | _
        Checkbox.located(By.shadowCSS("#shadow-checkbox").inHost("#shadow_host")) | _
    }


}
