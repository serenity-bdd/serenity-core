package net.thucydides.core.reports.html;

import serenitymodel.net.serenitybdd.core.time.Stopwatch;
import serenitymodel.net.thucydides.core.model.ReportType;
import serenitymodel.net.thucydides.core.reports.TestOutcomes;
import serenitymodel.net.thucydides.core.reports.html.ReportNameProvider;
import serenitymodel.net.thucydides.core.requirements.RequirementsService;
import serenitymodel.net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static serenitymodel.net.thucydides.core.reports.html.ReportNameProvider.NO_CONTEXT;

public class AggregateReportingTask extends BaseReportingTask implements ReportingTask  {

    private static final String HOME_PAGE_TEMPLATE_PATH = "freemarker/home.ftl";
    private static final String BUILD_INFO_TEMPLATE_PATH = "freemarker/build-info.ftl";

    private final RequirementsService requirementsService;
    private final TestOutcomes testOutcomes;


    public AggregateReportingTask(FreemarkerContext context,
                                  EnvironmentVariables environmentVariables,
                                  RequirementsService requirementsService,
                                  File outputDirectory,
                                  TestOutcomes testOutcomes) {
        super(context, environmentVariables, outputDirectory);
        this.requirementsService = requirementsService;
        this.testOutcomes = testOutcomes;
    }

    public void generateReports() throws IOException {

        Stopwatch stopwatch = Stopwatch.started();

        ReportNameProvider defaultNameProvider = new ReportNameProvider(NO_CONTEXT, ReportType.HTML, requirementsService);
        Map<String, Object> context = freemarker.getBuildContext(testOutcomes, defaultNameProvider, true);

        context.put("report", ReportProperties.forAggregateResultsReport());

        generateReportPage(context, HOME_PAGE_TEMPLATE_PATH, "index.html");
        generateReportPage(context, BUILD_INFO_TEMPLATE_PATH, "build-info.html");

        generateCSVReportFor(testOutcomes, "results.csv").ifPresent(
                csvReport -> context.put("csvReport", "results.csv")
        );

         LOGGER.debug("Aggregate reports generated in {} ms ", stopwatch.stop());
    }

}
