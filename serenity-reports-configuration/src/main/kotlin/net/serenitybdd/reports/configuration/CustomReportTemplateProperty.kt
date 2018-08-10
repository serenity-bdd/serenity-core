package net.serenitybdd.reports.configuration

import net.thucydides.core.util.EnvironmentVariables

class CustomReportTemplateProperty(val templateProperty: String) : ReportProperty<String?> {

    override fun configuredIn(environmentVariables: EnvironmentVariables) : String? {
        return environmentVariables.getProperty(templateProperty)
    }
}