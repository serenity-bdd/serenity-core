package net.serenitybdd.cli.reporters;

import net.thucydides.core.reports.html.HtmlAggregateStoryReporter;
import net.thucydides.model.requirements.Requirements;

import java.io.IOException;
import java.nio.file.Path;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class CLIAggregateReportGenerator implements CLIReportGenerator {

    private final Path sourceDirectory;
    private final Path destinationDirectory;
    private final String project;
    private final String issueTrackerUrl;
    private final String jiraUrl;
    private final String jiraProject;
    private final String jiraUsername;
    private final String jiraPassword;
    private final String requirementsDirectory;
    private final String tags;

    public CLIAggregateReportGenerator(Path sourceDirectory,
                                        Path destinationDirectory,
                                        String project,
                                        String issueTrackerUrl,
                                        String jiraUrl,
                                        String jiraProject,
                                        String jiraUsername,
                                        String jiraPassword,
                                        String requirementsDirectory,
                                        String tags) {
        this.sourceDirectory = sourceDirectory;
        this.destinationDirectory = destinationDirectory;
        this.issueTrackerUrl = issueTrackerUrl;
        this.jiraUrl = jiraUrl;
        this.jiraProject = jiraProject;
        this.jiraUsername = jiraUsername;
        this.jiraPassword = jiraPassword;
        this.project = project;
        this.requirementsDirectory = requirementsDirectory;
        this.tags = tags;
    }

    @Override
    public void generateReportsFrom(Path sourceDirectory) throws IOException {

        Requirements requirements = RequirementsStrategy.forDirectory(requirementsDirectory);
        HtmlAggregateStoryReporter reporter = new HtmlAggregateStoryReporter(project, requirements);
        reporter.setSourceDirectory(sourceDirectory.toFile());
        reporter.setOutputDirectory(destinationDirectory.toFile());
        reporter.setIssueTrackerUrl(issueTrackerUrl);
        reporter.setJiraUrl(jiraUrl);
        reporter.setJiraProject(jiraProject);
        reporter.setJiraUsername(jiraUsername);
        reporter.setJiraPassword(jiraPassword);

        reporter.setGenerateTestOutcomeReports();

        if (!isBlank(tags)) {
            reporter.setTags(tags);
        }

        reporter.generateReportsForTestResultsFrom(sourceDirectory.toFile());
    }
}
