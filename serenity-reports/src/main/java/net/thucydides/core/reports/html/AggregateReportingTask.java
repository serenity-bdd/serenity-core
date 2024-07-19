package net.thucydides.core.reports.html;

import net.serenitybdd.model.time.Stopwatch;
import net.thucydides.model.domain.ReportType;
import net.thucydides.model.reports.TestOutcomes;
import net.thucydides.model.reports.html.ReportNameProvider;
import net.thucydides.model.requirements.RequirementsService;
import net.thucydides.model.util.EnvironmentVariables;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static net.thucydides.model.reports.html.ReportNameProvider.NO_CONTEXT;


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

    @Override
    public String reportName() {
        return "index.html";
    }


}
