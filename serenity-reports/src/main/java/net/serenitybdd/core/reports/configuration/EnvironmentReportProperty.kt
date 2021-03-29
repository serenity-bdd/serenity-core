package net.serenitybdd.core.reports.configuration

import serenitymodel.net.serenitybdd.core.environment.EnvironmentSpecificConfiguration
import serenitymodel.net.thucydides.core.util.EnvironmentVariables

class EnvironmentReportProperty(val property: String, val defaultValue: String) : ReportProperty<String> {
    override fun configuredIn(environmentVariables: EnvironmentVariables) : String {
//        return environmentVariables.getProperty(property, defaultValue)
        return EnvironmentSpecificConfiguration.from(environmentVariables)
                .getOptionalProperty(property)
                .orElse(defaultValue)
    }
}