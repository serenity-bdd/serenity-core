package net.thucydides.core.reports.html;

import net.serenitybdd.core.time.Stopwatch;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.ReportType;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.requirements.RequirementsService;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static net.thucydides.core.reports.html.ReportNameProvider.NO_CONTEXT;

public class AggregateReportingTask extends BaseReportingTask implements ReportingTask  {

    private static final String HOME_PAGE_TEMPLATE_PATH = "freemarker/home.ftl";
    private static final String BUILD_INFO_TEMPLATE_PATH = "freemarker/build-info.ftl";

    private RequirementsService requirementsService;

    public AggregateReportingTask(FreemarkerContext freemarker,
                                  EnvironmentVariables environmentVariables,
                                  File outputDirectory) {
        this(freemarker,
                environmentVariables,
                Injectors.getInjector().getInstance(RequirementsService.class),
                outputDirectory);
    }

    public AggregateReportingTask(FreemarkerContext context,
                                  EnvironmentVariables environmentVariables,
                                  RequirementsService requirementsService,
                                  File outputDirectory) {
        super(context, environmentVariables, outputDirectory);
        this.requirementsService = requirementsService;
    }

    public void generateReportsFor(TestOutcomes testOutcomes) throws IOException {

        Stopwatch stopwatch = Stopwatch.started();

        ReportNameProvider defaultNameProvider = new ReportNameProvider(NO_CONTEXT, ReportType.HTML, requirementsService);

        Map<String, Object> context = freemarker.getBuildContext(testOutcomes, defaultNameProvider, true);

        context.put("report", ReportProperties.forAggregateResultsReport());
        context.put("csvReport", "results.csv");

        generateReportPage(context, HOME_PAGE_TEMPLATE_PATH, "index.html");
        generateReportPage(context, BUILD_INFO_TEMPLATE_PATH, "build-info.html");
        generateCSVReportFor(testOutcomes, "results.csv");

         LOGGER.debug("Aggregate reports generated in {} ms ", stopwatch.stop());
    }

}
