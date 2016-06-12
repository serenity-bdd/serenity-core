package net.thucydides.core.reports.templates;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Version;

import java.util.Locale;

/**
 * Manages velocity templates.
 *
 */
public class FreeMarkerTemplateManager implements TemplateManager {

    public static final Version FREEMARKER_VERSION = new Version(2, 3, 23);
    Configuration cfg;

    public FreeMarkerTemplateManager() throws Exception {
        cfg = new Configuration(FREEMARKER_VERSION);
        cfg.setNumberFormat("0.######");
        cfg.setLocale(Locale.UK);
        cfg.setClassForTemplateLoading(getClass(), "/");
        cfg.setObjectWrapper(new DefaultObjectWrapper());
    }

    public ReportTemplate getTemplateFrom(final String template) throws Exception {
        return new FreemarkerReportTemplate(cfg, template);
    }

}
