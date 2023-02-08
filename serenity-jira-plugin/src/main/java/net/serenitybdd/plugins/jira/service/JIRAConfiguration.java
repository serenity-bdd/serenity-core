package net.serenitybdd.plugins.jira.service;

/**
 * JIRA configuration details for the target JIRA instance.
 */
public interface JIRAConfiguration {

    String getJiraUser();

    String getJiraPassword();

    String getJiraUrl();

    String getJiraWebserviceUrl();

    boolean isWikiRenderedActive();

    String getProject();
}
