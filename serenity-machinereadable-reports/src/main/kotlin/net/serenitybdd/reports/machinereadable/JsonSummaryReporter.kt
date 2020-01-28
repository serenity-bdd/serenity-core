package net.serenitybdd.reports.machinereadable

import net.serenitybdd.reports.model.*
import net.serenitybdd.reports.io.testOutcomesIn
import net.serenitybdd.reports.model.averageDurationOf
import net.serenitybdd.reports.model.formattedDuration
import net.serenitybdd.reports.model.maxDurationOf
import net.thucydides.core.guice.Injectors
import net.thucydides.core.model.TestResult.*
import net.thucydides.core.reports.ExtendedReport
import net.thucydides.core.reports.TestOutcomes
import net.thucydides.core.reports.html.Merger
import net.thucydides.core.util.EnvironmentVariables
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Path
import java.time.Duration
import java.time.ZonedDateTime

/**
 * Generate a json summary report from a set of Serenity test outcomes in a given directory.
 */
class JsonSummaryReporter(val environmentVariables: EnvironmentVariables) : ExtendedReport {
    override fun getName(): String = "json-summary"
    override fun getDescription(): String = "JSON Summary"

    constructor() : this(Injectors.getInjector().getProvider<EnvironmentVariables>(EnvironmentVariables::class.java).get())

    override fun generateReportFrom(sourceDirectory: Path): File {

        // Fetch the test outcomes
        val testOutcomes = testOutcomesIn(sourceDirectory).filteredByEnvironmentTags()

        // Prepare the parameters
        val outputDirectory = JsonSummaryReport.outputDirectory().configuredIn(environmentVariables)
        val fields = templateFields(environmentVariables, testOutcomes)

        // Write the report
        return writeJsonReportTo(outputDirectory, fields)
    }

    private fun writeJsonReportTo(outputDirectory: Path, fields: Map<String, Any>): File {
        val outputFile = newOutputFileIn(outputDirectory)
        val writer = outputFile.bufferedWriter()

        writer.use {
            val template = JsonSummaryReport.template().configuredIn(environmentVariables)
            mergeTemplate(template).withContext(fields).to(writer)
        }

        return outputFile
    }

    private fun mergeTemplate(templateFile: String): Merger {
        return Merger(templateFile)
    }

    private fun templateFields(environmentVariables: EnvironmentVariables,
                               testOutcomes: TestOutcomes): Map<String, Any> {
        val reportTitle = JsonSummaryReport.reportTitle().configuredIn(environmentVariables)
        val reportLink = JsonSummaryReport.reportLink().configuredIn(environmentVariables)
        val scoreboardSize = JsonSummaryReport.scoreboardSize().configuredIn(environmentVariables)
        val customReportFields = CustomReportFields(environmentVariables)
        val tagTypes = JsonSummaryReport.tagTypes().configuredIn(environmentVariables)
        val tagCategoryTitle = JsonSummaryReport.tagCategoryTitle().configuredIn(environmentVariables)
        val showFullTestResults = JsonSummaryReport.showFullTestResults().configuredIn(environmentVariables)

        val fields = hashMapOf(
                "testOutcomes" to testOutcomes,
                "showFullTestResults" to showFullTestResults,
                "report" to ReportInfo(
                        title = reportTitle,
                        link = reportLink,
                        tagCategoryTitle = tagCategoryTitle,
                        version = environmentVariables.getProperty("project.version", ""),
                        date = testOutcomes.startTime.orElse(ZonedDateTime.now()).toLocalDateTime()
                ),
                "results" to TestResultSummary(
                        totalCount = testOutcomes.total,
                        countByResult = countByResultLabelFrom(testOutcomes),
                        percentageByResult = percentageByResultLabelFrom(testOutcomes),
                        totalTestDuration = formattedDuration(Duration.ofMillis(testOutcomes.duration)),
                        clockTestDuration = formattedDuration(clockDurationOf(testOutcomes.outcomes)),
                        averageTestDuration = formattedDuration(averageDurationOf(testOutcomes.outcomes)),
                        maxTestDuration = formattedDuration(maxDurationOf(testOutcomes.outcomes)),
                        minTestDuration = formattedDuration(minDurationOf(testOutcomes.outcomes))
                ),
                "failuresByFeature" to FailuresByFeature.from(testOutcomes),
                "resultsByFeature" to TestResultsByFeature.from(testOutcomes),
                "frequentFailures" to FrequentFailures.from(testOutcomes).withMaxOf(scoreboardSize),
                "unstableFeatures" to UnstableFeatures.from(testOutcomes).withMaxOf(scoreboardSize),
                "coverage" to TagCoverage.from(testOutcomes).forTagTypes(tagTypes),
                "customFields" to customReportFields.fieldNames,
                "customFieldValues" to customReportFields.values,
                "formatted" to Formatted(),
                "tagResults" to TagResults.from(testOutcomes).groupedByType()
        )
        return fields
    }

    fun outputFileIn(outputDirectory: Path): File {
        return outputDirectory.resolve("serenity-summary.json").toFile()
    }

    fun newOutputFileIn(outputDirectory: Path): File {
        outputDirectory.toFile().mkdirs()

        val outputFile = outputFileIn(outputDirectory)
        outputFile.createNewFile()

        return outputFile
    }
}