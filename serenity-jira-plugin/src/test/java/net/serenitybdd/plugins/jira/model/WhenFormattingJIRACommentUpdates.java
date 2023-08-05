package net.serenitybdd.plugins.jira.model;

import com.google.common.collect.ImmutableList;
import net.serenitybdd.plugins.jira.workflow.ClasspathWorkflowLoader;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestResult;
import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.model.environment.MockEnvironmentVariables;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

public class WhenFormattingJIRACommentUpdates {

    EnvironmentVariables environmentVariables;

    @Mock
    TestOutcome successfulTestOutcome;

    @Mock
    TestOutcome failingTestOutcome;

    @Mock
    TestOutcome anotherSuccessfulTestOutcome;

    @Mock
    TestOutcome aNewTestOutcome;

    List<TestOutcome> testOutcomes;

    List<NamedTestResult> namedTestResults;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);

        when(successfulTestOutcome.getResult()).thenReturn(TestResult.SUCCESS);
        when(successfulTestOutcome.getTitle()).thenReturn("Successful Test");

        when(anotherSuccessfulTestOutcome.getResult()).thenReturn(TestResult.SUCCESS);
        when(anotherSuccessfulTestOutcome.getTitle()).thenReturn("Another Successful Test");

        when(failingTestOutcome.getResult()).thenReturn(TestResult.FAILURE);
        when(failingTestOutcome.getTitle()).thenReturn("Failing Test");

        when(aNewTestOutcome.getResult()).thenReturn(TestResult.SUCCESS);
        when(aNewTestOutcome.getTitle()).thenReturn("A New Test");

        testOutcomes = ImmutableList.of(anotherSuccessfulTestOutcome, failingTestOutcome, successfulTestOutcome);
        namedTestResults = namedTestResultsFrom(testOutcomes);

        environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("jira.url", "http://my.jira.server");
        environmentVariables.setProperty("thucydides.public.url", "http://my.server/myproject/thucydides");
        environmentVariables.setProperty(ClasspathWorkflowLoader.ACTIVATE_WORKFLOW_PROPERTY, "true");
    }

    private List<NamedTestResult> namedTestResultsFrom(List<TestOutcome> testOutcomes) {
        return testOutcomes.stream().map(this::toNamedTestResults).collect(Collectors.toList());
    }

    private NamedTestResult toNamedTestResults(TestOutcome from) {
        return new NamedTestResult(from.getTitle(), from.getResult());
    }

    @Test
    public void should_include_identifiable_title_in_comment_heading() {

        String updatedComment = TestResultComment.comment(true).asText();
        assertThat(updatedComment, containsString("Serenity BDD Automated Acceptance Tests"));
    }

    /*
      Serenity BDD Test Results
      Report: http://my.server/myproject/thucydides/my_test.html
      Build Job: 2012-01-17_15-39-03
      should_do_this: SUCCESS
      should_do_that: FAILURE
      should_also_do_this: SUCCESS
    */

    @Test
    public void should_include_test_result_title() {

        String updatedComment = TestResultComment.comment(true)
                .withReportUrl("http://my.server/myproject/thucydides/my_test.html")
                .withResults(namedTestResults)
                .withTestRun("2012-01-17_15-39-03")
                .asText();

        assertThat(updatedComment, containsString("Serenity BDD Automated Acceptance Tests"));
    }

    @Test
    public void should_be_able_to_fix_invalid_comments() {

        TestResultComment testReport = TestResultComment.fromText("Serenity BDD Automated Acceptance Tests");

        String updatedComment = testReport
                                    .withUpdatedTestResults(namedTestResults)
                                    .withUpdatedReportUrl("http://my.server/myproject/thucydides/my_test.html")
                                    .withUpdatedTestRunNumber("2012-01-17_15-39-03")
                                    .asText();

        String expectedComment = TestResultComment.comment(true)
                .withReportUrl("http://my.server/myproject/thucydides/my_test.html")
                .withResults(namedTestResults)
                .withTestRun("2012-01-17_15-39-03")
                .asText();

        assertThat(updatedComment, is(expectedComment));
    }

    @Test
    public void should_include_link_to_test_report() {

        String updatedComment = TestResultComment.comment(true)
                .withReportUrl("http://my.server/myproject/thucydides/my_test.html")
                .withResults(namedTestResults)
                .withTestRun("2012-01-17_15-39-03")
                .asText();

        assertThat(updatedComment, containsString("http://my.server/myproject/thucydides/my_test.html"));
    }

    @Test
    public void should_include_the_latest_build_job_number() {

        String updatedComment = TestResultComment.comment(true)
                .withReportUrl("http://my.server/myproject/thucydides/my_test.html")
                .withResults(namedTestResults)
                .withTestRun("2012-01-17_15-39-03")
                .asText();

        assertThat(updatedComment, containsString("Test Run: 2012-01-17_15-39-03"));
    }


    @Test
    public void should_include_the_result_for_each_test() {

        String updatedComment = TestResultComment.comment(true)
                .withReportUrl("http://my.server/myproject/thucydides/my_test.html")
                .withResults(namedTestResults)
                .withTestRun("2012-01-17_15-39-03")
                .asText();

        assertThat(updatedComment, containsString("Successful Test: SUCCESS"));
        assertThat(updatedComment, containsString("Another Successful Test: SUCCESS"));
        assertThat(updatedComment, containsString("Failing Test: FAILURE"));
    }

    @Test
    public void should_be_able_to_obtain_the_latest_build_job_number_from_a_comment() {

        String commentText = TestResultComment.comment(true)
                .withReportUrl("http://my.server/myproject/thucydides/my_test.html")
                .withResults(namedTestResults)
                .withTestRun("2012-01-17_15-39-03")
                .asText();

        TestResultComment comment = new TestResultComment(commentText);

        assertThat(comment.getTestRunNumber(), is("2012-01-17_15-39-03"));
    }

    @Test
    public void should_be_able_to_obtain_the_latest_report_url_from_a_comment() {

        String commentText = TestResultComment.comment(true)
                .withReportUrl("http://my.server/myproject/thucydides/my_test.html")
                .withResults(namedTestResults)
                .withTestRun("2012-01-17_15-39-03")
                .asText();

        TestResultComment comment = new TestResultComment(commentText);

        assertThat(comment.getReportUrl(), is("http://my.server/myproject/thucydides/my_test.html"));
    }

    @Test
    public void should_be_able_to_obtain_the_recorded_test_results_for_each_test() {

        String commentText = TestResultComment.comment(true)
                .withReportUrl("http://my.server/myproject/thucydides/my_test.html")
                .withResults(namedTestResults)
                .withTestRun("2012-01-17_15-39-03")
                .asText();

        TestResultComment comment = new TestResultComment(commentText);

        List<NamedTestResult> testResults = comment.getNamedTestResults();

        assertThat(testResults.get(0).getTestName(), is("Another Successful Test"));
        assertThat(testResults.get(0).getTestResult(), is(TestResult.SUCCESS));

        assertThat(testResults.get(1).getTestName(), is("Failing Test"));
        assertThat(testResults.get(1).getTestResult(), is(TestResult.FAILURE));

        assertThat(testResults.get(2).getTestName(), is("Successful Test"));
        assertThat(testResults.get(2).getTestResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void should_be_able_to_obtain_the_overall_test_result_from_tests_in_a_comment() {

        String commentText = TestResultComment.comment(true)
                .withReportUrl("http://my.server/myproject/thucydides/my_test.html")
                .withResults(namedTestResults)
                .withTestRun("2012-01-17_15-39-03")
                .asText();

        TestResultComment comment = new TestResultComment(commentText);

        assertThat(comment.getOverallResult(), is(TestResult.FAILURE));
    }

    @Test
    public void should_be_able_to_write_a_new_comment_using_the_existing_comments_data() {

        String commentText = TestResultComment.comment(true)
                .withReportUrl("http://my.server/myproject/thucydides/my_test.html")
                .withResults(namedTestResults)
                .withTestRun("2012-01-17_15-39-03")
                .asText();

        TestResultComment comment = TestResultComment.fromText(commentText);

        assertThat(comment.toString(), is(commentText));
    }

    @Test
    public void should_be_able_to_add_or_update_test_results_in_the_existing_comments_data() {

        String commentText = TestResultComment.comment(true)
                .withReportUrl("http://my.server/myproject/thucydides/my_test.html")
                .withResults(namedTestResults)
                .withTestRun("2012-01-17_15-39-03")
                .asText();

        
        List<TestOutcome> newResults = ImmutableList.of(successfulTestOutcome, anotherSuccessfulTestOutcome, aNewTestOutcome);
        TestResultComment comment = TestResultComment.fromText(commentText);
        
        TestResultComment updatedComment = comment.withUpdatedTestResults(namedTestResultsFrom(newResults));

        List<NamedTestResult> testResults = updatedComment.getNamedTestResults();

        assertThat(testResults.get(0).getTestName(), is("A New Test"));
        assertThat(testResults.get(0).getTestResult(), is(TestResult.SUCCESS));

        assertThat(testResults.get(1).getTestName(), is("Another Successful Test"));
        assertThat(testResults.get(1).getTestResult(), is(TestResult.SUCCESS));

        assertThat(testResults.get(2).getTestName(), is("Failing Test"));
        assertThat(testResults.get(2).getTestResult(), is(TestResult.FAILURE));

        assertThat(testResults.get(3).getTestName(), is("Successful Test"));
        assertThat(testResults.get(3).getTestResult(), is(TestResult.SUCCESS));
    }

}
