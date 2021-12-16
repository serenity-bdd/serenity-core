package net.serenitybdd.reports.model

import net.thucydides.core.model.TestResult

/*
"'rgba(153,204,51,0.5)'",      // PASSING
"'rgba(165, 199, 238, 0.5)'",  // PENDING
"'rgba(168, 168, 168, 0.5)'",  // IGNORED
"'rgba(238, 224, 152, 0.75)'", // SKIPPED
"'rgba(255, 153, 102, 0.5)'",  // ABORTED
"'rgba(255, 22, 49, 0.5)'",    // FAILED
"'rgba(255, 97, 8, 0.5)'",     // ERROR
"'rgba(255, 104, 255, 0.5)'"   // COMPROMISED
*/
class BackgroundColor {
    companion object {
        val RESULT_BACKGROUND_COLORS = hashMapOf(
                TestResult.SUCCESS to "#c5df91",
                TestResult.PENDING to "#cbddea",
                TestResult.IGNORED to "#cccdc7",
                TestResult.SKIPPED to "#e3c281",
                TestResult.FAILURE to "#fd938e",
                TestResult.ABORTED to "#f9c7a7",
                TestResult.ERROR to "#fab186",
                TestResult.COMPROMISED to "#fab0f3"
        )

        val DARKER_RESULT_BACKGROUND_COLORS = hashMapOf(
            TestResult.SUCCESS to "#b0cf73",
            TestResult.PENDING to "#a5c7ed",
            TestResult.IGNORED to "#a8a8a8",
            TestResult.SKIPPED to "#e3c281",
            TestResult.FAILURE to "#fd938e",
            TestResult.ABORTED to "#ff9966",
            TestResult.ERROR to "#fe6c2d",
            TestResult.COMPROMISED to "#fe6eec",
        )
    }
    fun forResult(result : TestResult) : String = RESULT_BACKGROUND_COLORS[result]!!
    fun forResult(result : String) : String = RESULT_BACKGROUND_COLORS[TestResult.valueOf(result.uppercase())]!!
    fun inDarkforResult(result : TestResult) : String = DARKER_RESULT_BACKGROUND_COLORS[result]!!
}
