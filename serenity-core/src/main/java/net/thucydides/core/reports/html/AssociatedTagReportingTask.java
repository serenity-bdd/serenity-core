package net.thucydides.core.reports.html;

import net.serenitybdd.core.time.Stopwatch;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.io.IOException;

public class AssociatedTagReportingTask extends TagReportingTask implements ReportingTask {

    final int batchNumber;
    final int totalBatches;

    public AssociatedTagReportingTask(FreemarkerContext freemarker,
                                      EnvironmentVariables environmentVariables,
                                      File outputDirectory,
                                      ReportNameProvider reportName) {
        this(freemarker, environmentVariables, outputDirectory, reportName, 1, 1);
    }

    public AssociatedTagReportingTask(FreemarkerContext freemarker,
                                      EnvironmentVariables environmentVariables,
                                      File outputDirectory,
                                      ReportNameProvider reportName,
                                      int batchNumber,
                                      int totalBatches) {
        super(freemarker, environmentVariables, outputDirectory, reportName);
        this.batchNumber = batchNumber;
        this.totalBatches = totalBatches;

    }

    public void generateReportsFor(final TestOutcomes testOutcomes) throws IOException {

        Stopwatch stopwatch = Stopwatch.started();

        int tagCounter = 0;
        int doneCounter = 0;
        for (final TestTag tag : testOutcomes.getTags()) {
            if (tagCounter % totalBatches == (batchNumber - 1)) {
                generateAssociatedTagReportsForTag(testOutcomes.withTag(tag), tag.getName());
                doneCounter++;
            }
            tagCounter++;
        }
        LOGGER.trace("Associate tag reports generated for batch {} ({} tags): {} ms", batchNumber, doneCounter, stopwatch.stop());

    }

    private void generateAssociatedTagReportsForTag(TestOutcomes testOutcomes, String sourceTag) throws IOException {
        ReportNameProvider reportName = new ReportNameProvider(sourceTag);
        for (TestTag tag : testOutcomes.getTags()) {
            generateTagReport(testOutcomes, reportName, tag);
        }
    }

    public AssociatedTagReportingTask forBatch(int batchNumber, int totalBatches) {
        return new AssociatedTagReportingTask(freemarker, environmentVariables, outputDirectory, reportNameProvider, batchNumber, totalBatches);
    }
}
