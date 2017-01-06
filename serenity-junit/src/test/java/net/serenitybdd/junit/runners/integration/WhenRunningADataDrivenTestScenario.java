package net.serenitybdd.junit.runners.integration;

import com.google.common.collect.Lists;
import net.serenitybdd.junit.runners.ParameterizedTestsOutcomeAggregator;
import net.serenitybdd.junit.runners.SerenityParameterizedRunner;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.batches.BatchManager;
import net.thucydides.core.batches.BatchManagerProvider;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.junit.annotations.Concurrent;
import net.thucydides.junit.annotations.TestData;
import net.thucydides.junit.rules.QuietThucydidesLoggingRule;
import net.thucydides.junit.rules.SaveWebdriverSystemPropertiesRule;
import net.thucydides.samples.*;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static ch.lambdaj.Lambda.filter;
import static net.thucydides.core.steps.stepdata.StepData.withTestDataFrom;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class WhenRunningADataDrivenTestScenario {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Rule
    public SaveWebdriverSystemPropertiesRule saveWebdriverSystemPropertiesRule = new SaveWebdriverSystemPropertiesRule();

    @Rule
    public QuietThucydidesLoggingRule quietThucydidesLoggingRule = new QuietThucydidesLoggingRule();

    MockEnvironmentVariables environmentVariables;

    Configuration configuration;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        environmentVariables = new MockEnvironmentVariables();
        configuration = new SystemPropertiesConfiguration(environmentVariables);
    }

    @Test
    public void a_data_driven_test_driver_should_run_one_test_per_row_of_data() throws Throwable  {

        SerenityParameterizedRunner runner = getStubbedTestRunnerUsing(SampleDataDrivenScenario.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = ParameterizedTestsOutcomeAggregator.from(runner).getTestOutcomesForAllParameterSets();
        assertThat(executedScenarios.size(), is(36));
    }

    @Test
    public void manual_data_driven_tests_should_be_allowed() throws Throwable  {

        SerenityParameterizedRunner runner = getStubbedTestRunnerUsing(AddDifferentSortsOfTodos.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = ParameterizedTestsOutcomeAggregator.from(runner).getTestOutcomesForAllParameterSets();
        assertThat(executedScenarios.size(), is(4));
    }

    @Test
    public void a_data_driven_test_driver_should_aggregate_test_outcomes() throws Throwable  {

        SerenityParameterizedRunner runner = getStubbedTestRunnerUsing(SampleDataDrivenScenario.class);
        runner.run(new RunNotifier());

        List<TestOutcome> aggregatedScenarios = ParameterizedTestsOutcomeAggregator.from(runner).aggregateTestOutcomesByTestMethods();
        assertThat(aggregatedScenarios.size(), is(3));
        assertThat(aggregatedScenarios.get(0).getStepCount(), is(12));
        assertThat(aggregatedScenarios.get(1).getStepCount(), is(12));
    }

    @Test
    public void a_data_driven_test_driver_should_record_a_sample_scenario() throws Throwable  {

        SerenityParameterizedRunner runner = getStubbedTestRunnerUsing(SampleDataDrivenScenario.class);
        runner.run(new RunNotifier());

        List<TestOutcome> aggregatedScenarios = ParameterizedTestsOutcomeAggregator.from(runner).aggregateTestOutcomesByTestMethods();
        assertThat(aggregatedScenarios.get(0).getDataDrivenSampleScenario(), containsString("Step with parameters: <Parameter 1>, <Parameter 2>\n" +
                "Step that succeeds\n" +
                "Another step that succeeds"));
    }

    @Test
    public void a_data_driven_test_driver_should_record_a_table_of_example() throws Throwable  {

        SerenityParameterizedRunner runner = getStubbedTestRunnerUsing(SampleSingleDataDrivenScenario.class);
        runner.run(new RunNotifier());

        List<TestOutcome> aggregatedScenarios = ParameterizedTestsOutcomeAggregator.from(runner).aggregateTestOutcomesByTestMethods();
        assertThat(aggregatedScenarios.size(), is(1));
        assertThat(aggregatedScenarios.get(0).getStepCount(), is(15));
    }

    @Test
    public void a_data_driven_test_with_a_failing_assumption_should_be_ignored() throws Throwable  {

        SerenityParameterizedRunner runner = getStubbedTestRunnerUsing(SampleSingleDataDrivenScenarioWithFailingAssumption.class);
        runner.run(new RunNotifier());

        List<TestOutcome> aggregatedScenarios = ParameterizedTestsOutcomeAggregator.from(runner).aggregateTestOutcomesByTestMethods();
        assertThat(aggregatedScenarios.size(), is(1));
        assertThat(aggregatedScenarios.get(0).getStepCount(), is(15));
        for(TestStep step :aggregatedScenarios.get(0).getTestSteps()) {
            assertThat(step.getResult(), is(TestResult.IGNORED));
        }
    }

    @Test
    public void a_data_driven_test_driver_should_aggregate_test_outcomes_without_steps() throws Throwable  {

        SerenityParameterizedRunner runner = getStubbedTestRunnerUsing(SimpleSuccessfulParametrizedTestSample.class);
        runner.run(new RunNotifier());

        List<TestOutcome> aggregatedScenarios = ParameterizedTestsOutcomeAggregator.from(runner).aggregateTestOutcomesByTestMethods();
        assertThat(aggregatedScenarios.size(), is(2));
        assertThat(aggregatedScenarios.get(0).getStepCount(), is(3));
        assertThat(aggregatedScenarios.get(1).getStepCount(), is(3));
    }

    @Test
    public void data_driven_tests_should_pass_even_if_no_steps_are_called() throws Throwable  {

        SerenityParameterizedRunner runner = getStubbedTestRunnerUsing(SimpleSuccessfulParametrizedTestSample.class);
        runner.run(new RunNotifier());

        List<TestOutcome> aggregatedScenarios = ParameterizedTestsOutcomeAggregator.from(runner).aggregateTestOutcomesByTestMethods();

        assertThat(aggregatedScenarios.get(0).getResult(), is(TestResult.SUCCESS));
        assertThat(aggregatedScenarios.get(1).getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void an_ignored_data_driven_test_should_have_result_status_as_ignored() throws Throwable  {

        SerenityParameterizedRunner runner = getStubbedTestRunnerUsing(SampleDataDrivenIgnoredScenario.class);
        runner.run(new RunNotifier());

        List<TestOutcome> aggregatedScenarios = ParameterizedTestsOutcomeAggregator.from(runner).aggregateTestOutcomesByTestMethods();
        assertThat(aggregatedScenarios.size(), is(1));
        assertThat(aggregatedScenarios.get(0).getResult(), is(TestResult.IGNORED));
    }

    @Test
    public void an_ignored_data_driven_test_should_have_a_step_for_each_row() throws Throwable  {

        SerenityParameterizedRunner runner = getStubbedTestRunnerUsing(SampleDataDrivenIgnoredScenario.class);
        runner.run(new RunNotifier());

        List<TestOutcome> aggregatedScenarios = ParameterizedTestsOutcomeAggregator.from(runner).aggregateTestOutcomesByTestMethods();
        assertThat(aggregatedScenarios.size(), is(1));
        assertThat(aggregatedScenarios.get(0).getTestSteps().size(), is(10));
    }

    @Test
    public void a_pending_data_driven_test_should_have_result_status_as_pending() throws Throwable  {

        SerenityParameterizedRunner runner = getStubbedTestRunnerUsing(SampleDataDrivenPendingScenario.class);
        runner.run(new RunNotifier());

        List<TestOutcome> aggregatedScenarios = ParameterizedTestsOutcomeAggregator.from(runner).aggregateTestOutcomesByTestMethods();
        assertThat(aggregatedScenarios.size(), is(1));
        assertThat(aggregatedScenarios.get(0).getResult(), is(TestResult.PENDING));
    }

    @Test
    public void a_pending_data_driven_test_should_have_a_test_step_for_each_row() throws Throwable  {

        SerenityParameterizedRunner runner = getStubbedTestRunnerUsing(SampleDataDrivenPendingScenario.class);
        runner.run(new RunNotifier());

        List<TestOutcome> aggregatedScenarios = ParameterizedTestsOutcomeAggregator.from(runner).aggregateTestOutcomesByTestMethods();
        assertThat(aggregatedScenarios.size(), is(1));
        assertThat(aggregatedScenarios.get(0).getTestSteps().size(), is(10));
    }

    @Test
    public void a_data_driven_test_should_also_be_able_to_use_data_from_a_CSV_file() throws Throwable  {

        SerenityParameterizedRunner runner = getTestRunnerUsing(SampleCSVDataDrivenScenario.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = ParameterizedTestsOutcomeAggregator.from(runner).getTestOutcomesForAllParameterSets();

        assertThat(executedScenarios.size(), is(24));
    }


    @Test
    public void a_separate_json_report_should_be_generated_for_each_scenario() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        environmentVariables.setProperty(ThucydidesSystemProperty.THUCYDIDES_OUTPUT_DIRECTORY.getPropertyName(),
                                         outputDirectory.getAbsolutePath());

        SerenityParameterizedRunner runner = getTestRunnerUsing(SampleDataDrivenScenario.class);

        runner.run(new RunNotifier());

        File[] reports = reload(outputDirectory).listFiles(new JSONFileFilter());
        assertThat(reports.length, is(3));
    }

    @Test
    public void a_separate_json_report_should_be_generated_for_each_scenario_when_using_data_from_a_CSV_file() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        environmentVariables.setProperty(ThucydidesSystemProperty.THUCYDIDES_OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        SerenityParameterizedRunner runner = getTestRunnerUsing(SampleCSVDataDrivenScenario.class);

        runner.run(new RunNotifier());

        File[] reports = reload(outputDirectory).listFiles(new JSONFileFilter());
        assertThat(reports.length, is(2));
    }

    @Test
    public void json_report_contents_should_reflect_the_test_data_from_the_csv_file() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        environmentVariables.setProperty(ThucydidesSystemProperty.THUCYDIDES_OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        SerenityParameterizedRunner runner = getTestRunnerUsing(SampleCSVDataDrivenScenario.class);

        runner.run(new RunNotifier());

        List<String> reportContents = contentsOf(reload(outputDirectory).listFiles(new JSONFileFilter()));

        assertThat(reportContents, hasItemContainsString("Jack Black"));
        assertThat(reportContents, hasItemContainsString("Joe Smith"));
    }

    private Matcher<? super List<String>> hasItemContainsString(String expectedValue) {
        return new HasItemContainsString(expectedValue);
    }

    private static class HasItemContainsString extends TypeSafeMatcher<List<String>> {

        private final String expectedValue;

        private HasItemContainsString(String expectedValue) {
            this.expectedValue = expectedValue;
        }

        @Override
        protected boolean matchesSafely(List<String> values) {
            return !filter(containsString(expectedValue), values).isEmpty();
        }

        public void describeTo(Description description) {
            description.appendText("Expecting a list containing a string that contains ").appendValue(expectedValue);
        }
    }


    @Test
    public void when_test_data_is_provided_for_a_step_a_single_test_should_be_executed() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        environmentVariables.setProperty(ThucydidesSystemProperty.THUCYDIDES_OUTPUT_DIRECTORY.getPropertyName(),
                                         outputDirectory.getAbsolutePath());

        SerenityRunner runner = getNormalTestRunnerUsing(SamplePassingScenarioWithTestSpecificData.class);

        runner.run(new RunNotifier());

        List reportContents = contentsOf(reload(outputDirectory).listFiles(new JSONFileFilter()));
        assertThat(reportContents.size(), is(1));
    }

    @Test
    public void when_a_step_fails_for_a_row_the_other_rows_should_be_executed() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        environmentVariables.setProperty(ThucydidesSystemProperty.THUCYDIDES_OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        SerenityRunner runner = getNormalTestRunnerUsing(ScenarioWithTestSpecificDataAndAFailingTestSample.class);

        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        assertThat(executedSteps.size(), is(1));
        TestOutcome testOutcome1 = executedSteps.get(0);

        List<TestStep> dataDrivenSteps = testOutcome1.getTestSteps();
        assertThat(dataDrivenSteps.size(), is(12));

    }

    @Test
    public void when_a_step_is_skipped_for_a_row_the_other_rows_should_be_executed() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        environmentVariables.setProperty(ThucydidesSystemProperty.THUCYDIDES_OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        SerenityRunner runner = getNormalTestRunnerUsing(ScenarioWithTestSpecificDataAndAFailingTestSample.class);

        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        assertThat(executedSteps.size(), is(1));
        TestOutcome testOutcome1 = executedSteps.get(0);

        List<TestStep> dataDrivenSteps = testOutcome1.getTestSteps();
        assertThat(dataDrivenSteps.size(), is(12));

    }


    @Test
    public void browser_should_be_restarted_periodically_if_requested() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        environmentVariables.setProperty(ThucydidesSystemProperty.THUCYDIDES_OUTPUT_DIRECTORY.getPropertyName(), outputDirectory.getAbsolutePath());
        environmentVariables.setProperty("thucydides.restart.browser.frequency","5");

        SerenityParameterizedRunner runner = getTestRunnerUsing(SampleSingleSessionDataDrivenScenario.class);

        runner.run(new RunNotifier());
    }


    @Test
    public void when_a_step_fails_for_a_row_the_other_rows_should_not_be_skipped() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        environmentVariables.setProperty(ThucydidesSystemProperty.THUCYDIDES_OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        SerenityRunner runner = getNormalTestRunnerUsing(ScenarioWithTestSpecificDataAndAFailingTestSample.class);

        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        assertThat(executedSteps.size(), is(1));
        TestOutcome testOutcome1 = executedSteps.get(0);

        List<TestStep> dataDrivenSteps = testOutcome1.getTestSteps();
        assertThat(dataDrivenSteps.size(), is(12));
        assertThat(dataDrivenSteps.get(1).getResult(), is(TestResult.FAILURE));
        assertThat(dataDrivenSteps.get(2).getResult(), is(TestResult.SUCCESS));

    }

    @Test
    public void when_a_parameterized_test_fails_outside_a_step_a_failure_should_be_recorded() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        environmentVariables.setProperty(ThucydidesSystemProperty.THUCYDIDES_OUTPUT_DIRECTORY.getPropertyName(),
                outputDirectory.getAbsolutePath());

        SerenityParameterizedRunner runner = getTestRunnerUsing(SampleDataDrivenScenarioWithExternalFailure.class);

        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = ParameterizedTestsOutcomeAggregator.from(runner).getTestOutcomesForAllParameterSets();
        assertThat(executedScenarios.size(), is(10));
        assertThat(executedScenarios.get(0).getResult(), is(TestResult.SUCCESS));
        assertThat(executedScenarios.get(1).getResult(), is(TestResult.FAILURE));
        assertThat(executedScenarios.get(2).getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void when_a_step_fails_with_an_error_for_a_row_the_other_rows_should_be_executed() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        environmentVariables.setProperty(ThucydidesSystemProperty.THUCYDIDES_OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        SerenityRunner runner = getNormalTestRunnerUsing(ScenarioWithTestSpecificDataAndABreakingTestSample.class);

        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        assertThat(executedSteps.size(), is(1));
        TestOutcome testOutcome1 = executedSteps.get(0);

        List<TestStep> dataDrivenSteps = testOutcome1.getTestSteps();
        assertThat(dataDrivenSteps.size(), is(12));
        assertThat(dataDrivenSteps.get(1).getResult(), is(TestResult.ERROR));
        assertThat(dataDrivenSteps.get(2).getResult(), is(TestResult.SUCCESS));
    }

    @RunWith(SerenityRunner.class)
    public static class ScenarioWithTestSpecificDataSample {

        @Managed(driver="htmlunit")
        public WebDriver webdriver;

        @ManagedPages(defaultUrl = "http://www.google.com")
        public Pages pages;

        @Steps
        public SampleScenarioSteps steps;


        @Test
        public void check_each_row() throws Throwable {
            withTestDataFrom("test-data/simple-data.csv").run(steps).data_driven_test_step();
        }
    }

    @RunWith(SerenityRunner.class)
    public static class ScenarioWithNestedTestSpecificDataSample {

        @Managed(driver="htmlunit")
        public WebDriver webdriver;

        @ManagedPages(defaultUrl = "http://www.google.com")
        public Pages pages;

        @Steps
        public NestedDatadrivenSteps steps;


        @Test
        public void check_each_row() throws Throwable {
            steps.check_each_row();
        }
    }


    @RunWith(SerenityRunner.class)
    public static class ScenarioWithDeeplyNestedTestSpecificDataSample {

        @Managed(driver="htmlunit")
        public WebDriver webdriver;

        @ManagedPages(defaultUrl = "http://www.google.com")
        public Pages pages;

        @Steps
        public NestedDatadrivenSteps steps;

        @Test
        public void happy_day_scenario() throws Throwable {
            steps.do_something();
            steps.run_data_driven_tests();
            steps.do_something_else();
        }
    }

    @RunWith(SerenityRunner.class)
    public static class ScenarioWithTestSpecificDataAndAFailingTestSample {

        @Managed(driver="htmlunit")
        public WebDriver webdriver;

        @ManagedPages(defaultUrl = "http://www.google.com")
        public Pages pages;

        @Steps
        public SampleScenarioSteps steps;


        @Test
        public void happy_day_scenario() throws Throwable {
            withTestDataFrom("test-data/simple-data.csv").run(steps).data_driven_test_step_that_fails();
        }
    }

    @RunWith(SerenityRunner.class)
    public static class ScenarioWithTestSpecificDataAndASkippedTestSample {

        @Managed(driver="htmlunit")
        public WebDriver webdriver;

        @ManagedPages(defaultUrl = "http://www.google.com")
        public Pages pages;

        @Steps
        public SampleScenarioSteps steps;


        @Test
        public void happy_day_scenario() throws Throwable {
            withTestDataFrom("test-data/simple-data.csv").run(steps).data_driven_test_step_that_is_skipped();
        }
    }

    @RunWith(SerenityRunner.class)
    public static class ScenarioWithTestSpecificDataAndABreakingTestSample {

        @Managed(driver="htmlunit")
        public WebDriver webdriver;

        @ManagedPages(defaultUrl = "http://www.google.com")
        public Pages pages;

        @Steps
        public SampleScenarioSteps steps;


        @Test
        public void happy_day_scenario() throws Throwable {
            withTestDataFrom("test-data/simple-data.csv").run(steps).data_driven_test_step_that_breaks();
        }
    }

    @Test
    public void when_test_data_is_provided_for_a_step_then_a_step_should_be_reported_for_each_data_row() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        environmentVariables.setProperty(ThucydidesSystemProperty.THUCYDIDES_OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        SerenityRunner runner = getNormalTestRunnerUsing(ScenarioWithTestSpecificDataSample.class);

        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        assertThat(executedSteps.size(), is(1));
        TestOutcome testOutcome1 = executedSteps.get(0);

        List<TestStep> dataDrivenSteps = testOutcome1.getTestSteps();
        assertThat(dataDrivenSteps.size(), is(12));

    }

    @Test
    public void when_test_data_is_provided_for_a_nested_step_then_a_step_should_be_reported_for_each_data_row() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        environmentVariables.setProperty(ThucydidesSystemProperty.THUCYDIDES_OUTPUT_DIRECTORY.getPropertyName(),
                outputDirectory.getAbsolutePath());

        SerenityRunner runner = getNormalTestRunnerUsing(ScenarioWithNestedTestSpecificDataSample.class);

        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        assertThat(executedSteps.size(), is(1));
        TestOutcome testOutcome1 = executedSteps.get(0);

        List<TestStep> dataDrivenSteps = testOutcome1.getTestSteps().get(0).getChildren();
        assertThat(dataDrivenSteps.size(), is(12));

    }

    @Test
    public void when_test_data_is_provided_for_a_deeply_nested_step_then_a_step_should_be_reported_for_each_data_row() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        environmentVariables.setProperty(ThucydidesSystemProperty.THUCYDIDES_OUTPUT_DIRECTORY.getPropertyName(),
                outputDirectory.getAbsolutePath());

        SerenityRunner runner = getNormalTestRunnerUsing(ScenarioWithDeeplyNestedTestSpecificDataSample.class);

        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        assertThat(executedSteps.size(), is(1));
        TestOutcome testOutcome1 = executedSteps.get(0);

        List<TestStep> dataDrivenSteps = testOutcome1.getTestSteps();
        assertThat(dataDrivenSteps.size(), is(3));

    }
    @Test
    public void test_step_data_should_appear_in_the_step_titles() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        environmentVariables.setProperty(ThucydidesSystemProperty.THUCYDIDES_OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        SerenityRunner runner = getNormalTestRunnerUsing(ScenarioWithTestSpecificDataSample.class);

        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        TestOutcome testOutcome1 = executedSteps.get(0);
        List<TestStep> dataDrivenSteps = testOutcome1.getTestSteps();

        TestStep step1 = dataDrivenSteps.get(0);
        TestStep setNameStep1 = step1.getFlattenedSteps().get(0);
        TestStep step2 = dataDrivenSteps.get(1);
        TestStep setNameStep2 = step2.getFlattenedSteps().get(0);

        assertThat(setNameStep1.getDescription(), containsString("Joe Smith"));
        assertThat(setNameStep2.getDescription(), containsString("Jack Black"));


    }


    @Test
    public void running_a_simple_parameterized_test_should_produce_an_outcome_per_data_row() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        environmentVariables.setProperty(ThucydidesSystemProperty.THUCYDIDES_OUTPUT_DIRECTORY.getPropertyName(),
            outputDirectory.getAbsolutePath());

        SerenityParameterizedRunner runner = getTestRunnerUsing(SimpleSuccessfulParametrizedTestSample.class);

        runner.run(new RunNotifier());

        List<String> reportContents = contentsOf(reload(outputDirectory).listFiles(new JSONFileFilter()));

        assertThat(reportContents.size(), is(2));

    }

    @Test
    public void when_the_Concurrent_annotation_is_used_tests_should_be_run_in_parallel() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        environmentVariables.setProperty(ThucydidesSystemProperty.THUCYDIDES_OUTPUT_DIRECTORY.getPropertyName(),
                            outputDirectory.getAbsolutePath());

        SerenityParameterizedRunner runner = getTestRunnerUsing(SampleParallelDataDrivenScenario.class);

        runner.run(new RunNotifier());

        List<String> reportContents = contentsOf(reload(outputDirectory).listFiles(new JSONFileFilter()));

        assertThat(reportContents, hasItemContainsString("Step with parameters: a, 1"));
        assertThat(reportContents, hasItemContainsString("Step with parameters: b, 2"));
        assertThat(reportContents, hasItemContainsString("Step with parameters: c, 3"));

    }

    @Test
    public void the_Concurrent_annotation_indicates_that_tests_should_be_run_in_parallel() throws Throwable  {

        SerenityParameterizedRunner runner = getTestRunnerUsing(SampleParallelDataDrivenScenario.class);

        assertThat(runner.runTestsInParallelFor(SampleParallelDataDrivenScenario.class), is(true));
        assertThat(runner.runTestsInParallelFor(SampleDataDrivenScenario.class), is(false));
    }

    @Test
    public void by_default_the_number_of_threads_is_2_times_the_number_of_CPU_cores() throws Throwable {

        SerenityParameterizedRunner runner = getTestRunnerUsing(SampleParallelDataDrivenScenario.class);
        int threadCount = runner.getThreadCountFor(SampleParallelDataDrivenScenario.class);

        int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

        assertThat(threadCount, is(AVAILABLE_PROCESSORS * 2));

    }

    @RunWith(SerenityParameterizedRunner.class)
    @Concurrent(threads = "7")
    public static final class ParallelDataDrivenScenarioWithSpecifiedThreadCountSample {
        @TestData
        public static Collection testData() {
                return Arrays.asList(new Object[][]{ });
            }
    }

    @Test
    public void the_number_of_threads_can_be_overridden_in_the_concurrent_annotation() throws Throwable  {

        SerenityParameterizedRunner runner
                   = getTestRunnerUsing(ParallelDataDrivenScenarioWithSpecifiedThreadCountSample.class);
        int threadCount = runner.getThreadCountFor(ParallelDataDrivenScenarioWithSpecifiedThreadCountSample.class);

        assertThat(threadCount, is(7));

    }

    @Test
    public void the_number_of_threads_can_be_overridden_with_a_system_property() throws Throwable {

        environmentVariables.setProperty("thucydides.concurrent.threads","4");
        SerenityParameterizedRunner runner
            = getTestRunnerUsing(ParallelDataDrivenScenarioWithSpecifiedThreadCountSample.class);
        int threadCount = runner.getThreadCountFor(ParallelDataDrivenScenarioWithSpecifiedThreadCountSample.class);

        assertThat(threadCount, is(4));

    }

    @RunWith(SerenityParameterizedRunner.class)
    @Concurrent(threads = "7x")
    public static final class ParallelDataDrivenScenarioWithRelativeThreadCountSample {
        @TestData
        public static Collection testData() {
                return Arrays.asList(new Object[][]{ });
            }
    }

    @Test
    public void the_number_of_threads_can_be_overridden_in_the_concurrent_annotation_using_a_relative_value() throws Throwable  {

        SerenityParameterizedRunner runner
                   = getTestRunnerUsing(ParallelDataDrivenScenarioWithRelativeThreadCountSample.class);
        int threadCount = runner.getThreadCountFor(ParallelDataDrivenScenarioWithRelativeThreadCountSample.class);

        int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

        assertThat(threadCount, is(7 * AVAILABLE_PROCESSORS));

    }

    @RunWith(SerenityParameterizedRunner.class)
    @Concurrent(threads = "xxx")
    public static final class ParallelDataDrivenScenarioWithInvalidThreadCountSample {
        @TestData
        public static Collection testData() {
                return Arrays.asList(new Object[][]{ });
            }
    }
    @Test(expected = IllegalArgumentException.class)
    public void if_the_thread_count_is_invalid_an_exception_should_be_thrown() throws Throwable  {

        SerenityParameterizedRunner runner
                   = getTestRunnerUsing(ParallelDataDrivenScenarioWithInvalidThreadCountSample.class);
        runner.getThreadCountFor(ParallelDataDrivenScenarioWithInvalidThreadCountSample.class);

    }

    private List<String> filenamesOf(File[] files) {
        List filenames = new ArrayList<String>();
        for(File file : files) {
            filenames.add(file.getName());
        }
        return filenames;
    }


    private List<String> contentsOf(File[] files) throws IOException {
        List<String> contents = Lists.newArrayList();
        for(File file : files) {
            contents.add(stringContentsOf(file));
        }
        return contents;
    }

    private String stringContentsOf(File reportFile) throws IOException {
        return FileUtils.readFileToString(reportFile);
    }

    private File reload(File old) {
        return Paths.get(old.getAbsolutePath()).toFile();
    }

    @Test
    public void a_separate_html_report_should_be_generated_from_each_scenario() throws Throwable  {

        File outputDirectory = tempFolder.newFolder("thucydides");
        environmentVariables.setProperty(ThucydidesSystemProperty.THUCYDIDES_OUTPUT_DIRECTORY.getPropertyName(),
                                         outputDirectory.getAbsolutePath());

        SerenityParameterizedRunner runner = getTestRunnerUsing(SampleDataDrivenScenario.class);

        runner.run(new RunNotifier());

        File[] reports = reload(outputDirectory).listFiles(new HTMLFileFilter());
        assertThat(reports.length, is(3));
    }

    private class HTMLFileFilter implements FilenameFilter {
        public boolean accept(File directory, String filename) {
            return filename.endsWith(".html") && !filename.endsWith("screenshots.html");
        }
    }

    private class JSONFileFilter implements FilenameFilter {
        public boolean accept(File directory, String filename) {
            return filename.endsWith(".json");
        }
    }

    protected SerenityRunner getNormalTestRunnerUsing(Class<?> testClass) throws Throwable {
        Configuration configuration = new SystemPropertiesConfiguration(environmentVariables);
        WebDriverFactory factory = new WebDriverFactory(environmentVariables);
        return new SerenityRunner(testClass, factory, configuration);
    }

    protected SerenityParameterizedRunner getTestRunnerUsing(Class<?> testClass) throws Throwable {
        Configuration configuration = new SystemPropertiesConfiguration(environmentVariables);
        WebDriverFactory factory = new WebDriverFactory(environmentVariables);
        BatchManager batchManager = new BatchManagerProvider(configuration).get();
        return new SerenityParameterizedRunner(testClass, configuration, factory, batchManager);
    }

    protected SerenityParameterizedRunner getStubbedTestRunnerUsing(Class<?> testClass) throws Throwable {
        Configuration configuration = new SystemPropertiesConfiguration(environmentVariables);
        WebDriverFactory factory = new WebDriverFactory(environmentVariables);
        BatchManager batchManager = new BatchManagerProvider(configuration).get();
        return new SerenityParameterizedRunner(testClass, configuration, factory, batchManager) {
            @Override
            public void generateReports() {
                //do nothing
            }
        };
    }

}
