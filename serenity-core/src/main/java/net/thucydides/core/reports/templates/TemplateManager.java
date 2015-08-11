package net.thucydides.core.reports.templates;

/**
 * Created by IntelliJ IDEA.
 * User: johnsmart
 * Date: 7/09/11
 * Time: 11:24 AM
 * To change this template use File | Settings | File Templates.
 */
public interface TemplateManager {
    ReportTemplate getTemplateFrom(String template) throws Exception;
}
