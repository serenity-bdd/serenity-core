package net.thucydides.core.reports.html;

import net.serenitybdd.core.reports.styling.TagStylist;
import net.serenitybdd.model.buildinfo.BuildInfoProvider;
import net.serenitybdd.model.buildinfo.BuildProperties;
import net.serenitybdd.model.di.ModelInfrastructure;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.reports.model.*;
import net.thucydides.core.reports.html.accessibility.ChartColorScheme;
import net.thucydides.model.domain.NumericalFormatter;
import net.thucydides.model.domain.ReportType;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestTag;
import net.thucydides.model.domain.formatters.ReportFormatter;
import net.thucydides.model.issues.IssueTracking;
import net.thucydides.model.reports.ReportOptions;
import net.thucydides.model.reports.TestOutcomes;
import net.thucydides.model.reports.html.ReportNameProvider;
import net.thucydides.model.reports.html.RequirementsFilter;
import net.thucydides.model.reports.html.ResultCounts;
import net.thucydides.model.reports.html.TagFilter;
import net.thucydides.model.requirements.RequirementsService;
import net.thucydides.model.requirements.model.Requirement;
import net.thucydides.model.requirements.reports.ScenarioOutcome;
import net.thucydides.model.requirements.reports.ScenarioOutcomes;
import net.thucydides.model.tags.OutcomeTagFilter;
import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.model.util.Inflector;
import net.thucydides.model.util.TagInflector;
import net.thucydides.model.util.VersionProvider;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.ReadableDateTime;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static net.serenitybdd.reports.model.DurationsKt.*;
import static net.thucydides.core.reports.html.HtmlReporter.READABLE_TIMESTAMP_FORMAT;
import static net.thucydides.core.reports.html.HtmlReporter.TIMESTAMP_FORMAT;
import static net.thucydides.model.ThucydidesSystemProperty.*;
import static net.thucydides.model.reports.html.ReportNameProvider.NO_CONTEXT;

/**
 * Created by john on 21/06/2016.
 */
public class FreemarkerContext {

    private final EnvironmentVariables environmentVariables;
    private final RequirementsService requirements;
    private final IssueTracking issueTracking;
    private final String relativeLink;
    private final BuildProperties buildProperties;
    private final TestTag parentTag;
    private final RequirementsService requirementsService;
    private final List<String> requirementTypes;
    private final String version;
    private final String buildNumber;
    private final List<String> tagTypes;
    private final ReportOptions reportOptions;
    private final CustomReportFields customReportFields;
    private final Collection<Requirement> requirementsWithTag;
    private final Collection<TestTag> tagsOfType;
    private final List<String> customFields;
    private final List<String> customFieldValues;
    private final TagFilter tagFilter;
    private final OutcomeTagFilter outcomeFilter;

    private static final BackgroundColor BACKGROUND_COLORS = new BackgroundColor();

    public FreemarkerContext(EnvironmentVariables environmentVariables,
                             RequirementsService requirements,
                             IssueTracking issueTracking,
                             String relativeLink,
                             TestTag parentTag) {
        this.environmentVariables = environmentVariables;
        this.requirements = requirements;
        this.issueTracking = issueTracking;
        this.relativeLink = relativeLink;
        buildProperties = new BuildInfoProvider(environmentVariables).getBuildProperties();
        this.parentTag = parentTag;
        this.requirementsService = ModelInfrastructure.getRequirementsService();
        this.requirementTypes = requirements.getRequirementTypes();

        VersionProvider versionProvider = new VersionProvider(environmentVariables);
        this.version = versionProvider.getVersion();
        this.buildNumber = versionProvider.getBuildNumberText();
        this.tagTypes = new ReportTags(environmentVariables).getDisplayedTagTypes();

        this.reportOptions = ReportOptions.forEnvironment(environmentVariables);
        this.customReportFields = new CustomReportFields(environmentVariables);
        this.requirementsWithTag = requirements.getRequirementsWithTagsOfType(tagTypes);
        this.tagsOfType = requirements.getTagsOfType(tagTypes);
        this.customFields = customReportFields.getFieldNames();
        this.customFieldValues = customReportFields.getValues();
        this.tagFilter = new TagFilter(environmentVariables);
        this.outcomeFilter = new OutcomeTagFilter(environmentVariables);

    }


    public FreemarkerContext(EnvironmentVariables environmentVariables,
                             RequirementsService requirements,
                             IssueTracking issueTracking,
                             String relativeLink) {
        this(environmentVariables, requirements, issueTracking, relativeLink, TestTag.EMPTY_TAG);
    }

