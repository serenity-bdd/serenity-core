package net.serenitybdd.maven.plugins;

import com.google.common.base.Splitter;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.environment.SystemEnvironmentVariables;
import net.serenitybdd.core.di.SerenityInfrastructure;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.reports.ExtendedReports;
import net.thucydides.core.reports.ResultChecker;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.UserStoryTestReporter;
import net.thucydides.core.reports.html.HtmlAggregateStoryReporter;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generate aggregate XML acceptance test reports.
 */
@Mojo(name = "aggregate", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, aggregator = true)
public class SerenityAggregatorMojo extends AbstractMojo {

    private final static Logger LOGGER = LoggerFactory.getLogger(SerenityAggregatorMojo.class);

    /**
     * Aggregate reports are generated here
     */
    @Parameter(property = "serenity.outputDirectory")

    public File outputDirectory;

    /**
     * Serenity test reports are read from here
     */
    @Parameter(property = "serenity.sourceDirectory")
    public File sourceDirectory;

    /**
     * URL of the issue tracking system to be used to generate links for issue numbers.
     */
    @Parameter
    public String issueTrackerUrl;

    /**
     * Base URL for JIRA, if you are using JIRA as your issue tracking system.
     * If you specify this property, you don't need to specify the issueTrackerUrl.
     */
    @Parameter
    public String jiraUrl;

    @Parameter
    public String jiraUsername;

    @Parameter
    public String jiraPassword;

    /**
     * JIRA project key, which will be prepended to the JIRA issue numbers.
     */
    @Parameter
    public String jiraProject;

    /**
     * Base directory for requirements.
     */
    @Parameter
    public String requirementsBaseDir;

    EnvironmentVariables environmentVariables;

    Configuration configuration;

    @Parameter(defaultValue = "${session}")
    protected MavenSession session;

    /**
     * Serenity project key
     */
    @Parameter(property = "thucydides.project.key", defaultValue = "default")
    public String projectKey;

    /**
     *
     */
    @Parameter(property = "tags")
    public String tags;

    @Parameter(defaultValue = "${project}")
    public MavenProject project;

    /**
     * Specify a comma-separated list of additional reports to be produced by the aggregate goal.
     */
    @Parameter(property = "serenity.reports")
    public String reports;

    @Parameter
    public Map<String, String> systemPropertyVariables;

    /**
     * Set this to true (default value) if you want the aggregate task to ignore test failures and continue the build process
     * If set to false, the aggregate task will end with an error if any tests are broken.
     */
    @Parameter(defaultValue="true")
    public boolean ignoreFailedTests;

    private Path projectDirectory;

    protected void setOutputDirectory(final File outputDirectory) {
        this.outputDirectory = outputDirectory;
        getConfiguration().setOutputDirectory(this.outputDirectory);
    }

    protected void setSourceDirectory(final File sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    public void prepareExecution() throws MojoExecutionException {
        projectDirectory = project.getBasedir().toPath();
        configureEnvironmentVariables();
        MavenProjectHelper.propagateBuildDir(session);
        configureOutputDirectorySettings();
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }
        UpdatedClassLoader.withProjectClassesFrom(project);
    }

    private void configureOutputDirectorySettings() {
        getConfiguration().setProjectDirectory(projectDirectory);

        if (outputDirectory == null) {
            outputDirectory = getConfiguration().getOutputDirectory();
        }
        if (sourceDirectory == null) {
            sourceDirectory = getConfiguration().getOutputDirectory();
        }
        if (!outputDirectory.isAbsolute()) {
            outputDirectory = projectDirectory.resolve(outputDirectory.toPath()).toFile();
        }
        if (!sourceDirectory.isAbsolute()) {
            sourceDirectory = projectDirectory.resolve(sourceDirectory.toPath()).toFile();
        }
        getLog().info("GENERATING REPORTS FOR: " + projectDirectory);
        SerenityInfrastructure.getConfiguration().setProjectDirectory(projectDirectory);
    }

    private EnvironmentVariables getEnvironmentVariables() {
        if (environmentVariables == null) {
            environmentVariables = SystemEnvironmentVariables.currentEnvironmentVariables();
        }
        return environmentVariables;
    }

    private Configuration getConfiguration() {
        if (configuration == null) {
            configuration = SerenityInfrastructure.getConfiguration();
        }
        return configuration;
    }

