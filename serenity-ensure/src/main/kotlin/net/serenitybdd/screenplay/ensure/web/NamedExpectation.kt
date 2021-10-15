package net.serenitybdd.screenplay.ensure.web

import net.serenitybdd.core.pages.WebElementFacade
import org.openqa.selenium.By

/**
 *
 */
class NamedExpectation<A>(val description: String, val predicate: (A) -> Boolean)

/**
 * Basic named expectations for web elements
 */
class AnElementThat {

    companion object {
        @JvmStatic
        fun isDisplayed() = NamedExpectation("is displayed", { it: WebElementFacade -> it.isDisplayed })

        @JvmStatic
        fun isNotDisplayed() = NamedExpectation("is not displayed", { it: WebElementFacade -> !it.isDisplayed })

        @JvmStatic
        fun isDisabled() = NamedExpectation("is disabled", { it: WebElementFacade -> it.isDisabled })

        @JvmStatic
        fun isNotDisabled() = NamedExpectation("is not disabled", { it: WebElementFacade -> !it.isDisabled })

        @JvmStatic
        fun isEnabled() = NamedExpectation("is enabled", { it: WebElementFacade -> it.isEnabled })

        @JvmStatic
        fun isNotEnabled() = NamedExpectation("is not enabled", { it: WebElementFacade -> !it.isEnabled })

        @JvmStatic
        fun hasCssClass(expectedClass: String) =
            NamedExpectation("has CSS class of $expectedClass", { it: WebElementFacade -> it.hasClass(expectedClass) })

        @JvmStatic
        fun doesNotHaveCssClass(expectedClass: String) = NamedExpectation(
            "does not have CSS class of $expectedClass",
            { it: WebElementFacade -> !it.hasClass(expectedClass) })

        @JvmStatic
        fun hasValue(expectedValue: String) =
            NamedExpectation("has value \"$expectedValue\"", { it: WebElementFacade -> it.value == expectedValue })

        @JvmStatic
        fun containsText(expectedText: String) = NamedExpectation(
            "contains text \"$expectedText\"",
            { it: WebElementFacade -> it.containsText(expectedText) })

        @JvmStatic
        fun containsOnlyText(expectedText: String) = NamedExpectation(
            "contains only text \"$expectedText\"",
            { it: WebElementFacade -> it.containsOnlyText(expectedText) })

        @JvmStatic
        fun isEmpty() = NamedExpectation("is empty", { it: WebElementFacade -> it.text.isEmpty() })

        @JvmStatic
        fun isNotEmpty() = NamedExpectation("is not empty", { it: WebElementFacade -> it.text.isNotEmpty() })

        @JvmStatic
        fun textContentIsEmpty() = NamedExpectation("is empty", { it: WebElementFacade -> it.textContent.isEmpty() })

        @JvmStatic
        fun textContentIsNotEmpty() =
            NamedExpectation("is not empty", { it: WebElementFacade -> it.textContent.isNotEmpty() })

        @JvmStatic
        fun containsElementsLocatedBy(byLocator: By) = NamedExpectation(
            "contains elements located by $byLocator",
            { it: WebElementFacade -> it.containsElements(byLocator) })

        @JvmStatic
        fun containsElementsLocatedBy(byLocator: String) = NamedExpectation(
            "contains elements located by \"$byLocator\"",
            { it: WebElementFacade -> it.containsElements(byLocator) })
    }
}

/**
 * Basic named expectations for strings
 */
class AStringThat {
    companion object {
        @JvmStatic
        fun isEmpty() = NamedExpectation("is empty", { it: String -> it.isEmpty() })

        @JvmStatic
        fun isNotEmpty() = NamedExpectation("is not empty", { it: String -> it.isNotEmpty() })

        @JvmStatic
        fun isBlank() = NamedExpectation("is blank", { it: String -> it.isBlank() })

        @JvmStatic
        fun isNotBlank() = NamedExpectation("is not blank", { it: String -> it.isNotBlank() })

        @JvmStatic
        fun contains(value: String) = NamedExpectation("contains $value", { it: String -> it.contains(value) })

        @JvmStatic
        fun startsWith(value: String) = NamedExpectation("starts with $value", { it: String -> it.startsWith(value) })

        @JvmStatic
        fun endsWith(value: String) = NamedExpectation("ends with $value", { it: String -> it.endsWith(value) })

        @JvmStatic
        fun matches(regex: Regex) = NamedExpectation("matches $regex", { it: String -> it.matches(regex) })

        @JvmStatic
        fun equalsIgnoringCase(value: String) = NamedExpectation("contains $value", { it: String -> it.lowercase().equals(value.lowercase()) })
    }
}

