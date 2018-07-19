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
        val fields = templateFields(environmentVariables, testOutcomes)

        // Merge the template
        val outputFile = newOutputFileIn(outputDirectory)
        val writer = outputFile.bufferedWriter()
        val template = SerenityReport.template().configuredIn(environmentVariables)

        TemplateEngine(environmentVariables).merge(template, fields, writer)

        return outputFile
    }

    private fun templateFields(environmentVariables: EnvironmentVariables, testOutcomes: TestOutcomes): Map<String, Any> {
        val reportTitle = SerenityReport.reportTitle().configuredIn(environmentVariables)

        val fields = hashMapOf(
                "testOutcomes" to testOutcomes,
                "report" to ReportInfo(title = reportTitle,
                        version = environmentVariables.getProperty("project.version",""),
                        date = Date())
        )
        return fields
    }

    fun testOutcomesIn(outputDirectory: Path): TestOutcomes {
        return TestOutcomeLoader.loadTestOutcomes().inFormat(OutcomeFormat.JSON).from(outputDirectory.toFile())
    }

    fun outputFileIn(outputDirectory: Path): File {
        return outputDirectory.resolve("serenity.asc").toFile()
    }

    fun newOutputFileIn(outputDirectory: Path) : File {
        outputDirectory.toFile().mkdirs()

        val outputFile = outputFileIn(outputDirectory)
        outputFile.createNewFile()

        return outputFile;
    }
}