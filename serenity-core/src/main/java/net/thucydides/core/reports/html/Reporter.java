package net.thucydides.core.reports.html;

import com.google.common.collect.Lists;
import net.serenitybdd.core.time.Stopwatch;
import net.thucydides.core.configuration.TimeoutConfiguration;
import net.thucydides.core.configuration.TimeoutValue;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.reports.NumberOfThreads;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

class Reporter {

    private static final TimeoutValue DEFAULT_TIMEOUT = new TimeoutValue(30, TimeUnit.SECONDS);

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlAggregateStoryReporter.class);

    private final Collection<ReportingTask> reportingTasks;

    private final EnvironmentVariables environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);

    private final ExecutorService executorPool = Executors.newFixedThreadPool(NumberOfThreads.forIOOperations());

    Reporter(Collection<ReportingTask> reportingTasks) {
        this.reportingTasks = reportingTasks;
    }

    public static void generateReportsFor(Collection<ReportingTask> reportingTasks) throws IOException {
        new Reporter(reportingTasks).generateReports();
    }

    private void generateReports() {
        Stopwatch stopwatch = Stopwatch.started();

        ErrorTally errorTally = new ErrorTally();
        try {
            final List<ReportExecutor> partitions = getReportExecutors(reportingTasks);

            List<ReportExecutorFuture> futures = getReportExecutorFutures(executorPool, partitions);

            TimeoutValue timeout = TimeoutConfiguration.from(environmentVariables).forProperty("report.timeout", DEFAULT_TIMEOUT);

            for (ReportExecutorFuture executedTask : futures) {
                try {
                    executedTask.getFuture().get(timeout.getTimeout(), timeout.getUnit());
                } catch (TimeoutException reportGenerationTimedOut) {
                    String errorMessage = reportFailureMessage("Report generation timed out", executedTask, reportGenerationTimedOut);
                    errorTally.recordReportFailure(errorMessage);
                    LOGGER.warn(errorMessage);
                } catch (InterruptedException reportGenerationInterrupted) {
                    String errorMessage = reportFailureMessage("Report generation interrupted", executedTask, reportGenerationInterrupted);
                    errorTally.recordReportFailure(errorMessage);
                    LOGGER.warn(errorMessage);
                } catch (ExecutionException reportGenerationFailed) {
                    String errorMessage = reportFailureMessage("Failed to generate report", executedTask, reportGenerationFailed);
                    errorTally.recordReportFailure(errorMessage);
                    LOGGER.warn(errorMessage);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Report generation failed", e);
        }
        LOGGER.debug("Test outcome reports generated in {} ms", stopwatch.stop());
        if (errorTally.hasErrors()) {
            LOGGER.warn(errorTally.errorSummary());
            if (showThreaddumpOnReportTimeout()) {
                System.err.println("REPORT GENERATION STACK DUMP");
                System.err.println(ThreadDump.forAllThreads());
            }
        }

    }

    private boolean showThreaddumpOnReportTimeout() {
        return environmentVariables.getPropertyAsBoolean("report.timeout.threaddumps", false);
    }

    private String reportFailureMessage(String reason, ReportExecutorFuture executedTask, Exception e) {
        return String.format("%s for %s - %s\n%s", reason, executedTask, e, errorCauseOf(e));
    }

    private static class ErrorRecord {
        public final String message;
        public final String threadDump;

        private ErrorRecord(String message, String threadDump) {
            this.message = message;
            this.threadDump = threadDump;
        }
    }
    private  class ErrorTally {
        private final List<ErrorRecord> errors = new ArrayList<>();

        public boolean hasErrors() { return !errors.isEmpty(); }

        public void recordReportFailure(String errorMessage) {
            String threadDump = (showThreaddumpOnReportTimeout()) ? ThreadDump.forAllThreads() : "";
            errors.add(new ErrorRecord(errorMessage, threadDump));
        }


        public String errorSummary() {
            StringBuffer errorMessage = new StringBuffer("SOME REPORT PAGES COULD NOT BE GENERATED\n");
            for(ErrorRecord error: errors) {
                errorMessage.append(" * " + error.message + "\n" + error.threadDump + "\n");
            }
            return errorMessage.toString();
        }
    }

    private List<ReportExecutorFuture> getReportExecutorFutures(ExecutorService executorPool, List<ReportExecutor> partitions) {
        List<ReportExecutorFuture> futures = new ArrayList<>();
        for(ReportExecutor reportGenerationTask : partitions) {
            futures.add(new ReportExecutorFuture(executorPool.submit(reportGenerationTask), reportGenerationTask.getReportingTask()));
        }
        return futures;
    }

    private List<ReportExecutor> getReportExecutors(Collection<ReportingTask> reportingTasks) {
        final List<ReportExecutor> partitions = Lists.newArrayList();
        for (ReportingTask reportingTask : reportingTasks) {
            partitions.add(new ReportExecutor(reportingTask));
        }
        return partitions;
    }

    private String errorCauseOf(Throwable e) {
        if (e.getCause() != null) {
            return topElementFrom(e.getCause().getStackTrace());
        } else {
            return topElementFrom(e.getStackTrace());
        }
    }

    private String topElementFrom(StackTraceElement[] elements) {
        if (elements.length == 0) {
            return "";
        } else {
            return elements[0].toString();
        }
    }
    static class ReportExecutorFuture {
        private final Future<Void> future;
        private final ReportingTask reportTask;

        ReportExecutorFuture(Future<Void> future, ReportingTask reportTask) {
            this.future = future;
            this.reportTask = reportTask;
        }

        Future<Void> getFuture() { return future; }

        @Override
        public String toString() {
            return reportTask.toString();
        }
    }
}
