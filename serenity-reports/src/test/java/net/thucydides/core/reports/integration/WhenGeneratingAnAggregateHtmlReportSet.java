package net.thucydides.core.reports.integration;

import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.reports.ResultChecker;
import net.thucydides.core.reports.html.HtmlAggregateStoryReporter;
import net.thucydides.core.reports.html.ReportNameProvider;
import net.thucydides.core.reports.html.ReportProperties;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;

import static net.thucydides.core.matchers.FileMatchers.exists;
import static net.thucydides.core.util.TestResources.directoryInClasspathCalled;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class WhenGeneratingAnAggregateHtmlReportSet {

    private static File outputDirectory;

    private static EnvironmentVariables environmentVariables = new MockEnvironmentVariables();

    @BeforeClass
    public static void generateReports() throws IOException {
        IssueTracking issueTracking = mock(IssueTracking.class);
        environmentVariables.setProperty("output.formats", "xml");
        environmentVariables.setProperty("report.customfields.env", "testenv");
        HtmlAggregateStoryReporter reporter = new HtmlAggregateStoryReporter("project", "", issueTracking, environmentVariables);
        outputDirectory = newTemporaryDirectory();
        reporter.setOutputDirectory(outputDirectory);

        File sourceDirectory = directoryInClasspathCalled("/test-outcomes/containing-nostep-errors");
        reporter.generateReportsForTestResultsFrom(sourceDirectory);
    }

    @AfterClass
    public static void deleteReportDirectory() {
        outputDirectory.delete();
    }

    private static File newTemporaryDirectory() throws IOException {
        File createdFolder = File.createTempFile("reports", "");
        createdFolder.delete();
        createdFolder.mkdir();
        return createdFolder;
    }

    @Before
    public void setupTestReporter() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void should_generate_an_aggregate_dashboard() throws Exception {
        assertThat(new File(outputDirectory, "index.html"), exists());
    }

    @Test
    public void should_generate_overall_passed_failed_and_pending_reports() throws Exception {
        ReportNameProvider reportName = new ReportNameProvider();
        String expectedSuccessReport = reportName.forTestResult("success");
        String expectedPendingReport = reportName.forTestResult("pending");

        assertThat(new File(outputDirectory, expectedSuccessReport), exists());
        assertThat(new File(outputDirectory, expectedPendingReport), exists());
    }

    @Test
    public void should_display_overall_passed_failed_and_pending_report_links_in_home_page() throws Exception {
        ReportNameProvider reportName = new ReportNameProvider();
        String expectedSuccessReport = reportName.forTestResult("success");
        String expectedPendingReport = reportName.forTestResult("pending");

        File report = new File(outputDirectory, "index.html");

        Document reportHomePage = Jsoup.parse(report, "UTF-8");

        Elements successReports = reportHomePage.select("a[href='" + expectedSuccessReport + "']");
        Elements pendingReports = reportHomePage.select("a[href='" + expectedPendingReport + "']");

        assertThat(successReports.size(), equalTo(1));
        assertThat(pendingReports.size(), equalTo(1));
    }

    @Test
    public void should_display_the_date_and_time_of_tests_on_the_home_page() throws Exception {

        File report = new File(outputDirectory, "index.html");
        Document reportHomePage = Jsoup.parse(report, "UTF-8");

        Elements dateAndTime = reportHomePage.select(".date-and-time");
        assertThat(dateAndTime.size(), is(1));
    }

    @Test
    public void should_display_the_date_and_time_of_tests_on_the_other_pages() throws Exception {
        ReportNameProvider reportName = new ReportNameProvider();
        String expectedSuccessReport = reportName.forTestResult("success");

        File report = new File(outputDirectory, expectedSuccessReport);
        Document reportHomePage = Jsoup.parse(report, "UTF-8");

        Elements dateAndTime = reportHomePage.select(".date-and-time");
        assertThat(dateAndTime.size(), is(1));
    }

    @Test
    public void should_display_custom_field_on_the_home_page() throws IOException {
        File report = new File(outputDirectory, "index.html");

        Document reportHomePage = Jsoup.parse(report, "UTF-8");

        String customTitle = reportHomePage.select(".custom-title").first().text();
        String customValue = reportHomePage.select(".custom-value").first().text();

        assertThat(customTitle, equalTo("Env"));
        assertThat(customValue, equalTo("testenv"));
    }

    @Test
    public void should_not_display_links_to_test_result_reports_in_test_result_reports() {
        ReportProperties reportProperties = ReportProperties.forTestResultsReport();
        assertThat(reportProperties.getShouldDisplayResultLink(), is(false));
    }

    @Test
    public void should_display_links_to_test_result_reports_in_tag_reports() {
        ReportProperties reportProperties = ReportProperties.forTagResultsReport();
        assertThat(reportProperties.getShouldDisplayResultLink(), is(true));
    }

    @Test
    public void should_display_links_to_test_result_reports_in_top_level_reports() {
        ReportProperties reportProperties = ReportProperties.forAggregateResultsReport();
        assertThat(reportProperties.getShouldDisplayResultLink(), is(true));
    }

    @Test
    public void errors_are_present() {
        File reports = directoryInClasspathCalled("/test-outcomes/containing-errors");
        ResultChecker resultChecker = new ResultChecker(reports);
        assertThat(resultChecker.checkTestResults(), is(TestResult.ERROR));
    }

    @Test
    public void failures_are_present() {
        File reports = directoryInClasspathCalled("/test-outcomes/containing-failure");
        ResultChecker resultChecker = new ResultChecker(reports);
        assertThat(resultChecker.checkTestResults(), is(TestResult.FAILURE));
    }

    @Test
    public void successful_tests() {
        File reports = directoryInClasspathCalled("/test-outcomes/all-successful");
        ResultChecker resultChecker = new ResultChecker(reports);
        assertThat(resultChecker.checkTestResults(), is(TestResult.SUCCESS));
    }

    @Test
    public void should_check_json_results() {
        File reports = directoryInClasspathCalled("/test-outcomes/full-json");
        ResultChecker resultChecker = new ResultChecker(reports);
        resultChecker.checkTestResults();
    }
}
