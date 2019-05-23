package net.serenitybdd.screenplay.ensure.web

import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.ensure.*
import net.serenitybdd.screenplay.targets.Target
import org.openqa.selenium.By

class TargetEnsure(val value: Target, val targetDescription: String = value.toString()) {

    constructor(byLocator: By) : this(Target.the(byLocator.toString()).located(byLocator))
    constructor(xPathOrCssExpression: String) : this(Target.the(xPathOrCssExpression).locatedBy(xPathOrCssExpression))

    private var negated = false

    fun not(): TargetEnsure {
        negated = !negated
        return this
    }

    private fun isNegated() = negated

    /**
     * Verifies that the element is currently displayed
     */
    fun isDisplayed() = PerformablePredicate(value, IS_DISPLAYED, isNegated(), targetDescription)

    /**
     * Verifies that the element is currently displayed
     */
    fun isDisabled() = PerformablePredicate(value, IS_DISABLED, isNegated(), targetDescription)

    /**
     * Verifies that the element is not currently displayed
     */
    fun isEnabled() = PerformablePredicate(value, IS_ENABLED, isNegated(), targetDescription)

    /**
     * Verifies the text value of the specified element
     */
    fun text(): StringEnsure = StringEnsure(textValueOf(value), targetDescription + " with text value")

    /**
     * Verifies the text content of the specified element
     */
    fun textContent(): StringEnsure = StringEnsure(textValueOf(value), targetDescription + " with text content")

    /**
     * Verifies the value attribute of an element
     */
    fun value(): StringEnsure = StringEnsure(valueOf(value), targetDescription + " with value")

    /**
     * Verifies the selected value attribute of a dropdown list
     */
    fun selectedValue(): StringEnsure = StringEnsure(selectedValueOf(value), targetDescription + " with selected value")

    /**
     * Verifies the selected value attribute of a dropdown list
     */
    fun selectedVisibleText(): StringEnsure = StringEnsure(selectedVisibleTextOf(value), targetDescription + " with selected visible text")

    /**
     * Verifies that the element has a given CSS class
     */
    fun hasCssClass(cssClass: String) = PerformablePredicate(value, hasCssClassWithName(cssClass), isNegated(),
            targetDescription)

    /**
     * Verifies that the element contains at least one element matching a given locator
     */
    fun containsElements(cssOrXPathExpression: String) = PerformablePredicate(value, containsElementsLocatedBy(cssOrXPathExpression), isNegated(),
            targetDescription)

    /**
     * Verifies that the element contains at least one element matching a given locator
     */
    fun containsElements(byLocator: By) = PerformablePredicate(value, containsElementsLocatedBy(byLocator), isNegated(), targetDescription)

    private fun textValueOf(target: Target): KnowableValue<String> =
            fun(actor: Actor?): String {
                if (actor == null) return ""
                return target.resolveFor(actor).text
            }

    private fun valueOf(target: Target): KnowableValue<String> =
            fun(actor: Actor?): String {
                if (actor == null) return ""
                return target.resolveFor(actor).value
            }

    private fun selectedValueOf(target: Target): KnowableValue<String> =
            fun(actor: Actor?): String {
                if (actor == null) return ""
                return target.resolveFor(actor).selectedValue
            }

    private fun selectedVisibleTextOf(target: Target): KnowableValue<String> =
            fun(actor: Actor?): String {
                if (actor == null) return ""
                return target.resolveFor(actor).selectedVisibleTextValue
            }

    private fun hasCssClassWithName(cssClass: String) = expectThatActualIs("has CSS class: \"$cssClass\"", "does not have CSS class: \"$cssClass\"",
            fun(actor: Actor?, element: Target?): Boolean {
                if ((actor == null) || (element == null)) return false
                val resolvedElement = element.resolveFor(actor)
                val actualCssClasses = resolvedElement.getAttribute("class")
                BlackBox.logAssertionValues(actualCssClasses, cssClass)
                return resolvedElement.hasClass(cssClass)
            }
    )

    private fun containsElementsLocatedBy(xPathOrCssExpression: String) = expectThatActualIs("contains elements located by: $xPathOrCssExpression", "does not contain any elements located by: $xPathOrCssExpression",
            fun(actor: Actor?, element: Target?): Boolean {
                if ((actor == null) || (element == null)) return false
                return element.resolveFor(actor).containsElements(xPathOrCssExpression)
            }
    )

    private fun containsElementsLocatedBy(byLocator: By?) = expectThatActualIs("contains elements located by: $byLocator", "does not contain any elements located by: $byLocator",
            fun(actor: Actor?, element: Target?): Boolean {
                if ((actor == null) || (element == null)) return false
                return element.resolveFor(actor).containsElements(byLocator)
            }
    )

    companion object {

        private val IS_DISPLAYED = expectThatActualIs("displayed",
                fun(actor: Actor?, element: Target?): Boolean {
                    if ((actor == null) || (element == null)) return false
                    val actualValue = element.resolveFor(actor).isDisplayed
                    BlackBox.logAssertionValues(isDisplayedOrNot(actualValue), "an element that is displayed")
                    return actualValue
                }
        )

        private val IS_DISABLED = expectThatActualIs("disabled",
                fun(actor: Actor?, element: Target?): Boolean {
                    if ((actor == null) || (element == null)) return false
                    val actualValue = element.resolveFor(actor).isDisabled
                    BlackBox.logAssertionValues(isDisabledOrNot(actualValue), "an element that is disabled")
                    return actualValue
                }
        )


        private val IS_ENABLED = expectThatActualIs("enabled",
                fun(actor: Actor?, element: Target?): Boolean {
                    if ((actor == null) || (element == null)) return false
                    val actualValue = !element.resolveFor(actor).isDisabled
                    BlackBox.logAssertionValues(isEnabledOrNot(actualValue), "an element that is not disabled")
                    return actualValue
                }
        )

        private fun isDisabledOrNot(actualValue: Boolean) = if (actualValue) "web element is disabled" else "web element is not disabled"
        private fun isEnabledOrNot(actualValue: Boolean) = if (actualValue) "web element is enabled" else "web element is not enabled"
        private fun isDisplayedOrNot(actualValue: Boolean) = if (actualValue) "web element is displayed" else "web element is not displayed"
    }

}