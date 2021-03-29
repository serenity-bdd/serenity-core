package net.serenitybdd.reports.configuration

import serenitymodel.net.serenitybdd.core.environment.EnvironmentSpecificConfiguration
import serenitymodel.net.thucydides.core.ThucydidesSystemProperty
import serenitymodel.net.thucydides.core.util.EnvironmentVariables

class StringReportProperty(val property: String, val defaultValue: String = "") : ReportProperty<String> {
    constructor(property: ThucydidesSystemProperty, defaultValue: String) : this(property.toString(), defaultValue)

    override fun configuredIn(environmentVariables: EnvironmentVariables) : String {
        return EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty(property).orElse(defaultValue);
//        return environmentVariables.getProperty(property, defaultValue)
    }
}