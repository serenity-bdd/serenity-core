package net.thucydides.core.reports.html;

import net.serenitybdd.model.time.Stopwatch;
import net.thucydides.model.configuration.TimeoutConfiguration;
import net.thucydides.model.configuration.TimeoutValue;
import net.thucydides.model.reports.NumberOfThreads;
import net.thucydides.model.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static net.thucydides.model.ThucydidesSystemProperty.REPORT_TIMEOUT_THREADDUMPS;

class Reporter implements Closeable {

    private static final TimeoutValue DEFAULT_TIMEOUT = new TimeoutValue(600, TimeUnit.SECONDS);

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlAggregateStoryReporter.class);

    private final EnvironmentVariables environmentVariables;

    private final ExecutorService executorPool;

    public Reporter(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.executorPool = Executors.newFixedThreadPool(NumberOfThreads.forIOOperations());
        LOGGER.info("GENERATING REPORTS USING {} THREADS", NumberOfThreads.forIOOperations());
    }

    public void generateReportsFor(Stream<ReportingTask> reportingTasks) {
        Stopwatch stopwatch = Stopwatch.started();

        ErrorTally errorTally = new ErrorTally();
        AtomicInteger reportCounter = new AtomicInteger();
        try {
            final TimeoutValue timeout = TimeoutConfiguration.from(environmentVariables).forProperty("report.timeout", DEFAULT_TIMEOUT);

            reportingTasks
                    .map(ReportExecutor::new)
                    .map(executorPool::submit)
                    .parallel()
                    .forEach(future -> {
                        try {
                            future.get(timeout.getTimeout(), timeout.getUnit());
                            reportCounter.incrementAndGet();
                        } catch (TimeoutException reportGenerationTimedOut) {
                            String errorMessage = reportFailureMessage("Report generation timed out", reportGenerationTimedOut);
                            errorTally.recordReportFailure(errorMessage);
                            LOGGER.warn(errorMessage);
                        } catch (InterruptedException reportGenerationInterrupted) {
                            String errorMessage = reportFailureMessage("Report generation interrupted", reportGenerationInterrupted);
                            errorTally.recordReportFailure(errorMessage);
                            LOGGER.warn(errorMessage);
                        } catch (ExecutionException reportGenerationFailed) {
                            String errorMessage = reportFailureMessage("Failed to generate report", reportGenerationFailed);
                            errorTally.recordReportFailure(errorMessage);
                            LOGGER.warn(errorMessage, reportGenerationFailed);
                        }
                    });
        } catch (Exception e) {
            LOGGER.error("Report generation failed", e);
        }
        LOGGER.debug("Generated {} pages in {} seconds", reportCounter.get(),(stopwatch.stop() / 1000));
        if (errorTally.hasErrors()) {
            LOGGER.warn(errorTally.errorSummary());
            if (showThreaddumpOnReportTimeout()) {
                System.err.println("REPORT GENERATION STACK DUMP");
                System.err.println(ThreadDump.forAllThreads());
            }
        }
    }

    private String ultimateError(Throwable cause) {
        if (cause.getCause() == null) {
            return cause.getMessage();
        } else {
            return ultimateError(cause.getCause());
        }
    }

    private boolean showThreaddumpOnReportTimeout() {
        return REPORT_TIMEOUT_THREADDUMPS.booleanFrom(environmentVariables, false);
    }

    private String reportFailureMessage(String reason, ReportExecutorFuture executedTask, Exception e) {
        return String.format("%s for %s - %s\n%s", reason, executedTask, e, errorCauseOf(e));
    }

    private String reportFailureMessage(String reason, Exception e) {
        return String.format("%s - %s\n%s", reason, e, errorCauseOf(e));
    }

    private String reportFailureMessage(String reason, ReportingTask executedTask, Exception e) {
        return String.format("%s for %s - %s\n%s", reason, executedTask, e, errorCauseOf(e));
    }

    @Override
    public void close() throws IOException {
        executorPool.shutdown();
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
            return ultimateError(e) + System.lineSeparator() + topElementFrom(e.getCause().getStackTrace());
        } else {
            return ultimateError(e) + System.lineSeparator() + topElementFrom(e.getStackTrace());
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
