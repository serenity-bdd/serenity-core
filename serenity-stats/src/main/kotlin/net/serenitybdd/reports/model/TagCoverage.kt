package net.serenitybdd.reports.model

import net.thucydides.core.model.TestResult
import net.thucydides.core.model.TestResult.*
import net.thucydides.core.model.TestTag
import net.thucydides.core.reports.TestOutcomes
import net.thucydides.core.reports.html.ReportNameProvider
import net.thucydides.core.reports.html.ResultIconFormatter
import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.NameConverter.humanize
import org.apache.commons.lang3.StringUtils


class TagCoverage(val environmentVariables: EnvironmentVariables, val testOutcomes: TestOutcomes) {

    companion object {
        @JvmStatic
        fun from(testOutcomes: TestOutcomes) = TagCoverageBuilder(testOutcomes)
    }
}

class TagCoverageBuilder(val testOutcomes: TestOutcomes, val tagsToDisplay: Collection<TestTag>) {

    constructor(testOutcomes: TestOutcomes) : this(testOutcomes, setOf())

    fun forTagTypes(displayedTagTypes: List<String>): List<CoverageByTagType> {

        return displayedTagTypes.map { displayedTagType ->
            CoverageByTagType(displayedTagType.capitalize(),
                    humanize(displayedTagType),
                    testOutcomes.withTagType(displayedTagType),
                    tagsToDisplay.filter{ t -> t.type.equals(displayedTagType) })
        }.filter { coverage -> coverage.tagCoverage.isNotEmpty() }
    }

    fun showingTags(tagsOfType: Collection<TestTag>): TagCoverageBuilder {
        return TagCoverageBuilder(testOutcomes, tagsOfType)
    }
}

class CoverageByTagType(val tagType: String, val tagTitle : String, val testOutcomes: TestOutcomes, tagsToDisplay: Collection<TestTag>) {
    val tagCoverage = coverageForEachTagOfType(tagType, testOutcomes, tagsToDisplay)

    private fun coverageForEachTagOfType(tagType: String,
                                         testOutcomes: TestOutcomes,
                                         tagsToDisplay: Collection<TestTag>): Collection<CoverageByTag> {
        val coverageFromTestOutcomes = testOutcomes.getTagsOfType(tagType).map { testTag -> coverageFor(testTag) }

        val zeroCoverageForUncoveredTags = uncoveredTags(tagsToDisplay, coverageFromTestOutcomes)

        var completeCoverage = mutableListOf<CoverageByTag>()
        completeCoverage.addAll(coverageFromTestOutcomes)

        zeroCoverageForUncoveredTags.filter { zeroCoverage -> coverageNotIncludedIn(zeroCoverage, completeCoverage) }
                                    .forEach { newCoverage -> completeCoverage.add(newCoverage) }

        return completeCoverage
    }

    private fun coverageNotIncludedIn(newCoverage: CoverageByTag, completeCoverage: MutableList<CoverageByTag>): Boolean {
        return completeCoverage.count { coverage -> coverage.tagName == newCoverage.tagName } == 0
    }

    private fun uncoveredTags(tagsToDisplay: Collection<TestTag>, coverageFromTestOutcomes: List<CoverageByTag>): List<CoverageByTag> {
        val uncoveredTags = tagsToDisplay.filter { tag -> noCoverageFor(tag, coverageFromTestOutcomes) }
        val zeroCoverageForUncoveredTags = uncoveredTags.map { tag -> coverageFor(tag) }
        return zeroCoverageForUncoveredTags
    }

    private fun noCoverageFor(tag: TestTag, coverageFromTestOutcomes: List<CoverageByTag>): Boolean =
            coverageFromTestOutcomes.count { coverage -> StringUtils.equalsIgnoreCase(coverage.tagName, tag.name) } == 0

    private fun coverageFor(testTag: TestTag): CoverageByTag {
        val testOutcomesForTag = testOutcomes.withTag(testTag)
        val successRate = testOutcomesForTag.formattedPercentage.withResult(SUCCESS, 0)

        return CoverageByTag(
                humanize(shortened(testTag.name)),
                testOutcomesForTag.testCount,
                successRate,
                testOutcomesForTag.result,
                ReportNameProvider().forTag(testTag),
                countByResultLabelFrom(testOutcomesForTag),
                percentageByResultFrom(testOutcomesForTag)
        )
    }

    private fun shortened(name: String): String = name.substringAfterLast("/")
}

class CoverageByTagResult(val tagName: String,
                          val testCount: Int,
                          val successRate: String,
                          val result: TestResult,
                          val report: String,
                          val countByResult: Map<String, Int>,
                          val percentageByResult: Map<String, Double>) {
    val resultClass = result.name.toLowerCase()
    val resultIcon = ResultIconFormatter().forResult(result)

    fun percentageForResult(result: TestResult): Double = if (percentageByResult[result.toString()] == null) 0.0 else percentageByResult[result.toString()]!!
    fun countForResult(result: TestResult): Int = if (countByResult[result.toString()] == null) 0 else countByResult[result.toString()]!!

    fun getCoverageSegments(): List<CoverageSegment> =
            listOf(SUCCESS, PENDING, IGNORED, SKIPPED, FAILURE, ERROR, COMPROMISED)
                    .filter { percentageForResult(it) > 0 }
                    .map { result -> CoverageSegment(percentageForResult(result), countForResult(result), result) }

}

class CoverageByTag(val tagName: String,
                    val testCount: Int,
                    val successRate: String,
                    val result: TestResult,
                    val report: String,
                    val countByResult: Map<String, Int>,
                    val percentageByResult: Map<String, Double>) {
    val resultClass = result.name.toLowerCase()
    val resultIcon = ResultIconFormatter().forResult(result)

    fun percentageForResult(result: TestResult): Double = if (percentageByResult[result.toString()] == null) 0.0 else percentageByResult[result.toString()]!!
    fun countForResult(result: TestResult): Int = if (countByResult[result.toString()] == null) 0 else countByResult[result.toString()]!!

    fun getCoverageSegments(): List<CoverageSegment> =
            listOf(SUCCESS, PENDING, IGNORED, SKIPPED, FAILURE, ERROR, COMPROMISED)
                    .filter { percentageForResult(it) > 0 }
                    .map { result -> CoverageSegment(percentageForResult(result), countForResult(result), result) }

}

class CoverageSegment(val percentage: Double, val count: Int, val result: TestResult) {
    val color = BackgroundColor().forResult(result)
    val roundedPercentage = Math.round(percentage);
    val title = "${roundedPercentage}% ${result.toString().toLowerCase()}"
}
