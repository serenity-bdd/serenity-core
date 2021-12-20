package net.serenitybdd.maven.plugins;

import com.google.common.base.Splitter;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
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
//@Mojo(name = "aggregate", requiresProject = false, requiresDependencyResolution = ResolutionScope.RUNTIME, aggregator = true)
@Mojo(name = "aggregate", requiresDependencyResolution = ResolutionScope.RUNTIME, aggregator = true)
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

    @Parameter(property = "tags")
    public String tags;

    @Parameter(defaultValue = "${project}")
    public MavenProject project;

    @Parameter
    public boolean generateOutcomes;

    @Parameter(property = "serenity.reports")
    public String reports;

    @Parameter
    public Map<String, String> systemPropertyVariables;

    protected void setOutputDirectory(final File outputDirectory) {
        this.outputDirectory = outputDirectory;
        getConfiguration().setOutputDirectory(this.outputDirectory);
    }

    protected void setSourceDirectory(final File sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    public void prepareExecution() throws MojoExecutionException {
        configureEnvironmentVariables();
        MavenProjectHelper.propagateBuildDir(session);
        configureOutputDirectorySettings();
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }
        UpdatedClassLoader.withProjectClassesFrom(project);
    }

    private void configureOutputDirectorySettings() {
        if (outputDirectory == null) {
            outputDirectory = getConfiguration().getOutputDirectory();
        }
        if (sourceDirectory == null) {
            sourceDirectory = getConfiguration().getOutputDirectory();
        }
        final Path projectDir = session.getCurrentProject().getBasedir().toPath();

        if (!outputDirectory.isAbsolute()) {
            outputDirectory = projectDir.resolve(outputDirectory.toPath()).toFile();
        }
        if (!sourceDirectory.isAbsolute()) {
            sourceDirectory = projectDir.resolve(sourceDirectory.toPath()).toFile();
        }
    }

    private EnvironmentVariables getEnvironmentVariables() {
        if (environmentVariables == null) {
            environmentVariables = Injectors.getInjector().getProvider(EnvironmentVariables.class).get();
        }
        return environmentVariables;
    }

    private Configuration getConfiguration() {
        if (configuration == null) {
            configuration = Injectors.getInjector().getProvider(Configuration.class).get();
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

    public void execute() throws MojoExecutionException {
        prepareExecution();

        try {
            generateHtmlStoryReports();
            generateExtraReports();
            generateCustomReports();
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
            return Optional.of((UserStoryTestReporter) Class.forName(reportClass).newInstance());
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

    private void generateHtmlStoryReports() throws IOException {
        getReporter().setSourceDirectory(sourceDirectory);
        getReporter().setOutputDirectory(outputDirectory);
        getReporter().setIssueTrackerUrl(issueTrackerUrl);
        getReporter().setJiraUrl(jiraUrl);
        getReporter().setJiraProject(jiraProject);
        getReporter().setJiraUsername(jiraUsername);
        getReporter().setJiraPassword(jiraPassword);
        getReporter().setTags(tags);

        if (generateOutcomes) {
            getReporter().setGenerateTestOutcomeReports();
        }
        TestOutcomes outcomes = getReporter().generateReportsForTestResultsFrom(sourceDirectory);
        new ResultChecker(outputDirectory).checkTestResults(outcomes);
    }

    private void generateExtraReports() {

        if (StringUtils.isEmpty(reports)) {
            return;
        }
        List<String> extendedReportTypes = Splitter.on(",").splitToList(reports);
        ExtendedReports.named(extendedReportTypes).forEach(
                report -> {
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
