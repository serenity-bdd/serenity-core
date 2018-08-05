package net.serenitybdd.reports.email.model

import net.thucydides.core.model.TestResult
import net.thucydides.core.reports.TestOutcomes
import net.thucydides.core.util.NameConverter.humanize

class FrequentFailures {
    companion object {
        fun from(testOutcomes: TestOutcomes) = FrequentFailuresBuilder(testOutcomes)
    }
}

class FrequentFailuresBuilder(val testOutcomes: TestOutcomes) {
    fun withMaxOf(maxEntries : Int) : List<FrequentFailure> =
            testOutcomes.failingOrErrorTests.outcomes
                    .groupBy { outcome -> outcome.testFailureErrorType }
                    .map { (error, outcomes) -> FrequentFailure(error, outcomes.size, outcomes.first().result) }
                    .sortedByDescending { frequentFailure -> frequentFailure.count }
                    .take(maxEntries)
}

class FrequentFailure(val type: String, val count: Int, val result: TestResult) {
    val name = humanize(type.substringAfterLast("."))
    val resultClass = result.name.toLowerCase()
}