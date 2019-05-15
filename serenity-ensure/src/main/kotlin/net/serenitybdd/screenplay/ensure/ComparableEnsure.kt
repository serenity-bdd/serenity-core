package net.serenitybdd.screenplay.ensure

import net.serenitybdd.screenplay.ensure.CommonPreconditions.ensureActualAndExpectedNotNull
import net.serenitybdd.screenplay.ensure.CommonPreconditions.ensureActualAndRangeValues

/**
 * Predicates about comparable types
 */
open class ComparableEnsure<A>(override val value: Comparable<A>?,
                               val comparator: Comparator<A>? = null) : CommonEnsure<Comparable<A>?, A>(value) {

    /**
     * Verifies that the actual value is greater than the given one.
     * <p>
     * Example:
     * <pre><code class='java'> // assertions will pass
     * actor.attemptsTo(Ensure.that(2).isGreaterThan(1));
     * </code></pre>
     */
    fun isGreaterThan(expected: Comparable<in A>) = PerformableExpectation(value, IS_GREATER_THAN, expected as A, isNegated())


    /**
     * Verifies that the actual value is greater than or equal to the given one.
     * <p>
     * Example:
     * <pre><code class='java'> // assertions will pass
     * actor.attemptsTo(Ensure.that(2).isGreaterThanOrEqualTo(1));
     * actor.attemptsTo(Ensure.that(1).isGreaterThanOrEqualTo(1));
     * </code></pre>
     */
    fun isGreaterThanOrEqualTo(expected: Comparable<in A>) = PerformableExpectation(value, IS_GREATER_THAN_OR_EQUAL_TO, expected as A, isNegated())

    /**
     * Verifies that the actual value is less than the given one.
     * <p>
     * Example:
     * <pre><code class='java'> // assertions will pass
     * actor.attemptsTo(Ensure.that(2).isLessThan(3));
     * </code></pre>
     */
    fun isLessThan(expected: Comparable<in A>) = PerformableExpectation(value, IS_LESS_THAN, expected as A, isNegated())

    /**
     * Verifies that the actual value is less than or equal to the given one.
     * <p>
     * Example:
     * <pre><code class='java'> // assertions will pass
     * actor.attemptsTo(Ensure.that(2).isLessThanOrEqualTo(2));
     * actor.attemptsTo(Ensure.that(1).isLessThanOrEqualTo(2));
     * </code></pre>
     */
    fun isLessThanOrEqualTo(expected: Comparable<in A>) = PerformableExpectation(value, IS_LESS_THAN_OR_EQUAL_TO, expected as A, isNegated())

    /**
     * Verifies that the actual value is between two values
     * <p>
     * Example:
     * <pre><code class='java'> // assertions will pass
     * actor.attemptsTo(Ensure.that(2).isBetween(1,3));
     * </code></pre>
     */

    fun isBetween(lowerBound: Comparable<in A>, upperBound: Comparable<*>) = BiPerformableExpectation(value, IS_BETWEEN, lowerBound as A, upperBound as A, isNegated())

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
    fun isStrictlyBetween(lowerBound: Comparable<in A>, upperBound: Comparable<*>) = BiPerformableExpectation(value, IS_STRICTLY_BETWEEN, lowerBound as A, upperBound as A, isNegated())

    open fun hasValue(): ComparableEnsure<A> = this
    open fun not(): ComparableEnsure<*> = negate() as ComparableEnsure<A>
    open fun usingComparator(comparator: Comparator<A>): ComparableEnsure<A> {
        return ComparableEnsure(value, comparator)
    }

    val IS_GREATER_THAN = expectThatActualIs("greater than", fun(actual: Comparable<A>?, expected: A): Boolean {
        ensureActualAndExpectedNotNull(actual, expected)
        return if (comparator != null) comparator.compare(actual as A, expected) > 0 else actual!! > expected
    })

    val IS_GREATER_THAN_OR_EQUAL_TO = expectThatActualIs("greater than or equal to", fun(actual: Comparable<A>?, expected: A): Boolean {
        ensureActualAndExpectedNotNull(actual, expected)
        return if (comparator != null) comparator.compare(actual as A, expected) >= 0 else actual!! >= expected
    })

    val IS_LESS_THAN = expectThatActualIs("less than", fun(actual: Comparable<A>?, expected: A): Boolean {
        ensureActualAndExpectedNotNull(actual, expected)
        return if (comparator != null) comparator.compare(actual as A, expected) < 0 else actual!! < expected
    })

    val IS_LESS_THAN_OR_EQUAL_TO = expectThatActualIs("less than or equal to", fun(actual: Comparable<A>?, expected: A): Boolean {
        ensureActualAndExpectedNotNull(actual, expected)
        return if (comparator != null) comparator.compare(actual as A, expected) <= 0 else actual!! <= expected
    })

    val IS_BETWEEN = expectThatActualIs("between", fun(actual: Comparable<A>?, startRange: A, endRange: A): Boolean {
        ensureActualAndRangeValues(actual, startRange, endRange)
        return if (comparator != null)
            comparator.compare(actual as A, startRange) >= 0 && comparator.compare(actual as A, endRange) <= 0
        else
            (actual!! >= startRange && actual <= endRange)
    })
    val IS_STRICTLY_BETWEEN = expectThatActualIs("strictly between", fun(actual: Comparable<A>?, startRange: A, endRange: A): Boolean {
        ensureActualAndRangeValues(actual, startRange, endRange)
        return if (comparator != null)
            comparator.compare(actual as A, startRange) > 0 && comparator.compare(actual as A, endRange) < 0
        else
            (actual!! > startRange && actual < endRange)
    })


}