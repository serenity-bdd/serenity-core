package net.thucydides.core.reports.html;

import net.serenitybdd.core.SerenitySystemProperties;
import net.serenitybdd.core.time.Stopwatch;
import net.serenitybdd.reports.model.DurationBucket;
import net.serenitybdd.reports.model.DurationDistribution;
import net.serenitybdd.reports.model.FrequentFailure;
import net.serenitybdd.reports.model.FrequentFailures;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.model.ReportType;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.*;
import net.thucydides.core.requirements.DefaultRequirements;
import net.thucydides.core.requirements.Requirements;
import net.thucydides.core.requirements.model.RequirementsConfiguration;
import net.thucydides.core.requirements.reports.RequirementsOutcomes;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

import static net.thucydides.core.ThucydidesSystemProperty.REPORT_SCOREBOARD_SIZE;
import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_TEST_ROOT;
import static net.thucydides.core.guice.Injectors.getInjector;
import static net.thucydides.core.reports.html.ReportNameProvider.NO_CONTEXT;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Generates an aggregate acceptance test report in HTML form.
 * Reads all the reports from the output directory to generates aggregate HTML reports
 * summarizing the results.
 */
public class HtmlAggregateStoryReporter extends HtmlReporter implements UserStoryTestReporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlAggregateStoryReporter.class);

    private String projectName;
    private String projectDirectory;
    private String relativeLink;
    private String tags;
    private final IssueTracking issueTracking;

    private final RequirementsConfiguration requirementsConfiguration;
    private final ReportNameProvider reportNameProvider;
    private final Requirements requirements;

    private FormatConfiguration formatConfiguration;
    private boolean generateTestOutcomeReports = false;

    public static final CopyOption[] COPY_OPTIONS = new CopyOption[]{StandardCopyOption.COPY_ATTRIBUTES};

    public HtmlAggregateStoryReporter(final String projectName) {
        this(projectName, "");
    }

    public HtmlAggregateStoryReporter(final String projectName, final Requirements requirements) {
        this(projectName,
                "",
                getInjector().getInstance(IssueTracking.class),
                getInjector().getProvider(EnvironmentVariables.class).get(),
                requirements);

    }

    public HtmlAggregateStoryReporter(final String projectName, final String relativeLink) {
        this(projectName,
                relativeLink,
                getInjector().getInstance(IssueTracking.class),
                getInjector().getProvider(EnvironmentVariables.class).get());
    }

    public HtmlAggregateStoryReporter(final String projectName,
                                      final IssueTracking issueTracking) {
        this(projectName,
                "",
                issueTracking,
                getInjector().getProvider(EnvironmentVariables.class).get(),
                new DefaultRequirements());
    }

    public HtmlAggregateStoryReporter(final String projectName,
                                      final String relativeLink,
                                      final IssueTracking issueTracking,
                                      final EnvironmentVariables environmentVariables) {
        this(projectName, relativeLink, issueTracking, environmentVariables, new DefaultRequirements());
    }

    public HtmlAggregateStoryReporter(final String projectName,
                                      final String relativeLink,
                                      final IssueTracking issueTracking,
                                      final EnvironmentVariables environmentVariables,
                                      final Requirements requirements) {
        super(environmentVariables);
        this.projectName = projectName;
        this.relativeLink = relativeLink;
        this.issueTracking = issueTracking;
        this.requirementsConfiguration = new RequirementsConfiguration(getEnvironmentVariables());
        this.formatConfiguration = new FormatConfiguration(environmentVariables);
        this.reportNameProvider = new ReportNameProvider(NO_CONTEXT, ReportType.HTML, requirements.getRequirementsService());
        this.requirements = requirements;
    }

    public OutcomeFormat getFormat() {
        return formatConfiguration.getPreferredFormat();
    }

    public String getProjectName() {
        return projectName;
    }

    public TestOutcomes generateReportsForTestResultsFrom(final File sourceDirectory) throws IOException {

        Stopwatch stopwatch = Stopwatch.started();
        copyScreenshotsFrom(sourceDirectory);

        LOGGER.trace("Copied screenshots after {}", stopwatch.lapTimeFormatted());

        TestOutcomes allTestOutcomes = loadTestOutcomesFrom(sourceDirectory);

        if (!isEmpty(tags)) {
            allTestOutcomes = allTestOutcomes.withTags(getTags());
        }
        LOGGER.trace("Loaded test outcomes after {}", stopwatch.lapTimeFormatted());

        generateReportsForTestResultsIn(allTestOutcomes);

        LOGGER.trace("Generated reports after {}", stopwatch.lapTimeFormatted());

        return allTestOutcomes;
    }

    private void copyScreenshotsFrom(File sourceDirectory) {
        CopyFiles.from(sourceDirectory).to(getOutputDirectory());
    }

    public void generateReportsForTestResultsIn(TestOutcomes testOutcomes) throws IOException {

        Stopwatch stopwatch = Stopwatch.started();
        LOGGER.debug("Generating test results for {} tests", testOutcomes.getTestCount());

        enhanceWithDurationTags(testOutcomes);

        FreemarkerContext context = new FreemarkerContext(environmentVariables, requirements.getRequirementsService(), issueTracking, relativeLink);

        RequirementsOutcomes requirementsOutcomes = requirements.getRequirementsOutcomeFactory().buildRequirementsOutcomesFrom(testOutcomes);

        LOGGER.debug("{} requirements loaded after {}", requirementsOutcomes.getFlattenedRequirementCount(), stopwatch.lapTimeFormatted());

//        requirementsOutcomes = requirementsOutcomes.withoutUnrelatedRequirements();

        LOGGER.debug("{} related requirements found after {}", requirementsOutcomes.getFlattenedRequirementCount(), stopwatch.lapTimeFormatted());

        List<String> knownRequirementReportNames = requirementReportNamesFrom(requirementsOutcomes, reportNameProvider);

        List<ReportingTask> reportingTasks = new ArrayList<>();

        if (generateTestOutcomeReports) {
            reportingTasks.addAll(HtmlTestOutcomeReportingTask.testOutcomeReportsFor(testOutcomes).using(environmentVariables, requirements.getRequirementsService(), getOutputDirectory(), issueTracking));
        }

        reportingTasks.add(new TextSummaryReportTask(context, environmentVariables, getOutputDirectory(), testOutcomes));
        reportingTasks.add(new CopyResourcesTask());
        reportingTasks.add(new CopyTestResultsTask());
        reportingTasks.add(new AggregateReportingTask(context, environmentVariables, requirements.getRequirementsService(), getOutputDirectory(), testOutcomes));

        List<String> requirementTypes =  requirementsConfiguration.getRequirementTypes();
        // CUSTOM TAG REPORTS
        reportingTasks.addAll(TagReportingTask.tagReportsFor(testOutcomes)
                .using(context,
                        environmentVariables,
                        getOutputDirectory(),
                        reportNameProvider,
                        testOutcomes.getTags(),
                        requirementTypes,
                        knownRequirementReportNames));

        // NESTED TAGS
        // Set<ReportingTask> nestedReports = nestedTagReports(testOutcomes, context, requirementTypes, knownRequirementReportNames);
        // reportingTasks.addAll(nestedReports);

        // ADD DURATION REPORTS
        reportingTasks.addAll(durationReports(testOutcomes, context, requirementTypes, knownRequirementReportNames));

        // REPORTS FOR EACH RESULT
        reportingTasks.addAll(ResultReports.resultReportsFor(testOutcomes, context, environmentVariables, getOutputDirectory(), reportNameProvider));

        // REQUIREMENTS REPORTS
        reportingTasks.addAll(
                RequirementsReports.requirementsReportsFor(
                        context, environmentVariables, getOutputDirectory(),
                        reportNameProvider,
                        requirements.getRequirementsOutcomeFactory(),
                        requirements.getRequirementsService(),
                        relativeLink,
                        testOutcomes,
                        requirementsOutcomes
                ));

        List<FrequentFailure> failures = FrequentFailures.from(testOutcomes).withMaxOf(REPORT_SCOREBOARD_SIZE.integerFrom(environmentVariables, 5));

        // ERROR REPORTS
        for(FrequentFailure failure : failures) {
            reportingTasks.add(new ErrorTypeReportingTask(context,
                    environmentVariables,
                    requirements.getRequirementsService(),
                    getOutputDirectory(),
                    reportNameProvider,
                    testOutcomes.withErrorType(failure.getType()).withLabel("Tests with error: " + failure.getName()),
                    failure.getType()));
        }

        Reporter.generateReportsFor(reportingTasks);

        LOGGER.info("Test results for {} tests generated in {} in directory: {}", testOutcomes.getTestCount(), stopwatch.executionTimeFormatted(), getOutputDirectory().toURI());
    }

    private Set<ReportingTask> nestedTagReports(TestOutcomes testOutcomes,
                                                 FreemarkerContext context,
                                                 List<String> requirementTypes,
                                                 List<String> knownRequirementReportNames) {
        Set<ReportingTask> reportingTasks = new HashSet<>();

        TagExclusions exclusions = TagExclusions.usingEnvironment(environmentVariables);
        DurationDistribution durationDistribution = new DurationDistribution(environmentVariables, testOutcomes);

        testOutcomes.getTags().stream()
                .filter(tag -> !requirements.getTypes().contains(tag.getType()))
                .filter(tag -> !tag.getType().equals("Duration"))
                .filter(exclusions::doNotExclude)
                .forEach(
                        knownTag -> {
                            List<ReportingTask> nested = TagReportingTask.tagReportsFor(testOutcomes.withTag(knownTag))
                                    .using(context.withParentTag(knownTag),
                                            environmentVariables,
                                            getOutputDirectory(),
                                            reportNameProvider.inContext(knownTag.getCompleteName()),
                                            durationDistribution.getDurationTags(),
                                            requirementTypes,
                                            knownRequirementReportNames);
                            reportingTasks.addAll(nested);
                        }
                );
        return reportingTasks;
    }

    private List<ReportingTask> durationReports(TestOutcomes testOutcomes,
                                                FreemarkerContext context,
                                                List<String> requirementTypes,
                                                List<String> knownRequirementReportNames) {
        DurationDistribution durationDistribution = new DurationDistribution(environmentVariables, testOutcomes);

        return TagReportingTask.tagReportsFor(testOutcomes).using(context,
                environmentVariables,
                getOutputDirectory(),
                reportNameProvider,
                durationDistribution.getDurationTags(),
                requirementTypes,
                knownRequirementReportNames);
    }

    private void enhanceWithDurationTags(TestOutcomes testOutcomes) {
        DurationDistribution durationDistribution = new DurationDistribution(environmentVariables, testOutcomes);
        for(TestOutcome testOutcome : testOutcomes.getOutcomes()) {
            enhanceWithDuration(testOutcome, durationDistribution);
        }
    }

    private void enhanceWithDuration(TestOutcome testOutcome, DurationDistribution durationDistribution) {
        Collection<DurationBucket> buckets = durationDistribution.findMatchingBucketsForTestOutcome(testOutcome);
        buckets.forEach(
                bucket -> testOutcome.addTag(TestTag.withName(bucket.getDuration()).andType("Duration"))
        );

    }

    private List<String> requirementReportNamesFrom(RequirementsOutcomes requirementsOutcomes,
                                                    ReportNameProvider reportNameProvider) {

        return requirementsOutcomes.getFlattenedRequirementOutcomes().stream()
                .map(req -> reportNameProvider.forRequirement(req.getRequirement()))
                .collect(Collectors.toList());
    }

    private TestOutcomes loadTestOutcomesFrom(File sourceDirectory) throws IOException {
        return TestOutcomeLoader.loadTestOutcomes().inFormat(getFormat()).from(sourceDirectory);//.withRequirementsTags();
    }

    protected SerenitySystemProperties getSystemProperties() {
        return SerenitySystemProperties.getProperties();
    }

    public void setIssueTrackerUrl(String issueTrackerUrl) {
        if (issueTrackerUrl != null) {
            setEnvironmentProperty(ThucydidesSystemProperty.SERENITY_ISSUE_TRACKER_URL, issueTrackerUrl);
        }
    }

    private void setEnvironmentProperty(ThucydidesSystemProperty property, String value) {
        getSystemProperties().setValue(property, value);
        environmentVariables.setProperty(property.getPropertyName(), value);
    }

    public void setJiraUrl(String jiraUrl) {
        if (jiraUrl != null) {
            setEnvironmentProperty(ThucydidesSystemProperty.JIRA_URL, jiraUrl);
        }
    }

    public void setJiraProject(String jiraProject) {
        if (jiraProject != null) {
            setEnvironmentProperty(ThucydidesSystemProperty.JIRA_PROJECT, jiraProject);
        }
    }

    public void setJiraUsername(String jiraUsername) {
        if (jiraUsername != null) {
            setEnvironmentProperty(ThucydidesSystemProperty.JIRA_USERNAME, jiraUsername);
        }
    }

    public void setTags(String tags) {
        this.tags = (tags != null) ? tags.replaceAll("\\s+((or)|(OR)|(and)|(AND))\\s+", ",") : null;
    }

    public void setJiraPassword(String jiraPassword) {
        if (jiraPassword != null) {
            setEnvironmentProperty(ThucydidesSystemProperty.JIRA_PASSWORD, jiraPassword);
        }
    }

    public List<String> getRequirementTypes() {
        List<String> types = requirements.getTypes();
        if (types.isEmpty()) {
            LOGGER.warn("No requirement types found in the test outcome requirements: using default requirements");
            return requirementsConfiguration.getRequirementTypes();
        } else {
            return types;
        }
    }

    public List<TestTag> getTags() {

        List<TestTag> tagList = new ArrayList<>();

        if (isEmpty(tags)) {
            return tagList;
        }

        for (String tagValue : StringUtils.split(tags, ",")) {
            tagList.add(TestTag.withValue(tagValue.trim()));
        }
        return tagList;
    }

    public void setGenerateTestOutcomeReports() {
        this.generateTestOutcomeReports = true;
    }

    public void setProjectDirectory(String projectDirectory) {
        this.projectDirectory = projectDirectory;
        environmentVariables.setProperty("serenity.project.directory", projectDirectory);
        System.setProperty("serenity.project.directory", projectDirectory);
    }

    public void setTestRoot(String testRoot) {
        if (testRoot != null) {
            setEnvironmentProperty(SERENITY_TEST_ROOT, testRoot);
            requirements.getRequirementsService().resetRequirements();
        }
    }

    private class CopyResourcesTask implements ReportingTask {
        @Override
        public void generateReports() throws IOException {
            copyResourcesToOutputDirectory();
        }

        @Override
        public String reportName() {
            return "CopyResourcesTask";
        }
    }

    private class CopyTestResultsTask implements ReportingTask {
        @Override
        public void generateReports() throws IOException {
            copyTestResultsToOutputDirectory();
        }

        @Override
        public String reportName() {
            return "CopyTestResultsTask";
        }
    }

}

