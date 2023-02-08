package net.serenitybdd.plugins.jira.integration;

import net.serenitybdd.plugins.jira.client.JIRAConfigurationError;
import net.serenitybdd.plugins.jira.domain.IssueComment;
import net.serenitybdd.plugins.jira.domain.IssueSummary;
import net.serenitybdd.plugins.jira.domain.IssueTransition;
import net.serenitybdd.plugins.jira.model.IssueTracker;
import net.serenitybdd.plugins.jira.model.IssueTrackerUpdateException;
import net.serenitybdd.plugins.jira.service.JIRAConfiguration;
import net.serenitybdd.plugins.jira.service.JiraIssueTracker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WhenUpdateingIssuesUsingTheJiraTracker {

    IssueTracker tracker;

    private static final String JIRA_WEBSERVICE_URL = "https://thucydides.atlassian.net/";
    private final static String USER_NAME = "serenity.jira@gmail.com";
    private final static String USER_PWD = "sZePVVAsoFW7E7bzZuZy43BF";

    private String issueKey;

    private final String CLOSED_ISSUE = "DEMO-1";

    private IssueHarness testIssueHarness;

    @Mock
    JIRAConfiguration configuration;

    @Mock
    Logger logger;

    @Before
    public void prepareIssueTracker() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(configuration.getJiraUser()).thenReturn(USER_NAME);
        when(configuration.getJiraPassword()).thenReturn(USER_PWD);
        when(configuration.getJiraWebserviceUrl()).thenReturn(JIRA_WEBSERVICE_URL);
        when(configuration.getProject()).thenReturn("DEMO");


        testIssueHarness = new IssueHarness(JIRA_WEBSERVICE_URL,USER_NAME,USER_PWD,"DEMO");
        issueKey = testIssueHarness.createTestIssue();
        tracker = new JiraIssueTracker(logger, configuration);
    }


    @After
    public void deleteTestIssue() throws Exception {
        testIssueHarness.deleteTestIssues();
    }

    @Test
    public void should_be_able_to_add_a_comment_to_an_issue() throws Exception {
        List<IssueComment> comments = tracker.getCommentsFor(issueKey);
        assertThat(comments.size(), is(0));

        tracker.addComment(issueKey, "Integration test comment");

        comments = tracker.getCommentsFor(issueKey);
        assertThat(comments.size(), is(1));
    }


    @Test
    public void should_be_able_to_add_a_comment_to_a_closed_issue() throws Exception {
        List<IssueComment> comments = tracker.getCommentsFor(CLOSED_ISSUE);

        String comment = "Comment on closed test: " + new Date();

        tracker.addComment(CLOSED_ISSUE, comment);

        comments = tracker.getCommentsFor(CLOSED_ISSUE);
        assertThat(comments.size(), greaterThan(0));
        assertThat(comments.get(comments.size() - 1).getBody(), is(comment));
    }


    @Test
    public void should_be_able_to_update_a_comment_from_an_issue() throws Exception {
        tracker.addComment(issueKey, "Integration test comment 1");
        tracker.addComment(issueKey, "Integration test comment 2");
        tracker.addComment(issueKey, "Integration test comment 3");

        List<IssueComment> comments = tracker.getCommentsFor(issueKey);

        IssueComment oldComment = comments.get(0);
        IssueComment updatedComment = new IssueComment(oldComment.getSelf(), oldComment.getId(), "Integration test comment 4", oldComment.getAuthor());

        tracker.updateComment(issueKey,updatedComment);

        comments = tracker.getCommentsFor(issueKey);
        assertThat(comments.get(0).getBody(), is("Integration test comment 4"));
    }

    @Test
    public void should_not_be_able_to_update_a_comment_from_an_issue_that_does_not_exist() throws Exception {

        try {
            tracker.addComment("#ISSUE-DOES-NOT-EXIST", "Integration test comment 1");
        } catch (JIRAConfigurationError err) {
            assertThat(err.getMessage(), is("Service not found (404) - try checking the JIRA URL?")) ;
        }
        //verify(logger).error("No JIRA issue found with key {}","#ISSUE-DOES-NOT-EXIST");
    }

    @Test
    public void should_be_able_to_read_the_status_of_an_issue_in_human_readable_form() throws Exception {

        String status = tracker.getStatusFor(issueKey);
        assertThat(status, is(IssueSummary.STATE_OPEN));
    }

    @Test
    public void should_not_be_able_to_update_the_status_of_a_closed_issue() throws Exception {
        tracker.doTransition(CLOSED_ISSUE, IssueTransition.RESOLVE_ISSUE);
        String newStatus = tracker.getStatusFor(CLOSED_ISSUE);
        assertThat(newStatus, is(IssueSummary.STATE_CLOSED));
    }

    @Test
    public void should_be_able_to_update_the_status_of_an_issue() throws Exception {
        String status = tracker.getStatusFor(issueKey);
        assertThat(status, is(IssueSummary.STATE_OPEN));

        tracker.doTransition(issueKey, IssueTransition.RESOLVE_ISSUE);

        String newStatus = tracker.getStatusFor(issueKey);
        assertThat(newStatus, is(IssueSummary.STATE_RESOLVED));
    }

    @Test
    public void should_not_be_able_to_update_the_status_of_an_issue_if_transition_is_not_allowed() throws Exception {
        String status = tracker.getStatusFor(issueKey);
        assertThat(status, is(IssueSummary.STATE_OPEN));

        tracker.doTransition(issueKey, IssueTransition.REOPEN_ISSUE);

        String newStatus = tracker.getStatusFor(issueKey);
        assertThat(newStatus, is(IssueSummary.STATE_OPEN));
    }

    @Test(expected = IssueTrackerUpdateException.class)
    public void should_not_be_able_to_update_the_status_for_an_issue_that_does_not_exist() throws Exception {
        tracker.doTransition("#ISSUE-DOES-NOT-EXIST", IssueTransition.RESOLVE_ISSUE);
        verify(logger).error("No JIRA issue found with key {}","#ISSUE-DOES-NOT-EXIST");
    }

}
