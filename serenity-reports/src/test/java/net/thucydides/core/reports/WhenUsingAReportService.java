package net.thucydides.core.reports;

import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.reports.AcceptanceTestReporter;
import net.thucydides.model.reports.ReportService;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class WhenUsingAReportService {


    @Rule
    public TemporaryFolder folder= new TemporaryFolder();

    File outputDirectory;

    @Mock
    AcceptanceTestReporter reporter;

    @Mock
    TestOutcome testOutcome;

    @Before
    public void initMocks() throws IOException {
        MockitoAnnotations.initMocks(this);
        when(testOutcome.getTestCaseName()).thenReturn("someTestCase");
        when(testOutcome.getId()).thenReturn("someId");
        when(testOutcome.getQualifiedId()).thenReturn("someId");

        outputDirectory = folder.newFolder();
    }

    @Test
    public void a_report_service_should_generate_reports_for_each_subscribed_reporter() throws Exception {
        List<TestOutcome> testOutcomeResults = new ArrayList<TestOutcome>();
        testOutcomeResults.add(testOutcome);

        ReportService reportService = new ReportService(outputDirectory, new ArrayList<AcceptanceTestReporter>());

        reportService.subscribe(reporter);

        reportService.generateReportsFor(testOutcomeResults);

        verify(reporter).generateReportFor(eq(testOutcome));
    }

    public static final class ATestCase {}

    @Test
    public void a_report_service_should_generate_reports_for_each_test_outcome() throws Exception {

        List<TestOutcome> testOutcomeResults = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            TestOutcome outcome = TestOutcome.forTest("test" + i, ATestCase.class);
            testOutcomeResults.add(outcome);
        }

        ReportService reportService = new ReportService(outputDirectory, new ArrayList<>());

        reportService.subscribe(reporter);

        reportService.generateReportsFor(testOutcomeResults);
        verify(reporter, times(100)).generateReportFor(ArgumentMatchers.any(TestOutcome.class));
    }


    @Test
    public void a_report_service_uses_the_provided_output_directory_for_all_reports() throws Exception {
        List<TestOutcome> testOutcomeResults = new ArrayList<TestOutcome>();
        testOutcomeResults.add(testOutcome);

        ReportService reportService = new ReportService(outputDirectory, new ArrayList<>());

        reportService.subscribe(reporter);

        reportService.generateReportsFor(testOutcomeResults);

        verify(reporter).setOutputDirectory(outputDirectory);
    }

    @Test
    public void default_reporters_should_include_json() {
        List reporters = ReportService.getDefaultReporters();
        assertThat(reporters.size(), is(1));
        Matcher calledJSON = hasProperty("name", is("json"));
        assertThat(reporters, allOf(hasItem(calledJSON)));
    }

    @Test
    public void new_reporters_should_be_instantiated_at_each_request() {
        List reporters = ReportService.getDefaultReporters();
        List reporters2 = ReportService.getDefaultReporters();
        assertThat(reporters, is(not(reporters2)));
    }

}
