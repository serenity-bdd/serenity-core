package net.serenitybdd.cli.reporters;

import net.serenitybdd.model.SerenitySystemProperties;
import net.thucydides.model.reports.JiraUpdaterService;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ServiceLoader;

import static net.thucydides.model.ThucydidesSystemProperty.SERENITY_JIRA_WORKFLOW;
import static net.thucydides.model.ThucydidesSystemProperty.SERENITY_JIRA_WORKFLOW_ACTIVE;

public class CLIIssueTrackerUpdater implements CLIReportGenerator  {
    public CLIIssueTrackerUpdater(String jiraWorkflow, String jiraWorkflowActive) {
        if(jiraWorkflow != null) {
            SerenitySystemProperties.getProperties().setValue(SERENITY_JIRA_WORKFLOW, jiraWorkflow);
        }
        if(jiraWorkflowActive != null) {
            SerenitySystemProperties.getProperties().setValue(SERENITY_JIRA_WORKFLOW_ACTIVE, jiraWorkflowActive);
        }
    }

    @Override
    public void generateReportsFrom(Path sourceDirectory) throws IOException {
        Iterable<JiraUpdaterService> jiraUpdaterServices = ServiceLoader.load(JiraUpdaterService.class);
        for(JiraUpdaterService jiraUpdaterService: jiraUpdaterServices) {
            jiraUpdaterService.updateJiraForTestResultsFrom(sourceDirectory.toAbsolutePath().toString());
        }
    }
}
