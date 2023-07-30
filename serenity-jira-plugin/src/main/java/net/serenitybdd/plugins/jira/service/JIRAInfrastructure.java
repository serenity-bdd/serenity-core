package net.serenitybdd.plugins.jira.service;

import net.serenitybdd.core.di.ModelInfrastructure;
import net.serenitybdd.plugins.jira.model.IssueTracker;
import net.serenitybdd.plugins.jira.workflow.ClasspathWorkflowLoader;
import net.serenitybdd.plugins.jira.workflow.WorkflowLoader;
import net.thucydides.core.util.EnvironmentVariables;

public class JIRAInfrastructure {
    static private EnvironmentVariables environmentVariables = ModelInfrastructure.getEnvironmentVariables();
    static private JIRAConfiguration configuration = new SystemPropertiesJIRAConfiguration();
    static private IssueTracker issueTracker = new JiraIssueTracker(configuration);
    static private WorkflowLoader workflowLoader = new ClasspathWorkflowLoader("jira-workflow.groovy", environmentVariables);

    public static JIRAConfiguration getConfiguration() {
        return configuration;
    }

    public static IssueTracker getIssueTracker() {
        return issueTracker;
    }

    public static WorkflowLoader getWorkflowLoader() {
        return workflowLoader;
    }
//            this(Injectors.getInjector().getInstance(IssueTracker .class),
//                Injectors.getInjector().getProvider(EnvironmentVariables .class).get() ,
//                Injectors.getInjector().getInstance(WorkflowLoader .class));
}
