package net.serenitybdd.screenplay.ensure

import net.serenitybdd.screenplay.Actor
import net.thucydides.core.steps.StepEventBus

object CommonPreconditions {

    fun ensureActualNotNull(actual: Any?) {
        ensureNotNull("actual should not be null", actual)
    }

    fun ensureActualAndExpectedNotNull(actual: Any?, expected: Any?) {
        ensureNotNull("actual should not be null", actual)
        ensureNotNull("expected should not be null", expected)
    }

    fun ensureActualAndActorNotNull(actual: Any?, actor: Actor?) {
        ensureNotNull("actual should not be null", actual)
        ensureNotNull("actor should not be null", actor)
    }

    fun ensureActualAndRangeValues(actual: Any?, startRange: Any?, endRange: Any?) {
        ensureNotNull("actual should not be null", actual)
        ensureNotNull("start range should not be null", startRange)
        ensureNotNull("end range should not be null", endRange)
    }


    fun ensureNotNull(message: String, value: Any?) {
        if (value == null) {
            StepEventBus.getEventBus().takeScreenshot()
            throw AssertionError(message)
        }
    }

    fun ensureNotEmpty(message: String, list: Collection<Any>?) {
        if ((list == null) || (list.isEmpty())) {
            StepEventBus.getEventBus().takeScreenshot()
            throw AssertionError(message)
        }
    }

    fun ensureNoNullElementsIn(message: String, list: Collection<Any>) {
        if (list.any { it == null })  {
            StepEventBus.getEventBus().takeScreenshot()
            throw AssertionError(message)
        }
    }
}