package net.serenitybdd.screenplay.ensure.web

import net.serenitybdd.core.pages.WebElementFacade
import org.openqa.selenium.By

class NamedExpectation<A>(val description: String, val predicate: (A) -> Boolean)

class TheMatchingElement {

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
        fun hasCssClass(expectedClass: String) = NamedExpectation("has CSS class of $expectedClass", { it: WebElementFacade -> it.hasClass(expectedClass) })

        @JvmStatic
        fun doesNotHaveCssClass(expectedClass: String) = NamedExpectation("does not have CSS class of $expectedClass", { it: WebElementFacade -> !it.hasClass(expectedClass) })

        @JvmStatic
        fun hasValue(expectedValue: String) = NamedExpectation("has value \"$expectedValue\"", { it: WebElementFacade -> it.value == expectedValue })

        @JvmStatic
        fun containsText(expectedText: String) = NamedExpectation("contains text \"$expectedText\"", { it: WebElementFacade -> it.containsText(expectedText) })

        @JvmStatic
        fun containsOnlyText(expectedText: String) = NamedExpectation("contains only text \"$expectedText\"", { it: WebElementFacade -> it.containsOnlyText(expectedText) })

        @JvmStatic
        fun containsElementsLocatedBy(byLocator: By) = NamedExpectation("contains elements located by $byLocator", { it: WebElementFacade -> it.containsElements (byLocator) })

        @JvmStatic
        fun containsElementsLocatedBy(byLocator: String) = NamedExpectation("contains elements located by \"$byLocator\"", { it: WebElementFacade -> it.containsElements (byLocator) })
    }
}
