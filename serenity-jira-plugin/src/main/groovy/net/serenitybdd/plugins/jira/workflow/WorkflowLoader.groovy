package net.serenitybdd.plugins.jira.workflow

public interface WorkflowLoader {
    Workflow load();
    String getDefaultWorkflow();
}
