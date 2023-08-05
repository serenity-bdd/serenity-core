package net.serenitybdd.reports.asciidoc.configuration

import net.thucydides.model.ThucydidesSystemProperty
import net.thucydides.model.util.EnvironmentVariables
import java.nio.file.Path
import java.nio.file.Paths

class PathReportProperty(val property: ThucydidesSystemProperty, val defaultValue: String) : ReportProperty<Path> {
    override fun configuredIn(environmentVariables: EnvironmentVariables) : Path {
        return Paths.get(environmentVariables.getProperty(property, defaultValue))
    }
}
