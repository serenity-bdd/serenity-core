package net.thucydides.core.reports.html;

import com.beust.jcommander.internal.Lists;
import net.serenitybdd.core.SerenitySystemProperties;
import net.serenitybdd.core.time.Stopwatch;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.model.ReportType;
import net.thucydides.core.reports.*;
import net.thucydides.core.requirements.DefaultRequirements;
import net.thucydides.core.requirements.model.RequirementsConfiguration;
import net.thucydides.core.requirements.Requirements;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.thucydides.core.guice.Injectors.getInjector;
import static net.thucydides.core.reports.html.ReportNameProvider.NO_CONTEXT;

/**
 * Generates an aggregate acceptance test report in HTML form.
 * Reads all the reports from the output directory to generates aggregate HTML reports
 * summarizing the results.
 */
public class HtmlAggregateStoryReporter extends HtmlReporter implements UserStoryTestReporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlAggregateStoryReporter.class);
    public static final int REPORT_GENERATION_THREAD_POOL_SIZE = 16;
    private static final int MAX_BATCHES = 128;

    private String projectName;
    private String relativeLink;
    private final IssueTracking issueTracking;

    private final RequirementsConfiguration requirementsConfiguration;
    private final ReportNameProvider reportNameProvider;
    private final Requirements requirements;

    private final EnvironmentVariables environmentVariables;
    private final FormatConfiguration formatConfiguration;

    private Stopwatch stopwatch = new Stopwatch();

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

        copyScreenshotsFrom(sourceDirectory);

        TestOutcomes allTestOutcomes = loadTestOutcomesFrom(sourceDirectory);

        generateReportsForTestResultsIn(allTestOutcomes);

        return allTestOutcomes;
    }

    private void copyScreenshotsFrom(File sourceDirectory) {
        if ((getOutputDirectory() == null) || (getOutputDirectory().equals(sourceDirectory))) {
            return;
        }

        CopyOption[] options = new CopyOption[]{StandardCopyOption.COPY_ATTRIBUTES};

        Path targetPath = Paths.get(getOutputDirectory().toURI());
        Path sourcePath = Paths.get(sourceDirectory.toURI());
        try (DirectoryStream<Path> directoryContents = Files.newDirectoryStream(sourcePath)) {
            for (Path sourceFile : directoryContents) {
                Path destinationFile = targetPath.resolve(sourceFile.getFileName());
                if (Files.notExists(destinationFile)) {
                    Files.copy(sourceFile, destinationFile, options);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error during copying files to the target directory", e);
        }
    }

    public void generateReportsForTestResultsIn(TestOutcomes testOutcomes) throws IOException {

        copyResourcesToOutputDirectory();

        FreemarkerContext context = new FreemarkerContext(environmentVariables, requirements, issueTracking, relativeLink);

        List<ReportingTask> reportingTasks = Lists.newArrayList();

        reportingTasks.add(new AggregateReportingTask(context, environmentVariables, requirements.getRequirementsService(), getOutputDirectory()));
        reportingTasks.add(new TagReportingTask(context, environmentVariables, getOutputDirectory(), reportNameProvider));
        reportingTasks.add(new TagTypeReportingTask(context, environmentVariables, getOutputDirectory(), reportNameProvider));
        reportingTasks.add(new ResultReportingTask(context, environmentVariables, getOutputDirectory(), reportNameProvider));
        reportingTasks.add(new RequirementsReportingTask(context, environmentVariables, getOutputDirectory(),
                reportNameProvider,
                requirements.getRequirementsOutcomeFactory(),
                requirements.getRequirementsService(),
                relativeLink));
        addAssociatedTagReporters(testOutcomes, context, reportingTasks);

        generateReportsFor(testOutcomes, reportingTasks);

        copyTestResultsToOutputDirectory();
    }

    private void addAssociatedTagReporters(TestOutcomes testOutcomes, FreemarkerContext context, List<ReportingTask> reportingTasks) {
        int maxPossibleBatches = testOutcomes.getTags().size();
        int totalBatches = (MAX_BATCHES < maxPossibleBatches) ? MAX_BATCHES : maxPossibleBatches;
        for (int batch = 1; batch <= totalBatches; batch++) {
            reportingTasks.add(new AssociatedTagReportingTask(context, environmentVariables, getOutputDirectory(), reportNameProvider)
                    .forBatch(batch, totalBatches));
        }
    }

    private void generateReportsFor(final TestOutcomes testOutcomes, List<ReportingTask> reportingTasks) throws IOException {
        stopwatch.start();

        ExecutorService executor = Executors.newFixedThreadPool(REPORT_GENERATION_THREAD_POOL_SIZE);

        for (final ReportingTask reportingTask : reportingTasks) {
            Runnable worker = new Runnable() {

                @Override
                public void run() {
                    try {
                        reportingTask.generateReportsFor(testOutcomes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            executor.execute(worker);
        }
        LOGGER.debug("Shutting down Test outcome reports generation");
        executor.shutdown();
        while (!executor.isTerminated()) {}

        LOGGER.debug("Test outcome reports generated in {} ms", stopwatch.stop());
    }

    private TestOutcomes loadTestOutcomesFrom(File sourceDirectory) throws IOException {
        return TestOutcomeLoader.loadTestOutcomes().inFormat(getFormat()).from(sourceDirectory).withRequirementsTags();
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
}

