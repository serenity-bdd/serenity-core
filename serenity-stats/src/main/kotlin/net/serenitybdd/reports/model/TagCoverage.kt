package net.serenitybdd.reports.model

import net.serenitybdd.model.di.ModelInfrastructure
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration
import net.thucydides.model.ThucydidesSystemProperty.SERENITY_REPORT_HIDE_EMPTY_REQUIREMENTS
import net.thucydides.model.domain.TestResult
import net.thucydides.model.domain.TestResult.*
import net.thucydides.model.domain.TestTag
import net.thucydides.model.reports.TestOutcomes
import net.thucydides.model.reports.html.ReportNameProvider
import net.thucydides.model.reports.html.ResultIconFormatter
import net.thucydides.model.util.EnvironmentVariables
import net.thucydides.model.util.NameConverter.humanize
import org.apache.commons.lang3.StringUtils
import java.util.*


class TagCoverage(val environmentVariables: EnvironmentVariables, val testOutcomes: TestOutcomes) {

    companion object {
        @JvmStatic
        fun from(testOutcomes: TestOutcomes) =
            TagCoverageBuilder(testOutcomes, ModelInfrastructure.getEnvironmentVariables())
    }
}

class TagCoverageBuilder(
    val testOutcomes: TestOutcomes,
    val tagsToDisplay: Collection<TestTag>,
    val environmentVariables: EnvironmentVariables,
    var reportNameProvider: ReportNameProvider
) {

    constructor(testOutcomes: TestOutcomes, environmentVariables: EnvironmentVariables) : this(
        testOutcomes,
        setOf(),
        environmentVariables,
        ReportNameProvider()
    )

    var hideEmptyRequirements: Boolean = false

    init {
        hideEmptyRequirements = EnvironmentSpecificConfiguration.from(environmentVariables)
            .getBooleanProperty(SERENITY_REPORT_HIDE_EMPTY_REQUIREMENTS, true)
    }

    fun withReportNameProvider(reportNameProvider: ReportNameProvider): TagCoverageBuilder {
        this.reportNameProvider = reportNameProvider
        return this
    }

    fun forTagTypes(displayedTagTypes: List<String>): List<CoverageByTagType> {

        var coveragesByTagType = mutableListOf<CoverageByTagType>()

        for (displayedTagType in displayedTagTypes) {
            val testOutcomesWithTag = testOutcomes.withTagType(displayedTagType)
            if (shouldShow(testOutcomesWithTag)) {
                coveragesByTagType.add(
                    CoverageByTagType(
                        reportNameProvider,
                        displayedTagType.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                        humanize(displayedTagType),
                        testOutcomes.withTagType(displayedTagType),
                        tagsToDisplay.filter { t -> t.type.equals(displayedTagType) })
                )
            }
        }
        return coveragesByTagType
    }

    private fun shouldShow(testOutcomes: TestOutcomes): Boolean {
        val hideThis = hideEmptyRequirements && testOutcomes.isEmpty
        return !hideThis
    }


    private fun shouldShow(coverage: CoverageByTagType): Boolean {
        val hideThis = hideEmptyRequirements && coverage.testOutcomes.isEmpty
        return !hideThis
    }

    fun showingTags(tagsOfType: Collection<TestTag>): TagCoverageBuilder {
        return TagCoverageBuilder(testOutcomes, tagsOfType, environmentVariables, reportNameProvider)
    }
}

