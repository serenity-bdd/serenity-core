package serenitycore.net.thucydides.core;

import serenitycore.net.serenitybdd.core.SerenityListeners;
import serenitycore.net.serenitybdd.core.SerenityReports;
import serenitymodel.net.thucydides.core.reports.ReportService;
import serenitymodel.net.thucydides.core.webdriver.Configuration;

/** @deprecated Use SerenityListeners instead
 *
 */
public class ThucydidesReports {
    public static ReportService getReportService(Configuration systemConfiguration) {
        return SerenityReports.getReportService(systemConfiguration);
    }

    public static SerenityListeners setupListeners(Configuration systemConfiguration) {
        return SerenityReports.setupListeners(systemConfiguration);
    }
}
