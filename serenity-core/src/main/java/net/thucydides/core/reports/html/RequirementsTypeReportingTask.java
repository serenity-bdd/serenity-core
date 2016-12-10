package net.thucydides.core.reports.html;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import net.thucydides.core.requirements.reports.RequirementsOutcomes;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

class RequirementsTypeReportingTask extends BaseReportingTask implements ReportingTask {

    private static final String REQUIREMENT_TYPE_TEMPLATE_PATH = "freemarker/requirement-type.ftl";

    private final ReportNameProvider reportNameProvider;
    private final RequirementsOutcomes requirementsOutcomes;
    private final String requirementType;

    RequirementsTypeReportingTask(FreemarkerContext freemarker,
                                         EnvironmentVariables environmentVariables,
                                         File outputDirectory,
                                         ReportNameProvider reportNameProvider,
                                         RequirementsOutcomes requirementsOutcomes,
                                         String requirementType) {
        super(freemarker, environmentVariables, outputDirectory);
        this.reportNameProvider = reportNameProvider;
        this.requirementsOutcomes = requirementsOutcomes;
        this.requirementType = requirementType;
    }

    public static List<ReportingTask> requirementTypeReports(final RequirementsOutcomes requirementsOutcomes,
                                                             final FreemarkerContext freemarker,
                                                             final EnvironmentVariables environmentVariables,
                                                             final File outputDirectory,
                                                             final ReportNameProvider reportNameProvider) {
        return Lambda.convert(requirementsOutcomes.getTypes(),
                toRequirementTypeReports(requirementsOutcomes, freemarker, environmentVariables, outputDirectory, reportNameProvider));
    }

    private static Converter<String, ReportingTask> toRequirementTypeReports(final RequirementsOutcomes requirementsOutcomes,
                                                                             final FreemarkerContext freemarker,
                                                                             final EnvironmentVariables environmentVariables,
                                                                             final File outputDirectory,
                                                                             final ReportNameProvider reportNameProvider) {
        return new Converter<String, ReportingTask>() {

            @Override
            public ReportingTask convert(final String requirementType) {
                return new RequirementsTypeReportingTask(freemarker, environmentVariables, outputDirectory,
                                                         reportNameProvider, requirementsOutcomes, requirementType);
            }
        };
    }

    @Override
    public void generateReports() throws IOException {
        Map<String, Object> context = freemarker.getBuildContext(requirementsOutcomes.getTestOutcomes(), reportNameProvider, true);

        context.put("report", ReportProperties.forAggregateResultsReport());
        context.put("requirementType", requirementType);
        context.put("requirements", requirementsOutcomes.requirementsOfType(requirementType));

        String reportName = reportNameProvider.forRequirementType(requirementType);
        generateReportPage(context, REQUIREMENT_TYPE_TEMPLATE_PATH, reportName);

    }
}