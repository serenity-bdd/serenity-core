package net.serenitybdd.reports.configuration

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration
import net.thucydides.core.util.EnvironmentVariables

class CustomReportTemplateProperty(val templateProperty: String) : ReportProperty<String?> {

    override fun configuredIn(environmentVariables: EnvironmentVariables) : String? {
        return EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty(templateProperty).orElse(null);
    }
}