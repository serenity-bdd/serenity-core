package net.serenitybdd.reports.configuration

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration
import net.thucydides.model.ThucydidesSystemProperty
import net.thucydides.model.util.EnvironmentVariables

class StringListReportProperty(val property: String, val defaultValues: List<String> = listOf()) : ReportProperty<List<String>> {
    constructor(property: ThucydidesSystemProperty, defaultValues: List<String> = listOf()) : this(property.toString(), defaultValues)

    override fun configuredIn(environmentVariables: EnvironmentVariables): List<String> {
        val propertyValue = EnvironmentSpecificConfiguration.from(environmentVariables)
                .getOptionalProperty(property)
                .orElse(defaultValues.joinToString(","))

        return propertyValue.split(",")
                .map { value -> value.trim() }
                .filter { value -> value.isNotEmpty() }
    }
}
