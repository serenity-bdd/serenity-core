package net.serenitybdd.screenplay.ensure

import net.serenitybdd.screenplay.Actor


class DoubleEnsure(override val value: KnowableValue<Double?>, comparator: Comparator<Double>) : ComparableEnsure<Double>(value, comparator) {

    companion object {
        fun fromKnowable(value: KnowableValue<Double?>) : DoubleEnsure = DoubleEnsure(value, naturalOrder<Double>())
    }

    constructor(value: Double?) : this(KnownValue<Double?>(value, value.toString()), naturalOrder<Double>())

    /**
     * Verifies that the actual {@code LocalDate} is before a specified date
     */
    fun isCloseTo(expected: Double, margin: Double) = PerformableExpectation(value, within(margin), expected, isNegated())
    override fun not(): DoubleEnsure = negate() as DoubleEnsure
    override fun silently(): DoubleEnsure = super.silently() as DoubleEnsure

    fun within(margin: Double) = expectThatActualIs("close to(within a margin of " + margin + ")",
            fun(actor: Actor?, actual: KnowableValue<Double?>?, expected: Double): Boolean {
                CommonPreconditions.ensureActualNotNull(actual)
                val actualValue = actual!!(actor!!) ?: return false
                return actualValue >= (expected - margin) && actualValue <= (expected + margin)
            })
}

