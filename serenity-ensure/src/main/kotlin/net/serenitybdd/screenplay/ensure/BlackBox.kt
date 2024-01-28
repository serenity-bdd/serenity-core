package net.serenitybdd.screenplay.ensure

import net.thucydides.core.steps.StepEventBus
import kotlin.collections.ArrayList

object BlackBox {
    private val flightLog = ThreadLocal.withInitial { ArrayList<ResolvedAssertion>() }
    private var usingSoftAssertions : ThreadLocal<Boolean> = ThreadLocal.withInitial { false }
    private val softAssertions : ThreadLocal<MutableList<String>> = ThreadLocal.withInitial { mutableListOf() }

    fun logAssertion(actual: Any?, expected: Any?) {
        val actualAsString = if (actual == null) "<null>" else asString(actual)
        val expectedAsString = if (expected == null) "<null>" else asString(expected)

        flightLog.get().add(ResolvedAssertion(actualAsString, expectedAsString))
    }

    fun logAssertionValues(actual: Any?, expected: Any?) {
        val actualAsString = if (actual == null) "<null>" else actual.toString()
        val expectedAsString = if (expected == null) "<null>" else expected.toString()

        flightLog.get().add(ResolvedAssertion(actualAsString, expectedAsString))
    }

    fun hasLastEntry() = flightLog.get().isNotEmpty()
    fun lastEntry() = flightLog.get().last()

    @JvmStatic
    fun reset() {
        flightLog.get().clear()
    }

    private fun asString(value: Any) = if (value is String) "\"$value\"" else value.toString()

    fun startSoftAssertions() {
        usingSoftAssertions.set(true)
        StepEventBus.getEventBus().enableSoftAsserts()
        softAssertions.get().clear()
    }

    fun endSoftAssertions() {
        usingSoftAssertions.set(false);
        StepEventBus.getEventBus().disableSoftAsserts();
    }

    fun isUsingSoftAssertions() = usingSoftAssertions.get()

    fun softlyAssert(softAssertion: String) {
        softAssertions.get().add(softAssertion)
    }

//    fun renderedAssertionMessages(): String {
//        if (!softAssertions.get().isEmpty()) {
//            val renderedErrorMessages = StringBuilder("SOFT ASSERTION FAILURES" + System.lineSeparator())
//            softAssertions.get().forEachIndexed { index, error ->
//                renderedErrorMessages.append("- ERROR ${index + 1}) ${normaliseSpacingIn(error)} ${System.lineSeparator()}")
//            }
//            return renderedErrorMessages.toString();
//        } else {
//            return "";
//        }
//    }
//
//    fun renderedAssertionMessageForThisStep(): String {
//        if (!softAssertions.get().isEmpty()) {
//            return softAssertions.get().last.toString();
//        } else {
//            return "";
//        }
//    }

    fun reportAnySoftAssertions() {
        if (!softAssertions.get().isEmpty()) {
            val renderedErrorMessages = StringBuilder(
                System.lineSeparator()
                        + "ASSERTION ERRORS"
                        + System.lineSeparator()
                        + "----------------------"
                        + System.lineSeparator()
            )
            softAssertions.get().forEachIndexed { index, error ->
                renderedErrorMessages.append("- ERROR ${index + 1}) ${normaliseSpacingIn(error)} ${System.lineSeparator()}")
            }
            endSoftAssertions()
            takeScreenshot()
            softAssertions.get().clear();
            throw AssertionError(renderedErrorMessages)
        } else {
            endSoftAssertions()
        }
    }

    fun normaliseSpacingIn(message: String) = message.trim().replace("But got........", "  But got.................")
//    fun hasPendingSoftAssertions() = softAssertions.get().isNotEmpty()
}

class ResolvedAssertion(val actual: String, val expected: String)
