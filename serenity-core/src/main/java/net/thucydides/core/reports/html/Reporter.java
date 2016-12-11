package net.thucydides.core.reports.html;

import net.serenitybdd.core.time.Stopwatch;
import net.thucydides.core.reports.NumberOfThreads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class Reporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlAggregateStoryReporter.class);

    public static void generateReportsFor(Collection<ReportingTask> reportingTasks) throws IOException {
        Stopwatch stopwatch = Stopwatch.started();

        try {

            final List<Callable<Void>> partitions = new ArrayList<>();
            for (ReportingTask reportingTask : reportingTasks) {
                partitions.add(new ReportExecutor(reportingTask));
            }

            final ExecutorService executorPool = Executors.newFixedThreadPool(NumberOfThreads.forIOOperations());
            for (Future<Void> executedTask : executorPool.invokeAll(partitions)) {
                executedTask.get();
            }
        } catch (Exception e) {
            LOGGER.error("Report generation failed", e);
        }

        LOGGER.debug("Test outcome reports generated in {} ms", stopwatch.stop());
    }

    private static class ReportExecutor implements Callable<Void> {
        private final ReportingTask reportingTask;

        ReportExecutor(ReportingTask reportingTask) {
            this.reportingTask = reportingTask;
        }

        @Override
        public Void call() throws Exception {
            Stopwatch reportingStopwatch = Stopwatch.started();
            reportingTask.generateReports();
            LOGGER.debug("{} generated in {} ms", reportingTask.toString(), reportingStopwatch.stop());
            return null;
        }
    }
}
