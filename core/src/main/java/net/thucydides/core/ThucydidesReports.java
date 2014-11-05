package net.thucydides.core;

import net.thucydides.core.reports.ReportService;
import net.thucydides.core.webdriver.Configuration;

/**
 * Provide supporting methods for creating report listeners and generating reports.
 */
public class ThucydidesReports {

    public static ReportService getReportService(Configuration systemConfiguration) {
        return new ReportService(systemConfiguration.getOutputDirectory(), ReportService.getDefaultReporters());
    }

    public static ThucydidesListeners setupListeners(Configuration systemConfiguration) {
        return new ThucydidesListeners(systemConfiguration);
    }
}
