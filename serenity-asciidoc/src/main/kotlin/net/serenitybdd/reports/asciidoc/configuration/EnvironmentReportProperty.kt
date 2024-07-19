package net.serenitybdd.reports.asciidoc.configuration

import net.thucydides.model.util.EnvironmentVariables

class EnvironmentReportProperty(val property: String, val defaultValue: String) : ReportProperty<String> {
    override fun configuredIn(environmentVariables: EnvironmentVariables) : String {
        return environmentVariables.getProperty(property, defaultValue)
    }
}
