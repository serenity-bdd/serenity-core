package net.thucydides.core.reports.html;

import net.serenitybdd.core.time.Stopwatch;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ResultReportingTask extends BaseReportingTask implements ReportingTask  {

    private static final String TEST_OUTCOME_TEMPLATE_PATH = "freemarker/home.ftl";

    private ReportNameProvider reportNameProvider;

    public ResultReportingTask(FreemarkerContext freemarker,
                               EnvironmentVariables environmentVariables,
                               File outputDirectory,
                               ReportNameProvider reportNameProvider) {
        super(freemarker, environmentVariables, outputDirectory);
        this.reportNameProvider = reportNameProvider;
    }

    public void generateReportsFor(TestOutcomes testOutcomes) throws IOException {

        Stopwatch stopwatch = Stopwatch.started();

        generateResultReports(testOutcomes, reportNameProvider);

        for (TestTag tag : testOutcomes.getTags()) {
            generateResultReports(testOutcomes.withTag(tag), new ReportNameProvider(tag.getName()), tag);
        }

        LOGGER.trace("Result reports generated: {} ms", stopwatch.stop());
    }

    private void generateResultReports(TestOutcomes testOutcomes, ReportNameProvider reportName) throws IOException {
        generateResultReports(testOutcomes, reportName, TestTag.EMPTY_TAG);
    }

    private void generateResultReports(TestOutcomes testOutcomesForThisTag, ReportNameProvider reportName, TestTag tag) throws IOException {
        if (testOutcomesForThisTag.getTotalTests().withResult(TestResult.SUCCESS) > 0) {
            generateResultReport(testOutcomesForThisTag.getPassingTests(), reportName, tag, "success");
        }
        if (testOutcomesForThisTag.getTotalTests().withResult(TestResult.PENDING) > 0) {
            generateResultReport(testOutcomesForThisTag.getPendingTests(), reportName, tag, "pending");
        }
        if ((testOutcomesForThisTag.getTotalTests().withResult(TestResult.FAILURE) > 0)
                || (testOutcomesForThisTag.getTotalTests().withResult(TestResult.ERROR) > 0)) {
            generateResultReport(testOutcomesForThisTag.getFailingOrErrorTests(), reportName, tag, "broken");
        }
        if (testOutcomesForThisTag.getTotalTests().withResult(TestResult.FAILURE) > 0) {
            generateResultReport(testOutcomesForThisTag.getFailingTests(), reportName, tag, "failure");
        }
        if (testOutcomesForThisTag.getTotalTests().withResult(TestResult.ERROR) > 0) {
            generateResultReport(testOutcomesForThisTag.getErrorTests(), reportName, tag, "error");
        }
        if (testOutcomesForThisTag.getTotalTests().withResult(TestResult.COMPROMISED) > 0) {
            generateResultReport(testOutcomesForThisTag.getCompromisedTests(), reportName, tag, "compromised");
        }
        if (testOutcomesForThisTag.getTotalTests().withResult(TestResult.IGNORED) > 0) {
            generateResultReport(testOutcomesForThisTag.havingResult(TestResult.IGNORED), reportName, tag, "ignored");
        }
        if (testOutcomesForThisTag.getTotalTests().withResult(TestResult.SKIPPED) > 0) {
            generateResultReport(testOutcomesForThisTag.havingResult(TestResult.SKIPPED), reportName, tag, "skipped");
        }
    }

    private void generateResultReport(TestOutcomes testOutcomes, ReportNameProvider reportName, TestTag tag, String testResult) throws IOException {
        Map<String, Object> context = freemarker.getBuildContext(testOutcomes, reportName, true);
        context.put("report", ReportProperties.forTestResultsReport());
        context.put("currentTagType", tag.getType());
        context.put("currentTag", tag);

        String csvReport = reportName.forCSVFiles().forTestResult(testResult);
        context.put("csvReport", csvReport);
        String report = reportName.withPrefix(tag).forTestResult(testResult);
        generateReportPage(context, TEST_OUTCOME_TEMPLATE_PATH, report);
        generateCSVReportFor(testOutcomes, csvReport);
    }


}
