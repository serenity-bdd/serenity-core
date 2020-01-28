package net.serenitybdd.reports.email

import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import org.assertj.core.api.Assertions.assertThat
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WhenGeneratingAnEmailableReport {

    val TEST_OUTCOMES_WITH_MULTIPLE_RESULTS = File(ClassLoader.getSystemResource("test_outcomes/with_different_results").file).toPath()

    private val environmentVariables: EnvironmentVariables = MockEnvironmentVariables()


    init {
        environmentVariables.setProperty("serenity.summary.report.title", "A Simple Cucumber Report")
        environmentVariables.setProperty("serenity.report.url", "http://my.serenity.report")
        environmentVariables.setProperty("project.version", "1.2.3")
    }

    @Nested
    inner class AllReports {

        private val generatedReport: Path
        private val reportContents: String

        init {
            val reporter = SinglePageHtmlReporter(environmentVariables)
            reporter.setSourceDirectory(TEST_OUTCOMES_WITH_MULTIPLE_RESULTS)
            generatedReport = reporter.generateReport()
            reportContents = generatedReport.toFile().readText()
        }

        @Test
        fun `should be written to serenity-summary_html`() {
            assertThat(generatedReport).exists()
            assertThat(generatedReport.toFile().name).isEqualTo("serenity-summary.html")
        }

        @Test
        fun `should have a configurable title`() {
            assertThat(reportContents).contains("A Simple Cucumber Report")
        }

        @Test
        fun `should have a report link`() {
            assertThat(reportContents).contains("View full report")
            assertThat(reportContents).contains("http://my.serenity.report")
        }

        @Test
        fun `should display the number of executed tests`() {
            assertThat(reportContents).contains("27 tests executed")
        }
    }


    @Nested
    inner class ReportWithCustomFields {

        private val generatedReport: Path
        private val reportContents: String
        private val parsedReport: Document

        init {
            environmentVariables.setProperty("report.customfields.environment", "NAV Automation INT5")
            environmentVariables.setProperty("report.customfields.version", "INT NAV 13.5.0")
            environmentVariables.setProperty("report.customfields.HostName", "localhost")
            environmentVariables.setProperty("report.customfields.User", "tim")
            environmentVariables.setProperty("report.customfields.order", "environment,version,HostName,User")

            val reporter = SinglePageHtmlReporter(environmentVariables)
            reporter.setSourceDirectory(TEST_OUTCOMES_WITH_MULTIPLE_RESULTS)
            generatedReport = reporter.generateReport()
            reportContents = generatedReport.toFile().readText()
            parsedReport = parse(reportContents)
        }

        @Test
        fun `should get display customisable environment variables from the report-summary-* properties`() {
            val fieldValues = parsedReport.getElementsByClass("custom-value").map { element -> element.text() }
            assertThat(fieldValues).contains("NAV Automation INT5", "INT NAV 13.5.0", "localhost", "tim")
        }

        @Test
        fun `customisable environment variables should appear in the order specified in the report-dot-customfields-order field`() {
            val fieldValues = parsedReport.getElementsByClass("custom-title").map { element -> element.text() }
            assertThat(fieldValues).contains("Environment", "Version", "Host name", "User")
        }
    }

    @Nested
    inner class ReportsWithCustomTags {
        private val generatedReport: Path
        private val reportContents: String
        private val parsedReport: Document

        init {
            environmentVariables.setProperty("report.tagtypes", "group, feature")

            val reporter = SinglePageHtmlReporter(environmentVariables)
            reporter.setSourceDirectory(TEST_OUTCOMES_WITH_MULTIPLE_RESULTS)
            generatedReport = reporter.generateReport()
            reportContents = generatedReport.toFile().readText()
            parsedReport = parse(reportContents)
        }

        @Test
        fun `should list all configured summary tag types as headings`() {
            val tagTitles = parsedReport.getElementsByClass("tag-title").map { element -> element.text() }
            assertThat(tagTitles).containsExactly("Group", "Feature")
        }

        @Test
        fun `should list the tags of each specified tag type as sub-headings`() {
            val tagSubTitles = parsedReport.getElementsByClass("tag-subtitle").map { element -> element.text() }
            assertThat(tagSubTitles).contains("Alpha", "Beta", "Gamma")
        }

        @Test
        fun `should list feature tags in their shortened form`() {
            val tagSubTitles = parsedReport.getElementsByClass("tag-subtitle").map { element -> element.text() }
            assertThat(tagSubTitles).contains("Broken scenarios", "Compromised scenarios", "Failed scenarios", "Ignored scenarios",
                    "Mixed scenarios", "Passing scenarios", "Pending scenarios")
        }

        @Test
        fun `should list top most frequent failures features`() {
            val unstableFeatures = parsedReport.getElementsByClass("failure-scoreboard")[0]
                    .getElementsByClass("frequent-failure")
                    .map { it.text() }

            assertThat(unstableFeatures).containsExactly("Assertion error", "Illegal argument exception", "Test compromised exception")
        }

        @Test
        fun `should list top most unstable features`() {
            val unstableFeatures = parsedReport.getElementsByClass("unstable-feature").map { element -> element.text() }
            assertThat(unstableFeatures).containsExactly("Failed scenarios", "Broken scenarios", "Compromised scenarios", "Mixed scenarios")
        }
    }

    @Nested
    inner class ReportsWithFailureScoreboards {
        private val generatedReport: Path
        private val reportContents: String
        private val parsedReport: Document

        init {
            val reporter = SinglePageHtmlReporter(environmentVariables)
            reporter.setSourceDirectory(TEST_OUTCOMES_WITH_MULTIPLE_RESULTS)
            generatedReport = reporter.generateReport()
            reportContents = generatedReport.toFile().readText()
            parsedReport = parse(reportContents)
        }


        @Test
        fun `should list feature tags in their shortened form`() {
            val tagSubTitles = parsedReport.getElementsByClass("tag-subtitle").map { element -> element.text() }
            assertThat(tagSubTitles).contains("Broken scenarios", "Compromised scenarios", "Failed scenarios", "Ignored scenarios",
                    "Mixed scenarios", "Passing scenarios", "Pending scenarios")
        }

        @Test
        fun `should list top most frequent failures features`() {
            val unstableFeatures = parsedReport.getElementsByClass("failure-scoreboard")[0]
                    .getElementsByClass("frequent-failure")
                    .map { it.text() }
            assertThat(unstableFeatures).containsExactly("Assertion error", "Illegal argument exception", "Test compromised exception")
        }

        @Test
        fun `should list top most unstable features`() {
            val unstableFeatures = parsedReport.getElementsByClass("unstable-feature").map { element -> element.text() }
            assertThat(unstableFeatures).containsExactly("Failed scenarios", "Broken scenarios", "Compromised scenarios", "Mixed scenarios")
        }

        @Nested
        inner class AndAConfiguredScoreboardSize {
            private val generatedReport: Path
            private val reportContents: String
            private val parsedReport: Document

            init {
                environmentVariables.setProperty("report.scoreboard.size", "2")

                val reporter = SinglePageHtmlReporter(environmentVariables)
                reporter.setSourceDirectory(TEST_OUTCOMES_WITH_MULTIPLE_RESULTS)
                generatedReport = reporter.generateReport()
                reportContents = generatedReport.toFile().readText()

                parsedReport = parse(reportContents)
            }


            @Test
            fun `should list no more than the configured number of errors or  unstable features`() {
                val unstableFeatures = parsedReport.getElementsByClass("unstable-feature").map { element -> element.text() }
                assertThat(unstableFeatures).containsExactly("Failed scenarios", "Broken scenarios")
            }
        }
    }

    @Nested
    inner class ReportShowingTheFullFeatureList {
        private val generatedReport: Path
        private val reportContents: String
        private val parsedReport: Document

        init {
            val reporter = SinglePageHtmlReporter(environmentVariables)
            reporter.setSourceDirectory(TEST_OUTCOMES_WITH_MULTIPLE_RESULTS)
            generatedReport = reporter.generateReport()
            reportContents = generatedReport.toFile().readText()
            parsedReport = parse(reportContents)
        }


        @Test
        fun `should list feature titles`() {
            val featureTitles = parsedReport.getElementsByClass("feature-title").map { element -> element.text() }
            assertThat(featureTitles).contains("Broken scenarios",
                    "Compromised scenarios",
                    "Failed scenarios",
                    "Ignored scenarios",
                    "Mixed scenarios",
                    "Passing scenarios",
                    "Pending scenarios")
        }
    }
}

fun parse(html: String): Document = Jsoup.parse(html)

