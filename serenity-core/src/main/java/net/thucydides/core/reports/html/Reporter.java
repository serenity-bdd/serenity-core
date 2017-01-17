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

    private static final TimeoutValue DEFAULT_TIMEOUT = new TimeoutValue(10, TimeUnit.SECONDS);

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlAggregateStoryReporter.class);

    public static void generateReportsFor(Collection<ReportingTask> reportingTasks) throws IOException {
        Stopwatch stopwatch = Stopwatch.started();

        EnvironmentVariables environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);

        try {
            final ExecutorService executorPool = Executors.newFixedThreadPool(NumberOfThreads.forIOOperations());

            final List<ReportExecutor> partitions = Lists.newArrayList();
            for (ReportingTask reportingTask : reportingTasks) {
                partitions.add(new ReportExecutor(reportingTask));
            }

            List<ReportExecutorFuture> futures = new ArrayList<>();
            for(ReportExecutor reportGenerationTask : partitions) {
                futures.add(new ReportExecutorFuture(executorPool.submit(reportGenerationTask), reportGenerationTask.getReportingTask()));
            }

            TimeoutValue timeout = TimeoutConfiguration.from(environmentVariables).forProperty("report.timeout", DEFAULT_TIMEOUT);

            for (ReportExecutorFuture executedTask : futures) {
                try {
                    executedTask.getFuture().get(timeout.getTimeout(), timeout.getUnit());
                } catch (TimeoutException reportGenerationTimedOut) {
                    LOGGER.warn("Report generation timed out for {} - {}\n{}", executedTask, reportGenerationTimedOut, errorCauseOf(reportGenerationTimedOut));
                } catch (InterruptedException reportGenerationInterrupted) {
                    LOGGER.warn("Report generation interrupted for {} - {}\n{}", executedTask, reportGenerationInterrupted, errorCauseOf(reportGenerationInterrupted));
                } catch (ExecutionException reportGenerationFailed) {
                    LOGGER.warn("Failed to generate report for {} - {}\n{}", executedTask, reportGenerationFailed, errorCauseOf(reportGenerationFailed));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Report generation failed", e);
        }
        LOGGER.debug("Test outcome reports generated in {} ms", stopwatch.stop());
    }

    private static String errorCauseOf(Throwable e) {
        if (e.getCause() != null) {
            return topElementFrom(e.getCause().getStackTrace());
        } else {
            return topElementFrom(e.getStackTrace());
        }
    }

    private static String topElementFrom(StackTraceElement[] elements) {
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
