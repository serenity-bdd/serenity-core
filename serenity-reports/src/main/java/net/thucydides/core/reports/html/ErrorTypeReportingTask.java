package net.thucydides.core.reports.html;

import net.serenitybdd.core.time.Stopwatch;
import net.thucydides.core.model.ReportType;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.requirements.RequirementsService;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static net.thucydides.core.reports.html.ReportNameProvider.NO_CONTEXT;

public class ErrorTypeReportingTask extends BaseReportingTask implements ReportingTask {

    private static final String HOME_PAGE_TEMPLATE_PATH = "freemarker/home.ftl";

    private final RequirementsService requirementsService;
    private final TestOutcomes testOutcomes;
    private final String filename;

    public ErrorTypeReportingTask(FreemarkerContext context,
                                  EnvironmentVariables environmentVariables,
                                  RequirementsService requirementsService,
                                  File outputDirectory,
                                  ReportNameProvider reportNameProvider,
                                  TestOutcomes testOutcomes,
                                  String errorType) {
        super(context, environmentVariables, outputDirectory);
        this.requirementsService = requirementsService;
        this.testOutcomes = testOutcomes;
        this.filename = reportNameProvider.forErrorType(errorType);
    }

    public void generateReports() throws IOException {

        Stopwatch stopwatch = Stopwatch.started();

        ReportNameProvider defaultNameProvider = new ReportNameProvider(NO_CONTEXT, ReportType.HTML, requirementsService);

        Map<String, Object> context = freemarker.getBuildContext(testOutcomes, defaultNameProvider, true);

        context.put("report", ReportProperties.forAggregateResultsReport());
        context.put("csvReport", "results.csv");

        generateReportPage(context, HOME_PAGE_TEMPLATE_PATH, filename);

        LOGGER.debug("Error type reports generated in {} ms ", stopwatch.stop());
    }

    @Override
    public String reportName() {
        return filename;
    }

}
