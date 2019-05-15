package net.serenitybdd.screenplay.ensure.strings

import net.serenitybdd.screenplay.ensure.BiPerformableExpectation
import net.serenitybdd.screenplay.ensure.ComparableEnsure
import net.serenitybdd.screenplay.ensure.PerformableExpectation
import net.serenitybdd.screenplay.ensure.PerformablePredicate
import net.serenitybdd.screenplay.ensure.strings.StringExpectations.BLANK
import net.serenitybdd.screenplay.ensure.strings.StringExpectations.CONTAINS
import net.serenitybdd.screenplay.ensure.strings.StringExpectations.CONTAINS_IGNORING_CASE
import net.serenitybdd.screenplay.ensure.strings.StringExpectations.CONTAINS_NO_WHITESPACES
import net.serenitybdd.screenplay.ensure.strings.StringExpectations.CONTAINS_ONLY_DIGITS
import net.serenitybdd.screenplay.ensure.strings.StringExpectations.CONTAINS_ONLY_LETTERS
import net.serenitybdd.screenplay.ensure.strings.StringExpectations.CONTAINS_ONLY_LETTERS_OR_DIGITS
import net.serenitybdd.screenplay.ensure.strings.StringExpectations.CONTAINS_ONLY_WHITESPACES
import net.serenitybdd.screenplay.ensure.strings.StringExpectations.CONTAINS_WHITESPACES
import net.serenitybdd.screenplay.ensure.strings.StringExpectations.DOES_NOT_CONTAINS
import net.serenitybdd.screenplay.ensure.strings.StringExpectations.EMPTY
import net.serenitybdd.screenplay.ensure.strings.StringExpectations.ENDS_WITH
import net.serenitybdd.screenplay.ensure.strings.StringExpectations.EQUALS_IGNORING_CASE
import net.serenitybdd.screenplay.ensure.strings.StringExpectations.HAS_LINE_COUNT
import net.serenitybdd.screenplay.ensure.strings.StringExpectations.HAS_SAME_SIZE
import net.serenitybdd.screenplay.ensure.strings.StringExpectations.HAS_SIZE
import net.serenitybdd.screenplay.ensure.strings.StringExpectations.HAS_SIZE_BETWEEN
import net.serenitybdd.screenplay.ensure.strings.StringExpectations.HAS_SIZE_GREATER_THAN
import net.serenitybdd.screenplay.ensure.strings.StringExpectations.HAS_SIZE_GREATER_THAN_OR_EQUAL_TO
import net.serenitybdd.screenplay.ensure.strings.StringExpectations.HAS_SIZE_LESS_THAN
import net.serenitybdd.screenplay.ensure.strings.StringExpectations.HAS_SIZE_LESS_THAN_OR_EQUAL_TO
import net.serenitybdd.screenplay.ensure.strings.StringExpectations.LOWER_CASE
import net.serenitybdd.screenplay.ensure.strings.StringExpectations.MATCHES
import net.serenitybdd.screenplay.ensure.strings.StringExpectations.MATCHES_PATTERN
import net.serenitybdd.screenplay.ensure.strings.StringExpectations.NULL_OR_EMPTY
import net.serenitybdd.screenplay.ensure.strings.StringExpectations.STARTS_WITH
import net.serenitybdd.screenplay.ensure.strings.StringExpectations.SUBSTRING_OF
import net.serenitybdd.screenplay.ensure.strings.StringExpectations.UPPER_CASE
import java.util.regex.Pattern

class StringEnsure(override val value: String?, comparator: Comparator<String>) : ComparableEnsure<String>(value, comparator) {

    constructor(value: String?) : this(value, Comparator.naturalOrder<String>())

    /**
     * Verifies that the actual {@code String} is either an empty string or <code>null</code>
     */
    fun isNullOrEmpty() = PerformablePredicate(value, NULL_OR_EMPTY, isNegated())

    /**
     * Verifies that the actual {@code String} is an empty string (contains no characters)
     */
    fun isEmpty() = PerformablePredicate(value, EMPTY, isNegated())

    /**
     * Verifies that the actual {@code String} contains all the given values.
     * <p>
     * You can use one or several {@code CharSequence}s as in this example:
     * <pre><code class='java'>actor.attemptsTo(Ensure.that("red green blue").contains("green"));</code></pre>
     * <pre><code class='java'>actor.attemptsTo(Ensure.that("red green blue").contains("green","red));</code></pre>
     */
    fun contains(vararg expected: String) = PerformableExpectation(value, CONTAINS, expected.toList(), isNegated())

    /**
     * Verifies that the actual {@code String} do not contain any the given values.
     */
    fun doesNotContain(vararg expected: String) = PerformableExpectation(value, DOES_NOT_CONTAINS, expected.toList(), isNegated())

    /**
     * Verifies that the actual {@code CharSequence} contains the given sequence, ignoring case considerations.
     */
    fun containsIgnoringCase(vararg expected: String) = PerformableExpectation(value, CONTAINS_IGNORING_CASE, expected.toList(), isNegated())

    /**
     * Verifies that the actual {@code String} is equal to the expected value, ignoring case.
     */
    fun isEqualToIgnoringCase(expected: CharSequence) = PerformableExpectation(value, EQUALS_IGNORING_CASE, expected, isNegated())

    /**
     * Verifies that the actual {@code String} is in upper case.
     * Empty or null strings will fail
     */
    fun isInUppercase() = PerformablePredicate(value, UPPER_CASE, isNegated())

