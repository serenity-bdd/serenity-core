package net.thucydides.core.reports.html;

import serenitymodel.net.serenitybdd.core.time.Stopwatch;
import serenitymodel.net.thucydides.core.model.ReportType;
import serenitymodel.net.thucydides.core.reports.TestOutcomes;
import serenitymodel.net.thucydides.core.reports.html.ReportNameProvider;
import serenitymodel.net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static serenitymodel.net.thucydides.core.reports.html.ReportNameProvider.NO_CONTEXT;

public class TextSummaryReportTask extends BaseReportingTask implements ReportingTask {

    private static final String TEST_SUMMARY_TEMPLATE_PATH = "freemarker/text-summary.ftl";
    private static final String TEST_SUMMARY_REPORT_NAME = "summary.txt";

    protected final ReportNameProvider reportNameProvider;
    private final TestOutcomes testOutcomes;


    public TextSummaryReportTask(FreemarkerContext context, EnvironmentVariables environmentVariables, File outputDirectory, TestOutcomes testOutcomes) {
        super(context, environmentVariables, outputDirectory);
        this.testOutcomes = testOutcomes;
        this.reportNameProvider = new ReportNameProvider(NO_CONTEXT, ReportType.HTML);
    }

    public void generateReports() throws IOException {

        Map<String, Object> context = freemarker.getBuildContext(testOutcomes, reportNameProvider, true);

        Stopwatch stopwatch = Stopwatch.started();

        generateReportPage(context, TEST_SUMMARY_TEMPLATE_PATH, TEST_SUMMARY_REPORT_NAME);

        LOGGER.trace("Summary report generated: {} ms", stopwatch.stop());
    }

    @Override
    public String toString() {
        return "Test Summary Report";
    }


}
