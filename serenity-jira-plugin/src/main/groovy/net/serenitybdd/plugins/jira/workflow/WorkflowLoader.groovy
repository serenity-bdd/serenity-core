package net.serenitybdd.plugins.jira.workflow

interface WorkflowLoader {
    Workflow load();
    String getDefaultWorkflow();
}
