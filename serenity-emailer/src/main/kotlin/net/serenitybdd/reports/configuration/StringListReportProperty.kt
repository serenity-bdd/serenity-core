package net.serenitybdd.reports.configuration

import net.thucydides.core.ThucydidesSystemProperty
import net.thucydides.core.util.EnvironmentVariables

class StringListReportProperty(val property: String) : ReportProperty<List<String>> {
    constructor(property: ThucydidesSystemProperty) : this(property.toString())

    override fun configuredIn(environmentVariables: EnvironmentVariables) : List<String> {
        return environmentVariables.getProperty(property, "")
                .split(",")
                .map { value -> value.trim() }
                .filter { value -> value.isNotEmpty() }
    }
}