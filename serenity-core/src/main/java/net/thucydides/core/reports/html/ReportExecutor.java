package net.thucydides.core.reports.html;

import net.serenitybdd.core.time.Stopwatch;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.concurrent.Callable;

import static net.thucydides.core.reports.html.RequirementsOverviewReportingTask.LOGGER;

class ReportExecutor implements Callable<Void> {
    private final ReportingTask reportingTask;

    private final EnvironmentVariables environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);

    public ReportExecutor(ReportingTask reportingTask) {
        this.reportingTask = reportingTask;
    }

    public ReportingTask getReportingTask() {
        return reportingTask;
    }

    @Override
    public Void call() throws Exception {
        Stopwatch reportingStopwatch = Stopwatch.started();
        if (verboseReporting()) {
            LOGGER.info("Generating report {}...", reportingTask);
        }
        reportingTask.generateReports();
        if (verboseReporting()) {
            LOGGER.info("Report {} generated in {} ms", reportingTask, reportingStopwatch.stop());
        }
        return null;
    }

    private Boolean verboseReporting() {
        return environmentVariables.getPropertyAsBoolean("verbose.reporting", false);
    }
}