class CoverageByTagType(
    val reportNameProvider: ReportNameProvider,
    val tagType: String,
    val tagTitle: String,
    val testOutcomes: TestOutcomes,
    tagsToDisplay: Collection<TestTag>
) {
    val tagCoverage = coverageForEachTagOfType(tagType, testOutcomes, tagsToDisplay)
    val featureNamesAreUnique = eachFeatureNameIsUniqueIn(tagCoverage)

    private fun eachFeatureNameIsUniqueIn(tagCoverage: Collection<CoverageByTag>): Any {
        return tagCoverage.size == tagCoverage.map { coverage -> coverage.tagName }.distinct().size
    }

    fun renderedTestCountsWithStatus(result: String): String {
        val resultCounts = tagCoverage.map { coverageByTag ->
            coverageByTag.getCoverageSegments().find { segment ->
                segment.result.isEqualTo(result)
            }?.count ?: 0
        }
        return if (resultCounts.isEmpty()) "[]" else "[" + resultCounts.joinToString(",") + "]"
    }

    private fun coverageForEachTagOfType(
        tagType: String,
        testOutcomes: TestOutcomes,
        tagsToDisplay: Collection<TestTag>
    ): Collection<CoverageByTag> {
        val coverageFromTestOutcomes = testOutcomes.getTagsOfType(tagType)
            .map { testTag -> coverageFor(testTag) }
            .filter { coverage -> shouldShow(coverage) }

        val zeroCoverageForUncoveredTags = uncoveredTags(tagsToDisplay, coverageFromTestOutcomes)

        var completeCoverage = mutableListOf<CoverageByTag>()
        completeCoverage.addAll(coverageFromTestOutcomes)

        zeroCoverageForUncoveredTags.filter { zeroCoverage -> coverageNotIncludedIn(zeroCoverage, completeCoverage) }
            .forEach { newCoverage -> completeCoverage.add(newCoverage) }

        return completeCoverage
    }

    private fun shouldShow(coverage: CoverageByTag): Boolean {
        return coverage.testCount > 0
    }

    private fun coverageNotIncludedIn(
        newCoverage: CoverageByTag,
        completeCoverage: MutableList<CoverageByTag>
    ): Boolean {
        return completeCoverage.count { coverage -> coverage.tagName == newCoverage.tagName } == 0
    }

    private fun uncoveredTags(
        tagsToDisplay: Collection<TestTag>,
        coverageFromTestOutcomes: List<CoverageByTag>
    ): List<CoverageByTag> {
        val uncoveredTags = tagsToDisplay.filter { tag -> noCoverageFor(tag, coverageFromTestOutcomes) }
        val zeroCoverageForUncoveredTags = uncoveredTags.map { tag -> coverageFor(tag) }
        return zeroCoverageForUncoveredTags
    }

    private fun noCoverageFor(tag: TestTag, coverageFromTestOutcomes: List<CoverageByTag>): Boolean =
        coverageFromTestOutcomes.count { coverage -> StringUtils.equalsIgnoreCase(coverage.tagName, tag.name) } == 0

    private fun coverageFor(testTag: TestTag): CoverageByTag {
        val testOutcomesForTag = testOutcomes.withTag(testTag)
        val successRate = testOutcomesForTag.formattedPercentage.withResult(SUCCESS, 0)

        print(">> ReportNameProvider: " + reportNameProvider +  "tag: " + testTag + " report link: " + reportNameProvider.forRequirementOrTag(testTag) + "\n")

        return CoverageByTag(
            humanize(shortened(testTag.displayName)),
            humanize(parent(testTag.name)),
//            humanize(""),
            testOutcomesForTag.scenarioCount,
            testOutcomesForTag.testCaseCount,
            successRate,
            testOutcomesForTag.result,
            reportNameProvider.forRequirementOrTag(testTag),    //            ReportNameProvider().forRequirementOrTag(testTag),
            countByResultLabelFrom(testOutcomesForTag),
            percentageByResultFrom(testOutcomesForTag)
        )
    }

    private fun shortened(name: String): String = name.substringAfterLast("/")
    private fun parent(name: String): String = name.substringBeforeLast("/")
//    private fun parent(name: String): String = secondLastElement(name)

    private fun secondLastElement(s: String): String {
        val parts = s.split("/")
        return if (parts.size > 1) {
            parts[parts.size - 2]
        } else {
            ""
        }
    }
}

class CoverageByTag(
    val tagName: String,
    val parentName: String,
    val scenarioCount: Long,
    val testCount: Long,
    val successRate: String,
    val result: TestResult,
    val report: String,
    val countByResult: Map<String, Long>,
    val percentageByResult: Map<String, Double>
) {
    val resultClass = result.name.lowercase()
    val resultIcon = ResultIconFormatter().forResult(result)
    val resultName = result.name

    fun percentageForResult(result: TestResult): Double =
        if (percentageByResult[result.toString()] == null) 0.0 else percentageByResult[result.toString()]!!

    fun countForResult(result: TestResult): Long =
        if (countByResult[result.toString()] == null) 0 else countByResult[result.toString()]!!

    fun getCoverageSegments(): List<CoverageSegment> =
        listOf(SUCCESS, PENDING, IGNORED, SKIPPED, ABORTED, FAILURE, ERROR, COMPROMISED)
            .filter { percentageForResult(it) > 0 }
            .map { result -> CoverageSegment(percentageForResult(result), countForResult(result), result) }

}

class CoverageSegment(val percentage: Double, val count: Long, val result: TestResult) {
    val color = BackgroundColor().inDarkforResult(result)
    val roundedPercentage = Math.round(percentage)
    val title = "${roundedPercentage}% ${result.toString().lowercase()}"
}
