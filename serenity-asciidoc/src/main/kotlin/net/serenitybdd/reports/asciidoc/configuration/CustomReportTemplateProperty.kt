package net.serenitybdd.reports.asciidoc.configuration

import net.thucydides.core.util.EnvironmentVariables

class CustomReportTemplateProperty : ReportProperty<String?> {

    companion object {
        private const val CUSTOM_TEMPLATE_PROPERTY = "asciidoc.template"
    }

    override fun configuredIn(environmentVariables: EnvironmentVariables) : String? {
        return environmentVariables.getProperty(CUSTOM_TEMPLATE_PROPERTY)
    }
}