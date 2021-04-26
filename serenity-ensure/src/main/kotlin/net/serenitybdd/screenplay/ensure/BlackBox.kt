package net.serenitybdd.screenplay.ensure

object BlackBox {
    private val flightLog = ThreadLocal.withInitial { ArrayList<ResolvedAssertion>() }
    private var usingSoftAssertions = false
    private val softAssertions: MutableList<String> = mutableListOf()

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
        usingSoftAssertions = true
        softAssertions.clear()
    }

    fun endSoftAssertions() {
        usingSoftAssertions = false
        softAssertions.clear()
    }

    fun isUsingSoftAssertions() = usingSoftAssertions

    fun softlyAssert(softAssertion: String) {
        softAssertions.add(softAssertion)
    }

    fun reportAnySoftAssertions() {
        if (!softAssertions.isEmpty()) {
            val renderedErrorMessages = StringBuilder("SOFT ASSERTION FAILURES" + System.lineSeparator())
            softAssertions.forEachIndexed { index, error ->
                renderedErrorMessages.append("- ERROR ${index + 1}) ${normaliseSpacingIn(error)} ${System.lineSeparator()}")
            }
            endSoftAssertions();
            throw AssertionError(renderedErrorMessages)
        }
    }

    fun normaliseSpacingIn(message: String) = message.trim().replace("But got........","  But got.................")
}

class ResolvedAssertion(val actual: String, val expected: String)