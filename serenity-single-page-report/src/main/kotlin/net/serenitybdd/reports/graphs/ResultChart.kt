package net.serenitybdd.reports.graphs

import net.thucydides.core.model.TestResult
import net.thucydides.core.model.TestResult.*
import org.knowm.xchart.BitmapEncoder.BitmapFormat.PNG
import org.knowm.xchart.BitmapEncoder.saveBitmap
import org.knowm.xchart.PieChart
import org.knowm.xchart.PieChartBuilder
import org.knowm.xchart.PieSeries.PieSeriesRenderStyle
import org.knowm.xchart.style.PieStyler
import java.awt.Color
import java.io.File


class ResultChart(val data: Map<TestResult, Int>) {

    companion object {
        val DISPLAYED_TEST_RESULTS = listOf(SUCCESS, PENDING, IGNORED, FAILURE, ERROR, COMPROMISED)

        val RESULT_COLORS = mapOf(
                SUCCESS to Color.decode("#61BD76"),
                PENDING to Color.decode("#92DCCE"),
                IGNORED to Color.decode("#ACB1B9"),
                FAILURE to Color.decode("#F33446"),
                ERROR to Color.decode("#FCB150"),
                COMPROMISED to Color.decode("#A4528B")
        )
    }

    val chart: PieChart = donutChart(data)

    fun donutChart(data: Map<TestResult, Int>): PieChart {
        // Create Chart
        val chart = PieChartBuilder().width(800).height(600).title("Test Results").build()

        // Customize Chart
        chart.styler.isLegendVisible = false
        chart.styler.isChartTitleVisible = false
        chart.styler.annotationType = PieStyler.AnnotationType.Label
        chart.styler.annotationDistance = .82
        chart.styler.plotContentSize = .9
        chart.styler.defaultSeriesRenderStyle = PieSeriesRenderStyle.Donut

        DISPLAYED_TEST_RESULTS.reversed().forEach { result ->
            chart.addSeries(result.toString(), data[result]).fillColor = RESULT_COLORS[result]
        }
        return chart
    }

    fun saveTo(savedGraph: File) = saveBitmap(chart, savedGraph.absolutePath, PNG);

}