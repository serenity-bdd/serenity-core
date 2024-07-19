package net.thucydides.model.reports.templates;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Version;

import java.util.Locale;

/**
 * Manages Freemarker templates.
 */
public class FreeMarkerTemplateManager {

    public static final Version FREEMARKER_VERSION = new Version(2, 3, 23);
    private Configuration configuration;

    public FreeMarkerTemplateManager() {
        configuration = new Configuration(FREEMARKER_VERSION);
        configuration.setNumberFormat("0.######");
        configuration.setLocale(Locale.UK);
        configuration.setClassForTemplateLoading(getClass(), "/");
        configuration.setTemplateUpdateDelayMilliseconds(1000 * 60 * 60);
        configuration.setObjectWrapper(new DefaultObjectWrapperBuilder(FREEMARKER_VERSION).build());
    }

    public ReportTemplate getTemplateFrom(final String template) throws Exception {
        return new FreemarkerReportTemplate(configuration, template);
    }
}
