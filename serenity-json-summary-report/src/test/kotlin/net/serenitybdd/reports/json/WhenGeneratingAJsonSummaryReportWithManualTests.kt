package net.serenitybdd.reports.json

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.environment.MockEnvironmentVariables
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File
import java.nio.file.Path

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WhenGeneratingAJsonSummaryReportWithManualTests {

    val TEST_OUTCOMES_WITH_MULTIPLE_RESULTS = File(ClassLoader.getSystemResource("test_outcomes/with_manual_tests").file).toPath()

    private val environmentVariables: EnvironmentVariables =
        MockEnvironmentVariables()

    init {
        environmentVariables.setProperty("serenity.summary.report.title", "A Simple Cucumber Report")
        environmentVariables.setProperty("serenity.report.url", "http://my.serenity.report")
        environmentVariables.setProperty("project.version", "1.2.3")
    }

    @Nested
    inner class ReportsWithCustomTags {
        private val generatedReport: Path
        private val reportContents: String
        private val jsonTree: JsonObject

        init {
            environmentVariables.setProperty("report.tagtypes", "group, feature")

            val reporter = JsonSummaryReporter(environmentVariables)
            reporter.setSourceDirectory(TEST_OUTCOMES_WITH_MULTIPLE_RESULTS)
            generatedReport = reporter.generateReport()
            reportContents = generatedReport.toFile().readText()

            jsonTree = JsonParser.parseString(reportContents).asJsonObject
        }

        @Test
        fun `should report coverage for manual tests`() {
            val coverages = jsonTree.getAsJsonArray("coverage")

            val coverageTagSubtitles = mutableListOf<String>();
            for (coverage in coverages) {
                val tagCoverages = coverage.asJsonObject.getAsJsonArray("tagCoverages")
                for (tagCoverage in tagCoverages) {
                    coverageTagSubtitles.add(tagCoverage.asJsonObject.get("tagName").asString)
                }
           }
            assertThat(coverages[0].toString()).contains("\"total\":9")
            assertThat(coverages[0].toString()).contains("\"success\":8")
            assertThat(coverages[0].toString()).contains("\"failure\":1")
            assertThat(coverages[0].toString()).contains("\"ignored\":0")
        }

    }
}


