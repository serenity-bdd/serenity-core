package net.serenitybdd.plugins.jira;


import net.serenitybdd.plugins.jira.domain.IssueComment;
import net.serenitybdd.plugins.jira.model.IssueTracker;
import net.serenitybdd.plugins.jira.service.NoSuchIssueException;
import net.serenitybdd.plugins.jira.workflow.ClasspathWorkflowLoader;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestOutcomeSummary;
import net.thucydides.core.steps.ExecutedStepDescription;
import net.thucydides.core.steps.StepFailure;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.FileSystemUtils;
import net.thucydides.core.environment.MockEnvironmentVariables;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class WhenUpdatingCommentsInJiraUsingFiles {

    @Mock
    IssueTracker issueTracker;

    private EnvironmentVariables environmentVariables;

    private ClasspathWorkflowLoader workflowLoader;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);

        environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("jira.url", "http://my.jira.server");
        environmentVariables.setProperty("thucydides.public.url", "http://my.server/myproject/thucydides");
        environmentVariables.setProperty(ClasspathWorkflowLoader.ACTIVATE_WORKFLOW_PROPERTY, "true");
        environmentVariables.setProperty("build.id", "2012-01-17_15-39-03");

        workflowLoader = new ClasspathWorkflowLoader(ClasspathWorkflowLoader.BUNDLED_WORKFLOW, environmentVariables);
    }

    @After
    public void resetPluginSpecificProperties() {
        System.clearProperty(JiraPluginConfigurationOptions.SKIP_JIRA_UPDATES);
    }

    private MockEnvironmentVariables prepareMockEnvironment() {
        MockEnvironmentVariables mockEnvironmentVariables = new MockEnvironmentVariables();
        mockEnvironmentVariables.setProperty("jira.project", "MYPROJECT");
        mockEnvironmentVariables.setProperty("jira.url", "http://my.jira.server");
        mockEnvironmentVariables.setProperty("thucydides.public.url", "http://my.server/myproject/thucydides");
        return mockEnvironmentVariables;
    }


    @Test
    public void when_a_test_with_a_referenced_issue_finishes_the_plugin_should_add_a_new_comment_for_this_issue() throws IOException {
        JiraFileServiceUpdater jiraUpdater = new JiraFileServiceUpdater(issueTracker, environmentVariables, workflowLoader);
        Path directory = FileSystemUtils.getResourceAsFile("/fileservice/sampletestsuite").toPath();
        jiraUpdater.updateJiraForTestResultsFrom(directory.toAbsolutePath().toString(), "issue_123_should.*");
        verify(issueTracker).addComment(eq("MYPROJECT-123"), anyString());
    }

    @Test
    public void should_not_add_the_project_prefix_to_the_issue_number_if_already_present() throws IOException {
        MockEnvironmentVariables mockEnvironmentVariables = prepareMockEnvironment();
        JiraFileServiceUpdater jiraUpdater = new JiraFileServiceUpdater(issueTracker, mockEnvironmentVariables, workflowLoader);
        Path directory = FileSystemUtils.getResourceAsFile("/fileservice/sampletestsuite").toPath();
        jiraUpdater.updateJiraForTestResultsFrom(directory.toAbsolutePath().toString(), "issue_123_should.*");
        verify(issueTracker).addComment(eq("MYPROJECT-123"), anyString());
    }

    @Test
    public void should_add_the_project_prefix_to_the_issue_number_if_not_already_present() throws IOException, InterruptedException {
        MockEnvironmentVariables mockEnvironmentVariables = prepareMockEnvironment();
        JiraFileServiceUpdater jiraUpdater = new JiraFileServiceUpdater(issueTracker, mockEnvironmentVariables, workflowLoader);
        Path directory = FileSystemUtils.getResourceAsFile("/fileservice/sampletestsuitewithoutprefixes").toPath();
        jiraUpdater.updateJiraForTestResultsFrom(directory.toAbsolutePath().toString());
        verify(issueTracker, atLeast(2)).addComment(contains("MYPROJECT"), anyString());
    }

    @Test
    public void when_a_test_with_a_referenced_annotated_issue_finishes_the_plugin_should_add_a_new_comment_for_this_issue() throws IOException {
        JiraFileServiceUpdater jiraUpdater = new JiraFileServiceUpdater(issueTracker, environmentVariables, workflowLoader);
        Path directory = FileSystemUtils.getResourceAsFile("/fileservice/sampletestsuitewithissueannotation").toPath();
        jiraUpdater.updateJiraForTestResultsFrom(directory.toAbsolutePath().toString(), "issue_123_should.*");
        verify(issueTracker).addComment(eq("MYPROJECT-123"), anyString());
    }

    @Test
    public void when_a_test_with_several_referenced_issues_finishes_the_plugin_should_add_a_new_comment_for_each_issue() throws IOException {
        JiraFileServiceUpdater jiraUpdater = new JiraFileServiceUpdater(issueTracker, environmentVariables, workflowLoader);
        Path directory = FileSystemUtils.getResourceAsFile("/fileservice/sampletestsuite").toPath();
        jiraUpdater.updateJiraForTestResultsFrom(directory.toAbsolutePath().toString());
        verify(issueTracker).addComment(eq("MYPROJECT-123"), anyString());
        verify(issueTracker).addComment(eq("MYPROJECT-456"), anyString());
    }

    @Mock
    ExecutedStepDescription stepDescription;

    @Mock
    StepFailure failure;

    @Mock
    TestOutcome testOutcome;

    @Test
    @Ignore("Needs reviewing")
    public void should_add_one_comment_even_when_several_steps_are_called() throws IOException {
        JiraFileServiceUpdater jiraUpdater = new JiraFileServiceUpdater(issueTracker, environmentVariables, workflowLoader);
        Path directory = FileSystemUtils.getResourceAsFile("/fileservice/sampletestsuitetestfailure").toPath();
        jiraUpdater.updateJiraForTestResultsFrom(directory.toAbsolutePath().toString(), "issue_123_and_456_should.*|anotherTest");
        verify(issueTracker).addComment(eq("MYPROJECT-123"), anyString());
        verify(issueTracker).addComment(eq("MYPROJECT-456"), anyString());
    }

    @Test
    public void the_comment_should_contain_a_link_to_the_corresponding_story_report() throws IOException {

        JiraFileServiceUpdater jiraUpdater = new JiraFileServiceUpdater(issueTracker, environmentVariables, workflowLoader);
        Path directory = FileSystemUtils.getResourceAsFile("/fileservice/sampletestsuitetestfailure").toPath();
        List<TestOutcomeSummary> testOutcomeSummaries = jiraUpdater.updateJiraForTestResultsFrom(directory.toAbsolutePath().toString(), "issue_123_should.*");

        TestOutcomeSummary failingTestOutcome = testOutcomeSummaryWithName("issue_123_should_be_fixed_now", testOutcomeSummaries);
        verify(issueTracker).addComment(eq("MYPROJECT-123"),
                contains("http://my.server/myproject/thucydides/" + failingTestOutcome.getReportName()));
    }

    private TestOutcomeSummary testOutcomeSummaryWithName(String name, List<TestOutcomeSummary> testOutcomeSummaries) {
        for(TestOutcomeSummary testOutcomeSummary : testOutcomeSummaries) {
            if (testOutcomeSummary.getName().equals(name)) {
                return testOutcomeSummary;
            }
        }
        return null;
    }

    @Test
    public void should_update_existing_thucydides_report_comments_if_present() throws IOException {

        List<IssueComment> existingComments = Arrays.asList(new IssueComment("", 1L, "a comment", "bruce"),
                new IssueComment("", 2L, "Serenity BDD Automated Acceptance Tests", "bruce"));
        when(issueTracker.getCommentsFor("MYPROJECT-123")).thenReturn(existingComments);

        JiraFileServiceUpdater jiraUpdater = new JiraFileServiceUpdater(issueTracker, environmentVariables, workflowLoader);
        Path directory = FileSystemUtils.getResourceAsFile("/fileservice/sampletestsuitetestfailure").toPath();
        jiraUpdater.updateJiraForTestResultsFrom(directory.toAbsolutePath().toString(), "issue_123_should.*");

        verify(issueTracker).updateComment(eq("MYPROJECT-123"), any(IssueComment.class));
    }


    @Test
    public void should_not_update_status_if_issue_does_not_exist() throws IOException {
        when(issueTracker.getStatusFor("MYPROJECT-123"))
                .thenThrow(new NoSuchIssueException("It ain't there no more."));

        JiraFileServiceUpdater jiraUpdater = new JiraFileServiceUpdater(issueTracker, environmentVariables, workflowLoader);
        Path directory = FileSystemUtils.getResourceAsFile("/fileservice/sampletestsuitetestfailure").toPath();
        jiraUpdater.updateJiraForTestResultsFrom(directory.toAbsolutePath().toString(), "issue_123_should.*");

        verify(issueTracker, never()).doTransition(anyString(), anyString());
    }

    @Test
    public void should_not_update_status_if_jira_url_is_undefined() throws IOException {
        MockEnvironmentVariables environmentVariables = prepareMockEnvironment();
        environmentVariables.setProperty("jira.url", "");

        JiraFileServiceUpdater jiraUpdater = new JiraFileServiceUpdater(issueTracker, environmentVariables, workflowLoader);
        Path directory = FileSystemUtils.getResourceAsFile("/fileservice/sampletestsuitetestfailure").toPath();
        jiraUpdater.updateJiraForTestResultsFrom(directory.toAbsolutePath().toString(), "issue_123_should.*");

        verify(issueTracker, never()).doTransition(anyString(), anyString());
    }

    @Test
    public void should_skip_JIRA_updates_if_requested() throws IOException {
        MockEnvironmentVariables environmentVariables = prepareMockEnvironment();
        environmentVariables.setProperty(JiraPluginConfigurationOptions.SKIP_JIRA_UPDATES, "true");

        JiraFileServiceUpdater jiraUpdater = new JiraFileServiceUpdater(issueTracker, environmentVariables, workflowLoader);
        Path directory = FileSystemUtils.getResourceAsFile("/fileservice/sampletestsuitetestfailure").toPath();
        jiraUpdater.updateJiraForTestResultsFrom(directory.toAbsolutePath().toString(), "issue_123_should.*");

        verify(issueTracker, never()).addComment(anyString(), anyString());
    }

    @Test
    public void should_skip_JIRA_updates_if_no_public_url_is_specified() throws IOException {

        MockEnvironmentVariables environmentVariables = prepareMockEnvironment();
        environmentVariables.setProperty("thucydides.public.url", "");
        environmentVariables.setProperty("serenity.public.url", "");

        JiraFileServiceUpdater jiraUpdater = new JiraFileServiceUpdater(issueTracker, environmentVariables, workflowLoader);
        Path directory = FileSystemUtils.getResourceAsFile("/fileservice/sampletestsuitetestfailure").toPath();
        jiraUpdater.updateJiraForTestResultsFrom(directory.toAbsolutePath().toString(), "issue_123_should.*");

        verify(issueTracker, never()).addComment(anyString(), anyString());
    }

    @Test
    public void default_listeners_should_use_default_issue_tracker() {
        JiraFileServiceUpdater listener = new JiraFileServiceUpdater();
        assertThat(listener.getJiraUpdater().getIssueTracker(), is(notNullValue()));
    }

    @Test
    public void a_passing_test_should_resolve_an_open_issue() throws IOException {

        when(issueTracker.getStatusFor("MYPROJECT-123")).thenReturn("Open");

        JiraFileServiceUpdater jiraUpdater = new JiraFileServiceUpdater(issueTracker, environmentVariables, workflowLoader);
        Path directory = FileSystemUtils.getResourceAsFile("/fileservice/sampletestsuite").toPath();
        jiraUpdater.updateJiraForTestResultsFrom(directory.toAbsolutePath().toString(), "issue_123_should.*");

        verify(issueTracker).doTransition("MYPROJECT-123", "Resolve Issue");
    }

    @Test
    public void a_failing_test_should_open_a_closed_issue() throws IOException {

        when(issueTracker.getStatusFor("MYPROJECT-123")).thenReturn("Closed");

        JiraFileServiceUpdater jiraUpdater = new JiraFileServiceUpdater(issueTracker, environmentVariables, workflowLoader);
        Path directory = FileSystemUtils.getResourceAsFile("/fileservice/sampletestsuitetestfailure").toPath();
        jiraUpdater.updateJiraForTestResultsFrom(directory.toAbsolutePath().toString(), "issue_123_should.*");

        verify(issueTracker).doTransition("MYPROJECT-123", "Reopen Issue");
    }

}
