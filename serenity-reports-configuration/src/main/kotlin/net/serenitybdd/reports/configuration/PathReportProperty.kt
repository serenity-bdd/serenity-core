package net.serenitybdd.reports.configuration

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration
import net.thucydides.model.ThucydidesSystemProperty
import net.thucydides.model.util.EnvironmentVariables
import java.nio.file.Path
import java.nio.file.Paths

class PathReportProperty(val property: ThucydidesSystemProperty, val defaultValue: String) : ReportProperty<Path> {
    override fun configuredIn(environmentVariables: EnvironmentVariables) : Path {
        return Paths.get(
            EnvironmentSpecificConfiguration.from(environmentVariables)
                .getOptionalProperty(property)
                .orElse(defaultValue)
        )
    }
}
