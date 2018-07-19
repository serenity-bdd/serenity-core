package net.serenitybdd.reports.asciidoc.configuration

import net.thucydides.core.ThucydidesSystemProperty
import java.nio.file.Path


/**
 * A utility class that provides information about report configuration, as provided in the configuration files
 * or via environment variables.
 */
sealed class SerenityReport {

    companion object {

        private const val DEFAULT_OUTPUT_DIRECTORY = "target/site/serenity"
        private const val DEFAULT_TITLE = "Serenity Living Documentation Report"

        fun outputDirectory() : ReportProperty<Path> {
            return PathReportProperty(ThucydidesSystemProperty.SERENITY_OUTPUT_DIRECTORY, DEFAULT_OUTPUT_DIRECTORY)
        }

        fun reportTitle() : ReportProperty<String> {
            return StringReportProperty(ThucydidesSystemProperty.SERENITY_PROJECT_NAME, DEFAULT_TITLE)
        }

        fun template() : ReportProperty<String> {
            return StringReportProperty(ThucydidesSystemProperty.SERENITY_PROJECT_NAME, DEFAULT_TITLE)
        }
    }
}