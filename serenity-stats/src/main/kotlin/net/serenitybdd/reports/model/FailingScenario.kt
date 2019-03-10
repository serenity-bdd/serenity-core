package net.serenitybdd.reports.model

import net.thucydides.core.model.TestResult

/**
 * A summary of a failing scenario to appear in the eamil reports
 */
class ScenarioSummary(val title: String, testResult: TestResult, val reportName: String, conciseErrorMessage: String) {
    val result = testResult.toString().toLowerCase()
    val errorMessage = removeNoiseFrom(conciseErrorMessage)

    private fun removeNoiseFrom(conciseErrorMessage: String): String =
            conciseErrorMessage.substringBefore("net.serenitybdd")  // Noisy exceptions
                                .substringBefore("(Session info:")  // WebDriver noise
                                .trim()
}