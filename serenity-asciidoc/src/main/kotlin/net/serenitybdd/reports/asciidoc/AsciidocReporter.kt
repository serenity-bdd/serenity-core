package net.serenitybdd.reports.asciidoc

import net.serenitybdd.reports.asciidoc.configuration.SerenityReport
import net.serenitybdd.reports.asciidoc.model.ReportInfo
import net.serenitybdd.reports.asciidoc.templates.TemplateEngine
import net.thucydides.core.reports.OutcomeFormat
import net.thucydides.core.reports.TestOutcomeLoader
import net.thucydides.core.reports.TestOutcomes
import net.thucydides.core.util.EnvironmentVariables
import java.io.File
import java.nio.file.Path
import java.time.LocalDateTime
import java.util.*

/**
 * Generate an Asciidoc report from a set of Serenity test outcomes in a given directory.
 */
public class AsciidocReporter(val environmentVariables: EnvironmentVariables) {

    /**
     * Generate an Asciidoc report using the json files in the specified directory
     */
    fun generateReportFrom(sourceDirectory: Path): File {

        // Fetch the test outcomes
        val testOutcomes = testOutcomesIn(sourceDirectory)

        // Prepare the parameters
        val outputDirectory = SerenityReport.outputDirectory().configuredIn(environmentVariables)
        val reportTitle = SerenityReport.reportTitle().configuredIn(environmentVariables)

        val fields = hashMapOf(
                "testOutcomes" to testOutcomes,
                "report" to ReportInfo(title = reportTitle,
                                       version = environmentVariables.getProperty("project.version",""),
                                       date = Date())
        )

        // Merge the template
        outputDirectory.toFile().mkdirs()
        val outputFile = outputFileIn(outputDirectory)
        outputFile.createNewFile()

        val writer = outputFileIn(outputDirectory).bufferedWriter()

        TemplateEngine.merge("full-report.adoc", fields, writer)

        return outputFile
    }

    fun testOutcomesIn(outputDirectory: Path): TestOutcomes {
        return TestOutcomeLoader.loadTestOutcomes().inFormat(OutcomeFormat.JSON).from(outputDirectory.toFile())
    }

    fun outputFileIn(outputDirectory: Path): File {
        return outputDirectory.resolve("serenity.asc").toFile()
    }
}