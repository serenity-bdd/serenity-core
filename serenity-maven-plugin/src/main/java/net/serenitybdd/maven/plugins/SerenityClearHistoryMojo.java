package net.serenitybdd.maven.plugins;

import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;
import org.apache.commons.io.FileUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This plugin records a summary of test results in the target directory
 */
@Mojo(name = "clear-history")
public class SerenityClearHistoryMojo extends AbstractMojo {

    @Parameter(property = "serenity.historyDirectory")
    public String historyDirectoryPath;

    @Parameter(defaultValue = "${session}")
    private MavenSession session;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Clearing Serenity test result summaries");

        EnvironmentVariables environmentVariables = SystemEnvironmentVariables.currentEnvironmentVariables();

        String configuredHistoryDirectoryPath = HistoryDirectory.configuredIn(environmentVariables, historyDirectoryPath);

        Path historyDirectory = Paths.get(configuredHistoryDirectoryPath);

        try {
            FileUtils.deleteDirectory(historyDirectory.toFile());
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage());
        }
    }
}
