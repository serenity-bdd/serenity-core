package net.thucydides.core.reports.html;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import net.thucydides.core.requirements.reports.RequirementsOutcomes;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

class RequirementsTypeReportingTask extends BaseReportingTask implements ReportingTask {

    private static final String REQUIREMENT_TYPE_TEMPLATE_PATH = "freemarker/requirement-type.ftl";

    private final ReportNameProvider reportNameProvider;
    private final RequirementsOutcomes requirementsOutcomes;
    private final String requirementType;
    private final String reportName;

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
        this.reportName = reportNameProvider.forRequirementType(requirementType);
    }

    public static Set<ReportingTask> requirementTypeReports(final RequirementsOutcomes requirementsOutcomes,
                                                            final FreemarkerContext freemarker,
                                                            final EnvironmentVariables environmentVariables,
                                                            final File outputDirectory,
                                                            final ReportNameProvider reportNameProvider) {
        return Sets.newHashSet(
                Lambda.convert(requirementsOutcomes.getTypes(),
                               toRequirementTypeReports(requirementsOutcomes,
                                                        freemarker,
                                                        environmentVariables,
                                                        outputDirectory,
                                                        reportNameProvider)));
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
        context.put("requirements", requirementsOutcomes.requirementsOfType(requirementType).withoutUnrelatedRequirements());

        generateReportPage(context, REQUIREMENT_TYPE_TEMPLATE_PATH, reportName);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequirementsTypeReportingTask that = (RequirementsTypeReportingTask) o;
        return Objects.equal(reportName, that.reportName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(reportName);
    }
}