package net.serenitybdd.screenplay.ensure

import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Performable

typealias KnowableValue<A> = (Actor) -> A?

class KnownValue<A>(val value: A?, val description: String) : KnowableValue<A> {
    override fun invoke(actor: Actor): A? = value
    override fun toString() = description
}

open class UICommonEnsure<A, E>(open val value: KnowableValue<A>) {

    constructor(value: A) : this(KnownValue(value, value.toString()))

    /**
     * Verifies that the actual value is equal to the given one.
     * <p>
     * Example:
     * <pre><code class='java'>
     * actor.attemptsTo(Ensure.that(&quot;abc&quot;).isEqualTo(&quot;abc&quot;));
     * </code></pre>
     */
    fun isEqualTo(expected: E) = PerformableExpectation(value, IS_EQUAL_TO, expected, isNegated())

    // TODO
    // fun isEqualToIgnoringFields(expected: E, vararg fieldsToIgnore: String) = PerformableExpectation(value, IS_EQUAL_TO, expected, isNegated())

    /**
     * Verifies that the actual value is _not_ equal to the given one.
     * <p>
     * Example:
     * <pre><code class='java'> // assertions will pass
     * actor.attemptsTo(Ensure.that(&quot;abc&quot;).isEqualTo(&quot;123&quot;));
     * </code></pre>
     */
    fun isNotEqualTo(expected: E) = PerformableExpectation(value, IS_NOT_EQUAL_TO, expected, isNegated())

    /**
     * Verifies that the actual value is {@code null}.
     * <p>
     * Example:
     * <pre><code class='java'> String name = null;
     * // assertions will pass
     * actor.attemptsTo(Ensure.that(name).isNull());
     *
     * // assertions will fail
     * actor.attemptsTo(Ensure.that(&quot;abc&quot;).isNull();
     * </code></pre>
     */
    open fun isNull() = PerformablePredicate(value, IS_NULL, isNegated())


    /**
     * Verifies that the actual value is not {@code null}.
     * <p>
     * Example:
     * <pre><code class='java'> String name = "joe;
     * // assertions will pass
     * actor.attemptsTo(Ensure.that(name).isNotNull());
     *
     * // assertions will fail
     * String name = null
     * actor.attemptsTo(Ensure.that(name).isNotNull();
     * </code></pre>
     */
    fun isNotNull() = PerformablePredicate(value, IS_NOT_NULL, isNegated())


    /**
     * Verifies that the actual value is present in the given array of values.
     * Example:
     * <pre><code class='java'>
     * actor.attemptsTo(Ensure.that("red").isIn("red","green","blue));
     * </code></pre>
     */
    fun isIn(vararg expected: A) = PerformableExpectation(value, IS_IN, expected.toList(), isNegated())

    /**
     * Verifies that the actual value is present in the given collection of values
     * Example:
     * <pre><code class='java'> List<String> colors = Arrays.asList("red", "green", "blue");
     * actor.attemptsTo(Ensure.that("red").isIn(colors));
     * </code></pre>
     *
     */
    fun isIn(expected: Collection<A>) = PerformableExpectation(value, IS_IN, expected.toList(), isNegated())

    /**
     * Verifies that the actual value is not present in the given array of values.
     * Example:
     * <pre><code class='java'>
     * actor.attemptsTo(Ensure.that("yellow").isIn("red","green","blue));
     * </code></pre>
     *
     */
    fun isNotIn(vararg expected: A) = PerformableExpectation(value, IS_NOT_IN, expected.toList(), isNegated())

    /**
     * Verifies that the actual value is not present in the given collection of values
     * Example:
     * <pre><code class='java'> List<String> colors = Arrays.asList("red", "green", "blue");
     * actor.attemptsTo(Ensure.that("yellow").isIn(colors));
     * </code></pre>
     *
     */
    fun isNotIn(expected: Collection<A>) = PerformableExpectation(value, IS_NOT_IN, expected.toList(), isNegated())

    /**
     * Verifies that the actual value matches a specified lambda expression
     * Example:
     * <pre><code class='java'> String color = "yellow;
     * actor.attemptsTo(Ensure.that(color).matches("is yellow", color -> color.equals("yellow")));
     * </code></pre>
     *
     * @param description a short description of the lambda expression, which will appear in failure messages
     * @param expected the lambda expression to check values against
     *
     */
    fun matches(description: String, expected: (A?) -> Boolean): Performable = PerformableExpectation(value, matchesPredicate(description), expected, isNegated())

    private var negated = false

    fun negate(): UICommonEnsure<A, E> {
        negated = !negated
        return this
    }

    open fun not(): UICommonEnsure<A, E> = negate()

    fun isNegated() = negated

    private val IS_EQUAL_TO = expectThatActualIs("equal to",
            fun(actor: Actor?, actual: KnowableValue<A>?, expected: E): Boolean =
                    (actor != null && actual != null) && actual(actor) == expected)

    private val IS_NOT_EQUAL_TO = expectThatActualIs("not equal to", fun(actor: Actor?, actual: KnowableValue<A>?, expected: E): Boolean =
            (actor != null && actual != null) && actual(actor) != expected)

    private val IS_NULL = expectThatActualIs("null", fun(actor: Actor?, actual: KnowableValue<A?>?): Boolean {
        if (actor == null || actual == null) return true
        val actualValue = actual(actor)
        return actualValue == null || actualValue == NULL_VALUE_AS_STRING
    })

    private val IS_NOT_NULL = expectThatActualIs("not null", fun(actor: Actor?, actual: KnowableValue<A?>?): Boolean {
        if (actor == null || actual == null) return false
        val actualValue = actual(actor)
        return actualValue != null && actualValue != NULL_VALUE_AS_STRING
    })

    private val IS_IN = expectThatActualIs("in", fun(actor: Actor?, actual: KnowableValue<A>?, expected: List<A>): Boolean = (actor != null && actual != null) && expected.contains(actual(actor)))

    private val IS_NOT_IN = expectThatActualIs("not in", fun(actor: Actor?, actual: KnowableValue<A>?, expected: List<A>): Boolean = (actor != null && actual != null) && !expected.contains(actual(actor)))

    private fun matchesPredicate(description: String) = expectThatActualIs("a match for", fun(actor: Actor?, actual: KnowableValue<A>?, expected: (A?) -> Boolean): Boolean =
            (actor != null && actual != null) && expected.invoke(actual(actor)), description)


    companion object {
        val NULL_VALUE_AS_STRING = "\u2205" // UNICODE EMPTY SET SYMBOL
    }

}