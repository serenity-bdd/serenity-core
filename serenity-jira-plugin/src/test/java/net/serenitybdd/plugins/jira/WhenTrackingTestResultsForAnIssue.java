package net.serenitybdd.plugins.jira;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class WhenTrackingTestResultsForAnIssue {

    @Mock
    TestOutcome successfulTestOutcome;

    @Mock
    TestOutcome failingTestOutcome;

    @Mock
    TestOutcome anotherSuccessfulTestOutcome;


    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);

        when(successfulTestOutcome.getResult()).thenReturn(TestResult.SUCCESS);
        when(anotherSuccessfulTestOutcome.getResult()).thenReturn(TestResult.SUCCESS);
        when(failingTestOutcome.getResult()).thenReturn(TestResult.FAILURE);
    }
    
    @Test
    public void should_record_the_result_for_a_given_test() {
        TestResultTally resultTally = new TestResultTally();

        TestOutcome outcome = new TestOutcome("someTest");
        resultTally.recordResult("ISSUE-1", successfulTestOutcome);

        TestResult recordedResult = resultTally.getResultForIssue("ISSUE-1");

        assertThat(recordedResult).isEqualTo(TestResult.SUCCESS);
    }

    @Test
    public void should_record_the_overall_result_for_a_given_test() {
        TestResultTally resultTally = new TestResultTally();

        resultTally.recordResult("ISSUE-1", successfulTestOutcome);
        resultTally.recordResult("ISSUE-1", failingTestOutcome);
        resultTally.recordResult("ISSUE-1", anotherSuccessfulTestOutcome);

        TestResult recordedResult = resultTally.getResultForIssue("ISSUE-1");

        assertThat(recordedResult).isEqualTo(TestResult.FAILURE);
    }
    
    @Test
    public void should_list_tallied_issues() {
        TestResultTally resultTally = new TestResultTally();

        resultTally.recordResult("ISSUE-1", successfulTestOutcome);
        resultTally.recordResult("ISSUE-1", failingTestOutcome);
        resultTally.recordResult("ISSUE-2", anotherSuccessfulTestOutcome);

        assertThat(resultTally.getIssues()).contains("ISSUE-1", "ISSUE-2");
    }

    @Test
    public void should_list_test_outcomes_for_an_issue() {
        TestResultTally resultTally = new TestResultTally();

        resultTally.recordResult("ISSUE-1", successfulTestOutcome);
        resultTally.recordResult("ISSUE-1", failingTestOutcome);
        resultTally.recordResult("ISSUE-2", anotherSuccessfulTestOutcome);

        assertThat(resultTally.getTestOutcomesForIssue("ISSUE-1")).contains(successfulTestOutcome, failingTestOutcome);
    }

}
