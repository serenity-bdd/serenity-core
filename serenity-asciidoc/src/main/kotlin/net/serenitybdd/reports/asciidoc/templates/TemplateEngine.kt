package net.serenitybdd.reports.asciidoc.templates

import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler
import java.io.File
import java.io.Writer


object TemplateEngine {

    val configuration : Configuration = Configuration(Configuration.VERSION_2_3_28)

    init {
        configuration.setDirectoryForTemplateLoading(templateDirectory())
        configuration.setDefaultEncoding("UTF-8")
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER)
        configuration.setLogTemplateExceptions(false)
    }

    fun merge(template: String, fields : Map<String, Any>, writer : Writer) {
        configuration.getTemplate(template)
                     .process(fields, writer)
    }

    fun templateDirectory() : File {
        println(File(ClassLoader.getSystemResource("templates/asciidoc").path))
        return File(ClassLoader.getSystemResource("templates/asciidoc").path)
    }

}