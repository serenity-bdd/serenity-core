package net.serenitybdd.screenplay.ensure

import net.serenitybdd.screenplay.Actor

class BooleanEnsure(override val value: KnowableValue<Boolean?>,
                   comparator: Comparator<Boolean>,
                   expectedDescription: String = descriptionOf(value)) : ComparableEnsure<Boolean>(value, comparator, expectedDescription) {

    constructor(value: KnowableValue<Boolean?>) : this(value, Comparator.naturalOrder<Boolean>())

    constructor(value: KnowableValue<Boolean?>, valueDescription: String) : this(value, Comparator.naturalOrder<Boolean>(), valueDescription)

    constructor(value: Boolean?) : this(
        KnownValue<Boolean?>(value, if (value == null) "<null>" else "\"$value\""),
        java.util.Comparator.naturalOrder<Boolean>()
    )
    
    fun isTrue() = PerformablePredicate<KnowableValue<Boolean?>?>(value, IS_TRUE, isNegated(), descriptionOf(value))
    fun isFalse() = PerformablePredicate<KnowableValue<Boolean?>?>(value, IS_FALSE, isNegated(), descriptionOf(value))
//
//    fun <T> descriptionOf(value: KnowableValue<T?>): String {
//        when(value) {
//            is KnowableBooleanValue<*> -> {
//                if (value.value is HasSubject) {
//                    return value.value.subject()
//                }
//            }
//            is KnowableStringValue<*> -> {
//                if (value.value is HasSubject) {
//                    return value.value.subject()
//                }
//            }
//            is KnowableBigDecimalValue<*> -> {
//                if (value.value is HasSubject) {
//                    return value.value.subject()
//                }
//            }
//            is KnowableDoubleValue<*> -> {
//                if (value.value is HasSubject) {
//                    return value.value.subject()
//                }
//            }
//            is KnowableFloatValue<*> -> {
//                if (value.value is HasSubject) {
//                    return value.value.subject()
//                }
//            }
//            is KnowableIntValue<*> -> {
//                if (value.value is HasSubject) {
//                    return value.value.subject()
//                }
//            }
//            is KnowableLocalDateValue<*> -> {
//                if (value.value is HasSubject) {
//                    return value.value.subject()
//                }
//            }
//            is KnowableLocalTimeValue<*> -> {
//                if (value.value is HasSubject) {
//                    return value.value.subject()
//                }
//            }
//        }
//        return "a value"
//    }

//    fun descriptionOf(value: KnowableValue<Boolean?>): String {
//        if (value is KnowableBooleanValue<*>) {
//            if (value.value is HasSubject) {
//                return value.value.subject()
//            }
//        }
//        return "a value"
//    }

    override fun not(): BooleanEnsure = negate() as BooleanEnsure

    val IS_TRUE = expectThatActualIs("true",
            fun(actor: Actor?, actual: KnowableValue<Boolean?>?): Boolean {
                if (actual == null || actor == null) return false;

                val resolvedValue = actual(actor)
                BlackBox.logAssertion(resolvedValue, "true")
                return resolvedValue ?: false
            })

    val IS_FALSE = expectThatActualIs("false",
            fun(actor: Actor?, actual: KnowableValue<Boolean?>?): Boolean {
                if (actual == null || actor == null) return true;

                val resolvedValue = actual(actor)
                BlackBox.logAssertion(resolvedValue, "false")
                return if (resolvedValue == null) true else !resolvedValue
            })
}
