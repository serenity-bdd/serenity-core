package net.serenitybdd.screenplay.ensure.dates

import net.serenitybdd.screenplay.ensure.BiPerformableExpectation
import net.serenitybdd.screenplay.ensure.ComparableEnsure
import net.serenitybdd.screenplay.ensure.PerformableExpectation
import net.serenitybdd.screenplay.ensure.PerformablePredicate
import net.serenitybdd.screenplay.ensure.dates.DateExpectations.AFTER
import net.serenitybdd.screenplay.ensure.dates.DateExpectations.BEFORE
import net.serenitybdd.screenplay.ensure.dates.DateExpectations.DAY_OF_MONTH
import net.serenitybdd.screenplay.ensure.dates.DateExpectations.DAY_OF_WEEK
import net.serenitybdd.screenplay.ensure.dates.DateExpectations.MONTH
import net.serenitybdd.screenplay.ensure.dates.DateExpectations.YEAR
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
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.util.regex.Pattern

class DateEnsure(override val value: LocalDate?, comparator: Comparator<LocalDate>) : ComparableEnsure<LocalDate>(value, comparator) {

    constructor(value: LocalDate?) : this(value, Comparator.naturalOrder<LocalDate>())

    /**
     * Verifies that the actual {@code LocalDate} is before a specified date
     */
    fun isBefore(expected: LocalDate) = PerformableExpectation(value, BEFORE, expected, isNegated())

    /**
     * Verifies that the actual {@code LocalDate} is after a specified date
     */
    fun isAfter(expected: LocalDate) = PerformableExpectation(value, AFTER, expected, isNegated())

    /**
     * Verifies that the actual {@code LocalDate} is a given day of the week
     */
    fun isDayOfWeek(expected: DayOfWeek) = PerformableExpectation(value, DAY_OF_WEEK, expected, isNegated())

    /**
     * Verifies that the actual {@code LocalDate} is a given day of the month
     */
    fun isDayOfMonth(expected: Int) = PerformableExpectation(value, DAY_OF_MONTH, expected, isNegated())

    /**
     * Verifies that the actual {@code LocalDate} is a given  month
     */
    fun isInTheMonthOf(expected: Month) = PerformableExpectation(value, MONTH, expected, isNegated())

    /**
     * Verifies that the actual {@code LocalDate} is a given day of the month
     */
    fun isTheYear(expected: Int) = PerformableExpectation(value, YEAR, expected, isNegated())

    override fun hasValue(): DateEnsure = this
    override fun not(): DateEnsure = negate() as DateEnsure
    override fun usingComparator(comparator: Comparator<LocalDate>): DateEnsure {
        return DateEnsure(value, comparator)
    }
}
