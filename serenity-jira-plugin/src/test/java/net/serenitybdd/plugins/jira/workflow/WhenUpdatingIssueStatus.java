package net.serenitybdd.plugins.jira.workflow;

import net.serenitybdd.plugins.jira.JiraStepListener;
import net.serenitybdd.plugins.jira.model.IssueTracker;
import net.serenitybdd.annotations.Feature;
import net.serenitybdd.annotations.Issue;
import net.serenitybdd.annotations.Issues;
import net.serenitybdd.annotations.Story;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestResult;
import net.thucydides.model.domain.TestStep;
import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.model.environment.MockEnvironmentVariables;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class WhenUpdatingIssueStatus {

    @Feature
    public static final class SampleFeature {
        public class SampleStory {}
        public class SampleStory2 {}
    }

    @Story(SampleFeature.SampleStory.class)
    static class SampleTestCase {

        @Issue("#MYPROJECT-123")
        public void issue_123_should_be_fixed_now() {}

        @Issues({"#MYPROJECT-123","#MYPROJECT-456"})
        public void issue_123_and_456_should_be_fixed_now() {}

        public void anotherTest() {}
    }

    @Story(SampleFeature.SampleStory2.class)
    static class SampleTestCase2 {

        @Issue("#MYPROJECT-789")
        public void issue_789_should_be_fixed_now() {}

        @Issues({"#MYPROJECT-333","#MYPROJECT-444"})
        public void issue_333_and_444_should_be_fixed_now() {}

        public void anotherTest() {}
    }

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("jira.url", "http://my.jira.server");
        environmentVariables.setProperty("thucydides.public.url", "http://my.server/myproject/thucydides");

        workflowLoader = new ClasspathWorkflowLoader(ClasspathWorkflowLoader.BUNDLED_WORKFLOW, environmentVariables);
    }

    @Mock
    IssueTracker issueTracker;

    EnvironmentVariables environmentVariables;
    
    ClasspathWorkflowLoader workflowLoader;

    @Test
    public void a_successful_test_should_not_update_status_if_workflow_is_not_activated() {

        TestOutcome result = newTestOutcome("issue_123_should_be_fixed_now", TestResult.SUCCESS);

        when(issueTracker.getStatusFor("MYPROJECT-123")).thenReturn("Open");

        environmentVariables.setProperty(ClasspathWorkflowLoader.ACTIVATE_WORKFLOW_PROPERTY,"false");

        workflowLoader = new ClasspathWorkflowLoader(ClasspathWorkflowLoader.BUNDLED_WORKFLOW, environmentVariables);
        JiraStepListener listener = new JiraStepListener(issueTracker, environmentVariables, workflowLoader);

        listener.testSuiteStarted(SampleTestCase.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(result);
        listener.testSuiteFinished();
        verify(issueTracker, never()).doTransition(eq("MYPROJECT-123"),anyString());
    }

    @Test
    public void a_successful_test_should_not_update_status_if_workflow_update_status_is_not_specified() {

        TestOutcome result = newTestOutcome("issue_123_should_be_fixed_now", TestResult.SUCCESS);

        when(issueTracker.getStatusFor("MYPROJECT-123")).thenReturn("Open");

        environmentVariables.setProperty(ClasspathWorkflowLoader.ACTIVATE_WORKFLOW_PROPERTY,"");

        workflowLoader = new ClasspathWorkflowLoader(ClasspathWorkflowLoader.BUNDLED_WORKFLOW, environmentVariables);
        JiraStepListener listener = new JiraStepListener(issueTracker, environmentVariables, workflowLoader);

        listener.testSuiteStarted(SampleTestCase.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(result);
        listener.testSuiteFinished();

        verify(issueTracker, never()).doTransition(eq("MYPROJECT-123"),anyString());
    }

    @Test
    public void a_successful_test_should_resolve_an_open_issue() {


        environmentVariables.setProperty(ClasspathWorkflowLoader.ACTIVATE_WORKFLOW_PROPERTY,"true");

        TestOutcome result = newTestOutcome("issue_123_should_be_fixed_now", TestResult.SUCCESS);

        when(issueTracker.getStatusFor("MYPROJECT-123")).thenReturn("Open");

        JiraStepListener listener = new JiraStepListener(issueTracker, environmentVariables, workflowLoader);
        listener.testSuiteStarted(SampleTestCase.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(result);
        listener.testSuiteFinished();

        verify(issueTracker).doTransition("MYPROJECT-123", "Resolve Issue");
    }


    @Test
    public void a_successful_test_should_resolve_an_in_progress_issue() {

        environmentVariables.setProperty(ClasspathWorkflowLoader.ACTIVATE_WORKFLOW_PROPERTY,"true");

        TestOutcome result = newTestOutcome("issue_123_should_be_fixed_now", TestResult.SUCCESS);

        when(issueTracker.getStatusFor("MYPROJECT-123")).thenReturn("In Progress");

        JiraStepListener listener = new JiraStepListener(issueTracker, environmentVariables, workflowLoader);
        listener.testSuiteStarted(SampleTestCase.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(result);
        listener.testSuiteFinished();

        InOrder inOrder = inOrder(issueTracker);
        inOrder.verify(issueTracker).doTransition("MYPROJECT-123","Stop Progress");
        inOrder.verify(issueTracker).doTransition("MYPROJECT-123","Resolve Issue");
    }

    @Test
    public void a_successful_test_should_resolve_a_reopened_issue() {

        environmentVariables.setProperty(ClasspathWorkflowLoader.ACTIVATE_WORKFLOW_PROPERTY,"true");

        TestOutcome result = newTestOutcome("issue_123_should_be_fixed_now", TestResult.SUCCESS);

        when(issueTracker.getStatusFor("MYPROJECT-123")).thenReturn("Reopened");

        JiraStepListener listener = new JiraStepListener(issueTracker, environmentVariables, workflowLoader);
        listener.testSuiteStarted(SampleTestCase.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(result);
        listener.testSuiteFinished();

        verify(issueTracker).doTransition("MYPROJECT-123", "Resolve Issue");
    }

    @Test
    public void a_successful_test_should_not_affect_a_resolved_issue() {

        environmentVariables.setProperty(ClasspathWorkflowLoader.ACTIVATE_WORKFLOW_PROPERTY,"true");

        TestOutcome result = newTestOutcome("issue_123_should_be_fixed_now", TestResult.SUCCESS);

        when(issueTracker.getStatusFor("MYPROJECT-123")).thenReturn("Resolved");

        JiraStepListener listener = new JiraStepListener(issueTracker, environmentVariables, workflowLoader);
        listener.testSuiteStarted(SampleTestCase.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(result);
        listener.testSuiteFinished();

        verify(issueTracker, never()).doTransition(eq("MYPROJECT-123"), anyString());
    }

    @Test
    public void a_failing_test_should_open_a_resolved_issue() {

        environmentVariables.setProperty(ClasspathWorkflowLoader.ACTIVATE_WORKFLOW_PROPERTY,"true");

        TestOutcome result = newTestOutcome("issue_123_should_be_fixed_now", TestResult.FAILURE);

        when(issueTracker.getStatusFor("MYPROJECT-123")).thenReturn("Resolved");

        JiraStepListener listener = new JiraStepListener(issueTracker, environmentVariables, workflowLoader);
        listener.testSuiteStarted(SampleTestCase.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(result);
        listener.testSuiteFinished();

        verify(issueTracker).doTransition("MYPROJECT-123", "Reopen Issue");
    }

    @Test
    public void a_failing_test_should_open_a_closed_issue() {

        environmentVariables.setProperty(ClasspathWorkflowLoader.ACTIVATE_WORKFLOW_PROPERTY,"true");

        TestOutcome result = newTestOutcome("issue_123_should_be_fixed_now", TestResult.FAILURE);

        when(issueTracker.getStatusFor("MYPROJECT-123")).thenReturn("Closed");

        JiraStepListener listener = new JiraStepListener(issueTracker, environmentVariables, workflowLoader);
        listener.testSuiteStarted(SampleTestCase.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(result);
        listener.testSuiteFinished();

        verify(issueTracker).doTransition("MYPROJECT-123", "Reopen Issue");
    }

    @Test
    public void a_failing_test_should_leave_an_open_issue_open() {

        environmentVariables.setProperty(ClasspathWorkflowLoader.ACTIVATE_WORKFLOW_PROPERTY,"true");

        TestOutcome result = newTestOutcome("issue_123_should_be_fixed_now", TestResult.FAILURE);

        when(issueTracker.getStatusFor("MYPROJECT-123")).thenReturn("Open");

        JiraStepListener listener = new JiraStepListener(issueTracker, environmentVariables, workflowLoader);
        listener.testSuiteStarted(SampleTestCase.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(result);
        listener.testSuiteFinished();

        verify(issueTracker, never()).doTransition(eq("MYPROJECT-123"), anyString());
    }

    @Test
    public void a_failing_test_should_leave_a_reopened_issue_reopened() {

        environmentVariables.setProperty(ClasspathWorkflowLoader.ACTIVATE_WORKFLOW_PROPERTY,"true");
        TestOutcome result = newTestOutcome("issue_123_should_be_fixed_now", TestResult.FAILURE);

        when(issueTracker.getStatusFor("MYPROJECT-123")).thenReturn("Reopen");

        JiraStepListener listener = new JiraStepListener(issueTracker, environmentVariables, workflowLoader);
        listener.testSuiteStarted(SampleTestCase.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(result);
        listener.testSuiteFinished();

        verify(issueTracker, never()).doTransition(eq("MYPROJECT-123"), anyString());
    }

    @Test
    public void a_failing_test_should_leave_in_progress_issue_in_progress() {

        environmentVariables.setProperty(ClasspathWorkflowLoader.ACTIVATE_WORKFLOW_PROPERTY,"true");
        TestOutcome result = newTestOutcome("issue_123_should_be_fixed_now", TestResult.FAILURE);

        when(issueTracker.getStatusFor("MYPROJECT-123")).thenReturn("In Progress");

        JiraStepListener listener = new JiraStepListener(issueTracker, environmentVariables, workflowLoader);
        listener.testSuiteStarted(SampleTestCase.class);
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(result);
        listener.testSuiteFinished();

        verify(issueTracker, never()).doTransition(eq("MYPROJECT-123"), anyString());
    }

    @Test
    public void starting_a_new_test_suite_should_clear_issues_of_the_previous_suite() {

        environmentVariables.setProperty(ClasspathWorkflowLoader.ACTIVATE_WORKFLOW_PROPERTY,"true");
        TestOutcome result = newTestOutcome("issue_123_should_be_fixed_now", TestResult.FAILURE);

        JiraStepListener listener = new JiraStepListener(issueTracker, environmentVariables, workflowLoader);
        listener.testSuiteStarted(SampleTestCase.class);
        assertThat(listener.getTestSuiteIssues()).isEmpty();
        listener.testStarted("issue_123_should_be_fixed_now");
        listener.testFinished(result);
        listener.testSuiteFinished();
        assertThat(listener.getTestSuiteIssues()).containsSequence("MYPROJECT-123");
        assertThat(listener.getTestResultTally().getIssues()).containsSequence("MYPROJECT-123");

        result = newTestOutcomeForSampleClass2("issue_789_should_be_fixed_now", TestResult.FAILURE);
        listener.testSuiteStarted(SampleTestCase2.class);
        assertThat(listener.getTestSuiteIssues()).isEmpty();
        listener.testStarted("issue_789_should_be_fixed_now");
        listener.testFinished(result);
        listener.testSuiteFinished();
        assertThat(listener.getTestSuiteIssues()).containsSequence("MYPROJECT-789");
        assertThat(listener.getTestResultTally().getIssues()).containsSequence("MYPROJECT-123","MYPROJECT-789");
    }

    private TestOutcome newTestOutcome(String testMethod, TestResult testResult) {
        TestOutcome result = TestOutcome.forTest(testMethod, SampleTestCase.class);
        TestStep step = new TestStep("a narrative description");
        step.setResult(testResult);
        result.recordStep(step);
        return result;
    }

    private TestOutcome newTestOutcomeForSampleClass2(String testMethod, TestResult testResult) {
        TestOutcome result = TestOutcome.forTest(testMethod, SampleTestCase2.class);
        TestStep step = new TestStep("a narrative description");
        step.setResult(testResult);
        result.recordStep(step);
        return result;
    }
}
