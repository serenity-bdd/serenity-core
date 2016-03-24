package net.thucydides.core.reports.json

import com.google.common.base.Optional
import net.thucydides.core.ThucydidesSystemProperty
import net.thucydides.core.guice.Injectors
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.reports.AcceptanceTestLoader
import net.thucydides.core.reports.OutcomeFormat
import net.thucydides.core.util.EnvironmentVariables
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.nio.file.Path

class JSONTestOutcomeReader : AcceptanceTestLoader {

    protected val environmentVariables: EnvironmentVariables
            = Injectors.getInjector().getInstance(EnvironmentVariables::class.java)
    protected val jsonConverter: JSONConverter
            = Injectors.getInjector().getInstance(JSONConverter::class.java)
    protected val encoding
            = ThucydidesSystemProperty.THUCYDIDES_REPORT_ENCODING.from(environmentVariables, StandardCharsets.UTF_8.name())

    private val logger = LoggerFactory.getLogger(JSONTestOutcomeReader::class.java)

    fun load(reportFile : Path) : Optional<TestOutcome> {

        try {
            BufferedReader(InputStreamReader(FileInputStream(reportFile.toFile()), encoding)).use({ input ->
                val fromJson = jsonConverter.fromJson(input)
                return Optional.fromNullable<TestOutcome>(fromJson)
            })
        } catch (e: Throwable) {
            logger.warn("This file was not a valid JSON Serenity test report: {}{}{}",
                        reportFile.fileName, System.lineSeparator(), e.message)
            return Optional.absent<TestOutcome>()
        }
    }

    override fun loadReportFrom(reportFile: File?): Optional<TestOutcome>? {
        throw UnsupportedOperationException()
    }

    override fun loadReportsFrom(outputDirectory: File?): MutableList<TestOutcome>? {
        throw UnsupportedOperationException()
    }

    override fun getFormat(): Optional<OutcomeFormat>? {
        throw UnsupportedOperationException()
    }

}
