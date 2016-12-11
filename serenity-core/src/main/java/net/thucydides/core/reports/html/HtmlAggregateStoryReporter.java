package net.thucydides.core.reports.html;

import ch.lambdaj.function.convert.Converter;
import com.beust.jcommander.internal.Lists;
import net.serenitybdd.core.SerenitySystemProperties;
import net.serenitybdd.core.time.Stopwatch;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.model.ReportType;
import net.thucydides.core.reports.*;
import net.thucydides.core.requirements.DefaultRequirements;
import net.thucydides.core.requirements.Requirements;
import net.thucydides.core.requirements.model.RequirementsConfiguration;
import net.thucydides.core.requirements.reports.RequirementOutcome;
import net.thucydides.core.requirements.reports.RequirementsOutcomes;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static ch.lambdaj.Lambda.convert;
import static net.thucydides.core.guice.Injectors.getInjector;
import static net.thucydides.core.reports.html.ReportNameProvider.NO_CONTEXT;
import static net.thucydides.core.reports.html.TagReportingTask.tagReportsFor;

/**
 * Generates an aggregate acceptance test report in HTML form.
 * Reads all the reports from the output directory to generates aggregate HTML reports
 * summarizing the results.
 */
public class HtmlAggregateStoryReporter extends HtmlReporter implements UserStoryTestReporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlAggregateStoryReporter.class);

    private String projectName;
    private String relativeLink;
    private final IssueTracking issueTracking;

    private final RequirementsConfiguration requirementsConfiguration;
    private final ReportNameProvider reportNameProvider;
    private final Requirements requirements;

    private final EnvironmentVariables environmentVariables;
    private FormatConfiguration formatConfiguration;

    private Stopwatch stopwatch = new Stopwatch();
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
        this(projectName,relativeLink, issueTracking, environmentVariables, new DefaultRequirements());
    }

    public HtmlAggregateStoryReporter(final String projectName,
                                      final String relativeLink,
                                      final IssueTracking issueTracking,
                                      final EnvironmentVariables environmentVariables,
                                      final Requirements requirements) {
        this.projectName = projectName;
        this.relativeLink = relativeLink;
        this.issueTracking = issueTracking;
        this.requirementsConfiguration = new RequirementsConfiguration(getEnvironmentVariables());
        this.environmentVariables = environmentVariables;
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

        LOGGER.debug("Copied screenshots after {} ms",stopwatch.lapTime());

        TestOutcomes allTestOutcomes = loadTestOutcomesFrom(sourceDirectory);

        LOGGER.debug("Loaded test outcomes after {} ms",stopwatch.lapTime());

        generateReportsForTestResultsIn(allTestOutcomes);

        LOGGER.debug("Generated reports after {} ms",stopwatch.lapTime());

        return allTestOutcomes;
    }

    private void copyScreenshotsFrom(File sourceDirectory) {
        CopyFiles.from(sourceDirectory).to(getOutputDirectory());
    }

    public void generateReportsForTestResultsIn(TestOutcomes testOutcomes) throws IOException {

        Stopwatch stopwatch = Stopwatch.started();
        LOGGER.info("Generating test results for {} tests",testOutcomes.getTestCount());

        FreemarkerContext context = new FreemarkerContext(environmentVariables, requirements.getRequirementsService(), issueTracking, relativeLink);

        List<String> requirementTypes = requirements.getRequirementsService().getRequirementTypes();
        RequirementsOutcomes requirementsOutcomes = requirements.getRequirementsOutcomeFactory().buildRequirementsOutcomesFrom(testOutcomes);

        LOGGER.info("{} requirements loaded after {} ms",requirementsOutcomes.getFlattenedRequirementCount(), stopwatch.lapTime());

        requirementsOutcomes = requirementsOutcomes.withoutUnrelatedRequirements();

        LOGGER.info("{} related requirements found after {} ms",requirementsOutcomes.getFlattenedRequirementCount(), stopwatch.lapTime());


        List<String> knownRequirementReportNames = requirementReportNamesFrom(requirementsOutcomes, reportNameProvider);

        Set<ReportingTask> reportingTasks = new HashSet<>();

        reportingTasks.add(new CopyResourcesTask());
        reportingTasks.add(new CopyTestResultsTask());
        reportingTasks.add(new AggregateReportingTask(context, environmentVariables, requirements.getRequirementsService(), getOutputDirectory(), testOutcomes));
        reportingTasks.add(new TagTypeReportingTask(context, environmentVariables, getOutputDirectory(), reportNameProvider, testOutcomes));
        reportingTasks.addAll(tagReportsFor(testOutcomes).using(context,
                                                                environmentVariables,
                                                                getOutputDirectory(),
                                                                reportNameProvider,
                                                                testOutcomes.getTags(),
                                                                knownRequirementReportNames));

        reportingTasks.addAll(ResultReports.resultReportsFor(testOutcomes,context, environmentVariables, getOutputDirectory(),reportNameProvider));
        reportingTasks.addAll(RequirementsReports.requirementsReportsFor(
                context, environmentVariables, getOutputDirectory(),
                reportNameProvider,
                requirements.getRequirementsOutcomeFactory(),
                requirements.getRequirementsService(),
                relativeLink,
                testOutcomes,
                requirementsOutcomes
        ));

        LOGGER.info("Starting generating reports: {} ms", stopwatch.lapTime());
        generateReportsFor(reportingTasks);

        LOGGER.info("Finished generating test results for {} tests after {} ms",testOutcomes.getTestCount(), stopwatch.stop());
    }

    private List<String> requirementReportNamesFrom(RequirementsOutcomes requirementsOutcomes, ReportNameProvider reportNameProvider) {
        return convert(requirementsOutcomes.getFlattenedRequirementOutcomes(), toRequirementReportNames(reportNameProvider));
    }

    private Converter<RequirementOutcome, String> toRequirementReportNames(final ReportNameProvider reportNameProvider) {
            return new Converter<RequirementOutcome, String>() {
                @Override
                public String convert(RequirementOutcome from) {
                    return reportNameProvider.forRequirement(from.getRequirement());
                }
            };
    }

    private void generateReportsFor(Collection<ReportingTask> reportingTasks) throws IOException {
        stopwatch.start();

        try {
            Reporter.generateReportsFor(reportingTasks);

            final List<Callable<Void>> partitions = Lists.newArrayList();
            for (ReportingTask reportingTask : reportingTasks) {
                partitions.add(new ReportExecutor(reportingTask));
            }

            final ExecutorService executorPool = Executors.newFixedThreadPool(NumberOfThreads.forIOOperations());
            for (Future<Void> executedTask : executorPool.invokeAll(partitions)) {
                executedTask.get();
            }
        } catch (Exception e) {
            LOGGER.error("Report generation failed", e);
        }

        LOGGER.debug("Test outcome reports generated in {} ms", stopwatch.stop());
    }

    private TestOutcomes loadTestOutcomesFrom(File sourceDirectory) throws IOException {
        return TestOutcomeLoader.loadTestOutcomes().inFormat(getFormat()).from(sourceDirectory);//.withRequirementsTags();
    }

    protected SerenitySystemProperties getSystemProperties() {
        return SerenitySystemProperties.getProperties();
    }

    public void setIssueTrackerUrl(String issueTrackerUrl) {
        if (issueTrackerUrl != null) {
            getSystemProperties().setValue(ThucydidesSystemProperty.THUCYDIDES_ISSUE_TRACKER_URL, issueTrackerUrl);
        }
    }

    public void setJiraUrl(String jiraUrl) {
        if (jiraUrl != null) {
            getSystemProperties().setValue(ThucydidesSystemProperty.JIRA_URL, jiraUrl);
        }
    }

    public void setJiraProject(String jiraProject) {
        if (jiraProject != null) {
            getSystemProperties().setValue(ThucydidesSystemProperty.JIRA_PROJECT, jiraProject);
        }
    }

    public void setJiraUsername(String jiraUsername) {
        if (jiraUsername != null) {
            getSystemProperties().setValue(ThucydidesSystemProperty.JIRA_USERNAME, jiraUsername);
        }
    }

    public void setJiraPassword(String jiraPassword) {
        if (jiraPassword != null) {
            getSystemProperties().setValue(ThucydidesSystemProperty.JIRA_PASSWORD, jiraPassword);
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


    private class ReportExecutor implements Callable<Void> {
        private final ReportingTask reportingTask;

        public ReportExecutor(ReportingTask reportingTask) {
            this.reportingTask = reportingTask;
        }

        @Override
        public Void call() throws Exception {
            Stopwatch reportingStopwatch = Stopwatch.started();
            reportingTask.generateReports();
            LOGGER.debug("{} generated in {} ms", reportingTask.toString(), reportingStopwatch.stop());
            return null;
        }
    }

    private class CopyResourcesTask implements ReportingTask {
        @Override
        public void generateReports() throws IOException {
            LOGGER.info("Copying resources to directory");
            copyResourcesToOutputDirectory();
        }
    }

    private class CopyTestResultsTask implements ReportingTask {
        @Override
        public void generateReports() throws IOException {
            copyTestResultsToOutputDirectory();
        }
    }
}

