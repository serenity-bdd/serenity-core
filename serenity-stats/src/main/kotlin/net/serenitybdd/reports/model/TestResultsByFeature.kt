package net.serenitybdd.reports.model

import net.thucydides.core.model.TestOutcome
import net.thucydides.core.reports.TestOutcomes

class TestResultsByFeature(val featureName: String, val scenarios: List<ScenarioSummary>) {
    companion object {
        fun from(testOutcomes: TestOutcomes): List<TestResultsByFeature> {
            val outcomesGroupedByFeature = testOutcomes.tests.groupBy { it.userStory }
            return outcomesGroupedByFeature.keys.map { userStory ->
                TestResultsByFeature(userStory.name,
                        scenariosIn(outcomesGroupedByFeature.getOrDefault(userStory, listOf())))
            }.sortedBy { it.featureName }
        }

        private fun scenariosIn(testOutcomes: List<TestOutcome>): List<ScenarioSummary> =
                testOutcomes.map { outcome -> ScenarioSummary.ofFailingScenariosIn(outcome) }
    }
}
