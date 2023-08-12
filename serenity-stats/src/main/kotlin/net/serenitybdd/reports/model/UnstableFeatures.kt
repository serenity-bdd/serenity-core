package net.serenitybdd.reports.model

import net.thucydides.model.domain.TestOutcome
import net.thucydides.model.reports.TestOutcomes
import net.thucydides.model.reports.html.ReportNameProvider
import net.thucydides.model.requirements.ParentRequirementProvider
import net.thucydides.model.requirements.model.Requirement
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

    fun withMaxOf(maxEntries: Int): List<UnstableFeature> {
        return testOutcomes.unsuccessfulTests.outcomes
                .groupBy { outcome -> defaultStoryNameOr(outcome.userStory.displayName) }
                .map { (userStoryName, outcomes) ->
                    UnstableFeature(userStoryName,
                            outcomes.size,
                            percentageFailures(outcomes.size, userStoryName, testOutcomes),
                            featureReport(outcomes[0]))
                }
                .sortedWith(compareByDescending<UnstableFeature> { it.failurePercentage }
                            .thenByDescending { it.failureCount })
                .take(maxEntries)
    }

    private fun defaultStoryNameOr(displayName: String?): String = displayName ?: "Undefined Story"

    private fun percentageFailures(failingScenarios: Int, userStoryName: String, testOutcomes: TestOutcomes): Int {
        val totalScenarios = TestOutcomes.of(testOutcomes.outcomes.filter {
                                                outcome -> userStoryName == outcome.userStory?.displayName }
                                            ).total
        return if (totalScenarios == 0) 0 else failingScenarios * 100 / totalScenarios
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
