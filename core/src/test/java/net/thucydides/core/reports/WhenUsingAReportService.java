package net.thucydides.core.reports;

import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class WhenUsingAReportService {

    @Mock
    File outputDirectory;

    @Mock
    AcceptanceTestReporter reporter;

    @Mock
    TestOutcome testOutcome;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void a_report_service_should_generate_reports_for_each_subscribed_reporter() throws Exception {
        List<TestOutcome> testOutcomeResults = new ArrayList<TestOutcome>();
        testOutcomeResults.add(testOutcome);

        ReportService reportService = new ReportService(outputDirectory, new ArrayList<AcceptanceTestReporter>());

        reportService.subscribe(reporter);

        reportService.generateReportsFor(testOutcomeResults);

        verify(reporter).generateReportFor(eq(testOutcome), Matchers.any(TestOutcomes.class));
    }

    @Test
    public void a_report_service_should_generate_reports_for_each_test_outcome() throws Exception {

        List<TestOutcome> testOutcomeResults = new ArrayList<>();
        for(int i = 0; i < 1000; i++) {
            TestOutcome outcome = TestOutcome.forTestInStory("test" + i, Story.withId("s1", "Story 1"));
            testOutcomeResults.add(outcome);
        }

        ReportService reportService = new ReportService(outputDirectory, new ArrayList<AcceptanceTestReporter>());

        reportService.subscribe(reporter);

        reportService.generateReportsFor(testOutcomeResults);

        verify(reporter, times(1000)).generateReportFor(Matchers.any(TestOutcome.class), Matchers.any(TestOutcomes.class));
    }


    @Test
    public void a_report_service_uses_the_provided_output_directory_for_all_reports() throws Exception {
        List<TestOutcome> testOutcomeResults = new ArrayList<TestOutcome>();
        testOutcomeResults.add(testOutcome);

        ReportService reportService = new ReportService(outputDirectory, new ArrayList<AcceptanceTestReporter>());

        reportService.subscribe(reporter);

        reportService.generateReportsFor(testOutcomeResults);

        verify(reporter).setOutputDirectory(outputDirectory);
    }

    @Test
    public void default_reporters_should_include_xml_html_and_json() {
        List reporters = ReportService.getDefaultReporters();
        assertThat(reporters.size(), is(3));

        Matcher calledXml = hasProperty("name", is("xml"));
        Matcher calledHtml = hasProperty("name", is("html"));
        Matcher calledJSON = hasProperty("name", is("json"));
        assertThat(reporters, allOf(hasItem(calledXml), hasItem(calledHtml), hasItem(calledJSON)));
    }

    @Test
    public void new_reporters_should_be_instantiated_at_each_request() {
        List reporters = ReportService.getDefaultReporters();
        List reporters2 = ReportService.getDefaultReporters();
        assertThat(reporters, is(not(reporters2)));
    }

}