    /**
     * Verifies that the actual {@code String} is in lower case.
     * Empty or null strings will fail
     */
    fun isInLowercase() = PerformablePredicate(value, LOWER_CASE, isNegated())

    /**
     * Verifies that the actual {@code String} is empty or contains only whitespace
     * Empty or null strings will fail
     */
    fun isBlank() = PerformablePredicate(value, BLANK, isNegated())

    /**
     * Verifies that the actual {@code String} is a substring of the specified value.
     */
    fun isSubstringOf(expected: CharSequence) = PerformableExpectation(value, SUBSTRING_OF, expected, isNegated())

    /**
     * Verifies that the actual {@code String} starts with the specified value.
     */
    fun startsWith(expected: CharSequence) = PerformableExpectation(value, STARTS_WITH, expected, isNegated())

    /**
     * Verifies that the actual {@code String} ends with the specified value.
     */
    fun endsWith(expected: CharSequence) = PerformableExpectation(value, ENDS_WITH, expected, isNegated())

    /**
     * Verifies that the actual {@code String} matches a given regular expression
     */
    fun matches(expected: CharSequence) = PerformableExpectation(value, MATCHES, expected, isNegated())

    /**
     * Verifies that the actual {@code String} matches a given regular expression
     */
    fun matches(expected: Pattern) = PerformableExpectation(value, MATCHES_PATTERN, expected, isNegated())

    /**
     * Verifies that the actual {@code CharSequence} consists of one or more whitespace characters (according to
     * {@link Character#isWhitespace(char)}).
     */
    fun containsWhitespaces() = PerformablePredicate(value, CONTAINS_WHITESPACES, isNegated())

    /**
     * Verifies that the actual {@code CharSequence} consists of one or more whitespace characters (according to
     * {@link Character#isWhitespace(char)}).
     */
    fun containsOnlyWhitespaces() = PerformablePredicate(value, CONTAINS_ONLY_WHITESPACES, isNegated())

    /**
     * Verifies that the actual {@code CharSequence} is either {@code null}, empty or does not contain any whitespace characters (according to {@link Character#isWhitespace(char)}).
     */
    fun doesNotContainAnyWhitespaces() = PerformablePredicate(value, CONTAINS_NO_WHITESPACES, isNegated())

    /**
     * Verifies that the actual {@code CharSequence} contains only digits. It fails if it contains non-digit
     * characters or is empty.
     */
    fun containsOnlyDigits() = PerformablePredicate(value, CONTAINS_ONLY_DIGITS, isNegated())

    /**
     * Verifies that the actual {@code CharSequence} contains only letters.
     */
    fun containsOnlyLetters() = PerformablePredicate(value, CONTAINS_ONLY_LETTERS, isNegated())

    /**
     * Verifies that the actual {@code CharSequence} contains only letters or digits
     */
    fun containsOnlyLettersOrDigits() = PerformablePredicate(value, CONTAINS_ONLY_LETTERS_OR_DIGITS, isNegated())

    /**
     * Verifies that the actual {@code CharSequence} has the expected length using the {@code length()} method.
     */
    fun hasSize(expected: Int) = PerformableExpectation(value, HAS_SIZE, expected, isNegated())

    /**
     * Verifies that the actual {@code CharSequence} has has a length less than the given value using the {@code length()} method.
     */
    fun hasSizeLessThan(expected: Int) = PerformableExpectation(value, HAS_SIZE_LESS_THAN, expected, isNegated())

    /**
     * Verifies that the actual {@code CharSequence} has has a length less than or equal to the given value using the {@code length()} method.
     */
    fun hasSizeLessThanOrEqualTo(expected: Int) = PerformableExpectation(value, HAS_SIZE_LESS_THAN_OR_EQUAL_TO, expected, isNegated())

    /**
     * Verifies that the actual {@code CharSequence} has has a length greater than the given value using the {@code length()} method.
     */
    fun hasSizeGreaterThan(expected: Int) = PerformableExpectation(value, HAS_SIZE_GREATER_THAN, expected, isNegated())

    /**
     * Verifies that the actual {@code CharSequence} has has a length greater than the given value using the {@code length()} method.
     */
    fun hasSizeGreaterThanOrEqualTo(expected: Int) = PerformableExpectation(value, HAS_SIZE_GREATER_THAN_OR_EQUAL_TO, expected, isNegated())

    /**
     * Verifies that the actual {@code CharSequence} has length between the given boundaries (inclusive)
     * using the {@code length()} method.
     * */
    fun hasSizeBetween(startRange: Int, endRange: Int) = BiPerformableExpectation(value, HAS_SIZE_BETWEEN, startRange, endRange, isNegated())

    /**
     * Verifies that the actual {@code CharSequence} has the expected line count.
     */
    fun hasLineCount(expected: Int) = PerformableExpectation(value, HAS_LINE_COUNT, expected, isNegated())

    /**
     * Verifies that the actual {@code CharSequence} has a length that's the same as the length of the given
     * {@code CharSequence}.
     */
    fun hasSameSizeAs(expected: CharSequence) = PerformableExpectation(value, HAS_SAME_SIZE, expected, isNegated())

    override fun hasValue(): StringEnsure = this
    override fun not(): StringEnsure = negate() as StringEnsure
    override fun usingComparator(comparator: Comparator<String>): StringEnsure {
        return StringEnsure(value, comparator)
    }
}
