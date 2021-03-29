package net.serenitybdd.core.reports.configuration

import serenitymodel.net.serenitybdd.core.environment.EnvironmentSpecificConfiguration
import serenitymodel.net.thucydides.core.util.EnvironmentVariables

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