package net.serenitybdd.reports.asciidoc.configuration

import net.thucydides.core.ThucydidesSystemProperty
import net.thucydides.core.util.EnvironmentVariables
import java.nio.file.Path
import java.nio.file.Paths

class StringReportProperty(val property: ThucydidesSystemProperty, val defaultValue: String) : ReportProperty<String> {
    override fun configuredIn(environmentVariables: EnvironmentVariables) : String {
        return environmentVariables.getProperty(property, defaultValue)
    }
}