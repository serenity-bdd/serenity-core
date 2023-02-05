package net.serenitybdd.plugins.jira.integration;

import net.serenitybdd.plugins.jira.client.JerseyJiraClient;
import net.serenitybdd.plugins.jira.domain.IssueSummary;

import java.util.ArrayList;
import java.util.List;

class IssueHarness {

    public final static String PROJECT = "DEMO";
    private final static String USER_NAME = "serenity.jira@gmail.com";

    private JerseyJiraClient jiraClient;
    private List<IssueSummary> testIssues = new ArrayList<IssueSummary>();

    public IssueHarness(String url, String username, String password, String project) {
        jiraClient = new JerseyJiraClient(url,username,password,project) ;
    }

    public String createTestIssue() throws Exception {
        IssueSummary issue =  new IssueSummary();
        issue.setProject("DEMO");
        issue.setDescription("A new test issue");
        issue.setReporter(USER_NAME);
        issue.setType("1");
        issue.setSummary("A test issue");
        issue.setKey("DEMO-1");
        IssueSummary createdIssue =  jiraClient.createIssue(issue);
        testIssues.add(createdIssue);
        return createdIssue.getKey();
    }

    public void deleteTestIssues() throws Exception {
        for(IssueSummary issue : testIssues) {
            jiraClient.deleteIssue(issue);
        }
    }
}
