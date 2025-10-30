package net.serenitybdd.reports.email.templates

import org.thymeleaf.TemplateEngine
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import java.io.Writer


class ThymeleafTemplateEngine {

    val templateEngine : TemplateEngine

    init {
        templateEngine = TemplateEngine()
    }

    fun merge(template: String, fields : Map<String, Any>, writer : Writer) {
        val templateResolver = ClassLoaderTemplateResolver()
        templateEngine.setTemplateResolver(templateResolver)
        templateEngine.addDialect(Java8TimeDialect())

        val context = TestOutcomesContext()
        context.setVariables(fields)
        templateEngine.process(template, context, writer)
    }
}
