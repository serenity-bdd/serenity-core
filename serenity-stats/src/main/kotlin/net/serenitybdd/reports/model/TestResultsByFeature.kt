package net.serenitybdd.reports.model

import net.thucydides.model.domain.TestOutcome
import net.thucydides.model.reports.TestOutcomes

class TestResultsByFeature(val featureName: String,
                           val issues: List<String>,
                           val scenarios: List<ScenarioSummary>) {

    fun getDescription(): String = featureName + issueDescription()

    private fun issueDescription(): String = if (issues.isEmpty()) "" else " (" + issues.joinToString(",") + ")"

    companion object {
        fun from(testOutcomes: TestOutcomes): List<TestResultsByFeature> {
//            val outcomesGroupedByFeature = testOutcomes.tests.groupBy { it.userStory }
            val outcomesGroupedByFeature = testOutcomes.testCases.groupBy { it.userStory }
            return outcomesGroupedByFeature.keys.map { userStory ->
                TestResultsByFeature(userStory.name,
                        issuesIn(outcomesGroupedByFeature.getOrDefault(userStory, listOf())),
                        scenariosIn(outcomesGroupedByFeature.getOrDefault(userStory, listOf())))
            }.sortedBy { it.featureName }
        }

        private fun scenariosIn(testOutcomes: List<TestOutcome>): List<ScenarioSummary> =
                testOutcomes.map { outcome -> ScenarioSummary.ofAllScenariosIn(outcome) }

        private fun issuesIn(testOutcomes: List<TestOutcome>): List<String> =
                testOutcomes.map { outcome -> outcome.issues.toList() }.flatten().distinct()
    }
}
