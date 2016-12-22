package net.thucydides.core.reports.integration;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.thucydides.core.annotations.*;
import net.thucydides.core.annotations.Story;
import net.thucydides.core.digest.Digest;
import net.thucydides.core.model.*;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.xml.XMLTestOutcomeReporter;
import net.thucydides.core.screenshots.ScreenshotAndHtmlSource;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.ExtendedTemporaryFolder;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sample.steps.FailingStep;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static net.thucydides.core.hamcrest.XMLMatchers.isSimilarTo;
import static net.thucydides.core.reports.integration.TestStepFactory.successfulTestStepCalled;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class WhenGeneratingAnXMLReport {

    private AcceptanceTestReporter reporter;

    @Rule
    public ExtendedTemporaryFolder temporaryDirectory = new ExtendedTemporaryFolder();

    private File outputDirectory;

    @Mock
    TestOutcomes allTestOutcomes;
    
    @Before
    public void setupTestReporter() throws IOException {
        
        MockitoAnnotations.initMocks(this);
        
        reporter = new XMLTestOutcomeReporter();
        outputDirectory = temporaryDirectory.newFolder("temp");
        reporter.setOutputDirectory(outputDirectory);
    }

    class AUserStory {
    }

    @Story(AUserStory.class)
    class SomeTestScenario {
        public void a_simple_test_case() {
        }

        public void should_do_this() {
        }

        public void should_do_that() {
        }
    }

    @WithTag(name="important feature", type = "feature")
    class SomeTestScenarioWithTags {
        public void a_simple_test_case() {
        }

        @WithTag(name="simple story",type = "story")
        public void should_do_this() {
        }

        public void should_do_that() {
        }
    }

    @Feature
    class AFeature {
        class AUserStoryInAFeature {
        }
    }

    @Story(AFeature.AUserStoryInAFeature.class)
    class SomeTestScenarioInAFeature {
        public void should_do_this() {
        }

        public void should_do_that() {
        }
    }

    class ATestScenarioWithoutAStory {
        public void should_do_this() {
        }

        public void should_do_that() {
        }

        public void and_should_do_that() {
        }
    }

    @Story(AUserStory.class)
    @Issues({"#123", "#456"})
    class ATestScenarioWithIssues {
        public void a_simple_test_case() {
        }

        @Issue("#789")
        public void should_do_this() {
        }

        public void should_do_that() {
        }
    }


    @Test
    public void should_get_tags_from_user_story_if_present() {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);
        assertThat(testOutcome.getTags(), hasItem(TestTag.withName("When generating an XML report/A user story").andType("story")));
    }


    @Test
    public void should_get_tags_from_user_stories_and_features_if_present() {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioInAFeature.class);
        assertThat(testOutcome.getTags(), allOf(hasItem(TestTag.withName("A feature/A user story in a feature").andType("story")),
                                                hasItem(TestTag.withName("A feature").andType("feature"))));
    }

    @Test
    public void should_get_tags_using_tag_annotations_if_present() {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioWithTags.class);
        assertThat(testOutcome.getTags(), allOf(hasItem(TestTag.withName("important feature").andType("feature")),
                hasItem(TestTag.withName("simple story").andType("story"))));
    }

    @Test
    public void should_add_a_story_tag_based_on_the_class_name_if_nothing_else_is_specified() {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", ATestScenarioWithoutAStory.class);
        assertThat(testOutcome.getTags(), hasItem(TestTag.withName("When generating an XML report/A test scenario without a story").andType("story")));
    }


    @Test
    public void should_generate_an_XML_report_for_an_acceptance_test_run()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        // Approval tests instead of hard-coded text? Okey Dokey - approval testing tool
        // Approval tests - check the raw XML structure without the attributes
        String expectedReport =
                "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <tags>\n"
                        + "    <tag name='When generating an XML report/A user story' type='story'/>\n"
                        + "  </tags>"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        testOutcome.recordStep(successfulTestStepCalled("step 1"));

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        // Apache Digester?
        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }

    @Test
    public void should_generate_an_XML_report_for_a_manual_acceptance_test_run() throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class).asManualTest();
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);
        testOutcome.setDescription("Some description");

        String expectedReport =
                "<acceptance-test-run title='Should do this' name='should_do_this' description='Some description' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0' timestamp='2013-01-01T00:00:00.000-05:00' manual='true'>\n"
                        + "  <tags>\n"
                        + "    <tag name=\"Manual\" type=\"External Tests\"/>"
                        + "    <tag name='When generating an XML report/A user story' type='story'/>\n"
                        + "  </tags>"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        testOutcome.recordStep(successfulTestStepCalled("step 1"));

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }

    @Test
    public void should_generate_an_XML_report_for_an_acceptance_test_run_with_a_table()
            throws Exception {

        List<Object> row1 = new ArrayList<Object>(); row1.addAll(Lists.newArrayList("Joe", "Smith", "20"));
        List<Object> row2 = new ArrayList<Object>(); row2.addAll(Lists.newArrayList("Jack", "Jones", "21"));

        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        DataTable table = DataTable.withHeaders(ImmutableList.of("firstName","lastName","age")).
                                    andRows(ImmutableList.of(row1, row2)).build();
        testOutcome.useExamplesFrom(table);
        table.row(0).hasResult(TestResult.FAILURE);
        String expectedReport =
                "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <tags>\n"
                        + "    <tag name='When generating an XML report/A user story' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <examples>\n"
                        + "    <datasets>\n"
                        + "      <dataset startRow='0' rowCount='0'/>\n"
                        + "    </datasets>\n"
                        + "    <headers>\n"
                        + "      <header>firstName</header>\n"
                        + "      <header>lastName</header>\n"
                        + "      <header>age</header>\n"
                        + "    </headers>\n"
                        + "    <rows>\n"
                        + "      <row result=\"FAILURE\">\n"
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
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        testOutcome.recordStep(successfulTestStepCalled("step 1"));

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }

    @Test
    public void should_generate_an_XML_report_for_an_acceptance_test_run_with_a_table_with_a_title_and_description()
            throws Exception {

        List<Object> row1 = new ArrayList<Object>(); row1.addAll(Lists.newArrayList("Joe", "Smith", "20"));
        List<Object> row2 = new ArrayList<Object>(); row2.addAll(Lists.newArrayList("Jack", "Jones", "21"));

        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        DataTable table = DataTable.withHeaders(ImmutableList.of("firstName","lastName","age")).
                andRows(ImmutableList.of(row1, row2)).andTitle("a title").andDescription("some description").build();
        testOutcome.useExamplesFrom(table);
        table.row(0).hasResult(TestResult.FAILURE);
        String expectedReport =
                "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <tags>\n"
                        + "    <tag name='When generating an XML report/A user story' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <examples>\n"
                        + "    <datasets>\n"
                        + "      <dataset startRow='0' rowCount='0' name='a title' description='some description'/>\n"
                        + "    </datasets>\n"
                        + "    <headers>\n"
                        + "      <header>firstName</header>\n"
                        + "      <header>lastName</header>\n"
                        + "      <header>age</header>\n"
                        + "    </headers>\n"
                        + "    <rows>\n"
                        + "      <row result=\"FAILURE\">\n"
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
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        testOutcome.recordStep(successfulTestStepCalled("step 1"));

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }

    @Test
    public void should_generate_an_XML_report_for_an_acceptance_test_run_with_a_qualifier()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class).withQualifier("a qualifier");
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='Should do this [a qualifier]' name='should_do_this' qualifier='a qualifier' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <tags>\n"
                        + "    <tag name='When generating an XML report/A user story' type='story'/>\n"
                        + "  </tags>"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        testOutcome.recordStep(successfulTestStepCalled("step 1"));

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }

    @Test
    public void should_escape_new_lines_in_title_and_qualifier_attributes()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class).withQualifier("a qualifier with \n a new line");
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='Should do this [a qualifier with &amp;#10;" +
                        " a new line]' name='should_do_this' qualifier='a qualifier with &amp;#10;" +
                        " a new line' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <tags>\n"
                        + "    <tag name='When generating an XML report/A user story' type='story'/>\n"
                        + "  </tags>"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        testOutcome.recordStep(successfulTestStepCalled("step 1"));

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }


    @Test
    public void should_store_tags_in_the_XML_reports()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioWithTags.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.SomeTestScenarioWithTags' name='Some test scenario with tags' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <tags>\n"
                        + "    <tag name='important feature' type='feature' />\n"
                        + "    <tag name='simple story' type='story' />\n"
                        + "    <tag name='When generating an XML report/Some test scenario with tags' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        testOutcome.recordStep(successfulTestStepCalled("step 1"));

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }

    @Test
    public void should_include_the_session_id_if_provided_in_the_XML_report()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0'   timestamp='2013-01-01T00:00:00.000-05:00' session-id='1234'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <tags>\n"
                        + "    <tag name='When generating an XML report/A user story' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        testOutcome.setSessionId("1234");
        testOutcome.recordStep(successfulTestStepCalled("step 1"));

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }

    @Test
    public void should_include_issues_in_the_XML_report()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", ATestScenarioWithIssues.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);
        String expectedReport =
                "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0'  timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <issues>\n"
                        + "    <issue>#789</issue>\n"
                        + "    <issue>#123</issue>\n"
                        + "    <issue>#456</issue>\n"
                        + "  </issues>\n"
                        + "  <tags>\n"
                        + "    <tag name='When generating an XML report/A user story' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        testOutcome.recordStep(successfulTestStepCalled("step 1"));

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport, "timestamp"));
    }

    @Test
    public void should_include_versions_in_the_XML_report()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", ATestScenarioWithIssues.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);
        String expectedReport =
                "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0'  timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <issues>\n"
                        + "    <issue>#789</issue>\n"
                        + "    <issue>#123</issue>\n"
                        + "    <issue>#456</issue>\n"
                        + "  </issues>\n"
                        + "  <versions>\n"
                        + "    <version>Release 1</version>\n"
                        + "    <version>Version 1.1</version>\n"
                        + "  </versions>\n"
                        + "  <tags>\n"
                        + "    <tag name='When generating an XML report/A user story' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        testOutcome.recordStep(successfulTestStepCalled("step 1"));
        testOutcome.addVersion("Release 1").addVersion("Version 1.1");

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport, "timestamp"));
    }
    @Test
    public void the_xml_report_should_contain_the_feature_if_provided()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioInAFeature.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AFeature.AUserStoryInAFeature' name='A user story in a feature' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AFeature'>\n"
                        + "    <feature id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AFeature' name='A feature'/>\n"
                        + "  </user-story>\n"
                        + "  <tags>\n"
                        + "    <tag name='A feature' type='feature'/>\n"
                        + "    <tag name='A feature/A user story in a feature' type='story'/>\n"
                        + "  </tags>"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        testOutcome.recordStep(successfulTestStepCalled("step 1"));

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }

    @Test
    public void the_xml_report_should_record_features_and_stories_as_tags()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenarioInAFeature.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AFeature.AUserStoryInAFeature' name='A user story in a feature' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AFeature'>\n"
                        + "    <feature id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AFeature' name='A feature'/>\n"
                        + "  </user-story>\n"
                        + "  <tags>\n"
                        + "    <tag name='A feature' type='feature' />\n"
                        + "    <tag name='A feature/A user story in a feature' type='story' />\n"
                        + "  </tags>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        testOutcome.recordStep(successfulTestStepCalled("step 1"));

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }


    @Test
    public void should_generate_a_qualified_XML_report_for_an_acceptance_test_run_if_the_qualifier_is_specified() throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='A simple test case [qualifier]' name='a_simple_test_case' qualifier='qualifier' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <tags>\n"
                        + "    <tag name='When generating an XML report/A user story' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        testOutcome.recordStep(successfulTestStepCalled("step 1"));

        reporter.setQualifier("qualifier");
        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }

    @Test
    public void should_generate_a_qualified_XML_report_with_formatted_parameters_if_the_qualifier_is_specified()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='A simple test case [a_b]' name='a_simple_test_case' qualifier='a_b' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <tags>\n"
                        + "    <tag name='When generating an XML report/A user story' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        testOutcome.recordStep(successfulTestStepCalled("step 1"));

        reporter.setQualifier("a_b");
        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }


    @Test
    public void should_generate_an_XML_report_with_a_name_based_on_the_test_run_title()
            throws Exception {
        TestOutcome testOutcome = new TestOutcome("a_simple_test_case");
        File xmlReport = reporter.generateReportFor(testOutcome);

        assertThat(xmlReport.getName(), is(Digest.ofTextValue("a_simple_test_case") + ".xml"));
    }

    @Test
    public void should_generate_an_XML_report_in_the_target_directory() throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);

        File xmlReport = reporter.generateReportFor(testOutcome);

        assertThat(xmlReport.getPath(), startsWith(outputDirectory.getPath()));
    }

    @Test
    public void should_count_the_total_number_of_steps_with_each_outcome_in_acceptance_test_run()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='A simple test case' name='a_simple_test_case' steps='9' successful='2' failures='2' errors='1' skipped='1' ignored='2' pending='1' result='ERROR' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <tags>\n"
                        + "    <tag name='When generating an XML report/A user story' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "  <test-step result='IGNORED' duration='0'>\n"
                        + "    <description>step 2</description>\n"
                        + "  </test-step>\n"
                        + "  <test-step result='IGNORED' duration='0'>\n"
                        + "    <description>step 3</description>\n"
                        + "  </test-step>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <description>step 4</description>\n"
                        + "  </test-step>\n"
                        + "  <test-step result='FAILURE' duration='0'>\n"
                        + "    <description>step 5</description>\n"
                        + "    <error>Unspecified failure</error>\n"
                        + "  </test-step>\n"
                        + "  <test-step result='FAILURE' duration='0'>\n"
                        + "    <description>step 6</description>\n"
                        + "    <error>Unspecified failure</error>\n"
                        + "  </test-step>\n"
                        + "  <test-step result='ERROR' duration='0'>\n"
                        + "    <description>step 7</description>\n"
                        + "    <error>Unspecified failure</error>\n"
                        + "  </test-step>\n"
                        + "  <test-step result='SKIPPED' duration='0'>\n"
                        + "    <description>step 8</description>\n"
                        + "  </test-step>\n"
                        + "  <test-step result='PENDING' duration='0'>\n"
                        + "    <description>step 9</description>\n"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        testOutcome.recordStep(successfulTestStepCalled("step 1"));
        testOutcome.recordStep(TestStepFactory.ignoredTestStepCalled("step 2"));
        testOutcome.recordStep(TestStepFactory.ignoredTestStepCalled("step 3"));
        testOutcome.recordStep(successfulTestStepCalled("step 4"));
        testOutcome.recordStep(TestStepFactory.failingTestStepCalled("step 5"));
        testOutcome.recordStep(TestStepFactory.failingTestStepCalled("step 6"));
        testOutcome.recordStep(TestStepFactory.errorTestStepCalled("step 7"));
        testOutcome.recordStep(TestStepFactory.skippedTestStepCalled("step 8"));
        testOutcome.recordStep(TestStepFactory.pendingTestStepCalled("step 9"));

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }


    @Story(AUserStory.class)
    class SomeNestedTestScenario {
        public void a_nested_test_case() {
        }

        ;

        public void should_do_this() {
        }

        ;

        public void should_do_that() {
        }

        ;
    }

    @Test
    public void should_record_test_groups_as_nested_structures()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='A nested test case' name='a_nested_test_case' steps='3' successful='3' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <tags>\n"
                        + "    <tag name='When generating an XML report/A user story' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <test-group name='Group 1' result='SUCCESS'>\n"
                        + "    <test-step result='SUCCESS' duration='0'>\n"
                        + "      <description>step 1</description>\n"
                        + "    </test-step>\n"
                        + "    <test-step result='SUCCESS' duration='0'>\n"
                        + "      <description>step 2</description>\n"
                        + "    </test-step>\n"
                        + "    <test-step result='SUCCESS' duration='0'>\n"
                        + "      <description>step 3</description>\n"
                        + "    </test-step>\n"
                        + "  </test-group>\n"
                        + "</acceptance-test-run>";

        testOutcome.startGroup("Group 1");
        testOutcome.recordStep(successfulTestStepCalled("step 1"));
        testOutcome.recordStep(successfulTestStepCalled("step 2"));
        testOutcome.recordStep(successfulTestStepCalled("step 3"));
        testOutcome.endGroup();

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }

    @Test
    public void should_record_nested_test_groups_as_nested_structures()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='A nested test case' name='a_nested_test_case' steps='5' successful='5' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <tags>\n"
                        + "    <tag name='When generating an XML report/A user story' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <test-group name='Group 1' result='SUCCESS'>\n"
                        + "    <test-step result='SUCCESS' duration='0'>\n"
                        + "      <description>step 1</description>\n"
                        + "    </test-step>\n"
                        + "    <test-step result='SUCCESS' duration='0'>\n"
                        + "      <description>step 2</description>\n"
                        + "    </test-step>\n"
                        + "    <test-step result='SUCCESS' duration='0'>\n"
                        + "      <description>step 3</description>\n"
                        + "    </test-step>\n"
                        + "    <test-group name='Group 1.1' result='SUCCESS'>\n"
                        + "      <test-step result='SUCCESS' duration='0'>\n"
                        + "        <description>step 4</description>\n"
                        + "      </test-step>\n"
                        + "      <test-step result='SUCCESS' duration='0'>\n"
                        + "        <description>step 5</description>\n"
                        + "      </test-step>\n"
                        + "    </test-group>\n"
                        + "  </test-group>\n"
                        + "</acceptance-test-run>";

        testOutcome.startGroup("Group 1");
        testOutcome.recordStep(successfulTestStepCalled("step 1"));
        testOutcome.recordStep(successfulTestStepCalled("step 2"));
        testOutcome.recordStep(successfulTestStepCalled("step 3"));
        testOutcome.startGroup("Group 1.1");
        testOutcome.recordStep(successfulTestStepCalled("step 4"));
        testOutcome.recordStep(successfulTestStepCalled("step 5"));
        testOutcome.endGroup();
        testOutcome.endGroup();

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }

    @Test
    public void should_record_minimal_nested_test_groups_as_nested_structures()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='A nested test case' name='a_nested_test_case' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <tags>\n"
                        + "    <tag name='When generating an XML report/A user story' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <test-group name='Group 1' result='SUCCESS'>\n"
                        + "    <test-group name='Group 1.1' result='SUCCESS'>\n"
                        + "      <test-group name='Group 1.1.1' result='SUCCESS'>\n"
                        + "        <test-step result='SUCCESS' duration='0'>\n"
                        + "          <description>step 1</description>\n"
                        + "        </test-step>\n"
                        + "      </test-group>\n"
                        + "    </test-group>\n"
                        + "  </test-group>\n"
                        + "</acceptance-test-run>";

        testOutcome.startGroup("Group 1");
        testOutcome.startGroup("Group 1.1");
        testOutcome.startGroup("Group 1.1.1");
        testOutcome.recordStep(successfulTestStepCalled("step 1"));
        testOutcome.endGroup();
        testOutcome.endGroup();
        testOutcome.endGroup();

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }

    @Test
    public void should_record_minimal_nested_test_steps_as_nested_structures()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_nested_test_case", SomeNestedTestScenario.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='A nested test case' name='a_nested_test_case' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <tags>\n"
                        + "    <tag name='When generating an XML report/A user story' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <test-group name='Group 1' result='SUCCESS'>\n"
                        + "    <test-group name='Group 1.1' result='SUCCESS'>\n"
                        + "      <test-group name='Group 1.1.1' result='SUCCESS'>\n"
                        + "        <test-step result='SUCCESS' duration='0'>\n"
                        + "          <description>step 1</description>\n"
                        + "        </test-step>\n"
                        + "      </test-group>\n"
                        + "    </test-group>\n"
                        + "  </test-group>\n"
                        + "</acceptance-test-run>";

        testOutcome.startGroup("Group 1");
        testOutcome.startGroup("Group 1.1");
        testOutcome.startGroup("Group 1.1.1");
        testOutcome.recordStep(successfulTestStepCalled("step 1"));
        testOutcome.endGroup();
        testOutcome.endGroup();
        testOutcome.endGroup();

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }

    @Test
    public void should_include_the_name_of_any_screenshots_where_present() throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        DateTime startTime = new DateTime(2013,1,1,0,0,0,0);
        testOutcome.setStartTime(startTime);

        String expectedReport =
                "<acceptance-test-run title='A simple test case' name='a_simple_test_case' steps='2' successful='1' failures='1' skipped='0' ignored='0' pending='0' result='FAILURE' duration='0' timestamp='2013-01-01T00:00:00.000-05:00'>\n"
                        + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' path='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport'/>\n"
                        + "  <tags>\n"
                        + "    <tag name='When generating an XML report/A user story' type='story'/>\n"
                        + "  </tags>\n"
                        + "  <test-step result='SUCCESS' duration='0'>\n"
                        + "    <screenshots>\n"
                        + "      <screenshot image='step_1.png' source='step_1.html'/>\n"
                        + "    </screenshots>\n"
                        + "    <description>step 1</description>\n"
                        + "  </test-step>\n"
                        + "  <test-step result='FAILURE' duration='0'>\n"
                        + "    <description>step 2</description>\n"
                        + "    <error>Unspecified failure</error>"
                        + "  </test-step>\n"
                        + "</acceptance-test-run>";

        File screenshot = temporaryDirectory.newFile("step_1.png");
        File source = temporaryDirectory.newFile("step_1.html");

        TestStep step1 = successfulTestStepCalled("step 1");
        step1.addScreenshot(new ScreenshotAndHtmlSource(screenshot, source));
        testOutcome.recordStep(step1);
        testOutcome.recordStep(TestStepFactory.failingTestStepCalled("step 2"));

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, isSimilarTo(expectedReport,"timestamp"));
    }

    @Test
    public void should_include_error_message_for_failing_test()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);

        TestStep step = TestStepFactory.failingTestStepCalled("step 1");
        step.failedWith(new IllegalArgumentException("Oh nose!"));

        testOutcome.recordStep(step);

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, containsString("<error>java.lang.IllegalArgumentException: Oh nose!</error>"));
    }

    @Test
    public void should_include_exception_stack_dump_for_failing_test()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);

        TestStep step = TestStepFactory.failingTestStepCalled("step 1");
        step.failedWith(new FailingStep().failsWithMessage("Oh nose!"));

        testOutcome.recordStep(step);

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, containsString("sample.steps.FailingStep"));
    }

    @Test
    public void should_include_test_source()
            throws Exception {
        TestOutcome testOutcome = TestOutcome.forTest("a_simple_test_case", SomeTestScenario.class);
        testOutcome.setTestSource(StepEventBus.TEST_SOURCE_JUNIT);

        TestStep step = TestStepFactory.failingTestStepCalled("step 1");
        step.failedWith(new FailingStep().failsWithMessage("Oh nose!"));

        testOutcome.recordStep(step);

        File xmlReport = reporter.generateReportFor(testOutcome);
        String generatedReportText = getStringFrom(xmlReport);

        assertThat(generatedReportText, containsString(StepEventBus.TEST_SOURCE_JUNIT));
    }

    private String getStringFrom(File reportFile) throws IOException {
        return FileUtils.readFileToString(reportFile);
    }

}
