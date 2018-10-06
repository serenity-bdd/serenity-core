package net.serenitybdd.reports.model

import net.thucydides.core.model.TestOutcome
import net.thucydides.core.model.TestResult
import net.thucydides.core.reports.TestOutcomes

class FailuresByFeature(val featureName: String, val failures: List<FailingScenario>) {
    companion object {
        fun from(testOutcomes: TestOutcomes): List<FailuresByFeature> {
            val failingOutcomesGroupedByFeature = testOutcomes.failingOrErrorTests.tests.groupBy { it.userStory }
            return failingOutcomesGroupedByFeature.keys.map { userStory ->
                FailuresByFeature(userStory.name,
                        failingScenariosIn(failingOutcomesGroupedByFeature.getOrDefault(userStory, listOf())))
            }.sortedBy { it.featureName }
        }

        private fun failingScenariosIn(testOutcomes: List<TestOutcome>): List<FailingScenario> =
                testOutcomes.map { outcome ->
                    FailingScenario(outcome.title,
                            outcome.result,
                            outcome.conciseErrorMessage)
                }
    }
}

/**
 * A summary of a failing scenario to appear in the eamil reports
 */
class FailingScenario(val title: String, testResult: TestResult, conciseErrorMessage: String) {
    val result = testResult.toString().toLowerCase()
    val errorMessage = removeNoiseFrom(conciseErrorMessage)

    private fun removeNoiseFrom(conciseErrorMessage: String): String =
            conciseErrorMessage.substringBefore("net.serenitybdd")  // Noisy exceptions
                                .substringBefore("(Session info:")  // WebDriver noise
                                .trim()
}