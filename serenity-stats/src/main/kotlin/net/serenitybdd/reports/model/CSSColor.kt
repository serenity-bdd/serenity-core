package net.serenitybdd.reports.model

import net.thucydides.model.domain.TestResult

class CSSColor {
    companion object {
        val RESULT_CSS_COLORS = hashMapOf(
                TestResult.SUCCESS to "#30cb23",
                TestResult.PENDING to "#A2C4EC",
                TestResult.IGNORED to "darkgrey",
                TestResult.SKIPPED to "#EEE098",
                TestResult.ABORTED to "#FF9966",
                TestResult.FAILURE to "#f8001f",
                TestResult.ERROR to "darkorange",
                TestResult.COMPROMISED to "fuchsia"
        )
    }
    fun forResult(result : TestResult) : String = RESULT_CSS_COLORS[result]!!
}
