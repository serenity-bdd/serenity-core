package net.serenitybdd.maven.plugins;

import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.reports.ResultChecker;
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

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * This plugin checks for the presence of failing tests in the target directory.
 * You typically run this goal directly after the aggregate goal, to ensure that the build fails if there are errors.
 */
@Mojo(name = "check", requiresDependencyResolution = ResolutionScope.RUNTIME)
public class SerenityCheckMojo extends AbstractMojo {
    /**
     * Aggregate reports are generated here
     */
    @Parameter(property = "serenity.outputDirectory")
    public String outputDirectoryPath;

    @Parameter(property = "tags", defaultValue = "")
    public String tags;

    @Parameter(defaultValue = "${session}")
    private MavenSession session;

    @Parameter(defaultValue = "${project}")
    public MavenProject project;

    protected ResultChecker getResultChecker() {

        MavenProjectHelper.propagateBuildDir(session);
        File outputDirectory;

        if(isNotEmpty(outputDirectoryPath)){
            outputDirectory = session.getCurrentProject().getBasedir().toPath().resolve(outputDirectoryPath).toFile();
        }else{
            outputDirectory = session.getCurrentProject().getBasedir().toPath().resolve(getConfiguration().getOutputDirectory().toPath()).toFile();
        }
        return new ResultChecker(outputDirectory, StringUtils.trimToEmpty(tags));
    }

    private Configuration getConfiguration() {
        return Injectors.getInjector().getProvider(Configuration.class).get();
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Checking Serenity test results");

        UpdatedClassLoader.withProjectClassesFrom(project);

        TestResult testResult = getResultChecker().checkTestResults();

        switch (testResult) {
            case ERROR: throw new MojoFailureException("An error occurred in the Serenity tests");
            case FAILURE: throw new MojoFailureException("A failure occurred in the Serenity tests");
            case COMPROMISED: throw new MojoFailureException("There were compromised tests in the Serenity test suite");
        }
    }
}
