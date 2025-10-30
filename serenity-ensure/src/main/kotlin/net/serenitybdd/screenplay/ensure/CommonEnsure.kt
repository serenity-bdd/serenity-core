package net.serenitybdd.screenplay.ensure

import net.serenitybdd.markers.CanBeSilent
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Performable

fun pluralFormOf(targetName: String) = targetName.replace("web element ", "web elements ")

open class CommonEnsure<A, E>(open val value: KnowableValue<A>,
                              val expectedDescription: String = descriptionOf { value }) : CanBeSilent {

    constructor(value: A) : this(KnownValue(value, value.toString()))

    /**
     * Verifies that the actual value is equal to the given one.
     * <p>
     * Example:
     * <pre><code class='java'>
     * actor.attemptsTo(Ensure.that(&quot;abc&quot;).isEqualTo(&quot;abc&quot;));
     * </code></pre>
     */
    open fun isEqualTo(expected: E) = PerformableExpectation(value, IS_EQUAL_TO, expected, isNegated(), expectedDescription)

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
    open fun isNotEqualTo(expected: E) = PerformableExpectation(value, IS_NOT_EQUAL_TO, expected, isNegated(), expectedDescription)

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
    open fun isNull() = PerformablePredicate(value, IS_NULL, isNegated(), expectedDescription)

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
    fun isNotNull() = PerformablePredicate(value, IS_NOT_NULL, isNegated(), expectedDescription)

    /**
     * Verifies that the actual value is present in the given array of values.
     * Example:
     * <pre><code class='java'>
     * actor.attemptsTo(Ensure.that("red").isIn("red","green","blue));
     * </code></pre>
     */
    fun isIn(vararg expected: A) = PerformableExpectation(value, IS_IN, expected.toList(), isNegated(), expectedDescription)

    /**
     * Verifies that the actual value is present in the given collection of values
     * Example:
     * <pre><code class='java'> List<String> colors = Arrays.asList("red", "green", "blue");
     * actor.attemptsTo(Ensure.that("red").isIn(colors));
     * </code></pre>
     *
     */
    fun isIn(expected: Collection<A>) = PerformableExpectation(value, IS_IN, expected.toList(), isNegated(), expectedDescription)

    /**
     * Verifies that the actual value is not present in the given array of values.
     * Example:
     * <pre><code class='java'>
     * actor.attemptsTo(Ensure.that("yellow").isIn("red","green","blue));
     * </code></pre>
     *
     */
    fun isNotIn(vararg expected: A) = PerformableExpectation(value, IS_NOT_IN, expected.toList(), isNegated(), expectedDescription)

    /**
     * Verifies that the actual value is not present in the given collection of values
     * Example:
     * <pre><code class='java'> List<String> colors = Arrays.asList("red", "green", "blue");
     * actor.attemptsTo(Ensure.that("yellow").isIn(colors));
     * </code></pre>
     *
     */
    fun isNotIn(expected: Collection<A>) = PerformableExpectation(value, IS_NOT_IN, expected.toList(), isNegated(), expectedDescription)

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
    fun matches(description: String, expected: (A?) -> Boolean): Performable = PerformableExpectation(value, matchesPredicate(description), expected, isNegated(), expectedDescription)

    private var negated = false

    fun negate(): CommonEnsure<A, E> {
        negated = !negated
        return this
    }

    open fun not(): CommonEnsure<A, E> = negate()

    fun isNegated() = negated

    var silent = false

    override fun isSilent() = silent

    open fun silently(): CommonEnsure<A, E> {
        silent = true
        return this
    }

    fun asAString() = StringEnsure(KnowableStringValue(value, expectedDescription), expectedDescription)
    fun asAnInteger() = ComparableEnsure(KnowableIntValue(value, expectedDescription), null, expectedDescription)
    fun asADouble() = DoubleEnsure.fromKnowable(KnowableDoubleValue(value, expectedDescription))
    fun asAFloat() = FloatEnsure.fromKnowable(KnowableFloatValue(value, expectedDescription))
    fun asABigDecimal() = ComparableEnsure(KnowableBigDecimalValue(value, expectedDescription), null, expectedDescription)
    fun asADate() = DateEnsure(KnowableLocalDateValue(value, expectedDescription), naturalOrder())
    fun asADate(format: String) = DateEnsure(KnowableLocalDateValue(value, expectedDescription, format), naturalOrder())
    fun asATime() = TimeEnsure(KnowableLocalTimeValue(value, expectedDescription), naturalOrder())
    fun asATime(format: String) = TimeEnsure(KnowableLocalTimeValue(value, expectedDescription, format), naturalOrder())
    fun asABoolean() = BooleanEnsure(KnowableBooleanValue(value, expectedDescription))

    private val IS_EQUAL_TO = expectThatActualIs("equal to",
            fun(actor: Actor?, actual: KnowableValue<A>?, expected: E): Boolean {
                if (actor == null || actual == null) return false;
                val actualValue = actual(actor)
                BlackBox.logAssertion(actualValue, expected)

                return actual(actor) == expected

            })

    private val IS_NOT_EQUAL_TO = expectThatActualIs("not equal to",
            fun(actor: Actor?, actual: KnowableValue<A>?, expected: E): Boolean {
                if (actor == null || actual == null) return false;
                val actualValue = actual(actor)
                BlackBox.logAssertion(actualValue, expected)

                return actual(actor) != expected
            })

    private val IS_NULL = expectThatActualIs("null", fun(actor: Actor?, actual: KnowableValue<A?>?): Boolean {
        if (actor == null || actual == null) return true
        val actualValue = actual(actor)
        BlackBox.logAssertion(actualValue, null)

        return actualValue == null
    })

    private val IS_NOT_NULL = expectThatActualIs("not null", fun(actor: Actor?, actual: KnowableValue<A?>?): Boolean {
        if (actor == null || actual == null) return false
        val actualValue = actual(actor)
        BlackBox.logAssertion(actualValue, "not null")
        return actualValue != null
    })

    private val IS_IN = expectThatActualIs("in",
            fun(actor: Actor?, actual: KnowableValue<A>?, expected: List<A>): Boolean {
                if (actor == null || actual == null) return false
                val actualValue = actual(actor)
                BlackBox.logAssertion(actualValue, expected)

                return expected.contains(actualValue)
            })

    private val IS_NOT_IN = expectThatActualIs("not in",
            fun(actor: Actor?, actual: KnowableValue<A>?, expected: List<A>): Boolean {
                if (actor == null || actual == null) return false
                val actualValue = actual(actor)
                BlackBox.logAssertion(actualValue, expected)

                return !expected.contains(actualValue)
            })

    private fun matchesPredicate(description: String) = expectThatActualIs("a match for",
            fun(actor: Actor?, actual: KnowableValue<A>?, expected: (A?) -> Boolean): Boolean {
                if (actor == null || actual == null) return false
                val actualValue = actual(actor)
                BlackBox.logAssertion(actualValue, null)

                return expected.invoke(actualValue)
            }, description)
}