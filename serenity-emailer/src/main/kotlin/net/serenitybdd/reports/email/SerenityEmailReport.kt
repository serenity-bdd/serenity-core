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
        private const val DEFAULT_TEMPLATE = "templates/email/serenity-summary-report.html"

        fun outputDirectory() : ReportProperty<Path> {
            return PathReportProperty(ThucydidesSystemProperty.SERENITY_OUTPUT_DIRECTORY, DEFAULT_OUTPUT_DIRECTORY)
        }

        fun reportTitle() : ReportProperty<String> {
            return StringReportProperty(ThucydidesSystemProperty.SERENITY_PROJECT_NAME, DEFAULT_TITLE)
        }

        fun template() : ReportProperty<String> {
            return TemplateFileProperty(DEFAULT_TEMPLATE)
        }

        fun templateDirectory() : ReportProperty<File> {
            return TemplateDirectoryProperty()
        }
    }
}