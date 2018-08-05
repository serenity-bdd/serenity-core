package net.serenitybdd.reports.email

import net.serenitybdd.reports.configuration.*
import net.thucydides.core.ThucydidesSystemProperty
import java.io.File
import java.nio.file.Path


/**
 * A utility class that provides information about report configuration, as provided in the configuration files
 * or via environment variables.
 */
sealed class SerenityEmailReport {

    companion object {

        private const val DEFAULT_OUTPUT_DIRECTORY = "target/site/serenity"
        private const val DEFAULT_TITLE = "Serenity Summary Report"
        private const val DEFAULT_TEMPLATE = "templates/email/serenity-summary-report-inlined.html"
        private const val DEFAULT_SCOREBOARD_SIZE= 5

        fun outputDirectory() : ReportProperty<Path> {
            return PathReportProperty(ThucydidesSystemProperty.SERENITY_OUTPUT_DIRECTORY, DEFAULT_OUTPUT_DIRECTORY)
        }

        fun reportTitle() : ReportProperty<String> {
            return StringReportProperty(ThucydidesSystemProperty.SERENITY_PROJECT_NAME, DEFAULT_TITLE)
        }

        fun scoreboardSize() : ReportProperty<Int> {
            return IntReportProperty(ThucydidesSystemProperty.REPORT_SCOREBOARD_SIZE, DEFAULT_SCOREBOARD_SIZE)
        }

        fun template() : ReportProperty<String> {
            return TemplateFileProperty(DEFAULT_TEMPLATE)
        }

        fun templateDirectory() : ReportProperty<File> {
            return TemplateDirectoryProperty()
        }

        fun tagTypes() : ReportProperty<List<String>> {
            return StringListReportProperty(ThucydidesSystemProperty.REPORT_TAGTYPES, listOf("feature"))
        }

    }
}