package net.serenitybdd.maven.plugins;

import com.google.common.base.Splitter;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.environment.SystemEnvironmentVariables;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.reports.ExtendedReports;
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

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Generate extended reports.
 * This allows extended reports to be generated independently of the full aggregate report, and opens the possibility
 * of more tailored next-generation reporting.
 */
@Mojo(name = "reports", requiresProject = false, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class SerenityReportMojo extends AbstractMojo {

    /**
     * Reports are generated here
     */
    @Parameter(property = "serenity.outputDirectory")

    public File outputDirectory;

    /**
     * Serenity test reports are read from here
     */
    @Parameter(property = "serenity.sourceDirectory")
    public File sourceDirectory;

    /**
     * Base directory for requirements.
     */
    @Parameter
    public String requirementsBaseDir;

    EnvironmentVariables environmentVariables;

    Configuration configuration;

    @Parameter(defaultValue = "${session}")
    protected MavenSession session;

    @Parameter(property = "tags")
    public String tags;

    @Parameter(defaultValue = "${project}")
    public MavenProject project;

    /**
     * Serenity project key
     */
    @Parameter(property = "thucydides.project.key", defaultValue = "default")
    public String projectKey;

    @Parameter(property = "serenity.reports")
    public String reports;

    public void prepareExecution() throws MojoExecutionException {
        MavenProjectHelper.propagateBuildDir(session);
        configureOutputDirectorySettings();
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }
        configureEnvironmentVariables();
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
        return SystemEnvironmentVariables.currentEnvironmentVariables();
    }

    private Configuration getConfiguration() {
        if (configuration == null) {
            configuration = Injectors.getInjector().getProvider(Configuration.class).get();
        }
        return configuration;
    }

    private void configureEnvironmentVariables() {
        Locale.setDefault(Locale.ENGLISH);
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

    public void execute() throws MojoExecutionException {
        prepareExecution();
        generateExtraReports();
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
                }
        );
    }

}
