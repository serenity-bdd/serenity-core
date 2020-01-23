package net.serenitybdd.screenplay.ensure

import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.ensure.CommonPreconditions.ensureActualAndActorNotNull

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month


class DateEnsure(override val value: KnowableValue<LocalDate?>, comparator: Comparator<LocalDate>) : ComparableEnsure<LocalDate>(value, comparator) {

    constructor(value: LocalDate?) : this(KnownValue<LocalDate?>(value, value.toString()), naturalOrder<LocalDate>())
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

    override fun not(): DateEnsure = negate() as DateEnsure
    override fun silently(): DateEnsure = super.silently() as DateEnsure

    override fun usingComparator(comparator: Comparator<LocalDate>): DateEnsure {
        return DateEnsure(value, comparator)
    }

    companion object {

        val BEFORE = expectThatActualIs("before",
                fun(actor: Actor?, actual: KnowableValue<LocalDate?>?, expected: LocalDate): Boolean {
                    ensureActualAndActorNotNull(actual, actor)
                    val actualValue = actual!!(actor!!) ?: return false
                    return actualValue.isBefore(expected)
                })

        val AFTER = expectThatActualIs("after",
                fun(actor: Actor?, actual: KnowableValue<LocalDate?>?, expected: LocalDate): Boolean {
                    ensureActualAndActorNotNull(actual, actor)
                    val actualValue = actual!!(actor!!) ?: return false
                    return actualValue.isAfter(expected)
                })

        val DAY_OF_WEEK = expectThatActualIs("on a",
                fun(actor: Actor?, actual: KnowableValue<LocalDate?>?, expected: DayOfWeek): Boolean {
                    ensureActualAndActorNotNull(actual, actor)
                    val actualValue = actual!!(actor!!) ?: return false
                    return expected == actualValue.dayOfWeek
                })

        val DAY_OF_MONTH = expectThatActualIs("on day of month",
                fun(actor: Actor?, actual: KnowableValue<LocalDate?>?, expected: Int): Boolean {
                    CommonPreconditions.ensureActualNotNull(actual)
                    val actualValue = actual!!(actor!!) ?: return false
                    return expected == actualValue.dayOfMonth
                })

        val MONTH = expectThatActualIs("in month",
                fun(actor: Actor?, actual: KnowableValue<LocalDate?>?, expected: Month): Boolean {
                    CommonPreconditions.ensureActualNotNull(actual)
                    val actualValue = actual!!(actor!!) ?: return false
                    return expected == actualValue.month
                })

        val YEAR = expectThatActualIs("in the year",
                fun(actor: Actor?, actual: KnowableValue<LocalDate?>?, expected: Int): Boolean {
                    CommonPreconditions.ensureActualNotNull(actual)
                    val actualValue = actual!!(actor!!) ?: return false
                    return expected == actualValue.year
                })
    }
}
