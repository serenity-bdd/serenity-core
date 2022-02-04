package net.serenitybdd.screenplay.webtests.ui.integration

import io.github.bonigarcia.wdm.WebDriverManager
import net.serenitybdd.screenplay.questions.SamplePage
import net.serenitybdd.screenplay.ui.Image
import net.serenitybdd.screenplay.ui.PageElement
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class WhenInteractingWithImages extends Specification {

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
        samplePage.openUrl("classpath:/sample-web-site/screenplay/ui-elements/elements/images.html")
    }

    def "Find an image (#image) by alt text"() {
        expect:
        samplePage.find(image).shouldBeVisible()
        where:
        image                                                         | _
        Image.withAltText("Girl in a jacket")                         | _
        Image.withAltText("Girl in a jacket").called("The first pic") | _
    }

    def "Find an image (#image) by src"() {
        expect:
        samplePage.find(image).shouldBeVisible()
        where:
        image                                                                                                                                    | _
        Image.withSrc("img_girl.jpg")                                                                                                            | _
        Image.withSrcEndingWith("girl.jpg")                                                                                                      | _
        Image.withSrcStartingWith("img_")                                                                                                        | _
        Image.withSrc("img_girl.jpg").called("The second pic").inside(PageElement.withCSSClass("image-container").containingText("Image Set 2")) | _
        Image.locatedBy("#img2")                                                                                                                 | _
    }

    def "Find an image (#image) by inside another element"() {
        expect:
        samplePage.find(image).shouldBeVisible()
        where:
        image                                                                                                                                    | _
        Image.withSrc("img_girl.jpg").called("The second pic").inside(PageElement.withCSSClass("image-container").containingText("Image Set 2")) | _
    }

    def "Find an image (#image) located by locator"() {
        expect:
        samplePage.find(image).shouldBeVisible()
        where:
        image                    | _
        Image.locatedBy("#img2") | _
    }
}
