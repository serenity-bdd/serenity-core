package net.serenitybdd.reports.model

import net.thucydides.core.model.TestResult
import net.thucydides.core.reports.TestOutcomes
import net.thucydides.core.reports.html.ReportNameProvider
import net.thucydides.core.reports.html.ResultIconFormatter
import net.thucydides.core.util.NameConverter.humanize
import org.apache.commons.lang3.StringUtils

class FrequentFailures {
    companion object {
        @JvmStatic
        fun from(testOutcomes: TestOutcomes) = FrequentFailuresBuilder(testOutcomes)
    }
}

class FrequentFailuresBuilder(val testOutcomes: TestOutcomes) {

    fun withMaxOf(maxEntries: Int): List<FrequentFailure> =
            testOutcomes.unsuccessfulTests.outcomes
                    .map { outcome -> ScenarioSummary.ofFailingScenariosIn(outcome).results }
                    .flatMap { it.toList() }
                    .filter { StringUtils.isNotEmpty(it.testFailureErrorType) }
                    .groupBy { it.testFailureErrorType }
                    .map { (error, outcomes) -> FrequentFailure(error, outcomes.size, testResultOf(outcomes)) }
                    .sortedByDescending { it.count }
                    .take(maxEntries)

    private fun testResultOf(outcomes: List<ScenarioSummaryResult>) =
            TestResult.valueOf(outcomes.first().result.toUpperCase())
}

class FrequentFailure(val type: String, val count: Int, val result: TestResult) {
    val name = humanize(type.substringAfterLast("."))
    val resultClass = result.name.toLowerCase()
    val resultIcon = ResultIconFormatter().forResult(result)
    val report = ReportNameProvider().forErrorType(type)
}


