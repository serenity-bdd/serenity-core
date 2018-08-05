package net.serenitybdd.reports.email.model

import net.serenitybdd.reports.email.EmailReporter
import net.thucydides.core.model.TestResult
import net.thucydides.core.reports.TestOutcomes

fun countByResultLabelFrom(testOutcomes: TestOutcomes): Map<String, Int> {
    return TestResult.values().associate { result -> Pair(result.toString(), testOutcomes.withResult(result).total) }
}

fun percentageByResultFrom(testOutcomes: TestOutcomes): Map<String, Int> {
    return TestResult.values().associate { result ->
        Pair(result.toString(), ((testOutcomes.withResult(result).total * 1.0 / testOutcomes.total) * 100).toInt())
    }
}

fun degreesByResultFrom(testOutcomes: TestOutcomes): Map<String, PieChartProgress> {

    val degreesByResult = mutableMapOf<String, PieChartProgress>()

    var startDegree = 0
    EmailReporter.DISPLAYED_TEST_RESULTS.forEach { result ->
        val deltaDegrees = if (result == EmailReporter.DISPLAYED_TEST_RESULTS.last()) (360 - startDegree)
        else ((testOutcomes.withResult(result).total * 1.0 / testOutcomes.total) * 360).toInt()

        degreesByResult[result.toString()] = PieChartProgress(startDegree, deltaDegrees)
        startDegree += deltaDegrees
    }

    return degreesByResult
}
