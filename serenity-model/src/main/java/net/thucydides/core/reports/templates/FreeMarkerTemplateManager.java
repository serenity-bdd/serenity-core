package net.thucydides.core.reports.templates;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Version;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages velocity templates.
 *
 */
public class FreeMarkerTemplateManager implements TemplateManager {

    public static final Version FREEMARKER_VERSION = new Version(2, 3, 23);
    Configuration cfg;

    Map<String, ReportTemplate> templateCache = new ConcurrentHashMap();

    public FreeMarkerTemplateManager() {
        cfg = new Configuration(FREEMARKER_VERSION);
        cfg.setNumberFormat("0.######");
        cfg.setLocale(Locale.UK);
        cfg.setClassForTemplateLoading(getClass(), "/");
        cfg.setObjectWrapper(new DefaultObjectWrapper());
    }

    public ReportTemplate getTemplateFrom(final String template) throws Exception {
        if (!templateCache.containsKey(template)) {
            templateCache.put(template, new FreemarkerReportTemplate(cfg, template));
        }
        return templateCache.get(template);
    }

}
