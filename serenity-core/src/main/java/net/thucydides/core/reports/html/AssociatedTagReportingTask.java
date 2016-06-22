package net.thucydides.core.reports.html;

import net.serenitybdd.core.time.Stopwatch;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AssociatedTagReportingTask extends TagReportingTask implements ReportingTask {

    public AssociatedTagReportingTask(FreemarkerContext freemarker,
                                      EnvironmentVariables environmentVariables,
                                      File outputDirectory,
                                      ReportNameProvider reportName) {
        super(freemarker, environmentVariables, outputDirectory, reportName);
    }

    public void generateReportsFor(final TestOutcomes testOutcomes) throws IOException {

        Stopwatch stopwatch = Stopwatch.started();

        ExecutorService executor = Executors.newFixedThreadPool(4);

        for (final TestTag tag : testOutcomes.getTags()) {
            Runnable worker = new Runnable(){

                @Override
                public void run() {
                    try {
                        generateAssociatedTagReportsForTag(testOutcomes.withTag(tag), tag.getName());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            executor.execute(worker);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {}

        LOGGER.debug("Associated tag reports generated: {} ms", stopwatch.stop());

    }

    private void generateAssociatedTagReportsForTag(TestOutcomes testOutcomes, String sourceTag) throws IOException {
        ReportNameProvider reportName = new ReportNameProvider(sourceTag);
        for (TestTag tag : testOutcomes.getTags()) {
            generateTagReport(testOutcomes, reportName, tag);
        }
    }

}
