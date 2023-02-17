package net.serenitybdd.plugins.jira.integration;

import net.serenitybdd.plugins.jira.JiraConnectionSettings;
import net.serenitybdd.plugins.jira.client.JerseyJiraClient;
import net.serenitybdd.plugins.jira.domain.IssueComment;
import net.serenitybdd.plugins.jira.domain.IssueSummary;
import net.serenitybdd.plugins.jira.domain.Project;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class WhenFindingAnIssueOnTheJIRAServer {

    private String issueKey;

    private IssueHarness testIssueHarness;

    private JerseyJiraClient jiraClient = new JerseyJiraClient(JiraConnectionSettings.getJIRAWebserviceURL(),
                                                                JiraConnectionSettings.getJIRAUserName(),
                                                                JiraConnectionSettings.getJIRAUserApiToken(),IssueHarness.PROJECT) ;

    @Before
    public void createTestIssue() throws Exception {
        testIssueHarness = new IssueHarness(JiraConnectionSettings.getJIRAWebserviceURL(),JiraConnectionSettings.getJIRAUserName(), JiraConnectionSettings.getJIRAUserApiToken(),IssueHarness.PROJECT);
        issueKey = testIssueHarness.createTestIssue();
    }

    @After
    public void deleteTestIssue() throws Exception {
        testIssueHarness.deleteTestIssues();
    }


    @Test
    public void should_be_able_to_find_a_project_by_key() throws Exception {
        Project project = jiraClient.getProjectByKey(IssueHarness.PROJECT);
        assertThat(project, is(not(nullValue())));
    }
    @Test
    public void should_be_able_to_find_an_issue_by_id() throws Exception {
        IssueSummary issue = jiraClient.getIssue(issueKey);
        assertThat(issue, is(not(nullValue())));
    }


    @Test
    public void should_be_able_to_find_an_issue_by_jql() throws Exception {

        List<IssueSummary> issues = jiraClient.findByJQL("key=" + issueKey);
        assertThat(issues.size(), is(1));
    }

    @Test
    public void should_be_able_to_list_the_comments_in_an_issue() throws Exception {

        IssueComment newComment = new IssueComment();
        newComment.setBody("A new comment");
        newComment.setAuthor("bruce");
        jiraClient.addComment(issueKey, newComment);

        List<IssueComment> comments = jiraClient.getComments(issueKey);
        assertThat(comments.size(), is(1));
    }

    @Test
    public void should_be_able_to_add_a_new_comment_to_an_issue() throws Exception {
        IssueComment newComment = new IssueComment();
        newComment.setBody("A new comment");
        newComment.setAuthor("bruce");
        jiraClient.addComment(issueKey, newComment);

        List<IssueComment> comments = jiraClient.getComments(issueKey);
        assertThat(comments.size(), is(1));
    }

    @Test
    public void should_be_able_to_read_the_existing_comments_on_an_issue() throws Exception {

        IssueComment newComment = new IssueComment();
        newComment.setBody("A new comment");
        newComment.setAuthor("bruce");
        jiraClient.addComment(issueKey, newComment);

        IssueComment newComment2 = new IssueComment();
        newComment2.setBody("Another new comment");
        newComment2.setAuthor("bruce");
        jiraClient.addComment( issueKey, newComment2);

        List<IssueComment> comments = jiraClient.getComments(issueKey);
        assertThat(comments.size(), is(2));
    }

    @Test
    public void should_be_able_to_read_the_status_of_an_issue() throws Exception {

        Optional<IssueSummary> issueSummary = jiraClient.loadByKey(issueKey);
        assertThat(issueSummary.get().getStatus(), is("Open"));
    }

}
