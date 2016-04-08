@file:JvmName("JSONTestOutcomeWriter")
package net.serenitybdd.core.reports.json

import com.google.common.eventbus.Subscribe
import net.serenitybdd.core.lifecycle.TestFinishedEvent
import net.serenitybdd.core.reports.copyWorkingCopyToTarget
import net.thucydides.core.guice.Injectors
import net.thucydides.core.model.ReportType
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.reports.json.JSONConverter
import net.thucydides.core.reports.json.gson.GsonJSONConverter
import net.thucydides.core.util.EnvironmentVariables
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.nio.file.Path
import java.util.*

class JSONTestOutcomeReportWriter(val outputDirectory: Path,
                                  val environmentVariables: EnvironmentVariables) {


    val jsonConverter : JSONConverter

    init {
        jsonConverter = GsonJSONConverter(environmentVariables)
    }

    constructor(outputDirectory: Path) : this(outputDirectory,
            Injectors.getInjector().getInstance(EnvironmentVariables::class.java))


    @Subscribe
    fun recordTestOutcomeFor(event: TestFinishedEvent) : Path {
        val testOutcomeToBeStored = event.testOutcome
        val reportName = jsonOutputFor(testOutcomeToBeStored);

        val targetReport = outputDirectory.resolve(reportName)
        val workingCopy = outputDirectory.resolve(workingCopyOf(reportName))

        saveAWorkingCopy(testOutcomeToBeStored, workingCopy)
        copyWorkingCopyToTarget(workingCopy, targetReport)

        return targetReport
    }

    fun withEnvironmentVariables(environmentVariables: EnvironmentVariables) : JSONTestOutcomeReportWriter {
        return JSONTestOutcomeReportWriter(outputDirectory, environmentVariables)
    }

    private fun saveAWorkingCopy(testOutcomeToBeStored: TestOutcome, workingCopy: Path) {
        BufferedOutputStream(FileOutputStream(workingCopy.toFile())).use { outputStream ->
            jsonConverter.toJson(testOutcomeToBeStored, outputStream)
            outputStream.flush()
        }
    }

    private fun workingCopyOf(reportName: String) = reportName + UUID.randomUUID().toString()

    private fun jsonOutputFor(storedTestOutcome: TestOutcome) = "outcome_" + storedTestOutcome.getReportName(ReportType.JSON)
}

fun withOutputDirectory(outputDirectory: Path) : JSONTestOutcomeReportWriter = JSONTestOutcomeReportWriter(outputDirectory)
