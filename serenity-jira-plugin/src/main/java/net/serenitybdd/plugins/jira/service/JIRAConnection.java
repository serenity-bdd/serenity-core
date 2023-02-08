package net.serenitybdd.plugins.jira.service;

import net.serenitybdd.plugins.jira.client.JerseyJiraClient;
import net.serenitybdd.plugins.jira.guice.Injectors;

public class JIRAConnection {

    private final JIRAConfiguration configuration;
    private final JerseyJiraClient jiraClient;

    public JIRAConnection() {
        this(Injectors.getInjector().getInstance(JIRAConfiguration.class));
    }

    public JIRAConnection(JIRAConfiguration configuration) {
        this.configuration = configuration;
        this.jiraClient = new JerseyJiraClient(configuration.getJiraWebserviceUrl(), configuration.getJiraUser(), configuration.getJiraPassword(), configuration.getProject());
    }

    public JerseyJiraClient getRestJiraClient() {
        return jiraClient;
    }

    protected JIRAConfiguration getConfiguration() {
        return configuration;
    }

    public String getJiraUser() {
        return getConfiguration().getJiraUser();
    }

    public String getJiraPassword() {
        return getConfiguration().getJiraPassword();
    }

    public String getJiraWebserviceUrl() {
        return getConfiguration().getJiraWebserviceUrl();
    }

    public String getProject() {
        return getConfiguration().getProject();
    }
    public void logout() { }
}
