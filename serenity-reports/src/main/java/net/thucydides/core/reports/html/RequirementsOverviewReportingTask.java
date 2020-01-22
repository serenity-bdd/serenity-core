package net.thucydides.core.reports.html;

import com.google.common.base.Objects;
import net.serenitybdd.core.time.Stopwatch;
import net.thucydides.core.model.ReportType;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.ReportOptions;
import net.thucydides.core.tags.OutcomeTagFilter;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.requirements.JSONRequirementsTree;
import net.thucydides.core.requirements.RequirementsService;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.requirements.reports.RequirementOutcome;
import net.thucydides.core.requirements.reports.RequirementsOutcomes;
import net.thucydides.core.requirements.reports.ScenarioOutcome;
import net.thucydides.core.requirements.reports.ScenarioOutcomes;
import net.thucydides.core.tags.BreadcrumbTagFilter;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.serenitybdd.core.environment.ConfiguredEnvironment.getEnvironmentVariables;
import static net.thucydides.core.ThucydidesSystemProperty.CUCUMBER_PRETTY_FORMAT_TABLES;
import static net.thucydides.core.reports.html.ReportNameProvider.NO_CONTEXT;

class RequirementsOverviewReportingTask extends BaseReportingTask implements ReportingTask {

    private static final String DEFAULT_REQUIREMENTS_REPORT = "freemarker/requirements.ftl";
    private static final String REPORT_NAME = "capabilities.html";

    protected static final Logger LOGGER = LoggerFactory.getLogger(RequirementsOverviewReportingTask.class);

    private final ReportNameProvider reportNameProvider;
    private final RequirementsOutcomes requirementsOutcomes;
    private final RequirementsService requirementsService;
    private final TestOutcomes testOutcomes;
    private final String relativeLink;
    private final String reportName;
    private boolean asParentRequirement;
    private  RequirementsFilter requirementsFilter;

    public RequirementsOverviewReportingTask(FreemarkerContext freemarker,
                                             EnvironmentVariables environmentVariables,
                                             File outputDirectory,
                                             ReportNameProvider reportNameProvider,
                                             RequirementsService requirementsService,
                                             RequirementsOutcomes requirementsOutcomes,
                                             String relativeLink,
                                             TestOutcomes testOutcomes) {
        super(freemarker, environmentVariables, outputDirectory);
        this.reportNameProvider = reportNameProvider;
        this.requirementsOutcomes = requirementsOutcomes;
        this.requirementsService = requirementsService;
        this.testOutcomes = testOutcomes;
        this.relativeLink = relativeLink;
        this.reportName = REPORT_NAME;
        this.asParentRequirement = true;
        this.requirementsFilter = new RequirementsFilter(environmentVariables);
    }

    public RequirementsOverviewReportingTask(FreemarkerContext freemarker,
                                             EnvironmentVariables environmentVariables,
                                             File outputDirectory,
                                             ReportNameProvider reportNameProvider,
                                             RequirementsService requirementsService,
                                             RequirementsOutcomes requirementsOutcomes,
                                             String relativeLink,
                                             TestOutcomes testOutcomes,
                                             String reportName) {
        super(freemarker, environmentVariables, outputDirectory);
        this.reportNameProvider = reportNameProvider;
        this.requirementsOutcomes = requirementsOutcomes;
        this.requirementsService = requirementsService;
        this.testOutcomes = testOutcomes;
        this.relativeLink = relativeLink;
        this.reportName = reportName;
        this.asParentRequirement = true;
        this.requirementsFilter = new RequirementsFilter(environmentVariables);
    }

    public RequirementsOverviewReportingTask asParentRequirement() {
        this.asParentRequirement = true;
        return this;
    }

    public RequirementsOverviewReportingTask asLeafRequirement() {
        this.asParentRequirement = false;
        return this;
    }

