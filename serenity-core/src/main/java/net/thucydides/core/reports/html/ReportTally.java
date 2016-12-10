package net.thucydides.core.reports.html;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReportTally {
    private static List<String> generatedReports = Collections.synchronizedList(new ArrayList());

    public static void recordReportGenerationFor(String filename) {
        generatedReports.add(filename);
    }

    public static boolean aReportWasGeneratedFor(String filename) {
        return generatedReports.contains(filename);
    }

}
