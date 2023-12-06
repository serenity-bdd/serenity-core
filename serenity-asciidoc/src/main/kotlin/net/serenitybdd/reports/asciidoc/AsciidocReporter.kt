package net.serenitybdd.reports.asciidoc

import net.serenitybdd.reports.asciidoc.configuration.SerenityReport
import net.serenitybdd.reports.asciidoc.model.ReportInfo
import net.serenitybdd.reports.asciidoc.templates.FreemarkerTemplateEngine
import net.thucydides.model.reports.OutcomeFormat
import net.thucydides.model.reports.TestOutcomeLoader
import net.thucydides.model.reports.TestOutcomes
import net.thucydides.model.requirements.reports.FileSystemRequirementsOutcomeFactory
import net.thucydides.model.requirements.reports.RequirementsOutcomes
import net.thucydides.model.util.EnvironmentVariables
import java.io.File
import java.nio.file.Path
import java.util.*

/**
 * Generate an Asciidoc report from a set of Serenity test outcomes in a given directory.
 */
class AsciidocReporter(val environmentVariables: EnvironmentVariables) {


    /**
     * Generate an Asciidoc report using the json files in the specified directory
     */
    fun generateReportFrom(sourceDirectory: Path): File {

        // Fetch the test outcomes
        val testOutcomes = testOutcomesIn(sourceDirectory)

        // Overall requirements hierarchy
        val requirementsOutcomes = requirementsOutcomesFrom(testOutcomes).using(environmentVariables)

        // Prepare the parameters
        val outputDirectory = SerenityReport.outputDirectory().configuredIn(environmentVariables)
        val fields = templateFields(environmentVariables, testOutcomes, requirementsOutcomes)

        // Merge the template
        val outputFile = newOutputFileIn(outputDirectory)
        val writer = outputFile.bufferedWriter()
        val template = SerenityReport.template().configuredIn(environmentVariables)

        FreemarkerTemplateEngine(environmentVariables).merge(template, fields, writer)

        return outputFile
    }

    private fun templateFields(environmentVariables: EnvironmentVariables,
                               testOutcomes: TestOutcomes,
                               requirementsOutcomes : RequirementsOutcomes): Map<String, Any> {
        val reportTitle = SerenityReport.reportTitle().configuredIn(environmentVariables)

        val fields = hashMapOf(
                "testOutcomes" to testOutcomes,
                "requirementsOutcomes" to requirementsOutcomes,
                "report" to ReportInfo(title = reportTitle,
                        version = environmentVariables.getProperty("project.version", ""),
                        date = Date()),
                "formatted" to Formatted()
        )
        return fields
    }

    fun testOutcomesIn(outputDirectory: Path): TestOutcomes {
        return TestOutcomeLoader.loadTestOutcomes().inFormat(OutcomeFormat.JSON).from(outputDirectory.toFile())
    }


    fun outputFileIn(outputDirectory: Path): File {
        return outputDirectory.resolve("serenity.asc").toFile()
    }

    fun newOutputFileIn(outputDirectory: Path): File {
        outputDirectory.toFile().mkdirs()

        val outputFile = outputFileIn(outputDirectory)
        outputFile.createNewFile()

        return outputFile;
    }

    fun requirementsOutcomesFrom(testOutcomes: TestOutcomes) = RequirementsOutcomesProvider(testOutcomes)

    class RequirementsOutcomesProvider(val testOutcomes: TestOutcomes) {
        fun using(environmentVariables: EnvironmentVariables): RequirementsOutcomes =
                FileSystemRequirementsOutcomeFactory(environmentVariables).buildRequirementsOutcomesFrom(testOutcomes)
    }
}
