package net.serenitybdd.reports.model

import net.thucydides.core.model.Story
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.reports.TestOutcomes
import net.thucydides.core.reports.html.ReportNameProvider
import net.thucydides.core.requirements.ParentRequirementProvider
import net.thucydides.core.requirements.model.Requirement
import java.util.*

class UnstableFeatures{

    companion object {
        @JvmStatic fun from(testOutcomes: TestOutcomes) = UnstableFeaturesBuilder(testOutcomes)
    }
}

class UnstableFeaturesBuilder(val testOutcomes: TestOutcomes) {

    var parentNameProvider : ParentRequirementProvider = DummyParentRequirementProvider()

    fun withRequirementsFrom(parentNameProvider : ParentRequirementProvider) : UnstableFeaturesBuilder {
        this.parentNameProvider = parentNameProvider
        return this
    }

    fun withMaxOf(maxEntries: Int): List<UnstableFeature> =
            testOutcomes.failingOrErrorTests.outcomes
                    .groupBy { outcome -> outcome.userStory }
                    .map { (userStory, outcomes) -> UnstableFeature(userStory.displayName,
                                                                    outcomes.size,
                                                                    percentageFailures(outcomes.size, userStory, testOutcomes),
                                                                    featureReport(outcomes[0])) }
                    .sortedByDescending { unstableFeature -> unstableFeature.failurePercentage }
                    .take(maxEntries)

    private fun percentageFailures(failingScenarios: Int, userStory: Story, testOutcomes: TestOutcomes): Int {
        val totalScenarios = testOutcomes.withTag(userStory.asTag()).total
        return failingScenarios * 100 / totalScenarios
    }

    fun featureReport(outcome : TestOutcome) : String {

        val parentRequirement = parentNameProvider.getParentRequirementFor(outcome)

        if (!parentRequirement.isPresent) { return "#" }

        return ReportNameProvider().forRequirement(parentRequirement.get())
    }

}

class DummyParentRequirementProvider : ParentRequirementProvider {
    override fun getParentRequirementFor(testOutcome: TestOutcome?): Optional<Requirement> = Optional.empty()
}

class UnstableFeature(val name: String, val failureCount: Int, val failurePercentage: Int, val report: String)
