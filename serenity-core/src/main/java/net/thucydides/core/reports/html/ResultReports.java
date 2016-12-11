package net.thucydides.core.reports.html;

import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ResultReports {

    private static final String TEST_OUTCOME_TEMPLATE_PATH = "freemarker/home.ftl";

    public static List<ReportingTask> resultReportsFor(TestOutcomes testOutcomes,
                                                       FreemarkerContext freemarker,
                                                       EnvironmentVariables environmentVariables,
                                                       File outputDirectory,
                                                       ReportNameProvider reportNameProvider) {
        List<ReportingTask> reportingTasks = new ArrayList<>();

        reportingTasks.addAll(
                resultReportsFor(
                        freemarker,
                        environmentVariables,
                        outputDirectory,
                        testOutcomes,
                        reportNameProvider,
                        TestTag.EMPTY_TAG));

        for (TestTag tag : testOutcomes.getTags()) {
            reportingTasks.addAll(
                    resultReportsFor(
                            freemarker,
                            environmentVariables,
                            outputDirectory,
                            testOutcomes.withTag(tag),
                            new ReportNameProvider(tag.getName()),
                            tag));
        }
        return reportingTasks;
    }

    private static List<ReportingTask> resultReportsFor(final FreemarkerContext freemarker,
                                                        final EnvironmentVariables environmentVariables,
                                                        final File outputDirectory,
                                                        final TestOutcomes testOutcomesForThisTag,
                                                        final ReportNameProvider reportName,
                                                        final TestTag tag) {

        List<ReportingTask> tasks = new ArrayList<>();

        if (testOutcomesForThisTag.getTotalTests().withResult(TestResult.SUCCESS) > 0) {
            tasks.add(resultReport(freemarker, environmentVariables, outputDirectory, testOutcomesForThisTag.getPassingTests(), reportName, tag, "success"));
        }
        if (testOutcomesForThisTag.getTotalTests().withResult(TestResult.PENDING) > 0) {
            tasks.add(resultReport(freemarker, environmentVariables, outputDirectory, testOutcomesForThisTag.getPendingTests(), reportName, tag, "pending"));
        }
        if ((testOutcomesForThisTag.getTotalTests().withResult(TestResult.FAILURE) > 0)
                || (testOutcomesForThisTag.getTotalTests().withResult(TestResult.ERROR) > 0)) {
            tasks.add(resultReport(freemarker, environmentVariables, outputDirectory, testOutcomesForThisTag.getFailingOrErrorTests(), reportName, tag, "broken"));
        }
        if (testOutcomesForThisTag.getTotalTests().withResult(TestResult.FAILURE) > 0) {
            tasks.add(resultReport(freemarker, environmentVariables, outputDirectory, testOutcomesForThisTag.getFailingTests(), reportName, tag, "failure"));
        }
        if (testOutcomesForThisTag.getTotalTests().withResult(TestResult.ERROR) > 0) {
            tasks.add(resultReport(freemarker, environmentVariables, outputDirectory, testOutcomesForThisTag.getErrorTests(), reportName, tag, "error"));
        }
        if (testOutcomesForThisTag.getTotalTests().withResult(TestResult.COMPROMISED) > 0) {
            tasks.add(resultReport(freemarker, environmentVariables, outputDirectory, testOutcomesForThisTag.getCompromisedTests(), reportName, tag, "compromised"));
        }
        if (testOutcomesForThisTag.getTotalTests().withResult(TestResult.IGNORED) > 0) {
            tasks.add(resultReport(freemarker, environmentVariables, outputDirectory, testOutcomesForThisTag.havingResult(TestResult.IGNORED), reportName, tag, "ignored"));
        }
        if (testOutcomesForThisTag.getTotalTests().withResult(TestResult.SKIPPED) > 0) {
            tasks.add(resultReport(freemarker, environmentVariables, outputDirectory, testOutcomesForThisTag.havingResult(TestResult.SKIPPED), reportName, tag, "skipped"));
        }
        return tasks;
    }

    private static ReportingTask resultReport(final FreemarkerContext freemarker,
                                              final EnvironmentVariables environmentVariables,
                                              final File outputDirectory,
                                              final TestOutcomes testOutcomes,
                                              final ReportNameProvider reportName,
                                              final TestTag tag,
                                              final String testResult) {
        return new ResultReportingTask(freemarker, environmentVariables, outputDirectory,
                                       testOutcomes,reportName, tag, testResult);
    }
}
