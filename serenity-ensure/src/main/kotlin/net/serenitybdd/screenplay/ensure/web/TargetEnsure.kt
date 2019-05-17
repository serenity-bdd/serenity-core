package net.serenitybdd.screenplay.ensure.web

import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.ensure.*
import net.serenitybdd.screenplay.targets.Target
import org.openqa.selenium.By

class TargetEnsure(val value: Target)  {

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
    fun isDisplayed() = PerformablePredicate(value, IS_DISPLAYED, isNegated())

    fun text(): ComparableEnsure<String> = ComparableEnsure(textValueOf(value))

    val IS_DISPLAYED = expectThatActualIs("displayed",
            fun(actor: Actor?, element: Target?): Boolean {
                if ((actor == null) || (element == null)) return false
                return element.resolveFor(actor).isDisplayed
            }
    )

    fun textValueOf(element: Target): KnowableValue<String> =
            fun(actor: Actor?): String {
                if ((actor == null) || (element == null)) return ""
                return element.resolveFor(actor).text
            }

}