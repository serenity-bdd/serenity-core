package net.serenitybdd.reports.configuration

import net.thucydides.core.util.EnvironmentVariables
import java.io.File

/**
 * Return the directory where Freemarker should look for templates.
 * This is either the templates/asciidoc directory on the classpath,
 * or the parent directory of the specified custom template.
 */
class TemplateDirectoryProperty(private val defaultTemplateDirectory: String) : ReportProperty<File> {

    override fun configuredIn(environmentVariables: EnvironmentVariables) : File {
        val customTemplate = CustomReportTemplateProperty().configuredIn(environmentVariables)
        return if (customTemplate == null) {
            File(ClassLoader.getSystemResource(defaultTemplateDirectory).path)
        } else {
            File(customTemplate).parentFile
        }
    }
}