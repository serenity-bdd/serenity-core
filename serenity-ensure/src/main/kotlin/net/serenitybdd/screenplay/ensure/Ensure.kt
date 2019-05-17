@file:JvmName("Ensure")

package net.serenitybdd.screenplay.ensure

import net.serenitybdd.core.pages.PageObject
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Performable
import net.serenitybdd.screenplay.ensure.web.PageObjectEnsure
import net.serenitybdd.screenplay.ensure.web.TargetEnsure
import net.serenitybdd.screenplay.targets.Target
import org.openqa.selenium.By
import java.time.LocalDate

fun that(value: String?) = StringEnsure(value)
fun that(value: LocalDate?) = DateEnsure(value)
fun that(value: Boolean?) = BooleanEnsure(value)
fun <A> that(value: Comparable<A>) = ComparableEnsure(value)
fun <A> that(value: Collection<A>?) = CollectionEnsure(value)

fun that(value: PageObject) = PageObjectEnsure(value)
fun that(value: Target) = TargetEnsure(value)
fun that(value: By) = TargetEnsure(value)

private fun isAFailure(result: Boolean, isNegated: Boolean) = (!isNegated && !result || isNegated && result)

class PerformableExpectation<A, E>(val actual: A?,
                                   private val expectation: Expectation<A?, E>,
                                   val expected: E,
                                   private val isNegated: Boolean = false) : Performable {
    override fun <T : Actor?> performAs(actor: T) {
        val result = expectation.apply(actual, expected, actor)

        if (isAFailure(result, isNegated)) {
            throw AssertionError(expectation.describe(actual, expected, isNegated))
        }
    }
}

class BiPerformableExpectation<A, E>(val actual: A?,
                                     val expectation: DoubleValueExpectation<A?, E>,
                                     val startRange: E,
                                     val endRange: E,
                                     val isNegated: Boolean = false) : Performable {
    override fun <T : Actor?> performAs(actor: T) {
        val result = expectation.apply(actual, startRange, endRange, actor)

        if (isAFailure(result, isNegated)) {
            throw AssertionError(expectation.describe(actual, startRange, endRange))
        }
    }
}

class PerformablePredicate<A>(val actual: A?,
                              val expectation: PredicateExpectation<A?>,
                              val isNegated: Boolean = false) : Performable {
    override fun <T : Actor?> performAs(actor: T) {
        val result = expectation.apply(actual, actor)

        if (isAFailure(result, isNegated)) {
            throw AssertionError(expectation.describe(actual, isNegated))
        }
    }
}
