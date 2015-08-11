package net.thucydides.core.reports.html;

public class ReportProperties {

    private final boolean filteredByResults;

    private ReportProperties(boolean filteredByResults) {
        this.filteredByResults = filteredByResults;
    }

    public static ReportProperties forTestResultsReport() {
        return new ReportProperties(true);
    }

    public static ReportProperties forTagResultsReport() {
        return new ReportProperties(false);
    }

    public static ReportProperties forTagTypeResultsReport() {
        return new ReportProperties(false);
    }

    public static ReportProperties forAggregateResultsReport() {
        return new ReportProperties(false);
    }

    public boolean getShouldDisplayResultLink() {
        return !filteredByResults;
    }
}
