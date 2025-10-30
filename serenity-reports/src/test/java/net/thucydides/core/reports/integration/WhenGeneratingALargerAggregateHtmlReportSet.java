package net.thucydides.core.reports.integration;

import net.thucydides.core.reports.html.HtmlAggregateStoryReporter;
import net.thucydides.model.domain.TestResult;
import net.thucydides.model.environment.MockEnvironmentVariables;
import net.thucydides.model.issues.IssueTracking;
import net.thucydides.model.reports.OutcomeFormat;
import net.thucydides.model.reports.TestOutcomeLoader;
import net.thucydides.model.reports.TestOutcomes;
import net.thucydides.model.util.EnvironmentVariables;
import org.junit.jupiter.api.*;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;

import static net.thucydides.model.util.TestResources.directoryInClasspathCalled;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class WhenGeneratingALargerAggregateHtmlReportSet {

    private static File outputDirectory;
    HtmlAggregateStoryReporter reporter;

    private static final EnvironmentVariables environmentVariables = new MockEnvironmentVariables();

    @BeforeAll
    public static void generateReports() throws IOException {
        environmentVariables.setProperty("output.formats", "json");
        environmentVariables.setProperty("report.customfields.env", "testenv");

    }

    @AfterAll
    public static void deleteReportDirectory() {
        if (outputDirectory != null) {
            outputDirectory.delete();
        }
    }

    private static File newTemporaryDirectory() throws IOException {
        File createdFolder = File.createTempFile("reports", "");
        createdFolder.delete();
        createdFolder.mkdir();
        return createdFolder;
    }

    @BeforeEach
    public void setupTestReporter() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void should_generate_an_aggregate_dashboard() throws Exception {
        IssueTracking issueTracking = mock(IssueTracking.class);
        HtmlAggregateStoryReporter reporter = new HtmlAggregateStoryReporter("project", "", issueTracking, environmentVariables);
        outputDirectory = newTemporaryDirectory();
        reporter.setOutputDirectory(outputDirectory);
        File sourceDirectory = directoryInClasspathCalled("/sample-large-site");
        reporter.generateReportsForTestResultsFrom(sourceDirectory);
        assertThat(new File(outputDirectory, "index.html")).exists();
    }

    @Test
    public void should_generate_an_aggregate_report_for_data_driven_tests() throws Exception {
        IssueTracking issueTracking = mock(IssueTracking.class);
        HtmlAggregateStoryReporter reporter = new HtmlAggregateStoryReporter("project", "", issueTracking, environmentVariables);
        outputDirectory = newTemporaryDirectory();
        reporter.setOutputDirectory(outputDirectory);
        File sourceDirectory = directoryInClasspathCalled("/sample-data-driven-tests");
        reporter.generateReportsForTestResultsFrom(sourceDirectory);
        assertThat(new File(outputDirectory, "index.html")).exists();
    }

    @Nested
    class WhenCalculatingTestResultsForAggregateReports {

        TestOutcomes allTestOutcomes = TestOutcomeLoader.loadTestOutcomes().inFormat(OutcomeFormat.JSON).from(directoryInClasspathCalled("/sample-data-driven-tests"));

        WhenCalculatingTestResultsForAggregateReports() throws IOException {}

        @Test
        public void should_calculate_the_total_number_of_test_outcomes_by_result() throws Exception {
            TestOutcomes passingTestCases = allTestOutcomes.withResult(TestResult.SUCCESS);
            TestOutcomes failingTestCases = allTestOutcomes.withResult(TestResult.FAILURE);

            assertThat(passingTestCases.getScenarioCount()).isEqualTo(20);
            assertThat(passingTestCases.getTestCaseCount()).isEqualTo(40);
            assertThat(failingTestCases.getScenarioCount()).isEqualTo(1);
            assertThat(failingTestCases.getTestCaseCount()).isEqualTo(3);
        }

        @Test
        public void should_calculate_the_total_number_of_test_outcomes_by_duration() throws Exception {
            TestOutcomes passingTestCases = allTestOutcomes.withResult(TestResult.SUCCESS);
            TestOutcomes failingTestCases = allTestOutcomes.withResult(TestResult.FAILURE);

            assertThat(passingTestCases.getScenarioCount()).isEqualTo(20);
            assertThat(passingTestCases.getTestCaseCount()).isEqualTo(40);
            assertThat(failingTestCases.getScenarioCount()).isEqualTo(1);
            assertThat(failingTestCases.getTestCaseCount()).isEqualTo(3);
        }
    }

}
