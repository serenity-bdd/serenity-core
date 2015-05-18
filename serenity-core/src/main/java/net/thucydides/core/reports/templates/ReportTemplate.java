package net.thucydides.core.reports.templates;

import java.io.StringWriter;
import java.util.Map;

public interface ReportTemplate {
    void merge(Map<String,Object> context, StringWriter sw) throws TemplateMergeException;
}
