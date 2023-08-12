package net.serenitybdd.reports.navigator

import net.thucydides.model.util.EnvironmentVariables
import net.thucydides.model.environment.MockEnvironmentVariables
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File
import java.nio.file.Path

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WhenGeneratingFromAReportNavigatorTemplate {

    val outcomesDir: Path = File(ClassLoader.getSystemResource("test_outcomes/with_different_results").file).toPath()

    private val environmentVariables: EnvironmentVariables =
        MockEnvironmentVariables()

    @Nested
    inner class AllReports {

        private val generatedReport: Path
        private val reportContents: String

        init {
            val reporter = GenerateReport(environmentVariables)
            reporter.setSourceDirectory(outcomesDir)
            generatedReport = reporter.generateReport()
            reportContents = generatedReport.toFile().readText()
        }

        @Test
        fun `should be written to navigator index html`() {
            assertThat(generatedReport).exists()
            assertThat(generatedReport.toFile().name).isEqualTo("index.html")
            assertThat(generatedReport.parent.toFile().name).isEqualTo("navigator")
        }

        @Test
        fun `should contain outcomes`() {
            assertThat(reportContents).contains("window.outcomes=[{")
        }
    }

}

