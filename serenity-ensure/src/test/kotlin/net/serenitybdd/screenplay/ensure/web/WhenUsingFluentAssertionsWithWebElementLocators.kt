package net.serenitybdd.screenplay.ensure.web

import net.serenitybdd.core.pages.PageObject
import net.serenitybdd.core.pages.WebElementFacade
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.abilities.BrowseTheWeb
import net.serenitybdd.screenplay.actions.Open
import net.serenitybdd.screenplay.ensure.*
import net.serenitybdd.screenplay.targets.Target
import net.thucydides.core.annotations.DefaultUrl
import net.thucydides.core.steps.BaseStepListener
import net.thucydides.core.steps.StepEventBus
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertTimeout
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.phantomjs.PhantomJSDriver
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import java.time.Duration.ofSeconds


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WhenUsingFluentAssertionsWithWebElementLocators {

    val wendy: Actor

    @DefaultUrl("classpath:static-site/index.html")
    class DemoPage(driver: WebDriver) : PageObject(driver)

    val demoPage: DemoPage
    val driver: WebDriver
    val outputDirectory: Path

    init {
        driver = PhantomJSDriver() // ChromeDriver(options)
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
        val firstNameField = Target.the("First name field").locatedBy("#firstName")

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
            shouldPassWhenChecking(that(firstNameField).isDisplayed(), wendy)
        }
        @Test
        fun `using a Target with ElementLocated by`() {
            shouldPassWhenChecking(that(ElementLocated.by(firstNameField)).isDisplayed(), wendy)
        }
    }

    @Nested
    inner class WeCanCheckThat {

        @Nested
        inner class ThePage {
            @Test
            fun `has a given title`() {
                shouldPassWhenChecking(thatTheCurrentPage().title().isEqualTo("Test Page"), wendy)
                shouldFailWhenChecking(thatTheCurrentPage().title().isEqualTo("Wrong Page"), wendy)
            }

            @Test
            fun `has a given url`() {
                shouldPassWhenChecking(thatTheCurrentPage().currentUrl().endsWith("static-site/index.html"), wendy)
            }

            @Test
            fun `has a given page HTML content`() {
                shouldPassWhenChecking(thatTheCurrentPage().pageSource().contains("Test Page"), wendy)
            }

            @Test
            fun `has a given window handle`() {
                shouldPassWhenChecking(thatTheCurrentPage().windowHandle().not().isEmpty(), wendy)
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

                @Test
                fun `when the element is not displayed`() {
                    shouldFailWithMessage("""|Expecting web element located by #hidden-field that is displayed
                                             |But got: web element is not displayed"""
                            .trimMargin())
                            .whenChecking(that(ElementLocated.by("#hidden-field")).isDisplayed(), wendy)
                }

                @Test
                fun `when the element is not expected to be displayed`() {
                    shouldFailWithMessage("""|Expecting web element located by #firstName that is not displayed
                                             |But got: web element is displayed"""
                            .trimMargin())
                            .whenChecking(that(ElementLocated.by("#firstName")).not().isDisplayed(), wendy)
                }

            }

            @Nested
            inner class IsNotDisplayed {

                @Test
                fun `when the element is on the page`() {
                    shouldFailWhenChecking(that(ElementLocated.by(By.id("firstName"))).isNotDisplayed(), wendy)
                }

                @Test
                fun `when the element is not on the page`() {
                    shouldPassWhenChecking(that(ElementLocated.by(By.id("#unknown"))).isNotDisplayed(), wendy)
                }

                @Test
                fun `when the element is not displayed on the page`() {
                    shouldPassWhenChecking(that(ElementLocated.by(By.id("#hidden-field"))).isNotDisplayed(), wendy)
                }

                @Test
                fun `when the element is displayed but should not be`() {
                    shouldFailWithMessage("""|Expecting web element located by #firstName that is not displayed
                                             |But got: web element is displayed"""
                            .trimMargin())
                            .whenChecking(that(ElementLocated.by("#firstName")).isNotDisplayed(), wendy)
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
                    shouldFailWithMessage("""|Expecting web element located by #firstName that is disabled
                                             |But got: web element is not disabled"""
                            .trimMargin())
                            .whenChecking(that(ElementLocated.by("#firstName")).isDisabled(), wendy)
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
                    shouldFailWithMessage("""|Expecting web element located by #disabled-field that is enabled
                                             |But got: web element is not enabled"""
                            .trimMargin())
                            .whenChecking(that(ElementLocated.by("#disabled-field")).isEnabled(), wendy)
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
                    shouldPassWhenChecking(that(ElementLocated.by("#heading")).hasText("Heading"), wendy)
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
                            .whenChecking(that(ElementLocated.by("#heading")).text().isIn("Wrong Heading", "Another Wrong One"), wendy)
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
                    shouldPassWhenChecking(that(ElementLocated.by("#firstName")).hasValue("Joe"), wendy)
                }

                @Test
                fun `when the field is not present`() {
                    shouldFailWhenChecking(that(ElementLocated.by("#does-not-exist")).value().isEqualTo("Some value"), wendy)
                }


                @Test
                fun `when the value does not match`() {
                    shouldFailWithMessage("""|Expecting web element located by #firstName with value that is equal to: <"Jill">
                                             |But got................................................................: <"Joe">"""
                            .trimMargin())
                            .whenChecking(that(ElementLocated.by("#firstName")).value().isEqualTo("Jill"), wendy)
                }
            }

            @Nested
            inner class HasAnAttributeValue {

                @Test
                fun `for an input field`() {
                    shouldPassWhenChecking(that(ElementLocated.by("#firstName")).attribute("title").isEqualTo("First Name"), wendy)
                }

                @Test
                fun `when the field is not present`() {
                    shouldFailWhenChecking(that(ElementLocated.by("#does-not-exist")).attribute("title").isEqualTo("Some value"), wendy)
                }


                @Test
                fun `when the value does not match`() {
                    shouldFailWithMessage("""|Expecting web element located by #firstName with Title attribute that is equal to: <"Wrong Value">
                                             |But got..........................................................................: <"First Name">"""
                            .trimMargin())
                            .whenChecking(that(ElementLocated.by("#firstName")).attribute("Title").isEqualTo("Wrong Value"), wendy)
                }
            }
            @Nested
            inner class HasASelectedValue {

                @Test
                fun `for a dropdown list field`() {
                    shouldPassWhenChecking(that(ElementLocated.by("#colors")).selectedValue().isEqualTo("green"), wendy)
                    shouldPassWhenChecking(that(ElementLocated.by("#colors")).hasSelectedValue("green"), wendy)
                }

                @Test
                fun `for a dropdown list field with the wrong value`() {
                    shouldFailWithMessage("""|Expecting web element located by #colors with selected value that is equal to: <"blue">
                                             |But got......................................................................: <"green">"""
                            .trimMargin())
                            .whenChecking(that(ElementLocated.by("#colors")).selectedValue().isEqualTo("blue"), wendy)
                }
            }

            @Nested
            inner class HasASelectedVisibleText {

                @Test
                fun `for a dropdown list field`() {
                    shouldPassWhenChecking(that(ElementLocated.by("#colors")).selectedVisibleText().isEqualTo("Green"), wendy)
                }

                @Test
                fun `for a dropdown list field with the wrong value`() {
                    shouldFailWithMessage("""|Expecting web element located by #colors with selected visible text that is equal to: <"Blue">
                                             |But got.............................................................................: <"Green">"""
                            .trimMargin())
                            .whenChecking(that(ElementLocated.by("#colors")).selectedVisibleText().isEqualTo("Blue"), wendy)
                }
            }

            @Nested
            inner class HasCSSClass {

                @Test
                fun `for an element`() {
                    shouldPassWhenChecking(that(ElementLocated.by("#colors")).hasCssClass("color-list"), wendy)
                }

                @Test
                fun `for an element that does not have the class`() {
                    shouldFailWithMessage("""|Expecting web element located by #colors that has CSS class: "flavor-list"
                                             |But got: color-list dropdown"""
                            .trimMargin())
                            .whenChecking(that(ElementLocated.by("#colors")).hasCssClass("flavor-list"), wendy)
                }

                @Test
                fun `for an element that does not have the class but should`() {
                    shouldFailWithMessage("""|Expecting web element located by #colors that does not have CSS class: "color-list"
                                             |But got: color-list dropdown"""
                            .trimMargin())
                            .whenChecking(that(ElementLocated.by("#colors")).not().hasCssClass("color-list"), wendy)
                }
            }

            @Nested
            inner class ContainsElements {

                @Test
                fun `matching an XPath or CSS locator`() {
                    shouldPassWhenChecking(that(ElementLocated.by("#stations")).containsElements(".station"), wendy)
                }

                @Test
                fun `not matching an XPath or CSS locator`() {
                    shouldPassWhenChecking(that(ElementLocated.by("#stations")).not().containsElements(".busstop"), wendy)
                }


                @Test
                fun `unexpectedly not matching an XPath or CSS locator`() {
                    shouldFailWhenChecking(that(ElementLocated.by("#stations")).containsElements(".busstop"), wendy)
                }

                @Test
                fun `matching a By locator`() {
                    shouldPassWhenChecking(that(ElementLocated.by("#stations")).containsElements(By.cssSelector(".station")), wendy)
                }
            }

            @Nested
            inner class IsDisplayedAfterADelay {

                @Test
                fun `when the element is on the page`() {
                    val aSlowDisplayingField: Target = ElementLocated.by(By.id("city"))
                            .waitingForNoMoreThan(Duration.ofSeconds(0))

                    shouldPassWhenChecking(that(aSlowDisplayingField).isDisplayed(), wendy)
                }

                @Test()
                fun `when the element is not on the page`() {
                    val aMissingField: Target = ElementLocated.by(By.id("missing-field"))
                            .waitingForNoMoreThan(Duration.ofSeconds(1))

                    assertTimeout(ofSeconds(3)) {
                        shouldFailWhenChecking(that(aMissingField).silently().isDisplayed(), wendy)
                    }
                }

            }
        }

        @Nested
        inner class ACollectionOfMatchingElements() {

            @Test
            fun `that is non-empty`() {
                shouldPassWhenChecking(thatTheSetOf(ElementsLocated.by(".station")).isNotEmpty(), wendy)
            }

            @Test
            fun `that should be non-empty`() {
                shouldFailWithMessage("""|Expecting a collection of web elements located by .no-station that is not empty
                                         |But got: []"""
                        .trimMargin())
                        .whenChecking(thatTheSetOf(ElementsLocated.by(".no-station")).isNotEmpty(), wendy)
            }

            @Test
            fun `that have a certain number of elements`() {
                shouldPassWhenChecking(thatTheSetOf(ElementsLocated.by(By.cssSelector(".station"))).hasSize(3), wendy)
            }

            @Test
            fun `that all match a predicate`() {
                val isDisplayed = { it: WebElementFacade -> it.isDisplayed }

                shouldPassWhenChecking(thatAmongst(ElementsLocated.by(Target.the("list of stations").locatedBy(".station"))).allMatch("is displayed", isDisplayed), wendy)
            }

            @Test
            fun `where some match a predicate`() {
                val isDisplayed = { it: WebElementFacade -> it.isDisplayed }

                shouldPassWhenChecking(thatAmongst(ElementsLocated.by(".station")).atLeast(2, "is displayed", isDisplayed), wendy)
            }

            @Test
            fun `that fails to match a predicate`() {
                val isDisplayed = { it: WebElementFacade -> it.isDisplayed }

                shouldFailWhenChecking(thatAmongst(ElementsLocated.by(".station")).noMoreThan(1, "is displayed", isDisplayed), wendy)
            }

            @Test
            fun `that all elements are displayed`() {
                shouldPassWhenChecking(thatTheSetOf(ElementsLocated.by(".station")).allMatch(TheMatchingElement.isDisplayed()), wendy)
            }

            @Test
            fun `that no elements are displayed`() {
                shouldPassWhenChecking(thatAmongst(ElementsLocated.by(".hidden-station")).noneMatch(TheMatchingElement.isDisplayed()), wendy)
            }

            @Test
            fun `that no elements should be displayed`() {
                shouldFailWhenChecking(thatTheSetOf(ElementsLocated.by(".hidden-station")).noneMatch(TheMatchingElement.isNotDisplayed()), wendy)
            }

            @Test
            fun `that elements should be disabled`() {
                shouldPassWhenChecking(thatTheSetOf(ElementsLocated.by("#disabled-field")).allMatch(TheMatchingElement.isDisabled()), wendy)
            }

            @Test
            fun `that elements should not be disabled`() {
                shouldPassWhenChecking(thatTheSetOf(ElementsLocated.by("#disabled-field")).noneMatch(TheMatchingElement.isNotDisabled()), wendy)
            }

            @Test
            fun `that elements should be enabled`() {
                shouldPassWhenChecking(thatTheSetOf(ElementsLocated.by("#firstName")).allMatch(TheMatchingElement.isEnabled()), wendy)
            }

            @Test
            fun `that elements should not be enabled`() {
                shouldPassWhenChecking(thatTheSetOf(ElementsLocated.by("#disabled-field")).allMatch(TheMatchingElement.isNotEnabled()), wendy)
            }

            @Test
            fun `that elements have css classes`() {
                shouldPassWhenChecking(thatTheSetOf(ElementsLocated.by(".station")).allMatch(TheMatchingElement.hasCssClass("station")), wendy)
            }

            @Test
            fun `that elements do not have css classes`() {
                shouldPassWhenChecking(thatTheSetOf(ElementsLocated.by(".station")).allMatch(TheMatchingElement.doesNotHaveCssClass("bus-stop")), wendy)
            }

            @Test
            fun `that elements contain text`() {
                shouldPassWhenChecking(thatTheSetOf(ElementsLocated.by(".train-line")).allMatch(TheMatchingElement.containsText("Line")), wendy)
            }

            @Test
            fun `that elements contain exact text`() {
                shouldPassWhenChecking(thatTheSetOf(ElementsLocated.by(".train-line")).anyMatch(TheMatchingElement.containsOnlyText("Jubilee Line")), wendy)
            }

            @Test
            fun `that elements have values`() {
                shouldPassWhenChecking(thatTheSetOf(ElementsLocated.by(".form-field")).anyMatch(TheMatchingElement.hasValue("Joe")), wendy)
            }

            @Test
            fun `that elements contain other elements`() {
                shouldPassWhenChecking(thatTheSetOf(ElementsLocated.by(".train-line")).anyMatch(TheMatchingElement.containsElementsLocatedBy(By.cssSelector(".train-line-name"))), wendy)
            }


            @Test
            fun `that elements contain other elements using XPath or CSS`() {
                shouldPassWhenChecking(thatTheSetOf(ElementsLocated.by(".train-line")).anyMatch(TheMatchingElement.containsElementsLocatedBy(".train-line-name")), wendy)
            }
        }
    }
}