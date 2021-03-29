package net.serenitybdd.reports.navigator

import net.serenitybdd.core.reports.configuration.PathReportProperty
import net.serenitybdd.core.reports.configuration.ReportProperty
import serenitymodel.net.thucydides.core.ThucydidesSystemProperty.*
import java.nio.file.Path


/**
 * A utility class that provides information about report configuration, as provided in the configuration files
 * or via environment variables.
 */
sealed class ReportNavigator {

    companion object {

        private const val DEFAULT_OUTPUT_DIRECTORY = "target/site/serenity"

        fun outputDirectory(): ReportProperty<Path> = PathReportProperty(SERENITY_OUTPUT_DIRECTORY, DEFAULT_OUTPUT_DIRECTORY)
    }
}