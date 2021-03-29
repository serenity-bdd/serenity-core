package net.serenitybdd.reports.configuration

import serenitymodel.net.serenitybdd.core.environment.EnvironmentSpecificConfiguration
import serenitymodel.net.thucydides.core.util.EnvironmentVariables

class CustomReportTemplateProperty(val templateProperty: String) : ReportProperty<String?> {

    override fun configuredIn(environmentVariables: EnvironmentVariables) : String? {
        return EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty(templateProperty).orElse(null);
    }
}