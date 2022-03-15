package net.serenitybdd.reports.email

import net.serenitybdd.reports.model.*
import net.serenitybdd.reports.email.templates.ThymeleafTemplateEngine
import net.serenitybdd.reports.io.testOutcomesIn
import net.serenitybdd.reports.model.averageDurationOf
import net.serenitybdd.reports.model.formattedDuration
import net.serenitybdd.reports.model.maxDurationOf
import net.thucydides.core.guice.Injectors
import net.thucydides.core.reports.ExtendedReport
import net.thucydides.core.reports.TestOutcomes
import net.thucydides.core.util.EnvironmentVariables
import java.io.File
import java.nio.file.Path
import java.time.Duration
import java.time.ZonedDateTime

/**
 * Generate an HTML summary report from a set of Serenity test outcomes in a given directory.
 */
class SinglePageHtmlReporter(
        private val environmentVariables: EnvironmentVariables,
        private var sourceDirectory: Path = sourceDirectoryDefinedIn(environmentVariables),
        private var outputDirectory: Path = outputDirectoryDefinedIn(environmentVariables)) : ExtendedReport {

    constructor() : this(Injectors.getInjector().getProvider<EnvironmentVariables>(EnvironmentVariables::class.java).get())

    override fun getName(): String = "single-page-html"
    override fun getDescription(): String = "Single Page HTML Summary"


    override fun setSourceDirectory(sourceDirectory: Path) {
        this.sourceDirectory = sourceDirectory
    }

    override fun setOutputDirectory(outputDirectory: Path) {
        this.outputDirectory = outputDirectory
    }

    override fun generateReport(): Path {
        // Fetch the test outcomes
        val testOutcomes = testOutcomesIn(sourceDirectory).filteredByEnvironmentTags()

        // Prepare the parameters
        val fields = templateFields(environmentVariables, testOutcomes)

        // Write the report
        return writeHtmlReportTo(outputDirectory, fields)
    }

    private fun writeHtmlReportTo(outputDirectory: Path, fields: Map<String, Any>): Path {
        val outputFile = newOutputFileIn(outputDirectory)
        val writer = outputFile.bufferedWriter()

        writer.use {
            val template = SerenitySinglePageReport.template().configuredIn(environmentVariables)
            ThymeleafTemplateEngine().merge(template, fields, writer)
        }

        return outputFile.toPath()
    }

    private fun templateFields(environmentVariables: EnvironmentVariables,
                               testOutcomes: TestOutcomes): Map<String, Any> {
        val reportTitle = SerenitySinglePageReport.reportTitle().configuredIn(environmentVariables)
        val reportLink = SerenitySinglePageReport.reportLink().configuredIn(environmentVariables)
        val scoreboardSize = SerenitySinglePageReport.scoreboardSize().configuredIn(environmentVariables)
        val customReportFields = CustomReportFields(environmentVariables)
        val tagTypes = SerenitySinglePageReport.tagTypes().configuredIn(environmentVariables)
        val tagCategoryTitle = SerenitySinglePageReport.tagCategoryTitle().configuredIn(environmentVariables)
        val showFullTestResults = SerenitySinglePageReport.showFullTestResults().configuredIn(environmentVariables)

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
                "testFailuresPresent" to (FailuresByFeature.from(testOutcomes).size > 0),
                "failuresByFeature" to FailuresByFeature.from(testOutcomes),
                "resultsByFeature" to TestResultsByFeature.from(testOutcomes),
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
        val reportName = SerenitySinglePageReport.reportFilename().configuredIn(environmentVariables)
        return outputDirectory.resolve(reportName).toFile()
    }

    fun newOutputFileIn(outputDirectory: Path): File {
        outputDirectory.toFile().mkdirs()

        val outputFile = outputFileIn(outputDirectory)
        outputFile.createNewFile()

        return outputFile
    }
}

fun outputDirectoryDefinedIn(environmentVariables: EnvironmentVariables): Path = SerenitySinglePageReport.outputDirectory().configuredIn(environmentVariables)

fun sourceDirectoryDefinedIn(environmentVariables: EnvironmentVariables): Path = SerenitySinglePageReport.outputDirectory().configuredIn(environmentVariables)
