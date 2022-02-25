package net.serenitybdd.screenplay.ensure

import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.ensure.CommonPreconditions.ensureActualAndActorNotNull

import java.time.LocalTime


class TimeEnsure(override val value: KnowableValue<LocalTime?>, comparator: Comparator<LocalTime>) : ComparableEnsure<LocalTime>(value, comparator) {

    constructor(value: LocalTime?) : this(KnownValue<LocalTime?>(value, value.toString()), naturalOrder<LocalTime>())

    /**
     * Verifies that the actual {@code LocalTime} is before a specified date
     */
    fun isBefore(expected: LocalTime) = PerformableExpectation(value, BEFORE, expected, isNegated())

    /**
     * Verifies that the actual {@code LocalTime} is after a specified date
     */
    fun isAfter(expected: LocalTime) = PerformableExpectation(value, AFTER, expected, isNegated())

    override fun not(): TimeEnsure = negate() as TimeEnsure
    override fun silently(): TimeEnsure = super.silently() as TimeEnsure

    override fun usingComparator(comparator: Comparator<LocalTime>): TimeEnsure {
        return TimeEnsure(value, comparator)
    }

    companion object {

        val BEFORE = expectThatActualIs("before",
                fun(actor: Actor?, actual: KnowableValue<LocalTime?>?, expected: LocalTime): Boolean {
                    ensureActualAndActorNotNull(actual, actor)
                    val actualValue = actual!!(actor!!) ?: return false
                    return actualValue.isBefore(expected)
                })

        val AFTER = expectThatActualIs("after",
                fun(actor: Actor?, actual: KnowableValue<LocalTime?>?, expected: LocalTime): Boolean {
                    ensureActualAndActorNotNull(actual, actor)
                    val actualValue = actual!!(actor!!) ?: return false
                    return actualValue.isAfter(expected)
                })

    }
}
