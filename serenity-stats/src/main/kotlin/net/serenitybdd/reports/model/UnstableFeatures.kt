package net.serenitybdd.reports.model

import net.thucydides.core.model.DataTableRow
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.model.TestResult.*
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

    fun withMaxOf(maxEntries: Int): List<UnstableFeature> {
        return testOutcomes.unsuccessfulTests.outcomes
                .groupBy { outcome -> defaultStoryNameOr(outcome.userStory?.displayName) }
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

    private fun defaultStoryNameOr(displayName: String?): String = if (displayName != null) displayName else "Undefined Story"

    private fun unsuccessfulOutcomesIn(testOutcomes: TestOutcomes) : Int {
        return testOutcomes.outcomes.map { unsuccessfulOutcomesIn(it) }.sum()
    }

    private fun unsuccessfulOutcomesIn(outcome : TestOutcome) : Int {
        if (outcome.isDataDriven) {
            return outcome.dataTable.rows.count(this::isUnsuccessful)
        }
        return if (outcome.isError || outcome.isCompromised || outcome.isFailure) 1 else 0
    }

    private fun isUnsuccessful(row: DataTableRow) = row.result == FAILURE || row.result == ERROR || row.result == COMPROMISED

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
