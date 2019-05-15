package net.serenitybdd.screenplay.ensure.strings

import net.serenitybdd.screenplay.ensure.CommonPreconditions.ensureActualAndExpectedNotNull
import net.serenitybdd.screenplay.ensure.CommonPreconditions.ensureActualNotNull
import net.serenitybdd.screenplay.ensure.CommonPreconditions.ensureNoNullElementsIn
import net.serenitybdd.screenplay.ensure.CommonPreconditions.ensureNotEmpty
import net.serenitybdd.screenplay.ensure.expectThatActualIs
import org.assertj.core.internal.InputStreamsException
import java.io.IOException
import java.io.LineNumberReader
import java.io.StringReader
import java.util.function.Predicate
import java.util.regex.Pattern

object StringExpectations {
    val CONTAINS = expectThatActualIs("containing",
            fun(actual: CharSequence?, expectedList: List<CharSequence>): Boolean {
                ensureActualAndExpectedNotNull(actual,expectedList)
                ensureNotEmpty("expected should not be null", expectedList)
                ensureNoNullElementsIn("actual should not contain null elements", expectedList)
                return expectedList.all { expectedItem -> actual!!.contains(expectedItem) }
            }
    )

    val DOES_NOT_CONTAINS = expectThatActualIs("not containing",
            fun(actual: CharSequence?, expectedList: List<CharSequence>): Boolean {
                ensureActualAndExpectedNotNull(actual,expectedList)
                ensureNotEmpty("expected should not be null", expectedList)
                ensureNoNullElementsIn("actual should not contain null elements", expectedList)
                return expectedList.none { expectedItem -> actual!!.contains(expectedItem) }
            }
    )

    val CONTAINS_IGNORING_CASE = expectThatActualIs("containing (ignoring case)",
            fun(actual: CharSequence?, expectedList: List<CharSequence>): Boolean {
                ensureActualAndExpectedNotNull(actual,expectedList)
                ensureNotEmpty("expected should not be null", expectedList)
                ensureNoNullElementsIn("actual should not contain null elements", expectedList)
                return expectedList.all { expectedItem -> actual.toString().toLowerCase()!!.contains(expectedItem.toString().toLowerCase()) }
            }
    )

    val SUBSTRING_OF = expectThatActualIs("a substring of",
            fun(actual: CharSequence?, expected: CharSequence): Boolean {
                ensureActualNotNull(actual)
                return expected.contains(actual!!)
            })

    val STARTS_WITH = expectThatActualIs("starting with",
            fun(actual: CharSequence?, expected: CharSequence): Boolean {
                ensureActualAndExpectedNotNull(actual,expected)
                return actual!!.startsWith(expected)
            }
    )
    
    val ENDS_WITH = expectThatActualIs("ending with",
            fun(actual: CharSequence?, expected: CharSequence): Boolean {
                ensureActualAndExpectedNotNull(actual,expected)
                return actual!!.endsWith(expected)
            }
    )
    
    val MATCHES = expectThatActualIs("a match for",
            fun(actual: CharSequence?, expected: CharSequence): Boolean {
                ensureActualAndExpectedNotNull(actual,expected)
               return actual!!.matches(Regex(expected.toString()))
            }
    )
    
    val MATCHES_PATTERN = expectThatActualIs("a match for", fun(actual: CharSequence?, expected: Pattern): Boolean = expected.matcher(actual).matches())

    val EQUALS_IGNORING_CASE = expectThatActualIs("equal to (ignoring case)", fun(actual: CharSequence?, expected: CharSequence): Boolean = actual.toString().toLowerCase().equals(expected.toString().toLowerCase()))
    val UPPER_CASE = expectThatActualIs("in uppercase", fun(actual: CharSequence?): Boolean {
        ensureActualNotNull(actual)
        return actual.toString().isNotEmpty() && (actual.toString().toUpperCase() == actual)
    })

