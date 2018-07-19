package net.serenitybdd.reports.asciidoc

import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WhenGeneratingAnAsciidocReport {

    val TEST_OUTCOMES_WITH_A_SINGLE_TEST = ClassLoader.getSystemResource("test_outcomes/with_a_single_test").path

    @Nested
    inner class ASimpleCucumberReport {

        private val generatedReport : File
        private val reportContents : String;
        private val environmentVariables : EnvironmentVariables = MockEnvironmentVariables()

        init {
            environmentVariables.setProperty("serenity.project.name","A Simple Cucumber Report")
            environmentVariables.setProperty("project.version","1.2.3")

            generatedReport = AsciidocReporter(environmentVariables).generateReportFrom(Paths.get(TEST_OUTCOMES_WITH_A_SINGLE_TEST))

            reportContents = generatedReport.readText()
        }

        @Test
        fun `should have a correct title`() {
            assertThat(reportContents).contains("= A Simple Cucumber Report")
        }

        @Test
        fun `should display the date the report was generated`() {
            val expectedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"))
            assertThat(reportContents).contains(":revdate: ${expectedDate}")
        }

        @Test
        fun `should include the current project version number`() {
            assertThat(reportContents).contains(":revnumber: 1.2.3")
        }
    }

    @Nested
    inner class YouShouldBeAbleTo {
        private val reportContents : String;
        private val environmentVariables : EnvironmentVariables = MockEnvironmentVariables()

        init {
            environmentVariables.setProperty("asciidoc.template","custom_template/my_template.adoc")

            val generatedReport = AsciidocReporter(environmentVariables).generateReportFrom(Paths.get(TEST_OUTCOMES_WITH_A_SINGLE_TEST))

            reportContents = generatedReport.readText()
        }

        @Test
        fun `provide a custom asciidoc template using the asciidoc-dot-template system property and a file on the project classpath`() {
            assertThat(reportContents).contains("My Custom Asciidoc Template")
        }
    }

}