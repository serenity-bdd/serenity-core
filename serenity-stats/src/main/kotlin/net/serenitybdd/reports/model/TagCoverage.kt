package net.serenitybdd.reports.model

import net.thucydides.core.model.TestResult
import net.thucydides.core.model.TestResult.*
import net.thucydides.core.model.TestTag
import net.thucydides.core.reports.TestOutcomes
import net.thucydides.core.reports.html.ReportNameProvider
import net.thucydides.core.reports.html.ResultIconFormatter
import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.NameConverter.humanize


class TagCoverage(val environmentVariables: EnvironmentVariables, val testOutcomes: TestOutcomes) {

    companion object {
        @JvmStatic fun from(testOutcomes: TestOutcomes) = TagCoverageBuilder(testOutcomes)
    }
}

class TagCoverageBuilder(val testOutcomes: TestOutcomes) {
    fun forTagTypes(displayedTagTypes : List<String>) :  List<CoverageByTagType> {
        return displayedTagTypes.map {
            displayedTagType -> CoverageByTagType(displayedTagType.capitalize(), testOutcomes.withTagType(displayedTagType))
        }.filter { coverage -> coverage.tagCoverage.isNotEmpty() }
    }
}

class CoverageByTagType(val tagType: String, val testOutcomes: TestOutcomes) {
    val tagCoverage = coverageForEachTagOfType(tagType, testOutcomes)

    private fun coverageForEachTagOfType(tagType: String, testOutcomes: TestOutcomes): List<CoverageByTag> {
        return testOutcomes.getTagsOfType(tagType).map { testTag -> coverageFor(testTag) }
    }

    private fun coverageFor(testTag : TestTag) : CoverageByTag {
        val testOutcomesForTag = testOutcomes.withTag(testTag)
        val successRate = testOutcomesForTag.formattedPercentage.withResult(SUCCESS,0)

        return CoverageByTag(humanize(shortened(testTag.name)),
                             testOutcomesForTag.testCount,
                             successRate,
                             testOutcomesForTag.result,
                             ReportNameProvider().forTag(testTag),
                             countByResultLabelFrom(testOutcomesForTag),
                             percentageByResultLabelFrom(testOutcomesForTag)
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
                    val percentageByResult: Map<String, Int>) {
    val resultClass = result.name.toLowerCase()
    val resultIcon = ResultIconFormatter().forResult(result)

    fun percentageForResult(result : TestResult) : Int = if (percentageByResult[result.toString()] == null) 0 else  percentageByResult[result.toString()]!!
    fun countForResult(result : TestResult) : Int = if (countByResult[result.toString()] == null) 0 else countByResult[result.toString()]!!

    fun getCoverageSegments() : List<CoverageSegment> =
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
                    val percentageByResult: Map<String, Int>) {
    val resultClass = result.name.toLowerCase()
    val resultIcon = ResultIconFormatter().forResult(result)

    fun percentageForResult(result : TestResult) : Int = if (percentageByResult[result.toString()] == null) 0 else  percentageByResult[result.toString()]!!
    fun countForResult(result : TestResult) : Int = if (countByResult[result.toString()] == null) 0 else countByResult[result.toString()]!!

    fun getCoverageSegments() : List<CoverageSegment> =
            listOf(SUCCESS, PENDING, IGNORED, SKIPPED, FAILURE, ERROR, COMPROMISED)
                    .filter { percentageForResult(it) > 0 }
                    .map { result -> CoverageSegment(percentageForResult(result), countForResult(result), result) }

}

class CoverageSegment(val percentage: Int, val count: Int, val result: TestResult) {
    val color = BackgroundColor().forResult(result)
    val title = "${percentage}% ${result.toString().toLowerCase()}"
}
