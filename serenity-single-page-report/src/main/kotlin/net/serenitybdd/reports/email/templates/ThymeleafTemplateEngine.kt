package net.serenitybdd.reports.email.templates

import net.thucydides.core.util.EnvironmentVariables
import org.thymeleaf.TemplateEngine
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import org.thymeleaf.templateresolver.DefaultTemplateResolver
import java.io.Writer


class ThymeleafTemplateEngine() {

    val templateEngine : TemplateEngine;

    init {
        templateEngine = TemplateEngine()
    }

    fun merge(template: String, fields : Map<String, Any>, writer : Writer) {
        val templateResolver = ClassLoaderTemplateResolver()
        templateEngine.setTemplateResolver(templateResolver)
        templateEngine.addDialect(Java8TimeDialect());

        val context = TestOutcomesContext()
        context.setVariables(fields)
        templateEngine.process(template, context, writer)
    }
}