package net.serenitybdd.reports.model

import net.thucydides.core.model.TestResult
import net.thucydides.core.reports.TestOutcomes

fun countByResultLabelFrom(testOutcomes: TestOutcomes): Map<TestResult, Int> {
    return TestResult.values().associate { result -> Pair(result, testOutcomes.withResult(result).total) }
}

fun percentageByResultFrom(testOutcomes: TestOutcomes): Map<TestResult, Int> {
    return TestResult.values().associate { result ->
        Pair(result, ((testOutcomes.withResult(result).total * 1.0 / testOutcomes.total) * 100).toInt())
    }
}
