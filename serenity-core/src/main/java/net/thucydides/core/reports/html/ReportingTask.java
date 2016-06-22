package net.thucydides.core.reports.html;

import net.thucydides.core.reports.TestOutcomes;

import java.io.IOException;

public interface ReportingTask {
    void generateReportsFor(TestOutcomes testOutcomes) throws IOException;
}
