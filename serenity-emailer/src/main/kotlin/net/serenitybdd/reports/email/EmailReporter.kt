package net.serenitybdd.reports.email

import net.serenitybdd.reports.email.model.*
import net.serenitybdd.reports.email.templates.ThymeleafTemplateEngine
import net.serenitybdd.reports.io.testOutcomesIn
import net.serenitybdd.reports.outcomes.averageDurationOf
import net.serenitybdd.reports.outcomes.formattedDuration
import net.serenitybdd.reports.outcomes.maxDurationOf
import net.thucydides.core.model.TestResult.*
import net.thucydides.core.reports.TestOutcomes
import net.thucydides.core.util.EnvironmentVariables
import java.io.File
import java.nio.file.Path
import java.time.Duration
import java.time.ZonedDateTime

/**
 * Generate an HTML summary report from a set of Serenity test outcomes in a given directory.
 */
class EmailReporter(val environmentVariables: EnvironmentVariables) {

    companion object {
        val DISPLAYED_TEST_RESULTS = listOf(SUCCESS, PENDING, IGNORED, FAILURE, ERROR, COMPROMISED)
    }

    /**
     * Generate an HTMLsummary report using the json files in the specified directory
     */
    fun generateReportFrom(sourceDirectory: Path): File {

        // Fetch the test outcomes
        val testOutcomes = testOutcomesIn(sourceDirectory)

        // Prepare the parameters
        val outputDirectory = SerenityEmailReport.outputDirectory().configuredIn(environmentVariables)
        val fields = templateFields(environmentVariables, testOutcomes)

        // Write the report
        return writeHtmlReportTo(outputDirectory, fields)
    }

    private fun writeHtmlReportTo(outputDirectory: Path, fields: Map<String, Any>): File {
        val outputFile = newOutputFileIn(outputDirectory)
        val writer = outputFile.bufferedWriter()

        writer.use {
            val template = SerenityEmailReport.template().configuredIn(environmentVariables)
            ThymeleafTemplateEngine(environmentVariables).merge(template, fields, writer)
        }

        return outputFile
    }

    private fun templateFields(environmentVariables: EnvironmentVariables,
                               testOutcomes: TestOutcomes): Map<String, Any> {
        val reportTitle = SerenityEmailReport.reportTitle().configuredIn(environmentVariables)
        val scoreboardSize = SerenityEmailReport.scoreboardSize().configuredIn(environmentVariables)
        val customReportFields = CustomReportFields(environmentVariables)
        val tagTypes = SerenityEmailReport.tagTypes().configuredIn(environmentVariables)

        val fields = hashMapOf(
                "testOutcomes" to testOutcomes,
                "report" to ReportInfo(
                        title = reportTitle,
                        version = environmentVariables.getProperty("project.version", ""),
                        date = testOutcomes.startTime.orElse(ZonedDateTime.now()).toLocalDateTime()
                ),
                "results" to TestResultSummary(
                        totalCount = testOutcomes.total,
                        countByResult = countByResultLabelFrom(testOutcomes),
                        percentageByResult = percentageByResultFrom(testOutcomes),
                        totalTestDuration = formattedDuration(Duration.ofMillis(testOutcomes.duration)),
                        averageTestDuration = formattedDuration(averageDurationOf(testOutcomes.outcomes)),
                        maxTestDuration = formattedDuration(maxDurationOf(testOutcomes.outcomes))
                ),
                "frequentFailures" to FrequentFailures.from(testOutcomes).withMaxOf(scoreboardSize),
                "unstableFeatures" to UnstableFeatures.from(testOutcomes).withMaxOf(scoreboardSize),
                "coverage" to TagCoverage.from(testOutcomes).forTagTypes(tagTypes),
                "customFields" to customReportFields.fieldNames,
                "customFieldValues" to customReportFields.values,
                "formatted" to Formatted(),
                "css" to CSSFormatter()
        )
        return fields
    }

    fun outputFileIn(outputDirectory: Path): File {
        return outputDirectory.resolve("serenity-summary.html").toFile()
    }

    fun newOutputFileIn(outputDirectory: Path): File {
        outputDirectory.toFile().mkdirs()

        val outputFile = outputFileIn(outputDirectory)
        outputFile.createNewFile()

        return outputFile
    }
}