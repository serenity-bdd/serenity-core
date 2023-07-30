package net.serenitybdd.maven.plugins;

import net.serenitybdd.core.history.FileSystemTestOutcomeSummaryRecorder;
import net.serenitybdd.core.history.TestOutcomeSummaryRecorder;
import net.thucydides.core.environment.SystemEnvironmentVariables;
import net.serenitybdd.core.di.SerenityInfrastructure;
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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_HISTORY_DIRECTORY;

/**
 * This plugin records a summary of test results in the target directory
 */
@Mojo(name = "history", requiresDependencyResolution = ResolutionScope.RUNTIME)
public class SerenityHistoryMojo extends AbstractMojo {

    private final static String DEFAULT_HISTORY_DIRECTORY = "history";
    /**
     * Test outcome summaries are stored here
     */
    @Parameter(property = "serenity.outputDirectory")
    public String outcomesDirectoryPath;

    @Parameter(property = "serenity.historyDirectory")
    public String historyDirectoryPath;

    @Parameter(property = "serenity.deletePreviousHistory")
    public Boolean deletePreviousHistory = false;

    @Parameter(defaultValue = "${session}")
    private MavenSession session;


    @Parameter(defaultValue = "${project}")
    public MavenProject project;


    protected TestOutcomeSummaryRecorder getTestOutcomeSummaryRecorder() {

        MavenProjectHelper.propagateBuildDir(session);

        EnvironmentVariables environmentVariables = SystemEnvironmentVariables.currentEnvironmentVariables();

        String configuredHistoryDirectoryPath = SERENITY_HISTORY_DIRECTORY.from(environmentVariables,
                                                                                Optional.ofNullable(historyDirectoryPath).orElse(DEFAULT_HISTORY_DIRECTORY));

        Path historyDirectory = Paths.get(configuredHistoryDirectoryPath);

        return new FileSystemTestOutcomeSummaryRecorder(historyDirectory, deletePreviousHistory);
    }

    private Path outputDirectory() {
        return (!StringUtils.isEmpty(outcomesDirectoryPath)) ?
                session.getCurrentProject().getBasedir().toPath().resolve(outcomesDirectoryPath).toAbsolutePath() :
                getConfiguration().getOutputDirectory().toPath();
    }

    private Configuration getConfiguration() {
        return SerenityInfrastructure.getConfiguration();
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Storing Serenity test result summaries");

        UpdatedClassLoader.withProjectClassesFrom(project);

        getTestOutcomeSummaryRecorder().recordOutcomeSummariesFrom(outputDirectory());
    }
}
