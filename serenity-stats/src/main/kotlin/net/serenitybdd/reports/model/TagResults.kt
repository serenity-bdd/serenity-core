package net.serenitybdd.reports.model

import net.thucydides.core.guice.Injectors.getInjector
import net.thucydides.core.model.TestResult
import net.thucydides.core.model.TestTag
import net.thucydides.core.reports.TestOutcomes
import net.thucydides.core.reports.html.ReportNameProvider
import net.thucydides.core.requirements.RequirementsService


class TagResults(val testOutcomes: TestOutcomes) {

    val requirementsService = getInjector().getInstance<RequirementsService>(RequirementsService::class.java)

    companion object {
        @JvmStatic fun from(testOutcomes: TestOutcomes) = TagResults(testOutcomes)
    }

    fun forAllTags() : List<TagResult> =
        testOutcomes.tags
                .filter{ tag -> !requirementsService.requirementTypes.contains(tag.type) }
                .map { tag -> TagResult(tag,
                                        ReportNameProvider().forTag(tag),
                                        testOutcomes.withTag(tag).total,
                                        testOutcomes.withTag(tag).result) }
}

class TagResult(val tag: TestTag, val report: String, val count : Int, val result : TestResult) {
    val label = if (tag.type.equals("tag")) tag.name else "${tag.name} (${tag.type})"
    val color = BackgroundColor().inDarkforResult(result)
}