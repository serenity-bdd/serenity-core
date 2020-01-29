package net.serenitybdd.reports.email

import net.serenitybdd.reports.configuration.*
import net.thucydides.core.ThucydidesSystemProperty.*
import java.io.File
import java.nio.file.Path


/**
 * A utility class that provides information about report configuration, as provided in the configuration files
 * or via environment variables.
 */
sealed class SerenitySinglePageReport {

    companion object {

        private const val DEFAULT_OUTPUT_DIRECTORY = "target/site/serenity"
        private const val DEFAULT_TITLE = "Serenity Summary Report"
        private const val DEFAULT_TAGTYPE_TITLE = "Category"
        private const val DEFAULT_TEMPLATE = "templates/email/serenity-summary-report-inlined.html"
        private const val DEFAULT_SCOREBOARD_SIZE = 5
        private const val DEFAULT_TEMPLATE_DIRECTORY = "templates/email"

        fun outputDirectory(): ReportProperty<Path> = PathReportProperty(SERENITY_OUTPUT_DIRECTORY, DEFAULT_OUTPUT_DIRECTORY)
        fun sourceDirectory(): ReportProperty<Path> = PathReportProperty(SERENITY_OUTPUT_DIRECTORY, DEFAULT_OUTPUT_DIRECTORY)

        fun reportTitle(): ReportProperty<String> = StringReportProperty(SERENITY_SUMMARY_REPORT_TITLE, DEFAULT_TITLE)

        fun reportLink(): ReportProperty<String> = StringReportProperty(SERENITY_REPORT_URL, "")

        fun tagCategoryTitle(): ReportProperty<String> = StringReportProperty(REPORT_TAGTYPE_TITLE, DEFAULT_TAGTYPE_TITLE)

        fun scoreboardSize(): ReportProperty<Int> = IntReportProperty(REPORT_SCOREBOARD_SIZE, DEFAULT_SCOREBOARD_SIZE)

        fun template(): ReportProperty<String> = TemplateFileProperty(DEFAULT_TEMPLATE)

        fun templateDirectory(): ReportProperty<File> = TemplateDirectoryProperty(DEFAULT_TEMPLATE_DIRECTORY)

        fun showFullTestResults(): ReportProperty<Boolean> = BooleanReportProperty(SHOW_FULL_TEST_RESULTS, true)

        fun tagTypes(): ReportProperty<List<String>> = StringListReportProperty(REPORT_TAGTYPES, listOf("feature"))

    }
}