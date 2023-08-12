package net.serenitybdd.reports.model

import net.thucydides.model.domain.TestOutcome
import net.thucydides.model.reports.TestOutcomes

class FailuresByFeature(val featureName: String, val failures: List<ScenarioSummary>) {
    companion object {
        fun from(testOutcomes: TestOutcomes): List<FailuresByFeature> {
            val failingOutcomesGroupedByFeature = testOutcomes.unsuccessfulTests.tests.groupBy { it.userStory }
            return failingOutcomesGroupedByFeature.keys.map { userStory ->
                FailuresByFeature(userStory.name, failingScenariosIn(failingOutcomesGroupedByFeature.getOrDefault(userStory, listOf())))
            }.sortedBy { it.featureName }
        }

        private fun failingScenariosIn(testOutcomes: List<TestOutcome>): List<ScenarioSummary> {
            return testOutcomes.map { ScenarioSummary.ofFailingScenariosIn(it) }
        }
    }
}
