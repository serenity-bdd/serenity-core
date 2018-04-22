package net.thucydides.core.reports.html;

import net.serenitybdd.core.time.Stopwatch;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class TagTypeReportingTask extends BaseReportingTask implements ReportingTask  {

    private static final String TAGTYPE_TEMPLATE_PATH = "freemarker/results-by-tagtype.ftl";

    private ReportNameProvider reportNameProvider;
    private final TestOutcomes testOutcomes;

    public TagTypeReportingTask(FreemarkerContext freemarker,
                                EnvironmentVariables environmentVariables,
                                File outputDirectory,
                                ReportNameProvider reportNameProvider,
                                TestOutcomes testOutcomes) {
        super(freemarker, environmentVariables, outputDirectory);
        this.reportNameProvider = reportNameProvider;
        this.testOutcomes = testOutcomes;
    }

    public void generateReports() throws IOException {

        Stopwatch stopwatch = Stopwatch.started();

        generateTagTypeReportsFor(testOutcomes, reportNameProvider);

        for (String name : testOutcomes.getTagNames()) {
            generateTagTypeReportsFor(testOutcomes.withTag(name), new ReportNameProvider(name));
        }

        LOGGER.trace("Tag type reports generated: {} ms", stopwatch.stop());
    }

    private void generateTagTypeReportsFor(TestOutcomes testOutcomes, ReportNameProvider reportNameProvider) throws IOException {

        for (String tagType : testOutcomes.getTagTypes()) {
            generateTagTypeReport(testOutcomes, reportNameProvider, tagType);
        }
    }

    private void generateTagTypeReport(TestOutcomes testOutcomes, ReportNameProvider reportName, String tagType) throws IOException {
        TestOutcomes testOutcomesForTagType = testOutcomes.withTagType(tagType);

        Map<String, Object> context = freemarker.getBuildContext(testOutcomesForTagType, reportNameProvider, true);

        context.put("report", ReportProperties.forTagTypeResultsReport());
        context.put("tagType", tagType);

        String csvReport = reportName.forCSVFiles().forTagType(tagType);
        context.put("csvReport", csvReport);

        String report = reportName.forTagType(tagType);
        generateReportPage(context, TAGTYPE_TEMPLATE_PATH, report);
        generateCSVReportFor(testOutcomesForTagType, csvReport);
    }


}
