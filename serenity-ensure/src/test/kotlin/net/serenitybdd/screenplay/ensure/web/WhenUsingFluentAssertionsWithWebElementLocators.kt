package net.serenitybdd.screenplay.ensure.web

import net.serenitybdd.core.pages.PageObject
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.abilities.BrowseTheWeb
import net.serenitybdd.screenplay.actions.Open
import net.serenitybdd.screenplay.ensure.shouldFailWhenChecking
import net.serenitybdd.screenplay.ensure.shouldFailWithMessage
import net.serenitybdd.screenplay.ensure.shouldPassWhenChecking
import net.serenitybdd.screenplay.ensure.that
import net.serenitybdd.screenplay.targets.Target
import net.thucydides.core.annotations.DefaultUrl
import net.thucydides.core.steps.BaseStepListener
import net.thucydides.core.steps.StepEventBus
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxDriver
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WhenUsingFluentAssertionsWithWebElementLocators {

    val wendy: Actor

    @DefaultUrl("classpath:static-site/index.html")
    class DemoPage(driver: WebDriver) : PageObject(driver)

    val demoPage: DemoPage
    val driver: WebDriver
    val outputDirectory: Path

    init {
        val browserOptions = ChromeOptions()
        browserOptions.setHeadless(true)
        driver = ChromeDriver(browserOptions)
        outputDirectory = Files.createTempDirectory("output")
        val stepListener = BaseStepListener(outputDirectory.toFile())
        StepEventBus.getEventBus().registerListener(stepListener)

        demoPage = DemoPage(driver)

        wendy = Actor.named("Wendy").whoCan(BrowseTheWeb.with(driver))

        wendy.attemptsTo(Open.browserOn().the(demoPage))
    }

    @AfterAll
    fun closeDriver() {
        driver.quit()
        Files.delete(outputDirectory)
    }

    @Nested
    inner class WeCanLocateElements {
        @Test
        fun `using a By locator`() {
            shouldPassWhenChecking(that(ElementLocated.by(By.id("firstName"))).isDisplayed(), wendy)
        }

        @Test
        fun `using a String locator`() {
            shouldPassWhenChecking(that(ElementLocated.by("#firstName")).isDisplayed(), wendy)
        }

        @Test
        fun `using a Target`() {
            val firstNameField = Target.the("First name field").locatedBy("#firstName")
            shouldPassWhenChecking(that(ElementLocated.by(firstNameField)).isDisplayed(), wendy)
        }
    }

    @Nested
    inner class WeCanCheckThat {

        @Nested
        inner class ThePage {
            @Test
            fun `has a given title`() {
                shouldPassWhenChecking(that(demoPage).title().isEqualTo("Test Page"))
                shouldFailWhenChecking(that(demoPage).title().isEqualTo("Wrong Page"))
            }

            @Test
            fun `has a given url`() {
                shouldPassWhenChecking(that(demoPage).currentUrl().endsWith("static-site/index.html"))
            }

            @Test
            fun `has a given page HTML content`() {
                shouldPassWhenChecking(that(demoPage).pageSource().contains("<title>Test Page</title>"))
            }

            @Test
            fun `has a given window handle`() {
                shouldPassWhenChecking(that(demoPage).windowHandle().not().isEmpty())
            }
        }

        @Nested
        inner class AGivenElement {
            @Nested
            inner class IsDisplayed {

                @Test
                fun `when the element is on the page`() {
                    shouldPassWhenChecking(that(ElementLocated.by(By.id("firstName"))).isDisplayed(), wendy)
                }

                @Test
                fun `when the element is not on the page`() {
                    shouldFailWhenChecking(that(ElementLocated.by(By.id("#unknown"))).isDisplayed(), wendy)
                }

            }

            @Nested
            inner class IsDisabled {

                @Test
                fun `when the element is disabled`() {
                    shouldPassWhenChecking(that(ElementLocated.by("#disabled-field")).isDisabled(), wendy)
                }

                @Test
                fun `when the element is enabled`() {
                    shouldFailWhenChecking(that(ElementLocated.by("#firstName")).isDisabled(), wendy)
                }

                @Test
                fun `when the element is not on the page`() {
                    shouldFailWhenChecking(that(ElementLocated.by(By.id("#unknown"))).isDisabled(), wendy)
                }
            }

            @Nested
            inner class IsEnabled {

                @Test
                fun `when the element is not disabled`() {
                    shouldPassWhenChecking(that(ElementLocated.by("#firstName")).isEnabled(), wendy)
                }

                @Test
                fun `when the element is disabled`() {
                    shouldFailWhenChecking(that(ElementLocated.by("#disabled-field")).isEnabled(), wendy)
                }

                @Test
                fun `when the element is not on the page`() {
                    shouldFailWhenChecking(that(ElementLocated.by(By.id("#unknown"))).isEnabled(), wendy)
                }
            }

            @Nested
            inner class HasATextValue {

                @Test
                fun `when the element is on the page`() {
                    shouldPassWhenChecking(that(ElementLocated.by("#heading")).text().isEqualTo("Heading"), wendy)
                    shouldPassWhenChecking(that(ElementLocated.by("#heading")).text().isGreaterThan("Aardvark"), wendy)
                }

                @Test
                fun `when the element is on the page with the wrong value (using a string comparator)`() {
                    shouldFailWithMessage("""|Expecting web element located by #heading with text value that contains: <[Wrong]>
                                             |But got................................................................: <"Heading">"""
                            .trimMargin())
                            .whenChecking(that(ElementLocated.by("#heading")).text().contains("Wrong"), wendy)
                }


                @Test
                fun `when the element is on the page with the wrong value (using a comparable)`() {
                    shouldFailWithMessage("""|Expecting web element located by #heading with text value that is equal to: <"Wrong Heading">
                                             |But got...................................................................: <"Heading">"""
                            .trimMargin())
                            .whenChecking(that(ElementLocated.by("#heading")).text().isEqualTo("Wrong Heading"), wendy)
                }

                @Test
                fun `when the element is on the page with the wrong value (using a basic comparison)`() {
                    shouldFailWithMessage("""|Expecting web element located by #heading with text value that is in: <[Wrong Heading, Another Wrong One]>
                                             |But got.............................................................: <"Heading">"""
                            .trimMargin())
                            .whenChecking(that(ElementLocated.by("#heading")).text().isIn("Wrong Heading","Another Wrong One"), wendy)
                }

                @Test
                fun `when the element is not on the page`() {
                    shouldFailWhenChecking(that(ElementLocated.by(By.id("#unknown"))).isDisplayed(), wendy)
                }
            }

            @Nested
            inner class HasATextContent {

                @Test
                fun `when the text is present`() {
                    shouldPassWhenChecking(that(ElementLocated.by("#block-of-text")).textContent().contains("Some text content"), wendy)
                }

                @Test
                fun `when the text is not present`() {
                    shouldFailWhenChecking(that(ElementLocated.by("#block-of-text")).textContent().doesNotContain("Some text content"), wendy)
                }

                @Test
                fun `when the text content does not match`() {
                    shouldFailWithMessage("""|Expecting web element located by #block-of-text with text value that contains: <[Some other text content]>
                                             |But got......................................................................: <"Some text content">"""
                            .trimMargin())
                            .whenChecking(that(ElementLocated.by("#block-of-text")).text().contains("Some other text content"), wendy)
                }

            }

            @Nested
            inner class HasAValue {

                @Test
                fun `for an input field`() {
                    shouldPassWhenChecking(that(ElementLocated.by("#firstName")).value().isEqualTo("Joe"), wendy)
                }

                @Test
                fun `when the field is not present`() {
                    shouldFailWhenChecking(that(ElementLocated.by("#does-not-exist")).value().isEqualTo("Some value"), wendy)
                }
            }

        }
    }
}