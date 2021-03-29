package net.thucydides.core.reports.html;

import com.google.common.base.Splitter;
import serenitymodel.net.serenitybdd.core.buildinfo.BuildInfoProvider;
import serenitymodel.net.serenitybdd.core.buildinfo.BuildProperties;
import net.serenitybdd.core.reports.styling.TagStylist;
import net.serenitybdd.reports.model.*;
import serenitymodel.net.thucydides.core.guice.Injectors;
import serenitymodel.net.thucydides.core.issues.IssueTracking;
import serenitymodel.net.thucydides.core.model.NumericalFormatter;
import serenitymodel.net.thucydides.core.model.ReportType;
import serenitymodel.net.thucydides.core.model.TestOutcome;
import serenitymodel.net.thucydides.core.model.TestTag;
import serenitymodel.net.thucydides.core.model.formatters.ReportFormatter;
import serenitymodel.net.thucydides.core.reports.ReportOptions;
import serenitymodel.net.thucydides.core.reports.html.ReportNameProvider;
import serenitymodel.net.thucydides.core.reports.html.RequirementsFilter;
import serenitymodel.net.thucydides.core.reports.html.ResultCounts;
import serenitymodel.net.thucydides.core.reports.html.TagFilter;
import serenitymodel.net.thucydides.core.requirements.model.Requirement;
import serenitymodel.net.thucydides.core.tags.OutcomeTagFilter;
import serenitymodel.net.thucydides.core.reports.TestOutcomes;
import serenitymodel.net.thucydides.core.requirements.RequirementsService;
import serenitymodel.net.thucydides.core.requirements.reports.ScenarioOutcome;
import serenitymodel.net.thucydides.core.requirements.reports.ScenarioOutcomes;
import serenitymodel.net.thucydides.core.util.EnvironmentVariables;
import serenitymodel.net.thucydides.core.util.Inflector;
import serenitymodel.net.thucydides.core.util.TagInflector;
import serenitymodel.net.thucydides.core.util.VersionProvider;
import org.joda.time.DateTime;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static net.serenitybdd.reports.model.DurationsKt.*;
import static serenitymodel.net.thucydides.core.ThucydidesSystemProperty.REPORT_TAGTYPES;
import static serenitymodel.net.thucydides.core.ThucydidesSystemProperty.SERENITY_SHOW_STORY_DETAILS_IN_TESTS;
import static net.thucydides.core.reports.html.HtmlReporter.TIMESTAMP_FORMAT;
import static serenitymodel.net.thucydides.core.reports.html.ReportNameProvider.NO_CONTEXT;

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
        this.requirementsService = Injectors.getInjector().getInstance(RequirementsService.class);
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
        TagFilter tagFilter = new TagFilter(environmentVariables);
        OutcomeTagFilter outcomeFilter = new OutcomeTagFilter(environmentVariables);

        // WIP

        TestOutcomes testOutcomes =  completeTestOutcomes.filteredByEnvironmentTags();

        // EWIP

        context.put("testOutcomes", testOutcomes);
        context.put("allTestOutcomes", testOutcomes.getRootOutcomes());
        if (useFiltering) {
            context.put("tagTypes", tagFilter.filteredTagTypes(testOutcomes.getTagTypes()));
        } else {
            context.put("tagTypes", testOutcomes.getTagTypes());
        }
        context.put("currentTag", TestTag.EMPTY_TAG);
        context.put("parentTag", parentTag);
        context.put("reportName", reportName);

        context.put("absoluteReportName", new ReportNameProvider(NO_CONTEXT, ReportType.HTML, requirements));

        context.put("reportOptions", new ReportOptions(environmentVariables));
        context.put("timestamp", timestampFrom(new DateTime()));
        context.put("requirementTypes", requirements.getRequirementTypes());
        context.put("leafRequirementType", last(requirements.getRequirementTypes()));
        addFormattersToContext(context);

        context.put("totalTestDuration", formattedDuration(Duration.ofMillis(testOutcomes.getDuration())));
        context.put("totalClockDuration", formattedDuration(clockDurationOf(testOutcomes.getOutcomes())));
        context.put("averageTestDuration", formattedDuration(averageDurationOf(testOutcomes.getOutcomes())));
        context.put("maxTestDuration", formattedDuration(maxDurationOf(testOutcomes.getOutcomes())));
        context.put("minTestDuration", formattedDuration(minDurationOf(testOutcomes.getOutcomes())));

        VersionProvider versionProvider = new VersionProvider(environmentVariables);
        context.put("serenityVersionNumber", versionProvider.getVersion());
        context.put("buildNumber", versionProvider.getBuildNumberText());
        context.put("build", buildProperties);

        context.put("resultCounts", ResultCounts.forOutcomesIn(testOutcomes));

        List<ScenarioOutcome> scenarios = outcomeFilter.scenariosFilteredByTagIn(ScenarioOutcomes.from(testOutcomes));

        context.put("scenarios", scenarios);
        context.put("filteredScenarios", scenarios);
        context.put("testCases", executedScenariosIn(scenarios));
        context.put("automatedTestCases", automated(executedScenariosIn(scenarios)));
        context.put("manualTestCases", manual(executedScenariosIn(scenarios)));
        context.put("evidence", EvidenceData.from(outcomeFilter.outcomesFilteredByTagIn(testOutcomes.getOutcomes())));

        context.put("frequentFailures", FrequentFailures.from(testOutcomes).withMaxOf(5));
        context.put("unstableFeatures", UnstableFeatures.from(testOutcomes)
                .withRequirementsFrom(requirementsService)
                .withMaxOf(5));

        List<String> tagTypes = Splitter.on(",")
                .trimResults()
                .splitToList(REPORT_TAGTYPES.from(environmentVariables, "feature"));

        context.put("inflection", Inflector.getInstance());
        context.put("tagInflector", new TagInflector(environmentVariables));

        RequirementsFilter requirementsFilter = new RequirementsFilter(environmentVariables);

        Collection<TestTag> coveredTags = requirements.getRequirementsWithTagsOfType(tagTypes).stream()
                .filter(requirement -> testOutcomes.containTestFor(requirement) || requirement.containsNoScenarios())
                .filter(requirementsFilter::inDisplayOnlyTags)
                .map(Requirement::asTag)
                .collect(Collectors.toSet());

