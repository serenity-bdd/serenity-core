package net.thucydides.core.reports.html;

import com.google.common.base.Objects;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.model.time.Stopwatch;
import net.thucydides.core.reports.html.accessibility.ChartColorScheme;
import net.thucydides.model.domain.ReportType;
import net.thucydides.model.domain.Rule;
import net.thucydides.model.domain.TestTag;
import net.thucydides.core.reports.ScenarioOutcomeGroup;
import net.thucydides.model.reports.ReportOptions;
import net.thucydides.model.reports.TestOutcomes;
import net.thucydides.model.reports.html.*;
import net.thucydides.model.requirements.JSONRequirementsTree;
import net.thucydides.model.requirements.RequirementsService;
import net.thucydides.model.requirements.model.Requirement;
import net.thucydides.model.requirements.reports.RequirementOutcome;
import net.thucydides.model.requirements.reports.RequirementsOutcomes;
import net.thucydides.model.requirements.reports.ScenarioOutcome;
import net.thucydides.model.requirements.reports.ScenarioOutcomes;
import net.thucydides.model.tags.BreadcrumbTagFilter;
import net.thucydides.model.tags.OutcomeTagFilter;
import net.thucydides.model.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static net.thucydides.model.ThucydidesSystemProperty.CUCUMBER_PRETTY_FORMAT_TABLES;
import static net.thucydides.model.reports.html.ReportNameProvider.NO_CONTEXT;

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
    private final RequirementsFilter requirementsFilter;

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

    @Override
    public String reportName() {
        return reportName;
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
            requirements = List.of(requirementsOutcomes.getParentRequirement().get());
            context.put("requirement", requirementsOutcomes.getParentRequirement().get());
            context.put("requirementTags", new TagFilter().removeHiddenTagsFrom(requirementsOutcomes.getParentRequirement().get().getTags()));
        } else {
            requirements = requirementsOutcomes.getRequirementOutcomes().stream().map(RequirementOutcome::getRequirement).collect(toList());
        }

        //
        // If the cucumber.filter.tags parameter has been provided, we only display requirements with that tag type in the requirements tree
        //
        Optional<String> cucumberTags = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty("cucumber.filter.tags");
        JSONRequirementsTree requirementsTree
                = JSONRequirementsTree.forRequirements(requirementsFilter.filteredByDisplayTag(requirements),
                                                       requirementsOutcomes.filteredByDisplayTag(),
                                                       cucumberTags.orElse(""));
        if (asParentRequirement) {
            requirementsTree = requirementsTree.asAParentRequirement();
        }

        RequirementsOutcomes filteredRequirementsOutcomes = requirementsOutcomes.filteredByDisplayTag();
        context.put("requirements", filteredRequirementsOutcomes);
        context.put("duplicateRequirementNamesPresent", DuplicateRequirementNames.presentIn(filteredRequirementsOutcomes));
        context.put("requirementsTree", requirementsTree.asString());
        context.put("requirementsOverview", requirementsOverview);
        context.put("prettyTables", CUCUMBER_PRETTY_FORMAT_TABLES.booleanFrom(environmentVariables, false));

        context.put("isLeafRequirement", requirementsTree.isALeafNode());

        context.put("requirementTypes", requirementsService.getRequirementTypes());

        TestOutcomes filteredTestOutcomes = requirementsOutcomes.getTestOutcomes().filteredByEnvironmentTags();

        context.put("colorScheme", ChartColorScheme.forEnvironment(environmentVariables));
        context.put("testOutcomes", filteredTestOutcomes);
