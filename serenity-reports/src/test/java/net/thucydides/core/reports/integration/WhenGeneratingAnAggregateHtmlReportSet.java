package net.thucydides.core.reports.integration;

import net.thucydides.core.reports.html.HtmlAggregateStoryReporter;
import net.thucydides.core.reports.html.ReportProperties;
import net.thucydides.model.environment.MockEnvironmentVariables;
import net.thucydides.model.issues.IssueTracking;
import net.thucydides.model.reports.ResultChecker;
import net.thucydides.model.reports.html.ReportNameProvider;
import net.thucydides.model.util.EnvironmentVariables;
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

import static net.thucydides.model.matchers.FileMatchers.exists;
import static net.thucydides.model.util.TestResources.directoryInClasspathCalled;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class WhenGeneratingAnAggregateHtmlReportSet {

    private static File outputDirectory;

    private static final EnvironmentVariables environmentVariables = new MockEnvironmentVariables();

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
//        outputDirectory.delete();
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
    public void should_check_json_results() {
        File reports = directoryInClasspathCalled("/test-outcomes/with-duplicates");
        ResultChecker resultChecker = new ResultChecker(reports);
        resultChecker.checkTestResults();
    }
}
