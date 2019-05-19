package net.serenitybdd.screenplay.ensure.web

import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.ensure.*
import net.serenitybdd.screenplay.targets.Target
import org.openqa.selenium.By

class TargetEnsure(val value: Target, val targetDescription: String = value.toString())  {

    constructor(byLocator: By) : this(Target.the(byLocator.toString()).located(byLocator))

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

    companion object {

        private val IS_DISPLAYED = expectThatActualIs("displayed",
                fun(actor: Actor?, element: Target?): Boolean {
                    if ((actor == null) || (element == null)) return false
                    return element.resolveFor(actor).isDisplayed
                }
        )

        private val IS_DISABLED = expectThatActualIs("disabled",
                fun(actor: Actor?, element: Target?): Boolean {
                    if ((actor == null) || (element == null)) return false
                    return element.resolveFor(actor).isDisabled
                }
        )


        private val IS_ENABLED = expectThatActualIs("disabled",
                fun(actor: Actor?, element: Target?): Boolean {
                    if ((actor == null) || (element == null)) return false
                    return !element.resolveFor(actor).isDisabled
                }
        )
    }

}