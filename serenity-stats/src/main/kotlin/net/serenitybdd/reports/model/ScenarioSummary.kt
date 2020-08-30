package net.serenitybdd.reports.model

import net.thucydides.core.model.TestOutcome
import net.thucydides.core.model.TestResult

/**
 * A summary of a failing scenario to appear in the email reports
 * A scenario may include multiple rows from an example table
 */
class ScenarioSummary(val title: String,
                      testResult: TestResult,
                      val reportName: String,
                      val results: List<ScenarioSummaryResult>) {
    val result = testResult.toString().toLowerCase()
    val color = CSSColor().forResult(testResult)
    val errorMessage = if (results.isEmpty()) "" else results[0].errorMessage
    val hasExamples = results.size > 1

    companion object {
        fun ofFailingScenariosIn(outcome: TestOutcome): ScenarioSummary =
                if (outcome.isDataDriven) {
                    ScenarioSummary(outcome.title, outcome.result, outcome.htmlReport, failingScenariosIn(outcome))
                } else {
                    from(outcome)
                }

        private fun failingScenariosIn(outcome: TestOutcome): List<ScenarioSummaryResult> =
            outcome.testSteps
                    .filter { it.isCompromised || it.isFailure || it.isError }
                    .map { ScenarioSummaryResult(it.result, it.conciseErrorMessage, it.description, it.exception?.errorType.orEmpty()) }


        fun from(outcome: TestOutcome): ScenarioSummary
                = ScenarioSummary(outcome.title,
                                  outcome.result,
                                  outcome.htmlReport,
                                  listOf(ScenarioSummaryResult(outcome.result, outcome.conciseErrorMessage, outcome.description.orEmpty(), outcome.testFailureErrorType)))
    }
}

class ScenarioSummaryResult(testResult: TestResult, conciseErrorMessage: String, val description: String, val testFailureErrorType : String) {
    val errorMessage = removeNoiseFrom(conciseErrorMessage)
    val result = testResult.toString().toLowerCase()
}

private fun removeNoiseFrom(conciseErrorMessage: String): String =
        conciseErrorMessage.substringBefore("net.serenitybdd")  // Noisy exceptions
                .substringBefore("(Session info:")  // WebDriver noise
                .trim()
