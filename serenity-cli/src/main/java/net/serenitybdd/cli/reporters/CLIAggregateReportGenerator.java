package net.serenitybdd.cli.reporters;

import net.thucydides.core.reports.html.HtmlAggregateStoryReporter;
import net.thucydides.model.requirements.Requirements;
import net.thucydides.model.requirements.SerenityJsRequirements;

import java.io.IOException;
import java.nio.file.Path;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class CLIAggregateReportGenerator implements CLIReportGenerator {

    private final Path requirementsDirectory;
    private final Path destinationDirectory;
    private final String project;
    private final String issueTrackerUrl;
    private final String jiraUrl;
    private final String jiraProject;
    private final String jiraUsername;
    private final String jiraPassword;
    private final String tags;

    public CLIAggregateReportGenerator(
            Path testScenariosDirectory,
            Path destinationDirectory,
            String project,
            String issueTrackerUrl,
            String jiraUrl,
            String jiraProject,
            String jiraUsername,
            String jiraPassword,
            String tags
    ) {
        this.requirementsDirectory = testScenariosDirectory;
        this.destinationDirectory = destinationDirectory;
        this.issueTrackerUrl = issueTrackerUrl;
        this.jiraUrl = jiraUrl;
        this.jiraProject = jiraProject;
        this.jiraUsername = jiraUsername;
        this.jiraPassword = jiraPassword;
        this.project = project;
        this.tags = tags;
    }

    @Override
    public void generateReportsFrom(Path jsonOutcomesDirectory) throws IOException {

        Requirements requirements = new SerenityJsRequirements(requirementsDirectory, jsonOutcomesDirectory);

        HtmlAggregateStoryReporter reporter = new HtmlAggregateStoryReporter(project, requirements);
        reporter.setSourceDirectory(jsonOutcomesDirectory.toFile());
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

        reporter.generateReportsForTestResultsFrom(jsonOutcomesDirectory.toFile());
    }
}
