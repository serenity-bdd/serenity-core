package net.serenitybdd.ant;

import net.serenitybdd.ant.util.PathProcessor;
import net.thucydides.core.reports.html.HtmlAggregateStoryReporter;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class SerenityReportingTask extends Task {

    /**
     * Aggregate reports are generated here
     */
    public String outputDirectory;

    /**
     * Serenity test reports are read from here
     */
    public String sourceDirectory;

    /**
     * URL of the issue tracking system to be used to generate links for issue numbers.
     */
    public String issueTrackerUrl;

    /**
     * Base URL for JIRA, if you are using JIRA as your issue tracking system.
     * If you specify this property, you don't need to specify the issueTrackerUrl.
     */
    public String jiraUrl;
    public String jiraUsername;
    public String jiraPassword;

    /**
     * JIRA project key, which will be prepended to the JIRA issue numbers.
     */
    public String jiraProject;

    private static final String DEFAULT_SOURCE = "target/site/serenity";

    private final PathProcessor pathProcessor = new PathProcessor();

    public Path getSourceDirectoryFile() {
        return Paths.get(pathProcessor.normalize(getSourceDirectory()));
    }

    private String getSourceDirectory() {
        return Optional.ofNullable(sourceDirectory).orElse(DEFAULT_SOURCE);
    }

    public void setSourceDirectory(String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;

    }

    public void setIssueTrackerUrl(String issueTrackerUrl) {
        this.issueTrackerUrl = issueTrackerUrl;
    }

    public void setJiraUrl(String jiraUrl) {
        this.jiraUrl = jiraUrl;
    }

    public void setJiraUsername(String jiraUsername) {
        this.jiraUsername = jiraUsername;
    }

    public void setJiraPassword(String jiraPassword) {
        this.jiraPassword = jiraPassword;
    }

    public void setJiraProject(String jiraProject) {
        this.jiraProject = jiraProject;
    }

    private String normalizedPath(String directoryPath) {
        return pathProcessor.normalize(directoryPath);
    }

    public Path getOutputDirectoryFile() {
        return Paths.get(normalizedPath(getOutputDirectory().orElse(getSourceDirectory())));
    }

    private java.util.Optional<String> getOutputDirectory() {
        return java.util.Optional.ofNullable(outputDirectory);
    }

    public void execute() {
        log("Generating Serenity reports");


        try {
            prepareDirectories();

            HtmlAggregateStoryReporter reporter = new HtmlAggregateStoryReporter(getProject().getName());
            reporter.setSourceDirectory(getSourceDirectoryFile().toFile());
            reporter.setOutputDirectory(getOutputDirectoryFile().toFile());
            reporter.setIssueTrackerUrl(issueTrackerUrl);
            reporter.setJiraUrl(jiraUrl);
            reporter.setJiraProject(jiraProject);
            reporter.setJiraUsername(jiraUsername);
            reporter.setJiraPassword(jiraPassword);
            reporter.generateReportsForTestResultsFrom(sourceOfTestResult().toFile());
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }

    private void prepareDirectories() throws IOException {
        if (Files.notExists(getOutputDirectoryFile())) {
            Files.createDirectories(getOutputDirectoryFile());
        }

    }

    private Path sourceOfTestResult() {
        if ((getSourceDirectoryFile() != null) && (Files.exists(getSourceDirectoryFile()))) {
            return getSourceDirectoryFile();
        } else {
            return getOutputDirectoryFile();
        }
    }
}
