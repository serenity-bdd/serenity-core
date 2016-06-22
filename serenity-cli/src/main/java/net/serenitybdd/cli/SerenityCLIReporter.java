package net.serenitybdd.cli;

import net.thucydides.core.reports.html.HtmlAggregateStoryReporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SerenityCLIReporter {

    private final Path sourceDirectory;
    private final Path destinationDirectory;
    private final String project;
    private final String issueTrackerUrl;
    private final String jiraUrl;
    private final String jiraProject;
    private final String jiraUsername;
    private final String jiraPassword;

    public SerenityCLIReporter(Path sourceDirectory,
                               Path destinationDirectory,
                               String project,
                               String issueTrackerUrl,
                               String jiraUrl,
                               String jiraProject,
                               String jiraUsername,
                               String jiraPassword) {
        this.sourceDirectory = sourceDirectory;
        this.destinationDirectory = destinationDirectory;
        this.issueTrackerUrl = issueTrackerUrl;
        this.jiraUrl = jiraUrl;
        this.jiraProject = jiraProject;
        this.jiraUsername = jiraUsername;
        this.jiraPassword = jiraPassword;
        this.project = project;
    }

    public void execute() {
        try {

            printStartingBanner();

            prepareDirectories();

            HtmlAggregateStoryReporter reporter = new HtmlAggregateStoryReporter(project);
            reporter.setSourceDirectory(sourceDirectory.toFile());
            reporter.setOutputDirectory(destinationDirectory.toFile());
            reporter.setIssueTrackerUrl(issueTrackerUrl);
            reporter.setJiraUrl(jiraUrl);
            reporter.setJiraProject(jiraProject);
            reporter.setJiraUsername(jiraUsername);
            reporter.setJiraPassword(jiraPassword);
            reporter.generateReportsForTestResultsFrom(sourceDirectory.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Report generation done");
    }

    private void printStartingBanner() {
        System.out.println("-----------------");
        System.out.println("SERENITY CLI TOOL");
        System.out.println("-----------------");
        System.out.println("Loading test outcomes from " + sourceDirectory);
        System.out.println("Writing aggregated report to " + destinationDirectory);
        System.out.println();
    }

    private void prepareDirectories() throws IOException {
        if (Files.notExists(destinationDirectory)) {
            Files.createDirectories(destinationDirectory);
        }

    }

}
