package net.serenitybdd.reports.model

import net.thucydides.core.model.TestResult

class BackgroundColor {
    companion object {
        val RESULT_BACKGROUND_COLORS = hashMapOf(
                TestResult.SUCCESS to "#30cb23",
                TestResult.PENDING to "#A2C4EC",
                TestResult.IGNORED to "darkgrey",
                TestResult.SKIPPED to "#EEE098",
                TestResult.FAILURE to "#f8001f",
                TestResult.ERROR to "darkorange",
                TestResult.COMPROMISED to "fuchsia"
        )

        val DARKER_RESULT_BACKGROUND_COLORS = hashMapOf(
                TestResult.SUCCESS to "#1D9918",
                TestResult.PENDING to "#6884A6",
                TestResult.IGNORED to "#777777",
                TestResult.SKIPPED to "#B2A671c",
                TestResult.FAILURE to "#f8001f",
                TestResult.ERROR to "darkorange",
                TestResult.COMPROMISED to "fuchsia"
        )

    }
    fun forResult(result : TestResult) : String = RESULT_BACKGROUND_COLORS[result]!!
    fun forResult(result : String) : String = RESULT_BACKGROUND_COLORS[TestResult.valueOf(result.toUpperCase())]!!
    fun inDarkforResult(result : TestResult) : String = DARKER_RESULT_BACKGROUND_COLORS[result]!!
}
