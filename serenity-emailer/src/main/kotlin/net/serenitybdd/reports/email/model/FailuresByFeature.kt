package net.serenitybdd.reports.email.model

import net.thucydides.core.model.TestOutcome
import net.thucydides.core.reports.TestOutcomes

class FailuresByFeature(val featureName: String, val failures : List<TestOutcome>) {
    companion object {
        fun from(testOutcomes: TestOutcomes) : List<FailuresByFeature> {
            val failingOutcomesGroupedByFeature = testOutcomes.failingOrErrorTests.tests.groupBy { it.userStory }
            return failingOutcomesGroupedByFeature.keys.map {
                userStory -> FailuresByFeature(userStory.name, failingOutcomesGroupedByFeature.getOrDefault(userStory, listOf()))
            }.sortedBy { it.featureName }
        }
    }
}