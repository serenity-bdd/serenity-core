package net.thucydides.core.reports.templates;

public class TemplateManager {

    private static final FreeMarkerTemplateManager freemarkerTemplates = new FreeMarkerTemplateManager();

    public static ReportTemplate getTemplateFrom(String template) throws Exception {
        return freemarkerTemplates.getTemplateFrom(template);
    }
}
