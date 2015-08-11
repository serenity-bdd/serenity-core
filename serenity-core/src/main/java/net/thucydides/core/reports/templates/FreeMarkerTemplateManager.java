package net.thucydides.core.reports.templates;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;

/**
 * Manages velocity templates.
 *
 */
public class FreeMarkerTemplateManager implements TemplateManager {

    Configuration cfg;

    public FreeMarkerTemplateManager() throws Exception {
        cfg = new Configuration();
        cfg.setNumberFormat("0.######");
        cfg.setClassForTemplateLoading(getClass(), "/");
        cfg.setObjectWrapper(new DefaultObjectWrapper());
    }

    public ReportTemplate getTemplateFrom(final String template) throws Exception {
        return new FreemarkerReportTemplate(cfg, template);
    }

}
