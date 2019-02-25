package net.serenitybdd.reports.configuration

import net.thucydides.core.ThucydidesSystemProperty
import net.thucydides.core.util.EnvironmentVariables

class BooleanReportProperty(val property: String, val defaultValue: Boolean) : ReportProperty<Boolean> {
    constructor(property: ThucydidesSystemProperty, defaultValue: Boolean) : this(property.toString(), defaultValue)

    override fun configuredIn(environmentVariables: EnvironmentVariables) : Boolean {
        return environmentVariables.getPropertyAsBoolean(property, defaultValue)
    }
}