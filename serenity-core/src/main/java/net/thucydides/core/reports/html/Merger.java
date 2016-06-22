package net.thucydides.core.reports.html;

import net.thucydides.core.guice.Injectors;
import net.thucydides.core.reports.templates.ReportTemplate;
import net.thucydides.core.reports.templates.TemplateManager;

import java.io.StringWriter;
import java.util.Map;

public class Merger {
        final String templateFile;
        final TemplateManager templateManager;

        public Merger(final String templateFile) {
            this.templateFile = templateFile;
            this.templateManager = Injectors.getInjector().getInstance(TemplateManager.class);
        }

        public String usingContext(final Map<String, Object> context) {
            try {
                ReportTemplate template = templateManager.getTemplateFrom(templateFile);
                StringWriter sw = new StringWriter();
                template.merge(context, sw);
                return sw.toString();
            } catch (Exception e) {
                throw new RuntimeException("Failed to merge template: " + e.getMessage(), e);
            }
        }
    }