//        context.put("resultCounts", ResultCountCache.resultCountsFor(testOutcomes));
        context.put("resultCounts", ResultCounts.forOutcomesIn(testOutcomes));
        context.put("requirementCounts", RequirementCounts.forOutcomesIn(requirementsOutcomes));
        context.put("allTestOutcomes", testOutcomes);
        context.put("timestamp", TestOutcomeTimestamp.from(filteredTestOutcomes));
        context.put("reportName", new ReportNameProvider(NO_CONTEXT, ReportType.HTML, requirementsService));
        context.put("absoluteReportName", new ReportNameProvider(NO_CONTEXT, ReportType.HTML, requirementsService));
        context.put("reportOptions", ReportOptions.forEnvironment(environmentVariables));
        context.put("relativeLink", relativeLink);
        context.put("evidence", EvidenceData.from(outcomeFilter.outcomesFilteredByTagIn(filteredTestOutcomes.getOutcomes())));

        requirementsOutcomes.getParentRequirement().map(
                parentRequirement -> context.put("currentTag", parentRequirement.asTag())
        );

        List<ScenarioOutcome> scenarios = outcomeFilter.scenariosFilteredByTagIn(ScenarioOutcomes.from(requirementsOutcomes));

        // Group scenarios by rules
        List<ScenarioOutcome> scenariosWithoutARule = new ArrayList<>();

        Map<Rule, List<ScenarioOutcome>> scenarioOutcomeMap = new HashMap<>();
        List<Rule> scenarioRules = new ArrayList<>();
        for (ScenarioOutcome scenario : scenarios) {
            Rule rule = scenario.getRule();
            if (rule == null) {
                scenariosWithoutARule.add(scenario);
            } else {
                if (!scenarioOutcomeMap.containsKey(rule)) {
                    scenarioOutcomeMap.put(rule, new ArrayList<>());
                    scenarioRules.add(rule);
                }
                scenarioOutcomeMap.get(rule).add(scenario);
            }
        }
        List<ScenarioOutcomeGroup> scenarioGroups = new ArrayList<>();
        if (!scenariosWithoutARule.isEmpty()) {
            ScenarioOutcomeGroup scenarioGroup = new ScenarioOutcomeGroup(scenariosWithoutARule);
            requirements.stream()
                    .filter(requirement -> requirement.getBackground() != null)
                    .findFirst()
                    .ifPresent(
                            requirement -> {
                                scenarioGroup.setBackgroundTitle(requirement.getBackground().getTitle());
                                scenarioGroup.setBackgroundDescription(requirement.getBackground().getDescription());
                            }
                    );
            scenarioGroups.add(scenarioGroup);
        }
        for (Rule rule : scenarioRules) {
            scenarioGroups.add(new ScenarioOutcomeGroup(rule, scenarioOutcomeMap.get(rule)));
        }

        List<ScenarioOutcome> executedScenarios = executedScenariosIn(scenarios);
        ScenarioResultCounts scenarioResultCounts = ScenarioResultCounts.forScenarios(scenarios);

        context.put("scenarioGroups", scenarioGroups);
        context.put("scenarioResults", scenarioResultCounts);
        context.put("requirementsResultData", scenarioResultCounts.byTypeFor("success","pending","ignored","skipped","aborted","failure","error","compromised","undefined"));
        context.put("testCases", executedScenarios);
        context.put("automatedTestCases", automated(executedScenarios));
        context.put("manualTestCases", manual(executedScenarios));

        addBreadcrumbs(requirementsOutcomes, context, new ArrayList<>(filteredTestOutcomes.getTags()));

        generateReportPage(context, DEFAULT_REQUIREMENTS_REPORT, reportName);
        LOGGER.trace("Requirements report generated: {} in {} ms", reportName, stopwatch.stop());

    }

    private List<ScenarioOutcome> automated(List<ScenarioOutcome> executedScenariosIn) {
        return executedScenariosIn.stream().filter(scenarioOutcome -> !scenarioOutcome.isManual()).collect(toList());
    }

    private List<ScenarioOutcome> manual(List<ScenarioOutcome> executedScenariosIn) {
        return executedScenariosIn.stream().filter(ScenarioOutcome::isManual).collect(toList());
    }

    private List<ScenarioOutcome> executedScenariosIn(List<ScenarioOutcome> scenarios) {
        return scenarios.stream()
                .filter(scenarioOutcome -> !scenarioOutcome.isBackground())
                .filter(scenarioOutcome -> !scenarioOutcome.getName().isEmpty())
                .collect(toList());
    }

    private List<ScenarioOutcome> backgroundScenariosIn(List<ScenarioOutcome> scenarios) {
        return scenarios.stream()
                .filter(scenarioOutcome -> scenarioOutcome.isBackground())
                .filter(scenarioOutcome -> !scenarioOutcome.getName().isEmpty())
                .collect(toList());
    }

    private void addBreadcrumbs(RequirementsOutcomes requirementsOutcomes, Map<String, Object> context, List<TestTag> allTags) {
        if (this.requirementsOutcomes.getParentRequirement().isPresent()) {
//            context.put("breadcrumbs", Breadcrumbs.forRequirementsTag(this.requirementsOutcomes.getParentRequirement().get().asTag())
//                    .fromTagsIn(allTags));
            List<Requirement> parents = requirementsService.getParentRequirementsOf(this.requirementsOutcomes.getParentRequirement().get());
            context.put("breadcrumbs", Breadcrumbs.forParentRequirements(parents));
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
