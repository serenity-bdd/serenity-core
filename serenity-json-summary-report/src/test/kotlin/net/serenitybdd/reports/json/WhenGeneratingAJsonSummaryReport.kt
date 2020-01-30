package net.serenitybdd.reports.json

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File
import java.nio.file.Path

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WhenGeneratingAJsonSummaryReport {

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
            val reporter = JsonSummaryReporter(environmentVariables)
            reporter.setSourceDirectory(TEST_OUTCOMES_WITH_MULTIPLE_RESULTS)
            generatedReport = reporter.generateReport()
            reportContents = generatedReport.toFile().readText()
        }

        @Test
        fun `should be written to serenity-summary_html`() {
            assertThat(generatedReport).exists()
            assertThat(generatedReport.toFile().name).isEqualTo("serenity-summary.json")
        }

        @Test
        fun `should have a configurable title`() {
            assertThat(reportContents).contains("A Simple Cucumber Report")
        }

        @Test
        fun `should display the number of executed tests`() {
            assertThat(reportContents).contains("\"total\": 27")
        }
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

            val parser = JsonParser()
            jsonTree = parser.parse(reportContents).asJsonObject
        }

        @Test
        fun `should list all configured summary tag types as headings`() {
            val coverages = jsonTree.getAsJsonArray("coverage")

            val coverageTagTitles = mutableListOf<String>();
            for (coverage in coverages) {
                coverageTagTitles.add(coverage.asJsonObject.get("tagTitle").asString)
            }

            assertThat(coverageTagTitles).containsExactly("Group", "Feature")
        }

        @Test
        fun `should list the tags of each specified tag type as sub-headings`() {
            val coverages = jsonTree.getAsJsonArray("coverage")

            val coverageTagSubtitles = mutableListOf<String>();
            for (coverage in coverages) {
                val tagCoverages = coverage.asJsonObject.getAsJsonArray("tagCoverages")
                for (tagCoverage in tagCoverages) {
                    coverageTagSubtitles.add(tagCoverage.asJsonObject.get("tagName").asString)
                }
           }

            assertThat(coverageTagSubtitles).contains("Alpha", "Beta", "Gamma")
        }

        @Test
        fun `should list feature tags in their shortened form`() {
            val coverages = jsonTree.getAsJsonArray("coverage")

            val coverageTagSubtitles = mutableListOf<String>();
            for (coverage in coverages) {
                val tagCoverages = coverage.asJsonObject.getAsJsonArray("tagCoverages")
                for (tagCoverage in tagCoverages) {
                    coverageTagSubtitles.add(tagCoverage.asJsonObject.get("tagName").asString)
                }
            }

            assertThat(coverageTagSubtitles).contains("Broken scenarios", "Compromised scenarios", "Failed scenarios", "Ignored scenarios",
                    "Mixed scenarios", "Passing scenarios", "Pending scenarios")
        }

        @Test
        fun `should list top most frequent failures features`() {
            val frequentFailures = jsonTree.getAsJsonArray("frequentFailures")

            val frequentFailureNames = mutableListOf<String>();
            for (failure in frequentFailures) {
                frequentFailureNames.add(failure.asJsonObject.get("name").asString)
            }

            assertThat(frequentFailureNames).containsExactly("Assertion error", "Illegal argument exception", "Test compromised exception")
        }

        @Test
        fun `should list top most unstable features`() {
            val unstableFeatures = jsonTree.getAsJsonArray("unstableFeatures")

            val unstableFeatureNames = mutableListOf<String>();
            for (feature in unstableFeatures) {
                unstableFeatureNames.add(feature.asJsonObject.get("name").asString)
            }

            assertThat(unstableFeatureNames).containsExactly("Failed scenarios", "Broken scenarios", "Compromised scenarios", "Mixed scenarios")
        }
    }

    @Nested
    inner class ReportShowingTheFullFeatureList {
        private val generatedReport: Path
        private val reportContents: String
        private val jsonTree: JsonObject

        init {
            val reporter = JsonSummaryReporter(environmentVariables)
            reporter.setSourceDirectory(TEST_OUTCOMES_WITH_MULTIPLE_RESULTS)
            generatedReport = reporter.generateReport()
            reportContents = generatedReport.toFile().readText()

            val parser = JsonParser()
            jsonTree = parser.parse(reportContents).asJsonObject
        }


        @Test
        fun `should list feature titles`() {
            val features = jsonTree.getAsJsonArray("resultsByFeature")

            val featureTitles = mutableListOf<String>();
            for (feature in features) {
                featureTitles.add(feature.asJsonObject.get("featureName").asString)
            }

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


