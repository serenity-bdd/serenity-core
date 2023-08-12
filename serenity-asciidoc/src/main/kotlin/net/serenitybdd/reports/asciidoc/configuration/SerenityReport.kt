package net.serenitybdd.reports.asciidoc.configuration

import net.thucydides.model.ThucydidesSystemProperty
import java.io.File
import java.nio.file.Path


/**
 * A utility class that provides information about report configuration, as provided in the configuration files
 * or via environment variables.
 */
sealed class SerenityReport {

    companion object {

        private const val DEFAULT_OUTPUT_DIRECTORY = "target/site/serenity"
        private const val DEFAULT_TITLE = "Serenity Living Documentation Report"
        private const val DEFAULT_TEMPLATE = "full-report.adoc.ftl"

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


//        fun templateBaseDirectory() : ReportProperty<String> {
//            return TemplateNameProperty(ThucydidesSystemProperty.SERENITY_PROJECT_NAME, DEFAULT_TITLE)
//        }
        // "asciidoc.template"
    }
}
