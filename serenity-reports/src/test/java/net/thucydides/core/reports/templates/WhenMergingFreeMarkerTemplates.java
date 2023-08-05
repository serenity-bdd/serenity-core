package net.thucydides.core.reports.templates;


import net.thucydides.model.reports.templates.FreeMarkerTemplateManager;
import net.thucydides.model.reports.templates.ReportTemplate;
import net.thucydides.model.reports.templates.TemplateMergeException;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenMergingFreeMarkerTemplates {

    @Test
    public void should_load_freemarker_template_from_classpath() throws Exception {
        FreeMarkerTemplateManager templateManager = new FreeMarkerTemplateManager();
        ReportTemplate template = templateManager.getTemplateFrom("templates/test.ftl");

        Map<String, Object> context = new HashMap<String, Object>();
        context.put("name","Joe");
        context.put("age",20);
        StringWriter sw = new StringWriter();
        template.merge(context, sw);

        assertThat(sw.toString(), is("Hi Joe, aged 20. Next year you will be 21"));

    }

//    @Test(expected = TemplateMergeException.class)
//    public void should_handle_data_errors_in_template() throws Exception {
//        FreeMarkerTemplateManager templateManager = new FreeMarkerTemplateManager();
//        ReportTemplate template = templateManager.getTemplateFrom("templates/test.ftl");
//
//        Map<String, Object> context = new HashMap<String, Object>();
//        context.put("name","Joe");
//        context.put("age",null);
//        StringWriter sw = new StringWriter();
//        template.merge(context, sw);
//
//    }
    @Test(expected = FileNotFoundException.class)
    public void should_throw_exception_if_template_file_not_available() throws Exception {
        FreeMarkerTemplateManager templateManager = new FreeMarkerTemplateManager();
        ReportTemplate template = templateManager.getTemplateFrom("templates/does-not-exist.ftl");

        Map<String, Object> context = new HashMap<String, Object>();
        context.put("name","Joe");
        context.put("age","20");
        StringWriter sw = new StringWriter();
        template.merge(context, sw);
    }

    @Test(expected = TemplateMergeException.class)
    public void should_throw_exception_if_template_file_contains_an_error() throws Exception {
        FreeMarkerTemplateManager templateManager = new FreeMarkerTemplateManager();
        ReportTemplate template = templateManager.getTemplateFrom("templates/test-with-error.ftl");

        Map<String, Object> context = new HashMap<String, Object>();
        StringWriter sw = new StringWriter();
        template.merge(context, sw);

    }

}
