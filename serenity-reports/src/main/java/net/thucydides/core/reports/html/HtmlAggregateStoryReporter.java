package net.thucydides.core.reports.html;

import net.serenitybdd.model.SerenitySystemProperties;
import net.serenitybdd.model.di.ModelInfrastructure;
import net.serenitybdd.model.time.Stopwatch;
import net.serenitybdd.reports.model.DurationDistribution;
import net.serenitybdd.reports.model.FrequentFailure;
import net.serenitybdd.reports.model.FrequentFailures;
import net.thucydides.core.reports.CopyFiles;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.domain.ReportType;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestTag;
import net.thucydides.model.issues.IssueTracking;
import net.thucydides.model.reports.*;
import net.thucydides.model.reports.html.ReportNameProvider;
import net.thucydides.model.reports.html.TestOutcomesContext;
import net.thucydides.model.requirements.DefaultRequirements;
import net.thucydides.model.requirements.Requirements;
import net.thucydides.model.requirements.model.RequirementsConfiguration;
import net.thucydides.model.requirements.reports.RequirementsOutcomes;
import net.thucydides.model.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.thucydides.model.ThucydidesSystemProperty.REPORT_SCOREBOARD_SIZE;
import static net.thucydides.model.ThucydidesSystemProperty.SERENITY_TEST_ROOT;
import static net.thucydides.model.reports.html.ReportNameProvider.NO_CONTEXT;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Generates an aggregate acceptance test report in HTML form.
 * Reads all the reports from the output directory to generates aggregate HTML reports
 * summarizing the results.
 */
