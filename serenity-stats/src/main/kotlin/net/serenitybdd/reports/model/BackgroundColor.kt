package net.serenitybdd.reports.model

import net.thucydides.core.model.TestResult

class BackgroundColor {
    companion object {
        val RESULT_BACKGROUND_COLORS = hashMapOf(
                TestResult.SUCCESS to "#99cc33",
                TestResult.PENDING to "#A5C7EE",
                TestResult.IGNORED to "darkgrey",
                TestResult.SKIPPED to "#EEE098",
                TestResult.FAILURE to "#ff1631",
                TestResult.ABORTED to "#FF9966",
                TestResult.ERROR to "#ff6108",
                TestResult.COMPROMISED to "#ff68ff"
        )

        val DARKER_RESULT_BACKGROUND_COLORS = hashMapOf(
                TestResult.SUCCESS to "#89b72d",
                TestResult.PENDING to "#6884A6",
                TestResult.IGNORED to "#777777",
                TestResult.SKIPPED to "#B2A671c",
                TestResult.ABORTED to "#e5895b",
                TestResult.FAILURE to "#f8001f",
                TestResult.ERROR to "darkorange",
                TestResult.COMPROMISED to "fuchsia"
        )

    }
    fun forResult(result : TestResult) : String = RESULT_BACKGROUND_COLORS[result]!!
    fun forResult(result : String) : String = RESULT_BACKGROUND_COLORS[TestResult.valueOf(result.uppercase())]!!
    fun inDarkforResult(result : TestResult) : String = DARKER_RESULT_BACKGROUND_COLORS[result]!!
}
