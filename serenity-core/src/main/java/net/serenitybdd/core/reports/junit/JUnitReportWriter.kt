//@file:JvmName("JUnitReportWriter")
package net.serenitybdd.core.reports.json

import com.google.common.eventbus.Subscribe
import net.serenitybdd.core.lifecycle.TestRunFinishedEvent
import net.serenitybdd.core.reports.copyWorkingCopyToTarget
import net.serenitybdd.core.reports.groupedByTestCase
import net.serenitybdd.core.reports.junit.GroupedTestOutcomes
import net.serenitybdd.core.reports.workingCopyOf
import net.thucydides.core.guice.Injectors
import net.thucydides.core.model.ReportNamer
import net.thucydides.core.model.ReportType
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.reports.junit.JUnitXMLConverter
import net.thucydides.core.util.EnvironmentVariables
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.nio.file.Path

class JUnitReportWriter(val outputDirectory: Path,
                        val environmentVariables: EnvironmentVariables) {

    val junitXMLConverter = JUnitXMLConverter()
    val FILE_PREFIX = "serenity_junit_"

    constructor(outputDirectory: Path) : this(outputDirectory,
            Injectors.getInjector().getInstance(EnvironmentVariables::class.java))


    @Subscribe
    fun recordJUnitReportFor(event: TestRunFinishedEvent) {
         groupedByTestCase(event.testOutcomes)
                 .map { entry -> GroupedTestOutcomes(entry.key, entry.value) }
                 .forEach { generateJUnitReportFor(it) }
    }

    private fun generateJUnitReportFor(groupedTestOutcomes: GroupedTestOutcomes) {
        val reportName = reportFilenameFor(groupedTestOutcomes.outcomes.get(0))

        val targetReport = outputDirectory.resolve(reportName)
        val workingCopy = outputDirectory.resolve(workingCopyOf(reportName))

        saveAWorkingCopy(groupedTestOutcomes, workingCopy)
        copyWorkingCopyToTarget(workingCopy, targetReport)
    }

    private fun saveAWorkingCopy(groupedTestOutcomes: GroupedTestOutcomes, workingCopy: Path) {
        BufferedOutputStream(FileOutputStream(workingCopy.toFile())).use { outputStream ->
            junitXMLConverter.write(groupedTestOutcomes.testCaseName, groupedTestOutcomes.outcomes, outputStream)
            outputStream.flush()
        }
    }

    private fun reportFilenameFor(testOutcome: TestOutcome): String {
        val reportNamer = ReportNamer.forReportType(ReportType.XML)
        return FILE_PREFIX + reportNamer.getNormalizedTestNameFor(testOutcome)
    }
}
