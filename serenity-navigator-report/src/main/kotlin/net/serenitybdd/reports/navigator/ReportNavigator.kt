package net.serenitybdd.reports.navigator

import net.serenitybdd.model.environment.ConfiguredEnvironment
import net.serenitybdd.reports.configuration.*
import net.thucydides.model.ThucydidesSystemProperty.*
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
