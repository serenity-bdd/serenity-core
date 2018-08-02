package net.serenitybdd.reports.email.model

class TestResultSummary(val totalCount: Int,
                        val countByResult: Map<String, Int>,
                        val percentageByResult: Map<String, Int>,
                        val totalTestDuration: String,
                        val averageTestDuration: String,
                        val maxTestDuration: String
                        )


class PieChartProgress(val startDegrees : Int, val deltaDegrees : Int)