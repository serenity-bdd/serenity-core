package net.serenitybdd.reports.configuration

import net.thucydides.core.util.EnvironmentVariables
import java.io.File


class TemplateFileProperty(val defaultTemplate : String, val templateProperty : String) : ReportProperty<String> {

    override fun configuredIn(environmentVariables: EnvironmentVariables) : String {
        return if (environmentVariables.aValueIsDefinedFor(templateProperty)) {
            environmentVariables.getProperty(templateProperty)
        } else {
            defaultTemplate
        }
    }
}