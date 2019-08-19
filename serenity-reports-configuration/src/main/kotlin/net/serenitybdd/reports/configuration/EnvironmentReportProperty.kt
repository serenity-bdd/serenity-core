package net.serenitybdd.reports.configuration

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration
import net.thucydides.core.util.EnvironmentVariables

class EnvironmentReportProperty(val property: String, val defaultValue: String) : ReportProperty<String> {
    override fun configuredIn(environmentVariables: EnvironmentVariables) : String {
        return EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty(property).orElse(defaultValue)
//        environmentVariables.getProperty(property, defaultValue)
    }
}