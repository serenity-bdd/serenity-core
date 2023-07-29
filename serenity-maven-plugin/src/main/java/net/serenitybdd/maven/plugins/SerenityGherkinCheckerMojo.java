package net.serenitybdd.maven.plugins;

import net.thucydides.core.environment.SystemEnvironmentVariables;
import net.serenitybdd.core.di.SerenityInfrastructure;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.reports.ResultChecker;
import net.thucydides.core.requirements.RootDirectory;
import net.thucydides.core.requirements.model.cucumber.FeatureFileChecker;
import net.thucydides.core.requirements.model.cucumber.FeatureFileFinder;
import net.thucydides.core.requirements.model.cucumber.InvalidFeatureFileException;
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

import java.io.File;
import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * This plugin checks for inconsistencies or errors in Cucumber feature files that may cause issues with the Serenity reports.
 * Current checks include:
 *   - Duplicate scenario names
 *   - Empty feature names
 *   - Empty scenario names
 *   - Duplicate feature file names with the same parent folder names
 *   - Gherkin syntax errors
 */
@Mojo(name = "check-gherkin", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class SerenityGherkinCheckerMojo extends AbstractMojo {

    @Parameter(defaultValue = "${session}")
    protected MavenSession session;

    /**
     * The root directory containing the Cucumber feature files
     */
    @Parameter(defaultValue = "${basedir}/src/test/resources/features")
    protected String featureFilesDirectory;

    /**
     * Duplicate feature names are not allowed by default. Set this to true to allow duplicate feature names.
     * This is not recommended, as it can cause confusion in the reports and duplicate feature files may not be executed
     * correctly by the Cucumber runners classes.
     */
    @Parameter(defaultValue = "false")
    protected boolean allowDuplicateFeatureNames;

    @Parameter(defaultValue = "${project}")
    public MavenProject project;

    FeatureFileChecker checker = new FeatureFileChecker();

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Checking Feature Files");

        UpdatedClassLoader.withProjectClassesFrom(project);

        FeatureFileFinder finder = new FeatureFileFinder(featureFilesDirectory);
        try {
            checker.check(finder.findFeatureFiles(), allowDuplicateFeatureNames);
        } catch (InvalidFeatureFileException e) {
            throw new MojoFailureException(e.getMessage());
        } catch (Throwable e) {
            getLog().error(e);
            throw new MojoFailureException(e.getMessage());
        }
    }
}
