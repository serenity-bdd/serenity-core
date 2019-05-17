package net.serenitybdd.screenplay.ensure

import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.ensure.CommonPreconditions.ensureActualAndExpectedNotNull
import net.serenitybdd.screenplay.ensure.CommonPreconditions.ensureActualAndRangeValues
import net.serenitybdd.screenplay.ensure.CommonPreconditions.ensureActualNotNull

/**
 * Predicates about comparable types
 */
open class ComparableEnsure<A>(override val value: KnowableValue<Comparable<A>>,
                               val comparator: Comparator<A>? = null) : UICommonEnsure<Comparable<A>, A>(value) {


    constructor(value: Comparable<A>, comparator: Comparator<A>? = null):this(KnownValue(value, value.toString()), comparator)

    /**
     * Verifies that the actual value is greater than the given one.
     * <p>
     * Example:
     * <pre><code class='java'> // assertions will pass
     * actor.attemptsTo(Ensure.that(2).isGreaterThan(1));
     * </code></pre>
     */
    fun isGreaterThan(expected: Comparable<A>) = PerformableExpectation(value, IS_GREATER_THAN, expected, isNegated())

    /**
     * Verifies that the actual value is greater than or equal to the given one.
     * <p>
     * Example:
     * <pre><code class='java'> // assertions will pass
     * actor.attemptsTo(Ensure.that(2).isGreaterThanOrEqualTo(1));
     * actor.attemptsTo(Ensure.that(1).isGreaterThanOrEqualTo(1));
     * </code></pre>
     */
    fun isGreaterThanOrEqualTo(expected: Comparable<in A>) = PerformableExpectation(value, IS_GREATER_THAN_OR_EQUAL_TO, expected, isNegated())

    /**
     * Verifies that the actual value is less than the given one.
     * <p>
     * Example:
     * <pre><code class='java'> // assertions will pass
     * actor.attemptsTo(Ensure.that(2).isLessThan(3));
     * </code></pre>
     */
    fun isLessThan(expected: Comparable<in A>) = PerformableExpectation(value, IS_LESS_THAN, expected, isNegated())

    /**
     * Verifies that the actual value is less than or equal to the given one.
     * <p>
     * Example:
     * <pre><code class='java'> // assertions will pass
     * actor.attemptsTo(Ensure.that(2).isLessThanOrEqualTo(2));
     * actor.attemptsTo(Ensure.that(1).isLessThanOrEqualTo(2));
     * </code></pre>
     */
    fun isLessThanOrEqualTo(expected: Comparable<in A>) = PerformableExpectation(value, IS_LESS_THAN_OR_EQUAL_TO, expected, isNegated())

    /**
     * Verifies that the actual value is between two values
     * <p>
     * Example:
     * <pre><code class='java'> // assertions will pass
     * actor.attemptsTo(Ensure.that(2).isBetween(1,3));
     * </code></pre>
     */

    fun isBetween(lowerBound: Comparable<A>, upperBound: Comparable<A>) = BiPerformableExpectation(value, IS_BETWEEN, lowerBound, upperBound, isNegated())


    /**
     * Verifies that the actual value is between two values, excluding the boundaries
     * <p>
     * Example:
     * <pre><code class='java'> // assertions will pass
     * actor.attemptsTo(Ensure.that(2).isStrictlyBetween(1,3));
     *  // assertions will fail
     * actor.attemptsTo(Ensure.that(1).isStrictlyBetween(1,3));
     * </code></pre>
     */
    fun isStrictlyBetween(lowerBound: Comparable<A>, upperBound: Comparable<A>) = BiPerformableExpectation(value, IS_STRICTLY_BETWEEN, lowerBound, upperBound, isNegated())

    open fun hasValue(): ComparableEnsure<A> = this
    override fun not(): ComparableEnsure<A> = negate() as ComparableEnsure<A>
    open fun usingComparator(comparator: Comparator<A>): ComparableEnsure<A> {
        return ComparableEnsure(value, comparator)
    }

    private val IS_GREATER_THAN =
            expectThatActualIs("greater than",
                    fun(actor: Actor?, actual: KnowableValue<Comparable<A>>?, expected: Comparable<A>): Boolean {
                        ensureActualAndExpectedNotNull(actual, expected)
                        val resolvedValue = actual!!(actor!!)
                        ensureActualNotNull(resolvedValue)
                        return if (comparator != null) comparator.compare(resolvedValue as A, expected as A) > 0 else resolvedValue!! > expected as A
                    })

    val IS_GREATER_THAN_OR_EQUAL_TO = expectThatActualIs("greater than or equal to",
            fun(actor: Actor?, actual: KnowableValue<Comparable<A>>?, expected: Comparable<A>): Boolean {
                ensureActualAndExpectedNotNull(actual, expected)
                val resolvedValue = actual!!(actor!!)
                ensureActualNotNull(resolvedValue)
                return if (comparator != null) comparator.compare(resolvedValue as A, expected as A) >= 0 else resolvedValue!! >= expected as A
            })

    val IS_LESS_THAN = expectThatActualIs("less than",
            fun(actor: Actor?, actual: KnowableValue<Comparable<A>>?, expected: Comparable<A>): Boolean {
                ensureActualAndExpectedNotNull(actual, expected)
                val resolvedValue = actual!!(actor!!)
                ensureActualNotNull(resolvedValue)
                return if (comparator != null) comparator.compare(resolvedValue as A, expected as A) < 0 else resolvedValue!! < expected as A
            })

    val IS_LESS_THAN_OR_EQUAL_TO =
            expectThatActualIs("less than or equal to",
                    fun(actor: Actor?, actual: KnowableValue<Comparable<A>>?, expected: Comparable<A>): Boolean {
                        ensureActualAndExpectedNotNull(actual, expected)
                        val resolvedValue = actual!!(actor!!)
                        ensureActualNotNull(resolvedValue)
                        return if (comparator != null) comparator.compare(resolvedValue as A, expected as A) <= 0 else resolvedValue!! <= expected as A
                    })

    val IS_BETWEEN =
            expectThatActualIs("between",
                    fun(actor: Actor?, actual: KnowableValue<Comparable<A>>?, startRange: Comparable<A>, endRange: Comparable<A>): Boolean {
                        ensureActualAndRangeValues(actual, startRange, endRange)
                        val resolvedValue = actual!!(actor!!)
                        ensureActualNotNull(resolvedValue)
                        return if (comparator != null)
                            comparator.compare(resolvedValue as A, startRange as A) >= 0 && comparator.compare(resolvedValue as A, endRange as A) <= 0
                        else
                            (resolvedValue!! >= startRange as A && resolvedValue!! <= endRange as A)
                    })

    val IS_STRICTLY_BETWEEN =
            expectThatActualIs("strictly between",
                    fun(actor: Actor?, actual: KnowableValue<Comparable<A>>?, startRange: Comparable<A>, endRange: Comparable<A>): Boolean {
                        ensureActualAndRangeValues(actual, startRange, endRange)
                        val resolvedValue = actual!!(actor!!)
                        ensureActualNotNull(resolvedValue)
                        return if (comparator != null)
                            comparator.compare(resolvedValue as A, startRange as A) > 0 && comparator.compare(resolvedValue as A, endRange as A) < 0
                        else
                            (resolvedValue!! > startRange as A && resolvedValue!! < endRange as A)
                    })

}