package net.thucydides.core.reports.html;

import com.beust.jcommander.internal.Lists;
import net.serenitybdd.core.time.Stopwatch;
import net.thucydides.core.reports.NumberOfThreads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ReportGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportGenerator.class);

    private Stopwatch stopwatch = new Stopwatch();

    public void generateReportsFor(Collection<ReportingTask> reportingTasks) throws IOException {
        stopwatch.start();

        try {
            Reporter.generateReportsFor(reportingTasks);

            final List<Callable<Void>> partitions = Lists.newArrayList();
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

        LOGGER.debug("Reports generated in {} ms", stopwatch.stop());
    }

    private class ReportExecutor implements Callable<Void> {
        private final ReportingTask reportingTask;

        public ReportExecutor(ReportingTask reportingTask) {
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