    @Override
    public void generateReports() throws IOException {

        Stopwatch stopwatch = Stopwatch.started();

        Map<String, Object> context = freemarker.getBuildContext(requirementsOutcomes.getTestOutcomes(), reportNameProvider, true);

        String requirementsOverview = requirementsOutcomes.getOverview();
        OutcomeTagFilter outcomeFilter = new OutcomeTagFilter(environmentVariables);

        List<Requirement> requirements;
        if (requirementsOutcomes.getParentRequirement().isPresent()) {
            requirements = Arrays.asList(requirementsOutcomes.getParentRequirement().get());
        } else {
            requirements = requirementsOutcomes.getRequirementOutcomes().stream().map(RequirementOutcome::getRequirement).collect(Collectors.toList());

        }

        JSONRequirementsTree requirementsTree = JSONRequirementsTree.forRequirements(requirementsFilter.filteredByDisplayTag(requirements),
                                                                                      requirementsOutcomes.filteredByDisplayTag());
        if (asParentRequirement) {
            requirementsTree = requirementsTree.asAParentRequirement();
        }

        context.put("requirements", requirementsOutcomes.filteredByDisplayTag());
        context.put("requirementsTree", requirementsTree.asString());
        context.put("requirementsOverview", requirementsOverview);
        context.put("prettyTables", CUCUMBER_PRETTY_FORMAT_TABLES.booleanFrom(environmentVariables, false));

        context.put("isLeafRequirement", requirementsTree.isALeafNode());

        context.put("requirementTypes", requirementsService.getRequirementTypes());

        TestOutcomes filteredTestOutcomes = requirementsOutcomes.getTestOutcomes().filteredByEnvironmentTags();

        context.put("testOutcomes", filteredTestOutcomes);
        context.put("resultCounts", ResultCounts.forOutcomesIn(filteredTestOutcomes));
        context.put("requirementCounts", RequirementCounts.forOutcomesIn(requirementsOutcomes));
        context.put("allTestOutcomes", testOutcomes);
        context.put("timestamp", TestOutcomeTimestamp.from(filteredTestOutcomes));
        context.put("reportName", new ReportNameProvider(NO_CONTEXT, ReportType.HTML, requirementsService));
        context.put("absoluteReportName", new ReportNameProvider(NO_CONTEXT, ReportType.HTML, requirementsService));
        context.put("reportOptions", new ReportOptions(getEnvironmentVariables()));
        context.put("relativeLink", relativeLink);
        context.put("evidence", EvidenceData.from(outcomeFilter.outcomesFilteredByTagIn(filteredTestOutcomes.getOutcomes())));

        requirementsOutcomes.getParentRequirement().map(
                parentRequirement -> context.put("currentTag", parentRequirement.asTag())
        );

        List<ScenarioOutcome> scenarios
                = outcomeFilter.scenariosFilteredByTagIn(ScenarioOutcomes.from(requirementsOutcomes));


        List<ScenarioOutcome> executedScenarios = executedScenariosIn(scenarios);

        context.put("scenarios", scenarios);
        context.put("testCases", executedScenarios);
        context.put("automatedTestCases", automated(executedScenarios));
        context.put("manualTestCases", manual(executedScenarios));

        addBreadcrumbs(requirementsOutcomes, context, filteredTestOutcomes.getTags());

        generateReportPage(context, DEFAULT_REQUIREMENTS_REPORT, reportName);
        LOGGER.trace("Requirements report generated: {} in {} ms", reportName, stopwatch.stop());

    }

    private List<ScenarioOutcome> automated(List<ScenarioOutcome> executedScenariosIn) {
        return executedScenariosIn.stream().filter(scenarioOutcome -> !scenarioOutcome.isManual()).collect(Collectors.toList());
    }

    private List<ScenarioOutcome> manual(List<ScenarioOutcome> executedScenariosIn) {
        return executedScenariosIn.stream().filter(scenarioOutcome -> scenarioOutcome.isManual()).collect(Collectors.toList());
    }

    private List<ScenarioOutcome> executedScenariosIn(List<ScenarioOutcome> scenarios) {
        return scenarios.stream()
                .filter(scenarioOutcome -> !scenarioOutcome.getType().equalsIgnoreCase("background"))
                .collect(Collectors.toList());
    }

    private void addBreadcrumbs(RequirementsOutcomes requirementsOutcomes, Map<String, Object> context, List<TestTag> allTags) {
        if (this.requirementsOutcomes.getParentRequirement().isPresent()) {
            context.put("breadcrumbs", Breadcrumbs.forRequirementsTag(this.requirementsOutcomes.getParentRequirement().get().asTag())
                    .fromTagsIn(allTags));
        } else {
            context.put("breadcrumbs", new BreadcrumbTagFilter().getRequirementBreadcrumbsFrom(requirementsOutcomes));
        }
    }

    @Override
    public String toString() {
        return "Requirements report " + reportName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequirementsOverviewReportingTask that = (RequirementsOverviewReportingTask) o;
        return Objects.equal(reportName, that.reportName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(reportName);
    }
}