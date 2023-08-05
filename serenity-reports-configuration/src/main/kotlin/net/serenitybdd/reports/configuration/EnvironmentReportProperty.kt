package net.serenitybdd.reports.configuration

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration
import net.thucydides.model.util.EnvironmentVariables

class EnvironmentReportProperty(val property: String, val defaultValue: String) : ReportProperty<String> {
    override fun configuredIn(environmentVariables: EnvironmentVariables) : String {
//        return environmentVariables.getProperty(property, defaultValue)
        return EnvironmentSpecificConfiguration.from(environmentVariables)
                .getOptionalProperty(property)
                .orElse(defaultValue)
    }
}
