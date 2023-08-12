package net.serenitybdd.reports.configuration

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration
import net.thucydides.model.ThucydidesSystemProperty
import net.thucydides.model.util.EnvironmentVariables

class IntReportProperty(val property: String, val defaultValue: Int) : ReportProperty<Int> {
    constructor(property: ThucydidesSystemProperty, defaultValue: Int) : this(property.toString(), defaultValue)

    override fun configuredIn(environmentVariables: EnvironmentVariables) : Int {
        return EnvironmentSpecificConfiguration.from(environmentVariables)
                .getOptionalProperty(property)
                .map { value -> value.toInt() }
                .orElse(defaultValue)
    }
}
