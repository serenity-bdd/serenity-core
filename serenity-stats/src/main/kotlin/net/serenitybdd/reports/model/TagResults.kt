package net.serenitybdd.reports.model

import net.thucydides.core.guice.Injectors.getInjector
import net.thucydides.core.model.TestResult
import net.thucydides.core.model.TestTag
import net.thucydides.core.reports.TestOutcomes
import net.thucydides.core.reports.html.ReportNameProvider
import net.thucydides.core.reports.html.TagFilter
import net.thucydides.core.requirements.RequirementsService
import net.thucydides.core.util.EnvironmentVariables
import org.apache.commons.lang3.StringUtils


class TagResults(val testOutcomes: TestOutcomes) {

    val requirementsService = getInjector().getInstance<RequirementsService>(RequirementsService::class.java)
    val environmentVariables = getInjector().getInstance<EnvironmentVariables>(EnvironmentVariables::class.java)

    val tagFilter = TagFilter(environmentVariables)

    companion object {
        @JvmStatic
        fun from(testOutcomes: TestOutcomes) = TagResults(testOutcomes)
    }

    fun groupedByType(): List<TagResultSet> {
        return forAllTags()
                .filter { tagResult -> StringUtils.isNotEmpty(tagResult.tag.type) }
                .filter { tagResult -> tagFilter.shouldDisplayTagWithType(tagResult.tag.type) }
                .groupBy { tagResult -> tagResult.type }
                .map { (tagType, tagResults) -> TagResultSet(tagType, tagResults) }
                .sortedBy { tagResultSet -> tagResultSet.tagType }
    }



    fun forAllTags(): List<TagResult> =
            testOutcomes.tags
                    .filter { tag -> !requirementsService.requirementTypes.contains(tag.type) }
                    .map { tag ->
                        TagResult(tag,
                                ReportNameProvider().forTag(tag),
                                testOutcomes.withTag(tag).total,
                                testOutcomes.withTag(tag).result)
                    }
}

class TagResult(val tag: TestTag, val report: String, val count: Int, val result: TestResult) {
    val label = if (tag.type.equals("tag")) tag.name else "${tag.name} (${tag.type})"
    val type = if (tag.type.equals("tag")) "" else tag.type
    val color = BackgroundColor().inDarkforResult(result)
}

class TagResultSet(val tagType: String, val tagResults: List<TagResult>);