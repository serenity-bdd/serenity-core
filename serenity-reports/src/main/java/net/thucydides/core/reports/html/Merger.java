package net.thucydides.core.reports.html;

import net.serenitybdd.core.di.ModelInfrastructure;
import net.thucydides.core.reports.templates.TemplateManager;

import java.io.Writer;
import java.util.Map;

public class Merger {
    final String templateFile;
    final TemplateManager templateManager;

    public Merger(final String templateFile) {
        this.templateFile = templateFile;
        this.templateManager = ModelInfrastructure.getTemplateManager();
    }

    public MergeBuilder withContext(final Map<String, Object> context) {
        return new MergeBuilder(context, templateManager, templateFile);
    }

    public static class MergeBuilder {

        private final Map<String, Object> context;
        private final TemplateManager templateManager;
        private final String templateFile;

        public MergeBuilder(Map<String, Object> context, TemplateManager templateManager, String templateFile) {
            this.context = context;
            this.templateManager = templateManager;
            this.templateFile = templateFile;
        }

        public void to(Writer writer) {
            try {
                TemplateManager.getTemplateFrom(templateFile).merge(context, writer);
            } catch (Exception e) {
                throw new RuntimeException("Failed to merge template: " + e.getMessage(), e);
            }
        }
    }
}
