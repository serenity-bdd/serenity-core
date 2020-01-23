package net.thucydides.core.reports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class ExtendedReports {

    private static List<ExtendedReport> extendedReports;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtendedReports.class);
    /**
     * Returns a list of ExtendedReport instances matching the specified names.
     * The corresponding ExtendedReport classes must be on the classpath.
     */
    public static List<ExtendedReport> named(List<String> reportNames) {

        ensureAllReportsExistForReportNames(reportNames);
        return getReports().stream()
                .filter(report -> reportNames.contains(report.getName()))
                .collect(Collectors.toList());
    }

    private static void ensureAllReportsExistForReportNames(List<String> reportNames) {

        List<String> knownReports = getReports().stream().map(ExtendedReport::getName).collect(Collectors.toList());

        reportNames.forEach(
                reportName -> {
                    if (!knownReports.contains(reportName)) {
                        LOGGER.warn("No report found on classpath with name "+ reportName);
                    }
                }
        );
    }

    private static  List<ExtendedReport> getReports() {
        if (extendedReports == null) {
            extendedReports = new ArrayList<>();
            ServiceLoader.load(ExtendedReport.class).forEach(
                    report -> extendedReports.add(report)
            );
        }
        return extendedReports;
    }
}
