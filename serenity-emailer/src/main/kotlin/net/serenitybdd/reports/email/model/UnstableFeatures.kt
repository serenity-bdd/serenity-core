package net.serenitybdd.reports.email.model

import net.thucydides.core.reports.TestOutcomes

class UnstableFeatures{

    companion object {
        fun from(testOutcomes: TestOutcomes) = UnstableFeaturesBuilder(testOutcomes)
    }
}

class UnstableFeaturesBuilder(val testOutcomes: TestOutcomes) {
    fun withMaxOf(maxEntries: Int): List<UnstableFeature> =
            testOutcomes.failingOrErrorTests.outcomes
                    .groupBy { outcome -> outcome.userStory }
                    .map { (userStory, outcomes) -> UnstableFeature(userStory.displayName, outcomes.size) }
                    .sortedByDescending { unstableFeature -> unstableFeature.failureCount }
                    .take(maxEntries)
}

class UnstableFeature(val name: String, val failureCount: Int)