public class HtmlAggregateStoryReporter extends HtmlReporter implements UserStoryTestReporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlAggregateStoryReporter.class);

    private final String projectName;
    private String projectDirectory;
    private final String relativeLink;
    private String tags;
    private final IssueTracking issueTracking;

    private final RequirementsConfiguration requirementsConfiguration;
    private final ReportNameProvider reportNameProvider;
    private final Requirements requirements;

    private final FormatConfiguration formatConfiguration;
    private boolean generateTestOutcomeReports = false;

    public static final CopyOption[] COPY_OPTIONS = new CopyOption[]{StandardCopyOption.COPY_ATTRIBUTES};

    public HtmlAggregateStoryReporter(final String projectName) {
        this(projectName, "");
    }

    public HtmlAggregateStoryReporter(final String projectName, final Requirements requirements) {
        this(projectName,
                "",
                ModelInfrastructure.getIssueTracking(),
                ModelInfrastructure.getEnvironmentVariables(),
                requirements);

    }

    public HtmlAggregateStoryReporter(final String projectName, final String relativeLink) {
        this(projectName,
                relativeLink,
                ModelInfrastructure.getIssueTracking(),
                ModelInfrastructure.getEnvironmentVariables());
    }

    public HtmlAggregateStoryReporter(final String projectName,
                                      final IssueTracking issueTracking) {
        this(projectName,
                "",
                ModelInfrastructure.getIssueTracking(),
                ModelInfrastructure.getEnvironmentVariables(),
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

        if (projectDirectory != null) {
            ModelInfrastructure.getConfiguration().setProjectDirectory(Paths.get(projectDirectory));
        }
        Stopwatch stopwatch = Stopwatch.started();
        copyScreenshotsFrom(sourceDirectory);

        LOGGER.trace("Copied screenshots after {}", stopwatch.lapTimeFormatted());

        TestOutcomes allTestOutcomes = loadTestOutcomesFrom(sourceDirectory);

        if (!isEmpty(tags)) {
            allTestOutcomes = allTestOutcomes.withTags(getTags());
        }
        LOGGER.trace("Loaded test outcomes after {}", stopwatch.lapTimeFormatted());

        TestOutcomesContext.setTestOutcomes(allTestOutcomes);
        generateReportsForTestResultsIn(allTestOutcomes);

        LOGGER.trace("Generated reports after {}", stopwatch.lapTimeFormatted());

        return allTestOutcomes;
    }

    private void copyScreenshotsFrom(File sourceDirectory) {
        CopyFiles.from(sourceDirectory).to(getOutputDirectory());
    }

    public void generateReportsForTestResultsIn(TestOutcomes testOutcomes) throws IOException {

        Stopwatch stopwatch = Stopwatch.started();
        try (Reporter reporter = new Reporter(environmentVariables)) {

            LOGGER.debug("Generating test results for {} tests", testOutcomes.getTestCount());

            DurationDistribution durationDistribution = new DurationDistribution(environmentVariables, testOutcomes);

            enhanceWithDurationTags(durationDistribution, testOutcomes);

            FreemarkerContext context = new FreemarkerContext(environmentVariables, requirements.getRequirementsService(), issueTracking, relativeLink);

            RequirementsOutcomes requirementsOutcomes = requirements.getRequirementsOutcomeFactory().buildRequirementsOutcomesFrom(testOutcomes);

            LOGGER.debug("{} requirements loaded after {}", requirementsOutcomes.getFlattenedRequirementCount(), stopwatch.lapTimeFormatted());

//        requirementsOutcomes = requirementsOutcomes.withoutUnrelatedRequirements();

            LOGGER.debug("{} related requirements found after {}", requirementsOutcomes.getFlattenedRequirementCount(), stopwatch.lapTimeFormatted());

            List<String> knownRequirementReportNames = requirementReportNamesFrom(requirementsOutcomes, reportNameProvider);

            if (generateTestOutcomeReports) {
                reporter.generateReportsFor(HtmlTestOutcomeReportingTask.testOutcomeReportsFor(testOutcomes)
                        .using(environmentVariables, requirements.getRequirementsService(), getOutputDirectory(), issueTracking));
            }

            List<String> requirementTypes = requirementsConfiguration.getRequirementTypes();
            LOGGER.info("");
            LOGGER.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            LOGGER.info("Generating Serenity Reports");
            LOGGER.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            LOGGER.info("");
            LOGGER.info("Test Results: {} tests completed " +
                            "({} passed, {} failed, {} with errors, {} compromised, {} pending, {} aborted)",
                testOutcomes.getTestCount(),
                testOutcomes.getPassingTests().getTestCount(),
                testOutcomes.getFailingTests().getTestCount(),
                testOutcomes.getErrorTests().getTestCount(),
                testOutcomes.getCompromisedTests().getTestCount(),
                testOutcomes.getPendingTests().getTestCount(),
                testOutcomes.getAbortedTests().getTestCount());

            reporter.generateReportsFor(
                    Stream.of(
                            // SUMMARY REPORTS
                            Stream.of(
                                    new TextSummaryReportTask(context, environmentVariables, getOutputDirectory(), testOutcomes),
                                    new CopyResourcesTask(),
                                    new CopyTestResultsTask(),
                                    new AggregateReportingTask(context, environmentVariables, requirements.getRequirementsService(), getOutputDirectory(), testOutcomes)
                            ),
                            // CUSTOM TAG REPORTS
                            TagReportingTask.tagReportsFor(testOutcomes)
                                    .using(context,
                                            environmentVariables,
                                            getOutputDirectory(),
                                            reportNameProvider,
                                            testOutcomes.getTags(),
                                            requirementTypes,
                                            knownRequirementReportNames),
                            // DURATION REPORTS
                            tagReports(durationDistribution, testOutcomes, context, requirementTypes, knownRequirementReportNames)
                    ).flatMap(stream -> stream)
            );
            LOGGER.info("  ✓ Summary reports");

            // REQUIREMENTS REPORTS
            reporter.generateReportsFor(
                    RequirementsReports.requirementsReportsFor(
                            context, environmentVariables, getOutputDirectory(),
                            reportNameProvider,
                            requirements.getRequirementsOutcomeFactory(),
                            requirements.getRequirementsService(),
                            relativeLink,
                            testOutcomes,
                            requirementsOutcomes
                    )
            );
            LOGGER.info("  ✓ Requirements reports");

            // REPORTS FOR EACH RESULT
            reporter.generateReportsFor(ResultReports.resultReportsFor(testOutcomes, context, environmentVariables, getOutputDirectory(), reportNameProvider));
            LOGGER.info("  ✓ Result reports");

            // ERROR REPORTS
            List<FrequentFailure> failures = FrequentFailures.from(testOutcomes).withMaxOf(REPORT_SCOREBOARD_SIZE.integerFrom(environmentVariables, 5));
            reporter.generateReportsFor(
                    failures.stream()
                            .map(failure -> new ErrorTypeReportingTask(context,
                                    environmentVariables,
                                    requirements.getRequirementsService(),
                                    getOutputDirectory(),
                                    reportNameProvider,
                                    testOutcomes.withErrorType(failure.getType()).withLabel("Tests with error: " + failure.getName()),
                                    failure.getType()))
            );
            LOGGER.info("  ✓ Error reports");
            LOGGER.info("");
            LOGGER.info("Reports generated in {}", stopwatch.executionTimeFormatted());
        }
    }

    private Stream<ReportingTask> tagReports(DurationDistribution durationDistribution,
                                             TestOutcomes testOutcomes,
                                             FreemarkerContext context,
                                             List<String> requirementTypes,
                                             List<String> knownRequirementReportNames) {
        return TagReportingTask.tagReportsFor(testOutcomes).using(context,
                environmentVariables,
                getOutputDirectory(),
                reportNameProvider,
                durationDistribution.getDurationTagsSet(),
                requirementTypes,
                knownRequirementReportNames);
    }

    private void enhanceWithDurationTags(DurationDistribution distribution, TestOutcomes testOutcomes) {
        for (TestOutcome testOutcome : testOutcomes.getOutcomes()) {
            enhanceWithDuration(testOutcome, distribution);
        }
    }

    private void enhanceWithDuration(TestOutcome testOutcome, DurationDistribution durationDistribution) {
        durationDistribution.findMatchingBucketsForTestOutcome(testOutcome).forEach(
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
        return TestOutcomeLoader.loadTestOutcomes().inFormat(getFormat()).from(sourceDirectory).withRequirementsTags();
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

