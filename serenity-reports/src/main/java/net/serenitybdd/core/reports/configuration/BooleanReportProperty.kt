package net.serenitybdd.core.reports.configuration

import serenitymodel.net.serenitybdd.core.environment.EnvironmentSpecificConfiguration
import serenitymodel.net.thucydides.core.ThucydidesSystemProperty
import serenitymodel.net.thucydides.core.util.EnvironmentVariables

class BooleanReportProperty(val property: String, val defaultValue: Boolean) : ReportProperty<Boolean> {
    constructor(property: ThucydidesSystemProperty, defaultValue: Boolean) : this(property.toString(), defaultValue)

    override fun configuredIn(environmentVariables: EnvironmentVariables) : Boolean = valueDefinedIn(environmentVariables, property, defaultValue)

    private fun valueDefinedIn(environmentVariables: EnvironmentVariables, propertyName: String, defaultValue: Boolean) =
       EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty(propertyName)
                                                           .orElse(defaultValue.toString())
                                                           .toBoolean();
}