package net.serenitybdd.reports.model

import net.thucydides.core.model.TestResult
import net.thucydides.core.reports.TestOutcomes
import net.thucydides.core.reports.html.ResultCounts

fun countByResultLabelFrom(testOutcomes: TestOutcomes): Map<String, Int> {
    return TestResult.values().associate { result -> Pair(result.toString(),
                                                          ResultCounts.forOutcomesIn(testOutcomes).getOverallTestCount(result.toString()))
    }
}

fun percentageByResultLabelFrom(testOutcomes: TestOutcomes): Map<String, Int> {
    return TestResult.values().associate { result ->
        Pair(result.toString(), ResultCounts.forOutcomesIn(testOutcomes).getOverallTestPercentage(result.toString()))
    }
}

fun percentageByResultFrom(testOutcomes: TestOutcomes): Map<String, Double> {
    return TestResult.values().associate { result ->
        Pair(result.toString(), ResultCounts.forOutcomesIn(testOutcomes).getPreciseTestPercentage(result.toString()))
    }
}
