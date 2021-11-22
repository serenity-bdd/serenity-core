package net.serenitybdd.reports.json

import net.serenitybdd.reports.configuration.*
import net.thucydides.core.ThucydidesSystemProperty.*
import java.nio.file.Path


/**
 * A utility class that provides information about report configuration, as provided in the configuration files
 * or via environment variables.
 */
sealed class JsonSummaryReport {

    companion object {

        private const val DEFAULT_OUTPUT_DIRECTORY = "target/site/serenity"
        private const val DEFAULT_TITLE = "Serenity Summary Report"
        private const val DEFAULT_TAGTYPE_TITLE = "Category"
        private const val DEFAULT_TEMPLATE = "templates/json-summary.ftl"
        private const val DEFAULT_SCOREBOARD_SIZE= 5

        fun outputDirectory() : ReportProperty<Path> = PathReportProperty(SERENITY_OUTPUT_DIRECTORY, DEFAULT_OUTPUT_DIRECTORY)

        fun reportTitle() : ReportProperty<String> = StringReportProperty(SERENITY_SUMMARY_REPORT_TITLE, DEFAULT_TITLE)

        fun reportLink() : ReportProperty<String> = StringReportProperty(SERENITY_REPORT_URL, "")

        fun tagCategoryTitle() : ReportProperty<String> = StringReportProperty(REPORT_TAGTYPE_TITLE, DEFAULT_TAGTYPE_TITLE)

        fun scoreboardSize() : ReportProperty<Int> = IntReportProperty(REPORT_SCOREBOARD_SIZE, DEFAULT_SCOREBOARD_SIZE)

        fun template() : ReportProperty<String> = TemplateFileProperty(DEFAULT_TEMPLATE)

        fun showFullTestResults() : ReportProperty<Boolean> = BooleanReportProperty(SHOW_FULL_TEST_RESULTS, true)

        fun tagTypes() : ReportProperty<List<String>> = ReportTagTypeProperty()

    }
}