//        Collection<TestTag> coveredTags = requirements.getTagsOfType(tagTypes).stream()
//                .filter( tag -> testOutcomes.containsMatchingTag(tag) || (requirements.containsEmptyRequirementWithTag(tag)))
//                .filter(this::inDisplayOnlyTags)
//                .collect(Collectors.toSet());

        context.put("coverage", TagCoverage.from(testOutcomes)
                .showingTags(requirements.getTagsOfType(tagTypes))
                .showingTags(coveredTags)
                .forTagTypes(tagTypes));
        context.put("backgroundColor", new BackgroundColor());

        testOutcomes.getOutcomes().forEach(
                testOutcome -> addTags(testOutcome, context, null)
        );

        context.put("tagResults", TagResults.from(testOutcomes).groupedByType());

        CustomReportFields customReportFields = new CustomReportFields(environmentVariables);
        context.put("customFields", customReportFields.getFieldNames());
        context.put("customFieldValues", customReportFields.getValues());

        return context;
    }

    private void addTags(TestOutcome testOutcome, Map<String, Object> context, String parentTitle) {
        TagFilter tagFilter = new TagFilter(environmentVariables);
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
        context.put("reportOptions", new ReportOptions(environmentVariables));
        context.put("showDetailedStoryDescription", SERENITY_SHOW_STORY_DETAILS_IN_TESTS.booleanFrom(environmentVariables, false));
    }


    protected String timestampFrom(DateTime startTime) {
        return startTime == null ? "" : startTime.toString(TIMESTAMP_FORMAT);
    }

    public FreemarkerContext withParentTag(TestTag knownTag) {
        return new FreemarkerContext(environmentVariables, requirements, issueTracking, relativeLink, knownTag);
    }
}
