package net.serenitybdd.reports.asciidoc.templates

import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler
import net.serenitybdd.reports.asciidoc.configuration.SerenityReport
import net.serenitybdd.reports.asciidoc.configuration.TemplateDirectoryProperty
import net.thucydides.model.util.EnvironmentVariables
import java.io.Writer


class FreemarkerTemplateEngine(environmentVariables: EnvironmentVariables) {

    val configuration : Configuration = Configuration(Configuration.VERSION_2_3_28)

    init {
        val templateDirectory = SerenityReport.templateDirectory().configuredIn(environmentVariables)
        configuration.setDirectoryForTemplateLoading(templateDirectory)
        configuration.defaultEncoding = "UTF-8"
        configuration.templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
        configuration.logTemplateExceptions = false
    }

    fun merge(template: String, fields : Map<String, Any>, writer : Writer) {
        configuration.getTemplate(template)
                     .process(fields, writer)
    }
}
