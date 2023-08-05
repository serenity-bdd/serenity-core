package net.serenitybdd.reports.asciidoc.configuration

import net.thucydides.model.util.EnvironmentVariables
import java.io.File


/**
 * Return the directory where Freemarker should look for templates.
 * This is either the templates/asciidoc directory on the classpath,
 * or the parent directory of the specified custom template.
 */
class TemplateDirectoryProperty : ReportProperty<File> {

    companion object {
        private const val DEFAULT_TEMPLATE_DIRECTORY = "templates/asciidoc"
    }

    override fun configuredIn(environmentVariables: EnvironmentVariables) : File {
        val customTemplate = CustomReportTemplateProperty().configuredIn(environmentVariables)
        return if (customTemplate == null) {
            File(ClassLoader.getSystemResource(DEFAULT_TEMPLATE_DIRECTORY).path)
        } else {
            File(customTemplate).parentFile
        }
    }
}
