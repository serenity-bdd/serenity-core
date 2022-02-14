package net.thucydides.core.reports.html;

import com.google.common.base.Objects;
import net.thucydides.core.tags.OutcomeTagFilter;
import net.thucydides.core.requirements.reports.RequirementsOutcomes;
import net.thucydides.core.requirements.reports.ScenarioOutcome;
import net.thucydides.core.requirements.reports.ScenarioOutcomes;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

class RequirementsTypeReportingTask extends BaseReportingTask implements ReportingTask {

    private static final String REQUIREMENT_TYPE_TEMPLATE_PATH = "freemarker/requirement-type.ftl";

    private final ReportNameProvider reportNameProvider;
    private final RequirementsOutcomes requirementsOutcomes;
    private final String requirementType;
    private final String reportName;

    private RequirementsTypeReportingTask(FreemarkerContext freemarker,
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

    static Set<ReportingTask> requirementTypeReports(final List<String> requirementTypes,
                                                     final RequirementsOutcomes requirementsOutcomes,
                                                     final FreemarkerContext freemarker,
                                                     final EnvironmentVariables environmentVariables,
                                                     final File outputDirectory,
                                                     final ReportNameProvider reportNameProvider) {

        return requirementTypes.stream()
                .map(
                        type -> new RequirementsTypeReportingTask(freemarker,
                                environmentVariables,
                                outputDirectory,
                                reportNameProvider,
                                requirementsOutcomes,
                                type)
                ).collect(Collectors.toSet());

    }

    @Override
    public void generateReports() throws IOException {
        Map<String, Object> context = freemarker.getBuildContext(requirementsOutcomes.getTestOutcomes(), reportNameProvider, true);
        OutcomeTagFilter scenarioOutcomeFilter = new OutcomeTagFilter(environmentVariables);

        context.put("report", ReportProperties.forAggregateResultsReport());
        context.put("requirementType", requirementType);

        RequirementsOutcomes filteredRequirementsOutcomes = requirementsOutcomes.requirementsOfType(requirementType).filteredByDisplayTag();
        context.put("requirements", filteredRequirementsOutcomes);
        context.put("duplicateRequirementNamesPresent", DuplicateRequirementNames.presentIn(filteredRequirementsOutcomes));

        List<ScenarioOutcome> scenarios = scenarioOutcomeFilter.scenariosFilteredByTagIn(ScenarioOutcomes.from(requirementsOutcomes));

        context.put("scenarios", scenarios);

        generateReportPage(context, REQUIREMENT_TYPE_TEMPLATE_PATH, reportName);

    }

    @Override
    public String reportName() {
        return reportName;
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