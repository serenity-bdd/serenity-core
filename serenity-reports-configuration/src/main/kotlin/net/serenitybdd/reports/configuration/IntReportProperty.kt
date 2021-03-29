package net.serenitybdd.reports.configuration

import serenitymodel.net.serenitybdd.core.environment.EnvironmentSpecificConfiguration
import serenitymodel.net.thucydides.core.ThucydidesSystemProperty
import serenitymodel.net.thucydides.core.util.EnvironmentVariables

class IntReportProperty(val property: String, val defaultValue: Int = 0) : ReportProperty<Int> {
    constructor(property: ThucydidesSystemProperty, defaultValue: Int = 0) : this(property.toString(), defaultValue)

    override fun configuredIn(environmentVariables: EnvironmentVariables) : Int {
        return EnvironmentSpecificConfiguration.from(environmentVariables)
                .getOptionalProperty(property)
                .map { value -> value.toInt() }
                .orElse(defaultValue)
//        return environmentVariables.getPropertyAsInteger(property, defaultValue)
    }
}