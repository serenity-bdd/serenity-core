package net.serenitybdd.reports.email.model

import net.thucydides.core.model.TestResult
import net.thucydides.core.model.TestTag
import net.thucydides.core.reports.TestOutcomes
import net.thucydides.core.util.EnvironmentVariables


class TagCoverage(val environmentVariables: EnvironmentVariables, val testOutcomes: TestOutcomes) {

    companion object {
        fun from(testOutcomes: TestOutcomes) = TagCoverageBuilder(testOutcomes)
    }
}

class TagCoverageBuilder(val testOutcomes: TestOutcomes) {
    fun forTagTypes(displayedTagTypes : List<String>) :  List<CoverageByTagType> {
        return displayedTagTypes.map {
            displayedTagType -> CoverageByTagType(displayedTagType.capitalize(), testOutcomes.withTagType(displayedTagType))
        }
    }
}

class CoverageByTagType(val tagType: String, val testOutcomes: TestOutcomes) {
    val tagCoverage = coverageForEachTagOfType(tagType, testOutcomes)

    private fun coverageForEachTagOfType(tagType: String, testOutcomes: TestOutcomes): List<CoverageByTag> {
        return testOutcomes.getTagsOfType(tagType).map { testTag -> coverageFor(testTag) }
    }

    private fun coverageFor(testTag : TestTag) : CoverageByTag {
        val testOutcomesForTag = testOutcomes.withTag(testTag)
        val successRate = testOutcomesForTag.formattedPercentage.withResult(TestResult.SUCCESS,0)
        return CoverageByTag(shortened(testTag.name).capitalize(),
                             testOutcomesForTag.testCount,
                             successRate,
                             countByResultLabelFrom(testOutcomesForTag),
                             percentageByResultFrom(testOutcomesForTag)
        )
    }

    private fun shortened(name: String): String = name.substringAfterLast("/")
}

class CoverageByTag(val tagName: String,
                    val testCount: Int,
                    val successRate: String,
                    val countByResult: Map<String, Int>,
                    val percentageByResult: Map<String, Int>)