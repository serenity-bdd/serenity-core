package net.serenitybdd.screenplay.ensure

import net.serenitybdd.screenplay.Actor

class BooleanEnsure(override val value: KnowableValue<Boolean?>) : UICommonEnsure<Boolean?, Boolean>(value) {

    constructor(value: Boolean?) : this(KnownValue(value, value.toString()))

    fun isTrue() = PerformablePredicate<KnowableValue<Boolean?>?>(value, IS_TRUE, isNegated())
    fun isFalse() = PerformablePredicate<KnowableValue<Boolean?>?>(value, IS_FALSE, isNegated())

    override fun not(): BooleanEnsure = negate() as BooleanEnsure

    val IS_TRUE = expectThatActualIs("true",
            fun(actor: Actor?, actual: KnowableValue<Boolean?>?): Boolean {
                if (actual == null || actor == null) return false;

                val resolvedValue = actual(actor)
                return if (resolvedValue == null) false else resolvedValue
            })

    val IS_FALSE = expectThatActualIs("false",
            fun(actor: Actor?, actual: KnowableValue<Boolean?>?): Boolean {
                if (actual == null || actor == null) return true;

                val resolvedValue = actual(actor)
                return if (resolvedValue == null) true else !resolvedValue
            })
}