    val LOWER_CASE = expectThatActualIs("in lowercase", fun(actual: CharSequence?): Boolean {
        ensureActualNotNull(actual)
        return actual.toString().isNotEmpty() && (actual.toString().toLowerCase() == actual)
    })

    val BLANK = expectThatActualIs("blank", fun(actual: CharSequence?): Boolean = actual!!.isBlank())
    val EMPTY = expectThatActualIs("empty", fun(actual: CharSequence?): Boolean = actual!!.isEmpty())
    val NULL_OR_EMPTY = expectThatActualIs("null or empty", fun(actual: CharSequence?): Boolean = actual == null || actual.isEmpty())
    val CONTAINS_WHITESPACES = expectThatActualIs("containing whitespaces", fun(actual: CharSequence?): Boolean = actual != null && actual.isNotEmpty() && actual.chars().anyMatch(Character::isWhitespace))

    val CONTAINS_ONLY_WHITESPACES = expectThatActualIs("containing only whitespaces", fun(actual: CharSequence?): Boolean = actual != null && actual.isNotEmpty() && actual.chars().allMatch(Character::isWhitespace))

    val CONTAINS_NO_WHITESPACES = expectThatActualIs("without any whitespaces", fun(actual: CharSequence?): Boolean = actual != null && actual.chars().noneMatch(Character::isWhitespace))

    val CONTAINS_ONLY_DIGITS = expectThatActualIs("containing only digits", fun(actual: CharSequence?): Boolean = actual != null && actual.isNotEmpty() && actual.chars().allMatch(Character::isDigit))

    val CONTAINS_ONLY_LETTERS = expectThatActualIs("containing only letters", fun(actual: CharSequence?): Boolean = actual != null && actual.isNotEmpty() && actual.chars().allMatch(Character::isLetter))

    val CONTAINS_ONLY_LETTERS_OR_DIGITS = expectThatActualIs("containing only letters or digits", fun(actual: CharSequence?): Boolean = actual != null && actual.isNotEmpty() && actual.chars().allMatch(Character::isLetterOrDigit))

    val HAS_SIZE = expectThatActualIs("of size", fun(actual: CharSequence?, expected: Int): Boolean = actual != null && actual.length == expected)
    val HAS_SIZE_LESS_THAN = expectThatActualIs("of size less than", fun(actual: CharSequence?, expected: Int): Boolean = actual != null && actual.length < expected)
    val HAS_SIZE_LESS_THAN_OR_EQUAL_TO = expectThatActualIs("of size less than or equal to", fun(actual: CharSequence?, expected: Int): Boolean = actual != null && actual.length <= expected)
    val HAS_SIZE_GREATER_THAN = expectThatActualIs("of size greater than", fun(actual: CharSequence?, expected: Int): Boolean = actual != null && actual.length > expected)
    val HAS_SIZE_GREATER_THAN_OR_EQUAL_TO = expectThatActualIs("of size greater than or equal to", fun(actual: CharSequence?, expected: Int): Boolean = actual != null && actual.length >= expected)
    val HAS_SIZE_BETWEEN = expectThatActualIs("of size of between", fun(actual: CharSequence?, startRange: Int, endRange: Int): Boolean = actual != null && actual.length >= startRange && actual.length <= endRange)
    val HAS_LINE_COUNT = expectThatActualIs("with a line count of",
            fun(actual: CharSequence?, expected: Int): Boolean {
                val reader = LineNumberReader(StringReader(actual.toString()))
                try {
                    while (reader.readLine() != null);
                } catch (e: IOException) {
                    throw InputStreamsException("Unable to count lines in $actual", e)
                }
                return reader.lineNumber == expected
            }
    )
    val HAS_SAME_SIZE = expectThatActualIs("the same size as",
            fun(actual: CharSequence?, expected: CharSequence): Boolean =
                    (actual == null && expected == null)
                            || (actual != null && expected != null && actual.length == expected.length)
    )

}