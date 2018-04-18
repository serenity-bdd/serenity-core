package net.thucydides.core.reports;

import net.thucydides.core.annotations.Feature;
import net.thucydides.core.annotations.Story;
import net.thucydides.core.digest.Digest;
import net.thucydides.core.model.TestOutcome;
import org.junit.Test;

import static net.thucydides.core.model.ReportType.HTML;
import static net.thucydides.core.model.ReportType.XML;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class WhenNamingTheReports {


    class AUserStory {};

    @Feature
    class AFeature {
        class AUserStoryInAFeature {}
    }

    @Story(AUserStory.class)
    class SomeTestScenario {
        public void a_simple_test_case() {};
        public void should_do_this() {};
        public void should_do_that() {};
    }

    @Story(AFeature.AUserStoryInAFeature.class)
    class SomeOtherTestScenario {
        public void a_simple_test_case() {};
        public void should_do_this() {};
        public void should_do_that() {};
    }

    @Test
    public void the_report_filename_should_be_based_on_the_test_case_name() {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        assertThat(testOutcome.getReportName(), is(Digest.ofTextValue("net.thucydides.core.reports.whennamingthereports_sometestscenario_a_simple_test_case")));
    }

    @Test
    public void the_html_report_filename_should_be_based_on_the_test_case_name() {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        assertThat(testOutcome.getHtmlReport(), is(Digest.ofTextValue("net.thucydides.core.reports.whennamingthereports_sometestscenario_a_simple_test_case") + ".html"));
    }

    @Test
    public void the_report_screenshot_filename_should_be_based_on_the_test_case_name() {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        assertThat(testOutcome.getScreenshotReportName(), is(Digest.ofTextValue("net.thucydides.core.reports.whennamingthereports_sometestscenario_a_simple_test_case") + "_screenshots"));
    }

    @Test
    public void the_report_filename_should_replace_spaces_with_underscores() {

        TestOutcome testOutcome = TestOutcome.forTestInStory("A simple test case", net.thucydides.core.model.Story.from(AUserStory.class));
        String reportName = testOutcome.getReportName(XML);

        assertThat(reportName, is(Digest.ofTextValue("net.thucydides.core.reports.whennamingthereports.auserstory_a_simple_test_case") + ".xml"));
    }

    @Test
    public void the_report_filename_should_be_determined_even_if_no_method_is_named() {

        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        String reportName = testOutcome.getReportName(XML);

        assertThat(reportName, is(Digest.ofTextValue("net.thucydides.core.reports.whennamingthereports_sometestscenario_a_simple_test_case") + ".xml"));
    }

    @Test
    public void the_html_report_filename_should_have_the_html_suffix() {

        TestOutcome testOutcome = new TestOutcome("a_simple_test_case");
        String reportName = testOutcome.getReportName(HTML);
        
        assertThat(reportName, is(Digest.ofTextValue("a_simple_test_case") + ".html"));
    }

    @Test
    public void the_html_report_filename_should_refer_to_the_user_story_name_if_present() {

        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);

        String reportName = testOutcome.getReportName(HTML);
        
        assertThat(reportName, is(Digest.ofTextValue("net.thucydides.core.reports.whennamingthereports_sometestscenario_should_do_this") + ".html"));
    }

    @Test
    public void a_qualifier_can_be_provided_to_distinguish_html_reports_from_other_similar_reports() {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class)
                                             .withQualifier("qualifier");

        String reportName = testOutcome.getReportName(HTML);

        assertThat(reportName, is(Digest.ofTextValue("net.thucydides.core.reports.whennamingthereports_sometestscenario_should_do_this_qualifier") + ".html"));
    }

    @Test
    public void a_data_driven_test_should_have_the_same_report_for_all_examples() {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this[0]", SomeTestScenario.class);

        String reportName = testOutcome.getReportName(HTML);

        assertThat(reportName, is(Digest.ofTextValue("net.thucydides.core.reports.whennamingthereports_sometestscenario_should_do_this") + ".html"));
    }

    @Test
    public void a_null_qualifier_should_be_ignored() {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);

        String reportName = testOutcome.getReportName(HTML);

        assertThat(reportName, is(Digest.ofTextValue("net.thucydides.core.reports.whennamingthereports_sometestscenario_should_do_this") + ".html"));
    }

    @Test
    public void when_no_qualifier_is_provided_the_normal_report_name_is_used() {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);

        String reportName = testOutcome.getReportName(HTML);

        assertThat(reportName, is(Digest.ofTextValue("net.thucydides.core.reports.whennamingthereports_sometestscenario_should_do_this") + ".html"));
    }

    @Test
    public void a_qualifier_can_be_provided_to_distinguish_xml_reports_from_other_similar_reports() {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class)
                                             .withQualifier("qualifier");

        String reportName = testOutcome.getReportName(XML);
        assertThat(reportName, is(Digest.ofTextValue("net.thucydides.core.reports.whennamingthereports_sometestscenario_should_do_this_qualifier") + ".xml"));
    }

    @Test
    public void a_qualifier_should_be_indicated_in_the_test_title() {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class)
                .withQualifier("qualifier");

        assertThat(testOutcome.getTitle(), containsString("qualifier"));
    }

}

