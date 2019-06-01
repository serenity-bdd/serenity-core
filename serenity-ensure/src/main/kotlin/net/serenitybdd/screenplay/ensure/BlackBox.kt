package net.serenitybdd.screenplay.ensure

object BlackBox {
    private val flightLog = ThreadLocal.withInitial { ArrayList<ResolvedAssertion>() }

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
    @JvmStatic fun reset() { flightLog.get().clear() }

    private fun asString(value: Any) = if (value is String) "\"$value\"" else value.toString()
}

class ResolvedAssertion(val actual: String, val expected: String)