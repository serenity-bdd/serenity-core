package net.thucydides.core.reports.html;

import net.serenitybdd.core.time.*;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.configuration.*;
import net.thucydides.core.guice.*;
import net.thucydides.core.reports.*;
import net.thucydides.core.util.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

import static net.thucydides.core.ThucydidesSystemProperty.REPORT_TIMEOUT_THREADDUMPS;

class Reporter {

    private static final TimeoutValue DEFAULT_TIMEOUT = new TimeoutValue(600, TimeUnit.SECONDS);

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlAggregateStoryReporter.class);

    private final Collection<ReportingTask> reportingTasks;

    private final EnvironmentVariables environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);

    private Reporter(Collection<ReportingTask> reportingTasks) {
        this.reportingTasks = reportingTasks;
    }

    public static void generateReportsFor(Collection<ReportingTask> reportingTasks) {
        new Reporter(reportingTasks).generateReports();
    }

    private void generateReports() {
        Stopwatch stopwatch = Stopwatch.started();

        ExecutorService executorPool = Executors.newFixedThreadPool(NumberOfThreads.forIOOperations());

        ErrorTally errorTally = new ErrorTally();
        try {
            final List<ReportExecutor> partitions
                    = reportingTasks.stream()
                    .map(ReportExecutor::new)
                    .collect(Collectors.toList());

            final List<ReportExecutorFuture> futures
                    = partitions.stream()
                    .map( partition -> new ReportExecutorFuture(executorPool.submit(partition), partition.getReportingTask()) )
                    .collect(Collectors.toList());

            final TimeoutValue timeout = TimeoutConfiguration.from(environmentVariables).forProperty("report.timeout", DEFAULT_TIMEOUT);

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
                    LOGGER.warn(errorMessage, reportGenerationFailed);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Report generation failed", e);
        } finally {
            executorPool.shutdown();
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
        return REPORT_TIMEOUT_THREADDUMPS.booleanFrom(environmentVariables, false);
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
    private class ErrorTally {
        private final List<ErrorRecord> errors = new ArrayList<>();

        boolean hasErrors() { return !errors.isEmpty(); }

        void recordReportFailure(String errorMessage) {
            String threadDump = (showThreaddumpOnReportTimeout()) ? ThreadDump.forAllThreads() : "";
            errors.add(new ErrorRecord(errorMessage, threadDump));
        }


        String errorSummary() {
            StringBuilder errorMessage = new StringBuilder("SOME REPORT PAGES COULD NOT BE GENERATED\n");
            for(ErrorRecord error: errors) {
                errorMessage.append(" * ")
                        .append(error.message)
                        .append("\n")
                        .append(error.threadDump)
                        .append("\n");
            }
            return errorMessage.toString();
        }
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
