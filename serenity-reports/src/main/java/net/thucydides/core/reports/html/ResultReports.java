package net.thucydides.core.reports.html;

import net.serenitybdd.reports.model.DurationDistribution;
import net.thucydides.model.domain.TestResult;
import net.thucydides.model.domain.TestTag;
import net.thucydides.model.reports.TestOutcomes;
import net.thucydides.model.reports.html.ReportNameProvider;
import net.thucydides.model.reports.html.TagExclusions;
import net.thucydides.model.util.EnvironmentVariables;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static net.thucydides.model.ThucydidesSystemProperty.SERENITY_REPORT_TEST_DURATIONS;

public class ResultReports {

    public static Stream<ReportingTask> resultReportsFor(TestOutcomes testOutcomes,
                                                         FreemarkerContext freemarker,
                                                         EnvironmentVariables environmentVariables,
                                                         File outputDirectory,
                                                         ReportNameProvider reportNameProvider) {

        TagExclusions exclusions = TagExclusions.usingEnvironment(environmentVariables, testOutcomes);


        return Stream.of(
                // RESULT REPORTS
                resultReportsFor(
                        freemarker,
                        environmentVariables,
                        outputDirectory,
                        testOutcomes,
                        reportNameProvider,
                        TestTag.EMPTY_TAG),
                // DURATION REPORTS
                durationReportsFor(
                        freemarker,
                        environmentVariables,
                        outputDirectory,
                        testOutcomes,
                        reportNameProvider,
                        TestTag.EMPTY_TAG),
                // TAG REPORTS
                testOutcomes.getTags().stream()
                        .filter(exclusions::doNotExclude)
                        .flatMap(tag -> resultReportsFor(
                                freemarker,
                                environmentVariables,
                                outputDirectory,
                                testOutcomes.withTag(tag),
                                new ReportNameProvider(tag.getName()), tag)
                        ),
                // DURATIONS PER TAG
                testOutcomes.getTags().stream()
                        .filter(exclusions::doNotExclude)
                        .flatMap(tag -> durationReportsFor(
                                freemarker,
                                environmentVariables,
                                outputDirectory,
                                testOutcomes.withTag(tag),
                                new ReportNameProvider(tag.getName()), tag))

        ).flatMap(stream -> stream);
    }

    private static Stream<ReportingTask> resultReportsFor(final FreemarkerContext freemarker,
                                                        final EnvironmentVariables environmentVariables,
                                                        final File outputDirectory,
                                                        final TestOutcomes testOutcomesForThisTag,
                                                        final ReportNameProvider reportName,
                                                        final TestTag tag) {

        List<ReportingTask> tasks = new ArrayList<>();

        List<TestResult> allResults = testOutcomesForThisTag.getAllResults();
//        OutcomeCounter totalTests = testOutcomesForThisTag.getTotalTests();
        if (allResults.contains(TestResult.SUCCESS)) {
//        if (totalTests.withResult(TestResult.SUCCESS) > 0) {
            tasks.add(resultReport(freemarker, environmentVariables, outputDirectory, testOutcomesForThisTag.getPassingTests(), reportName, tag, "success"));
        }
        if (allResults.contains(TestResult.PENDING)) {
//        if (totalTests.withResult(TestResult.PENDING) > 0) {
            tasks.add(resultReport(freemarker, environmentVariables, outputDirectory, testOutcomesForThisTag.getPendingTests(), reportName, tag, "pending"));
        }
        if (allResults.contains(TestResult.FAILURE) || allResults.contains(TestResult.ERROR)) {
//        if ((totalTests.withResult(TestResult.FAILURE) > 0) || (totalTests.withResult(TestResult.ERROR) > 0)) {
            tasks.add(resultReport(freemarker, environmentVariables, outputDirectory, testOutcomesForThisTag.getUnsuccessfulTests(), reportName, tag, "broken"));
        }
        if (allResults.contains(TestResult.FAILURE)) {
//        if (totalTests.withResult(TestResult.FAILURE) > 0) {
            tasks.add(resultReport(freemarker, environmentVariables, outputDirectory, testOutcomesForThisTag.getFailingTests(), reportName, tag, "failure"));
        }
        if (allResults.contains(TestResult.ABORTED)) {
//        if (totalTests.withResult(TestResult.ABORTED) > 0) {
            tasks.add(resultReport(freemarker, environmentVariables, outputDirectory, testOutcomesForThisTag.getAbortedTests(), reportName, tag, "aborted"));
        }
        if (allResults.contains(TestResult.ERROR)) {
//        if (totalTests.withResult(TestResult.ERROR) > 0) {
            tasks.add(resultReport(freemarker, environmentVariables, outputDirectory, testOutcomesForThisTag.getErrorTests(), reportName, tag, "error"));
        }
        if (allResults.contains(TestResult.COMPROMISED)) {
//        if (totalTests.withResult(TestResult.COMPROMISED) > 0) {
            tasks.add(resultReport(freemarker, environmentVariables, outputDirectory, testOutcomesForThisTag.getCompromisedTests(), reportName, tag, "compromised"));
        }
        if (allResults.contains(TestResult.IGNORED)) {
//        if (totalTests.withResult(TestResult.IGNORED) > 0) {
            tasks.add(resultReport(freemarker, environmentVariables, outputDirectory, testOutcomesForThisTag.havingResult(TestResult.IGNORED), reportName, tag, "ignored"));
        }
        if (allResults.contains(TestResult.SKIPPED)) {
//        if (totalTests.withResult(TestResult.SKIPPED) > 0) {
            tasks.add(resultReport(freemarker, environmentVariables, outputDirectory, testOutcomesForThisTag.havingResult(TestResult.SKIPPED), reportName, tag, "skipped"));
        }
        return tasks.stream();
    }


    private static Stream<ReportingTask> durationReportsFor(final FreemarkerContext freemarker,
                                                          final EnvironmentVariables environmentVariables,
                                                          final File outputDirectory,
                                                          final TestOutcomes testOutcomesForThisTag,
                                                          final ReportNameProvider reportName,
                                                          final TestTag currentTag) {

        List<ReportingTask> tasks = new ArrayList<>();

        if (SERENITY_REPORT_TEST_DURATIONS.booleanFrom(environmentVariables,true)) {
            DurationDistribution durationDistribution = new DurationDistribution(environmentVariables, testOutcomesForThisTag);
            durationDistribution.getDurationBuckets().forEach(
                    bucket -> {
                        if (!bucket.getOutcomes().isEmpty()) {
                            String label = "Duration " + bucket.getDuration();
                            if (!currentTag.getCompleteName().isEmpty()) {
                                label = currentTag.getName() + " > " + label;
                            }
                            tasks.add(durationReport(freemarker,
                                    environmentVariables,
                                    outputDirectory,
                                    TestOutcomes.of(bucket.getTestOutcomes()).withLabel(label),
                                    reportName,
                                    currentTag,
                                    bucket.getDuration()));
                        }
                    }
            );
        }
        return tasks.stream();
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