    public Map<String, Object> getBuildContext(TestOutcomes completeTestOutcomes,
                                               ReportNameProvider reportName,
                                               boolean useFiltering) {
        Map<String, Object> context = new HashMap<>();
        TestOutcomes testOutcomes = completeTestOutcomes.filteredByEnvironmentTags();

        context.put("colorScheme", ChartColorScheme.forEnvironment(environmentVariables));
        context.put("testOutcomes", testOutcomes);

        // Calculate Duration
        context.put("reportDurations", SERENITY_REPORT_TEST_DURATIONS.booleanFrom(environmentVariables,true));
        context.put("durations", new DurationDistribution(environmentVariables, testOutcomes));

        context.put("allTestOutcomes", testOutcomes.getRootOutcomes());
        if (useFiltering) {
            context.put("tagTypes", tagFilter.filteredTagTypes(testOutcomes.getTagTypes()));
        } else {
            context.put("tagTypes", testOutcomes.getTagTypes());
        }
        context.put("currentTag", TestTag.EMPTY_TAG);
        context.put("parentTag", parentTag);
        context.put("reportName", reportName);
        context.put("reportNameInContext", reportName);

        ReportNameProvider absoluteReportNameProvider = new ReportNameProvider(NO_CONTEXT, ReportType.HTML, requirements);
        context.put("absoluteReportName", absoluteReportNameProvider);

        context.put("reportOptions", reportOptions);
        context.put("timestamp", timestampFrom(new DateTime()));
        context.put("requirementTypes", requirementTypes);
        context.put("leafRequirementType", last(requirementTypes));
        addFormattersToContext(context);

        ZonedDateTime startTime = startTimeOf(testOutcomes.getOutcomes());
        ZonedDateTime endTime = endTimeOf(testOutcomes.getOutcomes());

        context.put("startTimestamp", readableTimestampFrom(startTime));
        context.put("endTimestamp", readableTimestampFrom(endTime));
        context.put("totalTestDuration", formattedDuration(totalDurationOf(testOutcomes.getOutcomes())));
        context.put("totalClockDuration", formattedDuration(clockDurationOf(testOutcomes.getOutcomes())));
        context.put("averageTestDuration", formattedDuration(averageDurationOf(testOutcomes.getOutcomes())));
        context.put("maxTestDuration", formattedDuration(maxDurationOf(testOutcomes.getOutcomes())));
        context.put("minTestDuration", formattedDuration(minDurationOf(testOutcomes.getOutcomes())));

        context.put("serenityVersionNumber", version);
        context.put("buildNumber", buildNumber);
        context.put("build", buildProperties);


        List<ScenarioOutcome> scenarios = outcomeFilter.scenariosFilteredByTagIn(ScenarioOutcomes.from(testOutcomes, requirements));
        List<ScenarioOutcome> executedScenarios = executedScenariosIn(scenarios);

        context.put("testCount", testOutcomes.getOutcomes().size());
        context.put("scenarioCount", testOutcomes.getNumberOfTestScenarios());
//        context.put("resultCounts", ResultCountCache.resultCountsFor(testOutcomes));
        context.put("resultCounts", ResultCounts.forOutcomesIn(testOutcomes));

        context.put("scenarios", scenarios);
        context.put("filteredScenarios", scenarios);
        context.put("testCases", executedScenarios);
        context.put("automatedTestCases", automated(executedScenarios));
        context.put("executionContexts", executionContextsIn(executedScenarios));
        context.put("manualTestCases", manual(executedScenarios));
        context.put("evidence", EvidenceData.from(outcomeFilter.outcomesFilteredByTagIn(testOutcomes.getOutcomes())));

        context.put("frequentFailures", FrequentFailures.from(testOutcomes).withMaxOf(5));
        context.put("unstableFeatures", UnstableFeatures.from(testOutcomes)
                .withRequirementsFrom(requirementsService)
                .withMaxOf(5));

        context.put("inflection", Inflector.getInstance());
        context.put("tagInflector", new TagInflector(environmentVariables));

        RequirementsFilter requirementsFilter = new RequirementsFilter(environmentVariables);

        List<CoverageByTagType> coverage;
        if (resultsAreFilteredByRequirementTypeBasedOn(tagTypes)) {
            // If we are filtering for a specific type of requirement, only show coverage for this type of outcome
            Collection<TestTag> coveredTags = requirementsWithTag
                    .stream()
                    .filter(requirement -> testOutcomes.containTestFor(requirement) || requirement.containsNoScenarios())
                    .filter(requirementsFilter::inDisplayOnlyTags)
                    .map(Requirement::asTag)
                    .collect(Collectors.toSet());
            coverage = TagCoverage.from(testOutcomes).withReportNameProvider(absoluteReportNameProvider).showingTags(coveredTags).forTagTypes(tagTypes);
        } else {
            // Otherwise show coverage for all requirements
            coverage = TagCoverage.from(testOutcomes).withReportNameProvider(absoluteReportNameProvider).forTagTypes(requirements.getRequirementTypes());
        }

        boolean hideEmptyRequirements = EnvironmentSpecificConfiguration.from(environmentVariables).getBooleanProperty(SERENITY_REPORT_HIDE_EMPTY_REQUIREMENTS, true);

        context.put("hideEmptyRequirements", hideEmptyRequirements);
        context.put("coverage", coverage);
        context.put("backgroundColor", BACKGROUND_COLORS);

        testOutcomes.getOutcomes().forEach(
                testOutcome -> addTags(testOutcome, context, null)
        );

        context.put("tagResults",
                TagResults.from(testOutcomes)
                        .ignoringValues("ignore", "pending", "skip", "error", "compromised", "fail")
                        .ignoringTypes("Duration")
                        .groupedByType()

        );
        context.put("customFields", customFields);
        context.put("customFieldValues", customFieldValues);

        return context;
    }

