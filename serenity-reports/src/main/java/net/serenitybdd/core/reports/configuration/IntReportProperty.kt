package net.serenitybdd.core.reports.configuration

import serenitymodel.net.serenitybdd.core.environment.EnvironmentSpecificConfiguration
import serenitymodel.net.thucydides.core.ThucydidesSystemProperty
import serenitymodel.net.thucydides.core.util.EnvironmentVariables

class IntReportProperty(val property: String, val defaultValue: Int) : ReportProperty<Int> {
    constructor(property: ThucydidesSystemProperty, defaultValue: Int) : this(property.toString(), defaultValue)

    override fun configuredIn(environmentVariables: EnvironmentVariables) : Int {
        //return environmentVariables.getPropertyAsInteger(property, defaultValue)
        return EnvironmentSpecificConfiguration.from(environmentVariables)
                .getOptionalProperty(property)
                .map { value -> value.toInt() }
                .orElse(defaultValue)
    }
}