    private void configureEnvironmentVariables() {
        Locale.setDefault(Locale.ENGLISH);
        if (systemPropertyVariables != null) {
            systemPropertyVariables.forEach(
                    System::setProperty
            );
        }
        updateSystemProperty(ThucydidesSystemProperty.SERENITY_PROJECT_KEY.getPropertyName(), projectKey, Serenity.getDefaultProjectKey());
        updateSystemProperty(ThucydidesSystemProperty.SERENITY_TEST_REQUIREMENTS_BASEDIR.toString(), requirementsBaseDir);
    }

    private void updateSystemProperty(String key, String value, String defaultValue) {
        getEnvironmentVariables().setProperty(key,
                Optional.ofNullable(value).orElse(defaultValue));
    }

    private void updateSystemProperty(String key, String value) {

        Optional.ofNullable(value).ifPresent(
                propertyValue -> getEnvironmentVariables().setProperty(key, propertyValue)
        );
    }

    private HtmlAggregateStoryReporter reporter;

    protected void setReporter(final HtmlAggregateStoryReporter reporter) {
        this.reporter = reporter;
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        prepareExecution();

        try {
            TestResult testResult = generateHtmlStoryReports();
            generateExtraReports();
            generateCustomReports();
            if (!ignoreFailedTests) {
                switch (testResult) {
                    case ERROR: throw new MojoFailureException("An error occurred in the Serenity tests");
                    case FAILURE: throw new MojoFailureException("A failure occurred in the Serenity tests");
                    case COMPROMISED: throw new MojoFailureException("There were compromised tests in the Serenity test suite");
                }
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Error generating aggregate serenity reports", e);
        }
    }

    private void generateCustomReports() throws IOException {
        Collection<UserStoryTestReporter> customReporters = getCustomReportsFor(environmentVariables);

         for (UserStoryTestReporter reporter : customReporters) {
            reporter.generateReportsForTestResultsFrom(sourceOfTestResult());
        }
    }

    private Collection<UserStoryTestReporter> getCustomReportsFor(EnvironmentVariables environmentVariables) {

        return environmentVariables.getKeys().stream()
                .filter(key -> key.startsWith("serenity.custom.reporters."))
                .map(this::reportFrom)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<UserStoryTestReporter> reportFrom(String key) {
        String reportClass = environmentVariables.getProperty(key);
        try {
            return Optional.of((UserStoryTestReporter) Class.forName(reportClass, true, Thread.currentThread().getContextClassLoader()).newInstance());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    protected HtmlAggregateStoryReporter getReporter() {
        if (reporter == null) {
            reporter = new HtmlAggregateStoryReporter(projectKey);
        }
        return reporter;

    }

    private TestResult generateHtmlStoryReports() throws IOException {
        getReporter().setProjectDirectory(projectDirectory.toFile().getPath());
        getReporter().setSourceDirectory(sourceDirectory);
        getReporter().setOutputDirectory(outputDirectory);
        getReporter().setIssueTrackerUrl(issueTrackerUrl);
        getReporter().setJiraUrl(jiraUrl);
        getReporter().setJiraProject(jiraProject);
        getReporter().setJiraUsername(jiraUsername);
        getReporter().setJiraPassword(jiraPassword);
        getReporter().setTags(tags);
        getReporter().setGenerateTestOutcomeReports();
        TestOutcomes outcomes = getReporter().generateReportsForTestResultsFrom(sourceDirectory);
        return new ResultChecker(outputDirectory).checkTestResults(outcomes);
    }

    private void generateExtraReports() {

        if (StringUtils.isEmpty(reports)) {
            return;
        }
        List<String> extendedReportTypes = Splitter.on(",").splitToList(reports);
        ExtendedReports.named(extendedReportTypes).forEach(
                report -> {
                    report.setProjectDirectory(projectDirectory.toFile().getPath());
                    report.setSourceDirectory(sourceDirectory.toPath());
                    report.setOutputDirectory(outputDirectory.toPath());
                    Path generatedReport = report.generateReport();
                    LOGGER.info("  - {}: {}", report.getDescription(), generatedReport.toUri());
                }
        );
    }

    private File sourceOfTestResult() {
        if ((sourceDirectory != null) && (sourceDirectory.exists())) {
            return sourceDirectory;
        } else {
            return outputDirectory;
        }

    }
}
