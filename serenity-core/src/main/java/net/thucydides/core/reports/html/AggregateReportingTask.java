package net.thucydides.core.reports.html;

import net.serenitybdd.core.time.Stopwatch;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class AggregateReportingTask extends BaseReportingTask implements ReportingTask  {

    private static final String TEST_OUTCOME_TEMPLATE_PATH = "freemarker/home.ftl";
    private static final String BUILD_INFO_TEMPLATE_PATH = "freemarker/build-info.ftl";

    public AggregateReportingTask(FreemarkerContext freemarker,
                                  EnvironmentVariables environmentVariables,
                                  File outputDirectory) {
        super(freemarker, environmentVariables, outputDirectory);
    }

    public void generateReportsFor(TestOutcomes testOutcomes) throws IOException {

        Stopwatch stopwatch = Stopwatch.started();

        ReportNameProvider defaultNameProvider = new ReportNameProvider();

        Map<String, Object> context = freemarker.getBuildContext(testOutcomes, defaultNameProvider, true);
        context.put("report", ReportProperties.forAggregateResultsReport());
        context.put("csvReport", "results.csv");

        generateReportPage(context, TEST_OUTCOME_TEMPLATE_PATH, "index.html");
        generateReportPage(context, BUILD_INFO_TEMPLATE_PATH, "build-info.html");
        generateCSVReportFor(testOutcomes, "results.csv");

         LOGGER.debug("Aggregate reports generated in {} ms ", stopwatch.stop());
    }

}
