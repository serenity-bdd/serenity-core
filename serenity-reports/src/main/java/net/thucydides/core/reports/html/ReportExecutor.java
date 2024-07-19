package net.thucydides.core.reports.html;

import net.serenitybdd.model.time.Stopwatch;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

class ReportExecutor implements Callable<Void> {
    private final ReportingTask reportingTask;

    protected static final Logger LOGGER = LoggerFactory.getLogger(ReportExecutor.class);

    private final EnvironmentVariables environmentVariables = SystemEnvironmentVariables.currentEnvironmentVariables();

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
            LOGGER.debug("Generating report {}...", reportingTask);
        }
        reportingTask.generateReports();
        if (verboseReporting()) {
            LOGGER.debug("Report {} generated in {} ms", reportingTask, reportingStopwatch.stop());
        }
        return null;
    }

    private Boolean verboseReporting() {
        return ThucydidesSystemProperty.VERBOSE_REPORTING.booleanFrom(environmentVariables, false);
    }
}
