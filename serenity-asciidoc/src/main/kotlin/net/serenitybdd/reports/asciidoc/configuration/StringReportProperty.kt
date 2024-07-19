package net.serenitybdd.reports.asciidoc.configuration

import net.thucydides.model.ThucydidesSystemProperty
import net.thucydides.model.util.EnvironmentVariables
import java.nio.file.Path
import java.nio.file.Paths

class StringReportProperty(val property: String, val defaultValue: String) : ReportProperty<String> {
    constructor(property: ThucydidesSystemProperty, defaultValue: String) : this(property.toString(), defaultValue)

    override fun configuredIn(environmentVariables: EnvironmentVariables) : String {
        return environmentVariables.getProperty(property, defaultValue)
    }
}
