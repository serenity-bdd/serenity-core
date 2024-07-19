package net.serenitybdd.reports.configuration

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration
import net.thucydides.model.util.EnvironmentVariables

class CustomReportTemplateProperty : ReportProperty<String?> {

    companion object {
        private const val CUSTOM_TEMPLATE_PROPERTY = "asciidoc.template"
    }

    override fun configuredIn(environmentVariables: EnvironmentVariables) : String? {
        return EnvironmentSpecificConfiguration.from(environmentVariables)
                                                .getOptionalProperty(CUSTOM_TEMPLATE_PROPERTY)
                                                .orElse(null)
    }
}
