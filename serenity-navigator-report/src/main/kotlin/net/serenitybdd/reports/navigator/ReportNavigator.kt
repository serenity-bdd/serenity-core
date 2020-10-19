package net.serenitybdd.reports.navigator

import net.serenitybdd.reports.configuration.*
import net.thucydides.core.ThucydidesSystemProperty.*
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