    private boolean resultsAreFilteredByRequirementTypeBasedOn(List<String> tagTypes) {
        return requirements.getRequirementTypes().containsAll(tagTypes);
    }

    private void addTags(TestOutcome testOutcome, Map<String, Object> context, String parentTitle) {
        Set<TestTag> filteredTags = (parentTitle != null) ? tagFilter.removeTagsWithName(testOutcome.getTags(), parentTitle) : testOutcome.getTags();
        filteredTags = tagFilter.removeHiddenTagsFrom(filteredTags);
        context.put("filteredTags", filteredTags);
    }

    private String last(List<String> requirementTypes) {
        return (requirementTypes.size() > 0) ? requirementTypes.get(requirementTypes.size() - 1) : "Feature";
    }

    private List<ScenarioOutcome> automated(List<ScenarioOutcome> executedScenariosIn) {
        return executedScenariosIn.stream().filter(scenarioOutcome -> !scenarioOutcome.isManual()).collect(Collectors.toList());
    }

    private List<TestExecutionContext> executionContextsIn(List<ScenarioOutcome> executedScenariosIn) {
        return executedScenariosIn.stream()
                .filter(outcome -> StringUtils.isNotEmpty(outcome.getContext()))
                .flatMap(this::executionContextFor)
                .collect(Collectors.toList());
    }

    private Stream<TestExecutionContext> executionContextFor(ScenarioOutcome outcome) {
        return stream(outcome.getContext().split(",")).map(
                contextValue -> new TestExecutionContext()
        );
    }

    private List<ScenarioOutcome> manual(List<ScenarioOutcome> executedScenariosIn) {
        return executedScenariosIn.stream().filter(scenarioOutcome -> scenarioOutcome.isManual()).collect(Collectors.toList());
    }

    private List<ScenarioOutcome> executedScenariosIn(List<ScenarioOutcome> scenarioOutcomes) {
        return scenarioOutcomes.stream()
                .filter(scenarioOutcome -> !scenarioOutcome.getType().equalsIgnoreCase("background"))
                .collect(Collectors.toList());
    }

    private void addFormattersToContext(final Map<String, Object> context) {
        Formatter formatter = new Formatter();
        ReportFormatter reportFormatter = new ReportFormatter();
        context.put("formatter", formatter);
        context.put("reportFormatter", reportFormatter);
        context.put("formatted", new NumericalFormatter());
        context.put("inflection", Inflector.getInstance());
        context.put("tagInflector", new TagInflector(environmentVariables));
        context.put("styling", TagStylist.from(environmentVariables));
        context.put("relativeLink", relativeLink);
        context.put("reportOptions", ReportOptions.forEnvironment(environmentVariables));
        context.put("showDetailedStoryDescription", SERENITY_SHOW_STORY_DETAILS_IN_TESTS.booleanFrom(environmentVariables, false));
    }

    protected String readableTimestampFrom(ZonedDateTime time) {
        return time == null ? "" : time.format(DateTimeFormatter.ofPattern(READABLE_TIMESTAMP_FORMAT));
    }

    protected String timestampFrom(ReadableDateTime startTime) {
        return startTime == null ? "" : startTime.toString(TIMESTAMP_FORMAT);
    }
}
