package net.thucydides.core.reports.integration;

import net.thucydides.core.digest.Digest;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.reports.ResultChecker;
import net.thucydides.core.reports.TestOutcomesError;
import net.thucydides.core.reports.TestOutcomesFailures;
import net.thucydides.core.reports.html.HtmlAggregateStoryReporter;
import net.thucydides.core.reports.html.ReportNameProvider;
import net.thucydides.core.reports.html.ReportProperties;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import org.hamcrest.Matcher;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static net.thucydides.core.matchers.FileMatchers.exists;
import static net.thucydides.core.util.TestResources.directoryInClasspathCalled;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;

public class WhenGeneratingAnAggregateHtmlReportSet {

    private static File outputDirectory;

    static WebDriver driver;

    private static EnvironmentVariables environmentVariables = new MockEnvironmentVariables();

    @BeforeClass
    public static void generateReports() throws IOException {
        IssueTracking issueTracking = mock(IssueTracking.class);
        environmentVariables.setProperty("output.formats", "xml");
        HtmlAggregateStoryReporter reporter = new HtmlAggregateStoryReporter("project", "", issueTracking, environmentVariables);
        outputDirectory = newTemporaryDirectory();
        reporter.setOutputDirectory(outputDirectory);

        File sourceDirectory = directoryInClasspathCalled("/test-outcomes/containing-nostep-errors");
        reporter.generateReportsForTestResultsFrom(sourceDirectory);

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        driver = new ChromeDriver();
    }

    @AfterClass
    public static void deleteReportDirectory() {
        if (driver != null) {
            driver.close();
            driver.quit();
        }
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
        driver.get(urlFor(report));

        driver.findElement(By.cssSelector("a[href='" + expectedSuccessReport + "']"));
        driver.findElement(By.cssSelector("a[href='" + expectedPendingReport + "']"));
    }

    @Test
    public void should_display_the_date_and_time_of_tests_on_the_home_page() throws Exception {
        File report = new File(outputDirectory, "index.html");
        driver.get(urlFor(report));
        assertThat(driver.findElement(By.cssSelector(".date-and-time")).isDisplayed(), is(true));
    }

    @Test
    public void should_display_the_date_and_time_of_tests_on_the_other_pages() throws Exception {
        ReportNameProvider reportName = new ReportNameProvider();
        String expectedSuccessReport = reportName.forTestResult("success");

        File report = new File(outputDirectory, expectedSuccessReport);
        driver.get(urlFor(report));
        assertThat(driver.findElement(By.cssSelector(".date-and-time")).isDisplayed(), is(true));
    }

    @Test
    public void aggregate_dashboard_should_contain_a_list_of_all_tag_types() throws Exception {

        File report = new File(outputDirectory, "index.html");
        driver.get(urlFor(report));

        List<WebElement> tagTypes = driver.findElements(By.cssSelector(".tagTypeTitle"));
        List<String> tagTypeNames =
                tagTypes.stream()
                        .map(WebElement::getText)
                        .collect(Collectors.toList());
        assertThat(tagTypeNames, hasItems("Stories", "Features", "Epics"));
    }

    private String urlFor(File report) {
        return "file:///" + report.getAbsolutePath();
    }

    @Test
    public void aggregate_dashboard_should_contain_correct_test_counts() throws Exception {

        File report = new File(outputDirectory, "index.html");
        driver.get(urlFor(report));

        List<String> testCountLabels = convertToStrings(driver.findElements(By.cssSelector(".test-count")));
        assertThat(testCountLabels, hasSize(8));
        Matcher<Iterable<? super String>> passedMatcher = hasItem(containsString("2 passed"));
        Matcher<Iterable<? super String>> pendingMatcher = hasItem(containsString("2 pending"));
        Matcher<Iterable<? super String>> failedMatcher = hasItem(containsString("2 failed"));
        Matcher<Iterable<? super String>> errorMatcher = hasItem(containsString("1 with errors"));
        Matcher<Iterable<? super String>> ignoredMatcher = hasItem(containsString("1 ignored"));
        assertThat(testCountLabels, allOf(passedMatcher, pendingMatcher, failedMatcher, errorMatcher, ignoredMatcher));
    }

    private List<String> convertToStrings(List<WebElement> elements) {
        return elements.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
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
