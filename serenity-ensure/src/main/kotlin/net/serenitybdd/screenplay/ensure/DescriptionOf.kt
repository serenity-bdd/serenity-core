package net.serenitybdd.screenplay.ensure

fun <T> descriptionOf(value: KnowableValue<T?>): String {
    when(value) {
        is KnowableBooleanValue<*> -> {
            if (value.value is HasSubject) {
                return value.value.subject()
            }
        }
        is KnowableBooleanAnswer -> {
            return value.description
        }
        is KnowableStringValue<*> -> {
            if (value.value is HasSubject) {
                return value.value.subject()
            }
        }
        is KnowableStringAnswer -> {
            return value.description
        }
        is KnowableBigDecimalValue<*> -> {
            if (value.value is HasSubject) {
                return value.value.subject()
            }
        }
        is KnowableDoubleValue<*> -> {
            if (value.value is HasSubject) {
                return value.value.subject()
            }
        }
        is KnowableFloatValue<*> -> {
            if (value.value is HasSubject) {
                return value.value.subject()
            }
        }
        is KnowableIntValue<*> -> {
            if (value.value is HasSubject) {
                return value.value.subject()
            }
        }
        is KnowableLocalDateValue<*> -> {
            if (value.value is HasSubject) {
                return value.value.subject()
            }
        }
        is KnowableLocalTimeValue<*> -> {
            if (value.value is HasSubject) {
                return value.value.subject()
            }
        }
        is KnowableComparableAnswer -> {
            return value.description
        }
    }
    return "a value"
}