package net.serenitybdd.reports.configuration

import net.thucydides.core.util.EnvironmentVariables
import java.io.File


/**
 * Return the directory where Freemarker should look for templates.
 * This is either the templates/asciidoc directory on the classpath,
 * or the parent directory of the specified custom template.
 */
class TemplateFileProperty(val defaultTemplate : String) : ReportProperty<String> {

    override fun configuredIn(environmentVariables: EnvironmentVariables) : String {
        val customTemplate = CustomReportTemplateProperty().configuredIn(environmentVariables)

        return if (customTemplate == null) {
            defaultTemplate
        } else {
            File(customTemplate).name
        }
    }
}