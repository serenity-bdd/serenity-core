package net.serenitybdd.reports.model

import net.thucydides.model.domain.TestResult
import net.thucydides.model.reports.TestOutcomes
import net.thucydides.model.reports.html.ReportNameProvider
import net.thucydides.model.reports.html.ResultIconFormatter
import net.thucydides.model.util.NameConverter.humanize
import org.apache.commons.lang3.StringUtils
import java.util.*

class FrequentFailures {
    companion object {
        @JvmStatic
        fun from(testOutcomes: TestOutcomes) = FrequentFailuresBuilder(testOutcomes)
    }
}

class FrequentFailuresBuilder(val testOutcomes: TestOutcomes) {

    fun withMaxOf(maxEntries: Int): List<FrequentFailure> =
            testOutcomes.unsuccessfulTests.outcomes
                    .map { outcome -> ScenarioSummary.ofFailingScenariosIn(outcome).results.toList() }
                    .flatten()
                    .filter { StringUtils.isNotEmpty(it.testFailureErrorType) }
                    .groupBy { it.testFailureErrorType }
                    .map { (error, outcomes) -> FrequentFailure(error, outcomes.size, testResultOf(outcomes)) }
                    .sortedByDescending { it.count }
                    .take(maxEntries)

    private fun testResultOf(outcomes: List<ScenarioSummaryResult>) =
            TestResult.valueOf(outcomes.first().result.uppercase(Locale.getDefault()))
}

class FrequentFailure(val type: String, val count: Int, val result: TestResult) {
    val name = humanize(type.substringAfterLast("."))
    val resultClass = result.name.lowercase()
    val resultIcon = ResultIconFormatter().forResult(result)
    val report = ReportNameProvider().forErrorType(type)
}


