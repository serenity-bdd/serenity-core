package net.serenitybdd.reports.configuration

import net.thucydides.core.ThucydidesSystemProperty
import net.thucydides.core.util.EnvironmentVariables

class StringListReportProperty(val property: String, val defaultValues: List<String> = listOf()) : ReportProperty<List<String>> {
    constructor(property: ThucydidesSystemProperty, defaultValues: List<String> = listOf()) : this(property.toString(), defaultValues)

    override fun configuredIn(environmentVariables: EnvironmentVariables) : List<String> {
        return environmentVariables.getProperty(property, defaultValues.joinToString(","))
                .split(",")
                .map { value -> value.trim() }
                .filter { value -> value.isNotEmpty() }
    }
}