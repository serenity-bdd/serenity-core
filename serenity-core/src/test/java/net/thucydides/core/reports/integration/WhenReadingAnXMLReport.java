package net.thucydides.core.reports.integration;

import com.google.common.base.Optional;
import net.thucydides.core.model.*;
import net.thucydides.core.model.features.ApplicationFeature;
import net.thucydides.core.reports.xml.XMLTestOutcomeReporter;
import net.thucydides.core.util.ExtendedTemporaryFolder;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class WhenReadingAnXMLReport {

    @Rule
    public ExtendedTemporaryFolder temporaryDirectory = new ExtendedTemporaryFolder();

    private XMLTestOutcomeReporter outcomeReporter;

    private File outputDirectory;

    @Before
    public void setupTestReporter() throws IOException {
        outcomeReporter = new XMLTestOutcomeReporter();

        outputDirectory = temporaryDirectory.newFolder();

        outcomeReporter.setOutputDirectory(outputDirectory);
    }

    @Test
    public void should_load_acceptance_test_report_from_xml_file() throws Exception {
        String storedReportXML =
            "<acceptance-test-run title='Should do this' name='should_do_this' description='Some description' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'  timestamp='2013-01-01T00:00:00.000-05:00'>\n"
          + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
            + "  <issues>\n"
            + "    <issue>#456</issue>\n"
            + "    <issue>#789</issue>\n"
            + "    <issue>#123</issue>\n"
            + "  </issues>\n"
          + "  <test-step result='SUCCESS'>\n"
          + "    <description>step 1</description>\n"
          + "  </test-step>\n"
          + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
        assertThat(testOutcome.get().getIssues(), hasItems("#123", "#456", "#789"));
        assertThat(testOutcome.get().getStartTime(), notNullValue());
        assertThat(testOutcome.get().getDescription(), is("Some description"));
    }

    @Test
    public void should_load_manual_acceptance_test_report_from_xml_file() throws Exception {
        String storedReportXML =
                "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'  timestamp='2013-01-01T00:00:00.000-05:00' manual='true'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
                        + "  <issues>\n"
                        + "    <issue>#456</issue>\n"
                        + "    <issue>#789</issue>\n"
                        + "    <issue>#123</issue>\n"
                        + "  </issues>\n"
                        + "  <test-step result='SUCCESS'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
        assertThat(testOutcome.get().isManual(), is(true));
    }

    @Test
    public void should_load_a_qualified_acceptance_test_report_from_xml_file() throws Exception {
        String storedReportXML =
                "<acceptance-test-run title='Should do this [a qualifier]' qualifier='a qualifier' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
                        + "  <issues>\n"
                        + "    <issue>#456</issue>\n"
                        + "    <issue>#789</issue>\n"
                        + "    <issue>#123</issue>\n"
                        + "  </issues>\n"
                        + "  <test-step result='SUCCESS'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
        assertThat(testOutcome.get().getQualifier().get(), is("a qualifier"));
        assertThat(testOutcome.get().getTitle(), containsString("[a qualifier]"));
    }

    @Test
    public void should_unescape_newline_in_the_title_and_qualifier_of_a_qualified_acceptance_test_report_from_xml_file() throws Exception {
        String storedReportXML =
                "<acceptance-test-run title='Should do this [a qualifier with &amp;#10; a new line]' qualifier='a qualifier with &amp;#10; a new line' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
                        + "  <issues>\n"
                        + "    <issue>#456</issue>\n"
                        + "    <issue>#789</issue>\n"
                        + "    <issue>#123</issue>\n"
                        + "  </issues>\n"
                        + "  <test-step result='SUCCESS'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
        assertThat(testOutcome.get().getQualifier().get(), is("a qualifier with \n a new line"));
        assertThat(testOutcome.get().getTitle(), containsString("[a qualifier with \n a new line]"));
    }

    @Test
    public void should_load_tags_from_xml_file() throws Exception {
        String storedReportXML =
        "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0'>\n"
            + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.SomeTestScenarioWithTags' name='Some test scenario with tags' />\n"
            + "  <tags>\n"
            + "    <tag name='important feature' type='feature' />\n"
            + "    <tag name='simple story' type='story' />\n"
            + "  </tags>\n"
            + "  <test-step result='SUCCESS' duration='0'>\n"
            + "    <description>step 1</description>\n"
            + "  </test-step>\n"
            + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
        assertThat(testOutcome.get().getTags().size(), is(2));
    }

    @Test
    public void should_load_example_data_from_xml_file() throws Exception {
        String storedReportXML =
                "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.SomeTestScenarioWithTags' name='Some test scenario with tags' />\n"
                        + "  <tags>\n"
                        + "    <tag name='important feature' type='feature' />\n"
                        + "    <tag name='simple story' type='story' />\n"
                        + "  </tags>\n"
                        + "  <examples>\n"
                        + "    <headers>\n"
                        + "      <header>firstName</header>\n"
                        + "      <header>lastName</header>\n"
                        + "      <header>age</header>\n"
                        + "    </headers>\n"
                        + "    <rows>\n"
                        + "      <row result='FAILURE'>\n"
                        + "        <value>Joe</value>\n"
                        + "        <value>Smith</value>\n"
                        + "        <value>20</value>\n"
                        + "      </row>\n"
                        + "      <row>\n"
                        + "        <value>Jack</value>\n"
                        + "        <value>Jones</value>\n"
                        + "        <value>21</value>\n"
                        + "      </row>\n"
                        + "    </rows>\n"
                        + "  </examples>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
        DataTable table = testOutcome.get().getDataTable();
        assertThat(table.getHeaders(), hasItems("firstName","lastName","age"));
        assertThat(table.getRows().size(), is(2));
        assertThat(table.getRows().get(0).getStringValues(), hasItems("Joe","Smith","20"));
        assertThat(table.getRows().get(0).getResult(), is(TestResult.FAILURE));
        assertThat(table.getRows().get(1).getStringValues(), hasItems("Jack","Jones","21"));
        assertThat(table.getRows().get(1).getResult(), is(TestResult.SUCCESS));
    }


    @Test
    public void should_load_example_data_from_xml_file_with_a_title_and_description() throws Exception {
        String storedReportXML =
                "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.SomeTestScenarioWithTags' name='Some test scenario with tags' />\n"
                        + "  <tags>\n"
                        + "    <tag name='important feature' type='feature' />\n"
                        + "    <tag name='simple story' type='story' />\n"
                        + "  </tags>\n"
                        + "  <examples>\n"
                        + "    <headers>\n"
                        + "      <header>firstName</header>\n"
                        + "      <header>lastName</header>\n"
                        + "      <header>age</header>\n"
                        + "    </headers>\n"
                        + "    <rows>\n"
                        + "      <row result='FAILURE'>\n"
                        + "        <value>Joe</value>\n"
                        + "        <value>Smith</value>\n"
                        + "        <value>20</value>\n"
                        + "      </row>\n"
                        + "      <row>\n"
                        + "        <value>Jack</value>\n"
                        + "        <value>Jones</value>\n"
                        + "        <value>21</value>\n"
                        + "      </row>\n"
                        + "    </rows>\n"
                        + "  </examples>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
        DataTable table = testOutcome.get().getDataTable();
        assertThat(table.getHeaders(), hasItems("firstName","lastName","age"));
        assertThat(table.getRows().size(), is(2));
        assertThat(table.getRows().get(0).getStringValues(), hasItems("Joe","Smith","20"));
        assertThat(table.getRows().get(0).getResult(), is(TestResult.FAILURE));
        assertThat(table.getRows().get(1).getStringValues(), hasItems("Jack","Jones","21"));
        assertThat(table.getRows().get(1).getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void should_load_acceptance_test_report_including_issues() throws Exception {
        String storedReportXML =
            "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
          + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
          + "  <versions>\n"
          + "    <version>Release 1</version>\n"
          + "    <version>Version 1.1</version>\n"
          + "  </versions>\n"
          + "  <test-step result='SUCCESS'>\n"
          + "    <description>step 1</description>\n"
          + "  </test-step>\n"
          + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
        assertThat(testOutcome.get().getTitle(), is("Should do this"));
        assertThat(testOutcome.get().getVersions(),hasItem("Release 1"));
        assertThat(testOutcome.get().getVersions(),hasItem("Version 1.1"));
    }

    @Test
    public void should_load_acceptance_test_report_including_versions() throws Exception {
        String storedReportXML =
                "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
                        + "  <test-step result='SUCCESS'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
        assertThat(testOutcome.get().getTitle(), is("Should do this"));
    }

    @Test
    public void should_load_test_step_details_from_xml_file() throws Exception {
        String storedReportXML =
            "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
          + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
          + "  <test-step result='SUCCESS'>\n"
          + "    <screenshots>"
          + "      <screenshot image='step_1.png' source='step_1.html' />"
          + "    </screenshots>"
          + "    <description>step 1</description>\n"
          + "  </test-step>\n"
          + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);

        TestStep testStep = (TestStep) testOutcome.get().getTestSteps().get(0);
        assertThat(testOutcome.get().getTestSteps().size(), is(1));
        assertThat(testStep.getResult(), is(TestResult.SUCCESS));
        assertThat(testStep.getDescription(), is("step 1"));
        assertThat(testStep.getScreenshots().get(0).getScreenshot().getName(), is("step_1.png"));
        assertThat(testStep.getScreenshots().get(0).getHtmlSource().get().getName(), is("step_1.html"));
    }

    @Test
    public void should_load_test_step_details_with_no_screenshot_source_from_xml_file() throws Exception {
        String storedReportXML =
                "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
                        + "  <test-step result='SUCCESS'>\n"
                        + "    <screenshots>"
                        + "      <screenshot image='step_1.png'/>"
                        + "    </screenshots>"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);

        TestStep testStep = (TestStep) testOutcome.get().getTestSteps().get(0);
        assertThat(testOutcome.get().getTestSteps().size(), is(1));
        assertThat(testStep.getResult(), is(TestResult.SUCCESS));
        assertThat(testStep.getDescription(), is("step 1"));
        assertThat(testStep.getScreenshots().get(0).getScreenshot().getName(), is("step_1.png"));
        assertThat(testStep.getScreenshots().get(0).getHtmlSource().isPresent(), is(false));
    }


    @Test
    public void should_load_user_story_details_from_xml_file() throws Exception {
        String storedReportXML =
            "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
          + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
          + "  <test-step result='SUCCESS'>\n"
          + "    <description>step 1</description>\n"
          + "  </test-step>\n"
          + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
        assertThat(testOutcome.get().getUserStory(), is(Story.withId("net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory", "A user story")));
    }

    @Test
    public void should_load_feature_details_from_xml_file() throws Exception {
        String storedReportXML =
            "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
          + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'>\n"
          + "    <feature id='myapp.myfeatures.SomeFeature' name='Some feature' />\n"
          + "  </user-story>"
          + "  <test-step result='SUCCESS'>\n"
          + "    <description>step 1</description>\n"
          + "  </test-step>\n"
          + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
        testOutcome.get().getFeature();

        ApplicationFeature expectedFeature = new ApplicationFeature("myapp.myfeatures.SomeFeature", "Some feature");
        assertThat(testOutcome.get().getFeature().getId(), is("myapp.myfeatures.SomeFeature"));
        assertThat(testOutcome.get().getFeature().getName(), is("Some feature"));
        assertThat(testOutcome.get().getPath(), is("net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport"));
    }

    @Test
    public void should_load_the_session_id_from_xml_file() throws Exception {
        String storedReportXML =
                  "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' session-id='1234'>\n"
                + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story'>\n"
                + "    <feature id='myapp.myfeatures.SomeFeature' name='Some feature' />\n"
                + "  </user-story>"
                + "  <test-step result='SUCCESS'>\n"
                + "    <description>step 1</description>\n"
                + "  </test-step>\n"
                + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
        assertThat(testOutcome.get().getSessionId(), is("1234"));
    }

    @Test
    public void should_load_the_test_source_from_xml_file() throws Exception {
        String storedReportXML =
                "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' session-id='1234' test-source='JUnit'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story'>\n"
                        + "    <feature id='myapp.myfeatures.SomeFeature' name='Some feature' />\n"
                        + "  </user-story>"
                        + "  <test-step result='SUCCESS'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);
        assertThat(testOutcome.get().getTestSource(), is("JUnit"));
    }
    
    @Test
    public void should_return_null_feature_if_no_feature_is_present() {
        TestOutcome testOutcome = new TestOutcome("aTestMethod");
        assertThat(testOutcome.getFeature(), is(nullValue()));
    }

    @Test
    public void should_load_acceptance_test_report_with_nested_groups_from_xml_file() throws Exception {
        String storedReportXML = 
              "<acceptance-test-run title='A nested test case' name='a_nested_test_case' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
            + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
            + "  <test-group name='Group 1' result='SUCCESS'>\n"
            + "    <test-group name='Group 1.1' result='SUCCESS'>\n"
            + "      <test-group name='Group 1.1.1' result='SUCCESS'>\n"
            + "        <test-step result='SUCCESS'>\n"
            + "          <description>step 1</description>\n"
            + "        </test-step>\n"
            + "      </test-group>\n" 
            + "    </test-group>\n" 
            + "  </test-group>\n" 
            + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);

        assertThat(testOutcome.get().getTitle(), is("A nested test case"));
        
        TestStep group1 = testOutcome.get().getTestSteps().get(0);
        assertThat(testOutcome.get().getTestSteps().size(), is(1));
    }

    @Test
    public void should_load_acceptance_test_report_with_simple_nested_groups_from_xml_file() throws Exception {
        String storedReportXML = 
              "<acceptance-test-run title='A nested test case' name='a_nested_test_case' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
            + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
            + "  <test-group name='Group 1' result='SUCCESS'>\n"
            + "    <test-group name='Group 1.1' result='SUCCESS'>\n"
            + "      <test-step result='SUCCESS'>\n"
            + "        <description>step 1</description>\n"
            + "      </test-step>\n"
            + "    </test-group>\n" 
            + "  </test-group>\n" 
            + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);

        assertThat(testOutcome.get().getTitle(), is("A nested test case"));
        
        TestStep group1 = testOutcome.get().getTestSteps().get(0);
        assertThat(testOutcome.get().getTestSteps().size(), is(1));
    }


    @Test
    public void should_load_acceptance_test_report_with_multiple_test_steps_from_xml_file() throws Exception {
        String storedReportXML =
                  "<acceptance-test-run title='A simple test case' name='a_simple_test_case' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
                + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
                + "  <test-step result='SUCCESS'>\n"
                + "    <description>step 1</description>\n"
                + "  </test-step>\n"
                + "  <test-step result='FAILURE'>\n"
                + "    <description>step 2</description>\n"
                + "  </test-step>\n"
                + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);

        assertThat(testOutcome.get().getTitle(), is("A simple test case"));
        assertThat(testOutcome.get().getTestSteps().size(), is(2));
        assertThat(testOutcome.get().getTestSteps().get(0).getResult(), is(TestResult.SUCCESS));
        assertThat(testOutcome.get().getTestSteps().get(0).getDescription(), is("step 1"));
        assertThat(testOutcome.get().getTestSteps().get(1).getResult(), is(TestResult.FAILURE));
        assertThat(testOutcome.get().getTestSteps().get(1).getDescription(), is("step 2"));
    }

    @Test
    public void should_load_qualified_acceptance_test_report_with_a_qualified_name() throws Exception {
        String storedReportXML =
                "<acceptance-test-run title='Search for news [euro]' name='searchForNews[0]' qualifier='euro' steps='0' successful='0' failures='0' skipped='0' ignored='0' pending='0' result='PENDING' duration='85'>\n"+
                        "  <user-story id='samples.ParametrizedTest' name='Parametrized test'/>\n"+
                        "  <tags>\n"+
                        "    <tag name='Parametrized test' type='story'/>\n"+
                        "  </tags>\n"+
                        "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        Optional<TestOutcome> testOutcome = outcomeReporter.loadReportFrom(report);

        assertThat(testOutcome.get().getTitle(), is("Search for news [euro]"));
        assertThat(testOutcome.get().getTitleWithLinks(), is("Search for news [euro]"));
    }

}
