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

    companion object {
        private const val MAX_MESSAGE_LENGTH = 80
    }

    fun withMaxOf(maxEntries: Int): List<FrequentFailure> =
            testOutcomes.unsuccessfulTests.outcomes
                    .map { outcome -> ScenarioSummary.ofFailingScenariosIn(outcome).results.toList() }
                    .flatten()
                    .filter { StringUtils.isNotEmpty(it.testFailureErrorType) || StringUtils.isNotEmpty(it.errorMessage) }
                    .groupBy { groupingKeyFor(it) }
                    .map { (_, outcomes) ->
                        val first = outcomes.first()
                        FrequentFailure(
                            type = first.testFailureErrorType,
                            message = first.errorMessage,
                            count = outcomes.size,
                            result = testResultOf(outcomes)
                        )
                    }
                    .sortedByDescending { it.count }
                    .take(maxEntries)

    /**
     * Group failures by error message if available, otherwise by error type.
     * This ensures that failures with the same root cause are grouped together.
     */
    private fun groupingKeyFor(result: ScenarioSummaryResult): String {
        return if (StringUtils.isNotEmpty(result.errorMessage)) {
            result.errorMessage
        } else {
            result.testFailureErrorType
        }
    }

    private fun testResultOf(outcomes: List<ScenarioSummaryResult>) =
            TestResult.valueOf(outcomes.first().result.uppercase(Locale.getDefault()))
}

class FrequentFailure(val type: String, val message: String, val count: Int, val result: TestResult) {
    companion object {
        private const val MAX_DISPLAY_LENGTH = 80
    }

    /**
     * Display name shows the actual error message (truncated if needed),
     * falling back to the humanized error type if no message is available.
     */
    val name: String = if (StringUtils.isNotEmpty(message)) {
        truncateMessage(message)
    } else {
        humanize(type.substringAfterLast("."))
    }

    val resultClass = result.name.lowercase()
    val resultIcon = ResultIconFormatter().forResult(result)
    val report = ReportNameProvider().forErrorType(type)

    private fun truncateMessage(msg: String): String {
        val cleaned = msg.trim().replace("\n", " ").replace("\r", "")
        return if (cleaned.length > MAX_DISPLAY_LENGTH) {
            cleaned.take(MAX_DISPLAY_LENGTH) + "..."
        } else {
            cleaned
        }
    }
}


