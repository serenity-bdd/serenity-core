package net.serenitybdd.reports.navigator

import net.serenitybdd.reports.configuration.ConfiguredOutputDirectoryProperty
import net.serenitybdd.reports.configuration.ReportProperty
import java.nio.file.Path


/**
 * A utility class that provides information about report configuration, as provided in the configuration files
 * or via environment variables.
 */
sealed class ReportNavigator {

    companion object {
        fun outputDirectory(): ReportProperty<Path> = ConfiguredOutputDirectoryProperty()
    }
}
