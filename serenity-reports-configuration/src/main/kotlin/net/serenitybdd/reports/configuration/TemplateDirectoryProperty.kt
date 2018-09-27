package net.serenitybdd.reports.configuration

import net.thucydides.core.util.EnvironmentVariables
import java.io.File


/**
 * Return the directory where Freemarker should look for templates.
 * This is either the templates/asciidoc directory on the classpath,
 * or the parent directory of the specified custom template.
 */
class TemplateDirectoryProperty(val defaultTemplateDirectory : String,
                                val templateProperty: String) : ReportProperty<File> {

    companion object {
        fun definedByProperty(propertyName : String) : TemplateDirectoryPropertyBuilder = TemplateDirectoryPropertyBuilder(propertyName)
    }

    override fun configuredIn(environmentVariables: EnvironmentVariables) : File {
        val customTemplate = CustomReportTemplateProperty(templateProperty).configuredIn(environmentVariables)
        return if (customTemplate == null) {
            File(ClassLoader.getSystemResource(defaultTemplateDirectory).path)
        } else {
            File(customTemplate).parentFile
        }
    }

    class TemplateDirectoryPropertyBuilder(val propertyName: String) {
        fun withDefaultValueOf(defaultValue: String) : TemplateDirectoryProperty = TemplateDirectoryProperty(defaultValue, propertyName)
    }
}

