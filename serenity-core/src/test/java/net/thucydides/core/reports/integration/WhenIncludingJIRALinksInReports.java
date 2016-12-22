package net.thucydides.core.reports.integration;

import net.thucydides.core.annotations.Story;
import net.thucydides.core.annotations.Title;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.issues.SystemPropertiesIssueTracking;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.TestOutcomes;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;

public class WhenIncludingJIRALinksInReports extends AbstractReportGenerationTest {

    @Story(AUserStory.class)
    protected class JIRAAnnotatedTestScenario {

        @Title("a simple test case (#1234)")
        public void a_simple_test_case() {};

        @Title("should do this as well (#1234, #2345)")
        public void should_do_this_too() {};

        //@Issue("#3456")
        public void and_should_do_this() {};

        public void should_do_that() {};
    }


    IssueTracking issueTracking;
    
    @Mock
    TestOutcomes allTestOutcomes;

    @Before
    public void setupIssueTracker() {
        MockitoAnnotations.initMocks(this);
        issueTracking = new SystemPropertiesIssueTracking(environmentVariables);
    }

    @Test
    public void a_jira_issue_number_should_be_converted_to_URLs_when_a_jira_base_url_is_provided()  throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", JIRAAnnotatedTestScenario.class)
                                              .usingIssueTracking(issueTracking);
        environmentVariables.setProperty("thucydides.issue.tracker.url", "http://my.issue.tracker/{0}");

        recordSimpleTest(testOutcome);

        File screenshotReport = reporter.generateReportFor(testOutcome);

        String reportContents = FileUtils.readFileToString(screenshotReport);
        assertThat(reportContents, containsString("<a target=\"_blank\" href=\"http://my.issue.tracker/1234\">#1234</a>"));
    }


    @Test
    public void a_jira_issue_number_can_also_appear_in_the_issue_annotation()  throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", JIRAAnnotatedTestScenario.class)
                                              .usingIssueTracking(issueTracking);
        environmentVariables.setProperty("thucydides.issue.tracker.url", "http://my.issue.tracker/{0}");

        recordSimpleTest(testOutcome);

        File screenshotReport = reporter.generateReportFor(testOutcome);
        String reportContents = FileUtils.readFileToString(screenshotReport);
        assertThat(reportContents, containsString("<a target=\"_blank\" href=\"http://my.issue.tracker/1234\">#1234</a>"));
    }

    @Test
    public void a_jira_base_url_should_also_be_recognized()  throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", JIRAAnnotatedTestScenario.class)
                                              .usingIssueTracking(issueTracking);
        environmentVariables.setProperty("jira.url", "http://my.jira");

        recordSimpleTest(testOutcome);

        File screenshotReport =reporter.generateReportFor(testOutcome);

        String reportContents = FileUtils.readFileToString(screenshotReport);
        assertThat(reportContents, containsString("<a target=\"_blank\" href=\"http://my.jira/browse/1234\">#1234</a>"));
    }

    @Test
    public void the_jira_project_name_should_be_included_in_the_jira_links_if_defined()  throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", JIRAAnnotatedTestScenario.class)
                                              .usingIssueTracking(issueTracking);
        environmentVariables.setProperty("jira.url", "http://my.jira");
        environmentVariables.setProperty("jira.project", "MYPROJECT");

        recordSimpleTest(testOutcome);

        File screenshotReport = reporter.generateReportFor(testOutcome);

        String reportContents = FileUtils.readFileToString(screenshotReport);
        assertThat(reportContents, containsString("<a target=\"_blank\" href=\"http://my.jira/browse/MYPROJECT-1234\">#1234</a>"));
    }

    @Test
    public void jira_issue_numbers_should_be_converted_to_URLs_when_a_jira_base_url_is_provided()  throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this_too", JIRAAnnotatedTestScenario.class)
                                              .usingIssueTracking(issueTracking);
        environmentVariables.setProperty("thucydides.issue.tracker.url", "http://my.issue.tracker/{0}");

        recordSimpleTest(testOutcome);

        File screenshotReport = reporter.generateReportFor(testOutcome);

        String reportContents = FileUtils.readFileToString(screenshotReport);
        assertThat(reportContents, allOf(containsString("<a target=\"_blank\" href=\"http://my.issue.tracker/1234\">#1234</a>"),
                                         containsString("<a target=\"_blank\" href=\"http://my.issue.tracker/2345\">#2345</a>")));
    }

    private void recordSimpleTest(TestOutcome testOutcome) throws IOException {
        recordStepWithScreenshot(testOutcome, "Search cats on Google", "google_page_1.png");
        recordStepWithScreenshot(testOutcome, "View the results", "google_page_2.png");
        recordStepWithScreenshot(testOutcome, "Display a resulting page", "google_page_3.png");
    }


}
