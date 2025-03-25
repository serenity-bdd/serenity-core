package net.serenitybdd.screenplay.ensure

import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.ensure.CommonPreconditions.ensureActualAndExpectedNotNull
import net.serenitybdd.screenplay.ensure.CommonPreconditions.ensureActualNotNull
import net.serenitybdd.screenplay.ensure.CommonPreconditions.ensureNoNullElementsIn
import net.serenitybdd.screenplay.ensure.CommonPreconditions.ensureNotEmpty
import net.thucydides.core.steps.StepEventBus
import java.io.IOException
import java.io.LineNumberReader
import java.io.StringReader
import java.util.Comparator.naturalOrder
import java.util.regex.Pattern

class StringEnsure(override val value: KnowableValue<String?>,
                   comparator: Comparator<String>,
                   expectedDescription: String = descriptionOf(value)) : ComparableEnsure<String>(value, comparator, expectedDescription) {

    constructor(value: KnowableValue<String?>) : this(value, Comparator.naturalOrder<String>())

    constructor(value: KnowableValue<String?>, valueDescription: String) : this(value, Comparator.naturalOrder<String>(), valueDescription)

    constructor(value: String?) : this(
            KnownValue<String?>(value, if (value == null) "<null>" else "\"$value\""),
            naturalOrder<String>()
    )

    /**
     * Verifies that the actual {@code String} is either an empty string or <code>null</code>
     */
    fun isNullOrEmpty() = PerformablePredicate(value, NULL_OR_EMPTY, isNegated(), expectedDescription)

    /**
     * Verifies that the actual {@code String} is an empty string (contains no characters)
     */
    fun isEmpty() = PerformablePredicate(value, IS_EMPTY, isNegated(), expectedDescription)

    /**
     * Verifies that the actual {@code String} is an empty string (contains no characters)
     */
    fun isNotEmpty() = PerformablePredicate(value, IS_NOT_EMPTY, isNegated(), expectedDescription)

    /**
     * Verifies that the actual {@code String} contains all the given values.
     * <p>
     * You can use one or several {@code CharSequence}s as in this example:
     * <pre><code class='java'>actor.attemptsTo(Ensure.that("red green blue").contains("green"));</code></pre>
     * <pre><code class='java'>actor.attemptsTo(Ensure.that("red green blue").contains("green","red));</code></pre>
     */
    fun contains(vararg expected: String) = PerformableExpectation(value, CONTAINS, expected.toList(), isNegated(), expectedDescription)
//    fun contains(vararg expected: String) = PerformableExpectation(value, CONTAINS, expected.toList(), isNegated(), expectedDescription)

    /**
     * Verifies that the actual {@code String} do not contain any the given values.
     */
    fun doesNotContain(vararg expected: String) = PerformableExpectation(value, DOES_NOT_CONTAINS, expected.toList(), isNegated(), expectedDescription)

    /**
     * Verifies that the actual {@code CharSequence} contains the given sequence, ignoring case considerations.
     */
    fun containsIgnoringCase(vararg expected: String) = PerformableExpectation(value, CONTAINS_IGNORING_CASE, expected.toList(), isNegated(), expectedDescription)

    /**
     * Verifies that the actual {@code String} is equal to the expected value, ignoring case.
     */
    fun isEqualToIgnoringCase(expected: CharSequence) = PerformableExpectation(value, EQUALS_IGNORING_CASE, expected, isNegated(), expectedDescription)

    /**
     * Verifies that the actual {@code String} is in upper case.
     * Empty or null strings will fail
     */
    fun isInUppercase() = PerformablePredicate(value, IS_UPPER_CASE, isNegated(), expectedDescription)

    /**
     * Verifies that the actual {@code String} is in lower case.
     * Empty or null strings will fail
     */
    fun isInLowercase() = PerformablePredicate(value, IS_LOWER_CASE, isNegated(), expectedDescription)

    /**
     * Verifies that the actual {@code String} is empty or contains only whitespace
     * Empty or null strings will fail
     */
    fun isBlank() = PerformablePredicate(value, BLANK, isNegated(), expectedDescription)

    /**
     * Verifies that the actual {@code String} is not empty or contains only whitespace
     * Empty or null strings will fail
     */
    fun isNotBlank() = PerformablePredicate(value, NOT_BLANK, isNegated(), expectedDescription)


    /**
     * Verifies that the actual {@code String} is a substring of the specified value.
     */
    fun isSubstringOf(expected: CharSequence) = PerformableExpectation(value, SUBSTRING_OF, expected, isNegated(), expectedDescription)

    /**
     * Verifies that the actual {@code String} starts with the specified value.
     */
    fun startsWith(expected: CharSequence) = PerformableExpectation(value, STARTS_WITH, expected, isNegated(), expectedDescription)

    /**
     * Verifies that the actual {@code String} ends with the specified value.
     */
    fun endsWith(expected: CharSequence) = PerformableExpectation(value, ENDS_WITH, expected, isNegated(), expectedDescription)

    /**
     * Verifies that the actual {@code String} matches a given regular expression
     */
    fun matches(expected: CharSequence) = PerformableExpectation(value, MATCHES, expected, isNegated(), expectedDescription)

    /**
     * Verifies that the actual {@code String} matches a given regular expression
     */
    fun matches(expected: Pattern) = PerformableExpectation(value, MATCHES_PATTERN, expected, isNegated(), expectedDescription)

    /**
     * Verifies that the actual {@code CharSequence} consists of one or more whitespace characters (according to
     * {@link Character#isWhitespace(char)}).
     */
    fun containsWhitespaces() = PerformablePredicate(value, CONTAINS_WHITESPACES, isNegated(), expectedDescription)

    /**
     * Verifies that the actual {@code CharSequence} consists of one or more whitespace characters (according to
     * {@link Character#isWhitespace(char)}).
     */
    fun containsOnlyWhitespaces() = PerformablePredicate(value, CONTAINS_ONLY_WHITESPACES, isNegated(), expectedDescription)

    /**
     * Verifies that the actual {@code CharSequence} is either {@code null}, empty or does not contain any whitespace characters (according to {@link Character#isWhitespace(char)}).
     */
    fun doesNotContainAnyWhitespaces() = PerformablePredicate(value, CONTAINS_NO_WHITESPACES, isNegated(), expectedDescription)

    /**
     * Verifies that the actual {@code CharSequence} contains only digits. It fails if it contains non-digit
     * characters or is empty.
     */
    fun containsOnlyDigits() = PerformablePredicate(value, CONTAINS_ONLY_DIGITS, isNegated(), expectedDescription)

    /**
     * Verifies that the actual {@code CharSequence} contains only letters.
     */
    fun containsOnlyLetters() = PerformablePredicate(value, CONTAINS_ONLY_LETTERS, isNegated(), expectedDescription)

    /**
     * Verifies that the actual {@code CharSequence} contains only letters or digits
     */
    fun containsOnlyLettersOrDigits() = PerformablePredicate(value, CONTAINS_ONLY_LETTERS_OR_DIGITS, isNegated(), expectedDescription)

    /**
     * Verifies that the actual {@code CharSequence} has the expected length using the {@code length()} method.
     */
    fun hasSize(expected: Int) = PerformableExpectation(value, HAS_SIZE, expected, isNegated(), expectedDescription)

    /**
     * Verifies that the actual {@code CharSequence} has has a length less than the given value using the {@code length()} method.
     */
    fun hasSizeLessThan(expected: Int) = PerformableExpectation(value, HAS_SIZE_LESS_THAN, expected, isNegated(), expectedDescription)

    /**
     * Verifies that the actual {@code CharSequence} has has a length less than or equal to the given value using the {@code length()} method.
     */
    fun hasSizeLessThanOrEqualTo(expected: Int) = PerformableExpectation(value, HAS_SIZE_LESS_THAN_OR_EQUAL_TO, expected, isNegated(), expectedDescription)

    /**
     * Verifies that the actual {@code CharSequence} has has a length greater than the given value using the {@code length()} method.
     */
    fun hasSizeGreaterThan(expected: Int) = PerformableExpectation(value, HAS_SIZE_GREATER_THAN, expected, isNegated(), expectedDescription)

    /**
     * Verifies that the actual {@code CharSequence} has has a length greater than the given value using the {@code length()} method.
     */
    fun hasSizeGreaterThanOrEqualTo(expected: Int) = PerformableExpectation(value, HAS_SIZE_GREATER_THAN_OR_EQUAL_TO, expected, isNegated(), expectedDescription)

    /**
     * Verifies that the actual {@code CharSequence} has length between the given boundaries (inclusive)
     * using the {@code length()} method.
     * */
    fun hasSizeBetween(startRange: Int, endRange: Int) = BiPerformableExpectation(value, HAS_SIZE_BETWEEN, startRange, endRange, isNegated(), expectedDescription)

    /**
     * Verifies that the actual {@code CharSequence} has the expected line count.
     */
    fun hasLineCount(expected: Int) = PerformableExpectation(value, HAS_LINE_COUNT, expected, isNegated(), expectedDescription)

    /**
     * Verifies that the actual {@code CharSequence} has a length that's the same as the length of the given
     * {@code CharSequence}.
     */
    fun hasSameSizeAs(expected: CharSequence) = PerformableExpectation(value, HAS_SAME_SIZE, expected, isNegated(), expectedDescription)

//    fun asInteger() = ComparableEnsure<Comparable<Int>>(intValue(value), expectedDescription)

    override fun hasValue(): StringEnsure = this
    override fun not(): StringEnsure = negate() as StringEnsure
    override fun silently(): StringEnsure = super.silently() as StringEnsure
    override fun usingComparator(comparator: Comparator<String>): StringEnsure {
        return StringEnsure(value, comparator)
    }

    companion object {

        private val NULL_OR_EMPTY = expectThatActualIs("null or empty",
                fun(actor: Actor?, actual: KnowableValue<String>?): Boolean {
                    if (actor == null || actual == null) return true
                    val actualValue = actual(actor)
                    BlackBox.logAssertion(actualValue,"a null or empty value")
                    return actualValue == null || actualValue.isEmpty() //|| actualValue == NULL_VALUE_AS_STRING
                }
        )

        private val IS_EMPTY = expectThatActualIs("empty",
                fun(actor: Actor?, actual: KnowableValue<String>?): Boolean {
                    if (actor == null || actual == null) return true
                    val actualValue = actual(actor)
                    BlackBox.logAssertion(actualValue,"an empty value")
                    return actualValue != null && actualValue.isEmpty()

                }
        )


        private val IS_NOT_EMPTY = expectThatActualIs("not empty",
                fun(actor: Actor?, actual: KnowableValue<String>?): Boolean {
                    if (actor == null || actual == null) return true
                    val actualValue = actual(actor) ?: return false
                    BlackBox.logAssertion(actualValue,"a non-empty value")
                    return actualValue.isNotEmpty()
                }
        )

        private val CONTAINS = expectThatActualIs("contains", "does not contain",
                fun(actor: Actor?, actual: KnowableValue<String>?, expectedList: List<CharSequence>): Boolean {
                    ensureActualAndExpectedNotNull(actual, expectedList)
                    ensureNotEmpty("expected should not be null", expectedList)
                    ensureNoNullElementsIn("actual should not contain null elements", expectedList)

                    val actualValue = resolveActual(actual, actor, expectedList) ?: return false

                    return expectedList.all { expectedItem ->
                        actualValue.contains(expectedItem)
                    }
                }
        )


        private val DOES_NOT_CONTAINS = expectThatActualIs("does not contain", "contains",
                fun(actor: Actor?, actual: KnowableValue<String>?, expectedList: List<CharSequence>): Boolean {
                    ensureActualAndExpectedNotNull(actual, expectedList)
                    ensureNotEmpty("expected should not be null", expectedList)
                    ensureNoNullElementsIn("actual should not contain null elements", expectedList)

                    val actualValue = resolveActual(actual, actor, expectedList) ?: return true

                    return expectedList.none { expectedItem -> actualValue.contains(expectedItem) }
                }
        )

        private val CONTAINS_IGNORING_CASE = expectThatActualIs("contains (ignoring case)", "does not contain (ignoring case)",
                fun(actor: Actor?, actual: KnowableValue<String>?, expectedList: List<CharSequence>): Boolean {
                    ensureActualAndExpectedNotNull(actual, expectedList)
                    ensureNotEmpty("expected should not be null", expectedList)
                    ensureNoNullElementsIn("actual should not contain null elements", expectedList)

                    val actualValue = resolveActual(actual, actor, expectedList) ?: return false

                    return expectedList.all { expectedItem -> actualValue.lowercase().contains(expectedItem.toString().lowercase()) }
                }
        )

        private fun resolveActual(actual: KnowableValue<String>?, actor: Actor?, expectedList: Any): String? {
            val actualValue = actual!!(actor!!)
            BlackBox.logAssertion(actualValue, expectedList)
            return actualValue
        }

        private val EQUALS_IGNORING_CASE = expectThatActualIs("equal to (ignoring case)",
                fun(actor: Actor?, actual: KnowableValue<String>?, expected: CharSequence): Boolean {

                    val actualValue = resolveActual(actual, actor, expected) ?: return expected.isEmpty()

                    return actualValue.lowercase() == expected.toString().lowercase()
                }
        )

        private val IS_UPPER_CASE = expectThatActualIs("uppercase",
                fun(actor: Actor?, actual: KnowableValue<String>?): Boolean {
                    if (actor == null || actual == null) return false

                    val actualValue = actual(actor)
                    BlackBox.logAssertion(actualValue,"an uppercase value")
                    if (actualValue == null) { return false }

                    return actualValue.isNotEmpty() && actualValue.uppercase() == actualValue
                }
        )

        private val IS_LOWER_CASE = expectThatActualIs("lowercase",
                fun(actor: Actor?, actual: KnowableValue<String>?): Boolean {
                    if (actor == null || actual == null) return true

                    val actualValue = actual(actor)
                    BlackBox.logAssertion(actualValue,"a lowercase value")
                    if (actualValue == null) { return false }

                    return actualValue.isNotEmpty() && actualValue.lowercase() == actualValue
                }
        )

        private val BLANK = expectThatActualIs("blank",
                fun(actor: Actor?, actual: KnowableValue<String>?): Boolean {
                    if (actor == null || actual == null) return true
                    val actualValue = actual(actor)
                    BlackBox.logAssertion(actualValue,"a blank value")
                    if (actualValue == null) { return false }
                    return actualValue.isBlank()
                }
        )

        private val NOT_BLANK = expectThatActualIs(
                "is not blank", "is blank",
                fun(actor: Actor?, actual: KnowableValue<String>?): Boolean {
                    if (actor == null || actual == null) return true
                    val actualValue = actual(actor)
                    BlackBox.logAssertion(actualValue,"a non-blank value")
                    if (actualValue == null) { return false }
                    return actualValue.isNotBlank()
                }
        )

        private val SUBSTRING_OF = expectThatActualIs("a substring of",
                fun(actor: Actor?, actual: KnowableValue<String>?, expected: CharSequence): Boolean {
                    ensureActualNotNull(actual)

                    val actualValue = resolveActual(actual, actor, expected) ?: return false

                    return expected.contains(actualValue)
                }
        )

        private val STARTS_WITH = expectThatActualIs(
                "starts with","does not start with",
                fun(actor: Actor?, actual: KnowableValue<String>?, expected: CharSequence): Boolean {
                    ensureActualAndExpectedNotNull(actual, expected)

                    val actualValue = resolveActual(actual, actor, expected)
                    BlackBox.logAssertion(actualValue, expected)
                    if (actualValue == null) { return false }

                    return actualValue.startsWith(expected)
                }
        )

        private val ENDS_WITH = expectThatActualIs("ends with", "does not end with",
                fun(actor: Actor?, actual: KnowableValue<String>?, expected: CharSequence): Boolean {
                    ensureActualAndExpectedNotNull(actual, expected)

                    val actualValue = resolveActual(actual, actor, expected) ?: return false

                    return actualValue.endsWith(expected)
                }
        )

        private val MATCHES = expectThatActualIs("a match for",
                fun(actor: Actor?, actual: KnowableValue<String>?, expected: CharSequence): Boolean {
                    ensureActualAndExpectedNotNull(actual, expected)

                    val actualValue = resolveActual(actual, actor, expected) ?: return false

                    return actualValue.matches(Regex(expected.toString()))
                }
        )

        private val MATCHES_PATTERN = expectThatActualIs("a match for",
                fun(actor: Actor?, actual: KnowableValue<String>?, expected: Pattern): Boolean {

                    val actualValue = resolveActual(actual, actor, expected) ?: return false

                    return expected.matcher(actualValue).matches()
                })


        private val CONTAINS_WHITESPACES =
                expectThatActualIs("contains whitespaces","contains no whitespaces",
                fun(actor: Actor?, actual: KnowableValue<String>?): Boolean {

                    if (actor == null || actual == null) return true

                    val actualValue = actual(actor)
                    BlackBox.logAssertion(actualValue,"containing whitespaces")
                    if (actualValue == null) { return false }

                    return actualValue.isNotEmpty() && actualValue.chars().anyMatch(Character::isWhitespace)
                })

        private val CONTAINS_ONLY_WHITESPACES = expectThatActualIs("contains only whitespaces","does not contain only whitespaces",
                fun(actor: Actor?, actual: KnowableValue<String>?): Boolean {
                    if (actor == null || actual == null) return true

                    val actualValue = actual(actor)
                    BlackBox.logAssertion(actualValue,"contains only whitespaces")
                    if (actualValue == null) { return false }

                    return actualValue.isNotEmpty() && actualValue.chars().allMatch(Character::isWhitespace)
                })

        private val CONTAINS_NO_WHITESPACES = expectThatActualIs("contains no whitespace"," contains whitespace",
                fun(actor: Actor?, actual: KnowableValue<String>?): Boolean {
                    if (actor == null || actual == null) return true

                    val actualValue = actual(actor)
                    BlackBox.logAssertion(actualValue,"contains no whitespace")
                    if (actualValue == null) { return false }

                    return actualValue.chars().noneMatch(Character::isWhitespace)
                })

        private val CONTAINS_ONLY_DIGITS = expectThatActualIs("contains only digits", "does not contain only digits",
                fun(actor: Actor?, actual: KnowableValue<String>?): Boolean {
                    if (actor == null || actual == null) return true

                    val actualValue = actual(actor)
                    BlackBox.logAssertion(actualValue,"contains only digits")
                    if (actualValue == null) { return false }

                    return actualValue.isNotEmpty() && actualValue.chars().allMatch(Character::isDigit)
                })

        private val CONTAINS_ONLY_LETTERS = expectThatActualIs("contains only letters","does not contain only letters",
                fun(actor: Actor?, actual: KnowableValue<String>?): Boolean {
                    if (actor == null || actual == null) return true

                    val actualValue = actual(actor)
                    BlackBox.logAssertion(actualValue,"contains only letters")
                    if (actualValue == null) { return false }

                    return actualValue.isNotEmpty() && actualValue.chars().allMatch(Character::isLetter)
                })

        private val CONTAINS_ONLY_LETTERS_OR_DIGITS = expectThatActualIs("contains only letters or digits","contains characters other than letters and digits",
                fun(actor: Actor?, actual: KnowableValue<String>?): Boolean {
                    if (actor == null || actual == null) return true

                    val actualValue = actual(actor)
                    BlackBox.logAssertion(actualValue,"contains only letters or digits")
                    if (actualValue == null) { return false }

                    return actualValue.isNotEmpty() && actualValue.chars().allMatch(Character::isLetterOrDigit)
                })

        private val HAS_SIZE = expectThatActualIs("has size", "does not have size",
                fun(actor: Actor?, actual: KnowableValue<String>?, expected: Int): Boolean {
                    if (actual == null) return false
                    val actualValue = resolveActual(actual, actor, expected) ?: return false

                    return actualValue.length == expected
                })

        private val HAS_SIZE_LESS_THAN = expectThatActualIs("has size less than", "has size greater than or equal to",
                fun(actor: Actor?, actual: KnowableValue<String>?, expected: Int): Boolean {
                    if (actual == null) return false
                    val actualValue = resolveActual(actual, actor, expected) ?: return false
                    return actualValue.length < expected
                })

        private val HAS_SIZE_LESS_THAN_OR_EQUAL_TO = expectThatActualIs("has size less than or equal to","has size greater than",
                fun(actor: Actor?, actual: KnowableValue<String>?, expected: Int): Boolean {
                    if (actual == null) return false
                    val actualValue = resolveActual(actual, actor, expected) ?: return false
                    return actualValue.length <= expected
                })

        private val HAS_SIZE_GREATER_THAN = expectThatActualIs("has size greater than", "has size less than or equal to",
                fun(actor: Actor?, actual: KnowableValue<String>?, expected: Int): Boolean {
                    if (actual == null) return false
                    val actualValue = resolveActual(actual, actor, expected) ?: return false
                    return actualValue.length > expected
                })

        private val HAS_SIZE_GREATER_THAN_OR_EQUAL_TO = expectThatActualIs("has size greater than or equal to","has size less than",
                fun(actor: Actor?, actual: KnowableValue<String>?, expected: Int): Boolean {
                    if (actual == null) return false
                    val actualValue = resolveActual(actual, actor, expected) ?: return false
                    return actualValue.length >= expected
                })

        private val HAS_SIZE_BETWEEN = expectThatActualIs("of size between",
                fun(actor: Actor?, actual: KnowableValue<String>?, startRange: Int, endRange: Int): Boolean {
                    if (actual == null) return false
                    val actualValue = resolveActual(actual, actor, "of size between $startRange and $endRange") ?: return false
                    return actualValue.length in startRange..endRange
                })

        private val HAS_LINE_COUNT = expectThatActualIs("has a line count of","does not have a line count of",
                fun(actor: Actor?, actual: KnowableValue<String>?, expected: Int): Boolean {
                    val actualValue = resolveActual(actual, actor, expected) ?: return false
                    val reader = LineNumberReader(StringReader(actualValue))
                    try {
                        while (reader.readLine() != null);
                    } catch (e: IOException) {
                        StepEventBus.getEventBus().takeScreenshot()
                        throw AssertionError("Unable to count lines in $actual", e)
                    }
                    return reader.lineNumber == expected
                }
        )
        private val HAS_SAME_SIZE = expectThatActualIs("has the same size as","does not have the same size as",
                fun(actor: Actor?, actual: KnowableValue<String>?, expected: CharSequence): Boolean {
                    if (actual == null) return false
                    val actualValue = resolveActual(actual, actor, expected) ?: return false
                    return actualValue.length == expected.length
                }
        )
    }
}
