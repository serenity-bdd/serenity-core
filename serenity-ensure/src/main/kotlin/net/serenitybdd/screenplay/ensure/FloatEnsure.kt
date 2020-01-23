package net.serenitybdd.screenplay.ensure

import net.serenitybdd.screenplay.Actor


class FloatEnsure(override val value: KnowableValue<Float?>, comparator: Comparator<Float>) : ComparableEnsure<Float>(value, comparator) {

    constructor(value: Float?) : this(KnownValue<Float?>(value, value.toString()), naturalOrder<Float>())

    companion object {
        fun fromKnowable(value: KnowableValue<Float?>) : FloatEnsure = FloatEnsure(value, naturalOrder<Float>())
    }

    /**
     * Verifies that the actual {@code LocalDate} is before a specified date
     */
    fun isCloseTo(expected: Float, margin: Float) = PerformableExpectation(value, within(margin), expected, isNegated())
    override fun not(): FloatEnsure = negate() as FloatEnsure
    override fun silently(): FloatEnsure = super.silently() as FloatEnsure

    private fun within(margin: Float) = expectThatActualIs("close to (within a margin of " + margin + ")",
            fun(actor: Actor?, actual: KnowableValue<Float?>?, expected: Float): Boolean {
                CommonPreconditions.ensureActualNotNull(actual)
                val actualValue = actual!!(actor!!) ?: return false
                return actualValue >= (expected - margin) && actualValue <= (expected + margin)
            })
}

