package net.thucydides.core.reports.html;

import net.serenitybdd.reports.model.DurationDistribution;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ResultReports {

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
                        TestTag.EMPTY_TAG)
        );

        reportingTasks.addAll(
                durationReportsFor(
                        freemarker,
                        environmentVariables,
                        outputDirectory,
                        testOutcomes,
                        reportNameProvider,
                        TestTag.EMPTY_TAG)
        );

        // RESULT REPORTS
        for (TestTag tag : skippingDurationTags(testOutcomes.getTags())) {
            reportingTasks.addAll(
                    resultReportsFor(
                            freemarker,
                            environmentVariables,
                            outputDirectory,
                            testOutcomes.withTag(tag),
                            new ReportNameProvider(tag.getName()), tag)
            );
        }
        // DURATION REPORTS
        for (TestTag tag : skippingDurationTags(testOutcomes.getTags())) {
            reportingTasks.addAll(
                    durationReportsFor(
                            freemarker,
                            environmentVariables,
                            outputDirectory,
                            testOutcomes.withTag(tag),
                            new ReportNameProvider(tag.getName()), tag)
            );
        }
        return reportingTasks;
    }

    private static List<TestTag> skippingDurationTags(List<TestTag> tags) {
        return tags.stream()
                .filter(tag -> !tag.getType().equals("Duration"))
                .collect(Collectors.toList());
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
            tasks.add(resultReport(freemarker, environmentVariables, outputDirectory, testOutcomesForThisTag.getUnsuccessfulTests(), reportName, tag, "broken"));
        }
        if (testOutcomesForThisTag.getTotalTests().withResult(TestResult.FAILURE) > 0) {
            tasks.add(resultReport(freemarker, environmentVariables, outputDirectory, testOutcomesForThisTag.getFailingTests(), reportName, tag, "failure"));
        }
        if (testOutcomesForThisTag.getTotalTests().withResult(TestResult.ABORTED) > 0) {
            tasks.add(resultReport(freemarker, environmentVariables, outputDirectory, testOutcomesForThisTag.getAbortedTests(), reportName, tag, "aborted"));
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


    private static List<ReportingTask> durationReportsFor(final FreemarkerContext freemarker,
                                                          final EnvironmentVariables environmentVariables,
                                                          final File outputDirectory,
                                                          final TestOutcomes testOutcomesForThisTag,
                                                          final ReportNameProvider reportName,
                                                          final TestTag currentTag) {

        List<ReportingTask> tasks = new ArrayList<>();

        DurationDistribution durationDistribution = new DurationDistribution(environmentVariables, testOutcomesForThisTag);
        durationDistribution.getDurationBuckets().forEach(
                bucket -> {
                    if (!bucket.getOutcomes().isEmpty()) {
                        String label = "Duration " + bucket.getDuration();
                        if (!currentTag.getCompleteName().isEmpty()) {
                            label = currentTag.getName() +" > " + label;
                        }
                        tasks.add(durationReport(freemarker,
                                environmentVariables,
                                outputDirectory,
                                TestOutcomes.of(bucket.getOutcomes()).withLabel(label),
                                reportName,
                                currentTag,
                                bucket.getDuration()));
                    }
                }
        );
        return tasks;
    }

    private static ReportingTask resultReport(final FreemarkerContext freemarker,
                                              final EnvironmentVariables environmentVariables,
                                              final File outputDirectory,
                                              final TestOutcomes testOutcomes,
                                              final ReportNameProvider reportName,
                                              final TestTag tag,
                                              final String testResult) {
        return new ResultReportingTask(freemarker, environmentVariables, outputDirectory, testOutcomes, reportName, tag, testResult);
    }

    private static ReportingTask durationReport(final FreemarkerContext freemarker,
                                              final EnvironmentVariables environmentVariables,
                                              final File outputDirectory,
                                              final TestOutcomes testOutcomes,
                                              final ReportNameProvider reportName,
                                              final TestTag tag,
                                              final String testResult) {
        return new DurationReportingTask(freemarker, environmentVariables, outputDirectory, testOutcomes, reportName, tag, testResult);
    }


}
