package net.serenitybdd.reports.configuration

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration
import net.thucydides.core.util.EnvironmentVariables
import java.io.File


class TemplateFileProperty(val defaultTemplate : String,
                           val templateProperty : String,
                           val templateDirectoryProperty : String = "") : ReportProperty<String> {

    override fun configuredIn(environmentVariables: EnvironmentVariables) : String {
        return EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty(templateProperty).orElse(defaultTemplate);
    }
}