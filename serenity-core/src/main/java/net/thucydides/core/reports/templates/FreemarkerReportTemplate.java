package net.thucydides.core.reports.templates;

import freemarker.core.Environment;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class FreemarkerReportTemplate implements ReportTemplate {

    private final Template template;

    private static final Logger LOGGER = LoggerFactory.getLogger(FreemarkerReportTemplate.class);

    public FreemarkerReportTemplate(final Configuration configuration, final String templateFile) throws IOException, TemplateMergeException {
        try {
            template = configuration.getTemplate(templateFile);
        } catch (ParseException parseException) {
            throw new TemplateMergeException("Parsing error in template", parseException);
        }
    }

    public void merge(Map<String, Object> context, Writer writer) throws TemplateMergeException {
        try {
            Environment environment = template.createProcessingEnvironment(context, writer);
            environment.setOutputEncoding(StandardCharsets.UTF_8.name());
            environment.setTemplateExceptionHandler(new TemplateExceptionHandler() {
                @Override
                public void handleTemplateException(TemplateException te, Environment env, Writer out) throws TemplateException {
                    LOGGER.warn("Report generation failed", te);
                    throw te;
                }
            });
            environment.process();
        } catch (TemplateException templateException) {
            LOGGER.error("Syntax error in report template: {}\n{}", templateException.getMessage(), templateException.getFTLInstructionStack());
            throw new TemplateMergeException("Failed to process FreeMarker template", templateException);
        } catch (IOException e) {
            throw new TemplateMergeException("Could not read FreeMarker template", e);
        } catch (NumberFormatException e) {
            throw new TemplateMergeException("Number format exception during template merge", e);
        }
    }
}
