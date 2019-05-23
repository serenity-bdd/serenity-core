@file:JvmName("Ensure")

package net.serenitybdd.screenplay.ensure

import net.serenitybdd.core.pages.PageObject
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Performable
import net.serenitybdd.screenplay.Question
import net.serenitybdd.screenplay.ensure.web.PageObjectEnsure
import net.serenitybdd.screenplay.ensure.web.TargetEnsure
import net.serenitybdd.screenplay.targets.Target
import org.openqa.selenium.By
import java.time.LocalDate
import java.util.function.Consumer

fun that(value: String?) = StringEnsure(value)
fun that(value: LocalDate?) = DateEnsure(value)
fun that(value: Boolean?) = BooleanEnsure(value)
fun <A> that(value: Comparable<A>) = ComparableEnsure(value)
fun <A> that(value: Collection<A>?) = CollectionEnsure(value)

fun that(value: PageObject) = PageObjectEnsure(value)
fun that(value: Target) = TargetEnsure(value)
fun that(value: By) = TargetEnsure(value)

// Collection matchers
fun thatTheSetOf(value: Target) = CollectionEnsure(KnowableCollectionTarget(value), "a collection of ${KnowableCollectionTarget(value)}")
fun thatTheSetOf(value: By) = CollectionEnsure(KnowableCollectionTarget(value), "a collection of ${KnowableCollectionTarget(value)}")
fun thatAmongst(value: Target) = CollectionEnsure(KnowableCollectionTarget(value), "a collection of ${KnowableCollectionTarget(value)}")
fun thatAmongst(value: By) = CollectionEnsure(KnowableCollectionTarget(value), "a collection of ${KnowableCollectionTarget(value)}")

///**
// * Ensure something
// */
//fun that(description: String, check: Consumer<ValidatableResponse>) = net.serenitybdd.rest.Ensure.that(description, check)

private fun isAFailure(result: Boolean, isNegated: Boolean) = (!isNegated && !result || isNegated && result)

class PerformableExpectation<A, E>(val actual: A?,
                                   private val expectation: Expectation<A?, E>,
                                   val expected: E,
                                   private val isNegated: Boolean = false,
                                   private val expectedDescription: String = "a value") : Performable {
    override fun <T : Actor?> performAs(actor: T) {
        BlackBox.reset()
        val result = expectation.apply(actual, expected, actor)

        if (isAFailure(result, isNegated)) {
            throw AssertionError(expectation.describe(actual, expected, isNegated, expectedDescription))
        }
    }
}

class BiPerformableExpectation<A, E>(val actual: A?,
                                     private val expectation: DoubleValueExpectation<A?, E>,
                                     private val startRange: E,
                                     private val endRange: E,
                                     private val isNegated: Boolean = false,
                                     private val expectedDescription: String) : Performable {
    override fun <T : Actor?> performAs(actor: T) {
        BlackBox.reset()
        val result = expectation.apply(actual, startRange, endRange, actor)

        if (isAFailure(result, isNegated)) {
            throw AssertionError(expectation.describe(actual, startRange, endRange, isNegated, expectedDescription))
        }
    }
}

class PerformablePredicate<A>(val actual: A?,
                              private val expectation: PredicateExpectation<A?>,
                              private val isNegated: Boolean = false,
                              private val expectedDescription: String) : Performable {
    override fun <T : Actor?> performAs(actor: T) {
        BlackBox.reset()
        val result = expectation.apply(actual, actor)

        if (isAFailure(result, isNegated)) {
            throw AssertionError(expectation.describe(actual, isNegated, expectedDescription))
        }
    }
}
