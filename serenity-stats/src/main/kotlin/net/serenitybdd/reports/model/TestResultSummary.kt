package net.serenitybdd.reports.model

import net.thucydides.core.model.TestResult

class TestResultSummary(val totalCount: Int,
                        val countByResult: Map<TestResult, Int>,
                        val percentageByResult: Map<TestResult, Int>,
                        val totalTestDuration: String,
                        val averageTestDuration: String,
                        val maxTestDuration: String
                        )


class PieChartProgress(val startDegrees : Int, val deltaDegrees : Int)