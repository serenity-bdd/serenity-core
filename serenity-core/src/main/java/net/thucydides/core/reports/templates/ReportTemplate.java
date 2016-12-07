package net.thucydides.core.reports.templates;

import java.io.Writer;
import java.util.Map;

public interface ReportTemplate {
    void merge(Map<String,Object> context, Writer sw) throws TemplateMergeException;
}
