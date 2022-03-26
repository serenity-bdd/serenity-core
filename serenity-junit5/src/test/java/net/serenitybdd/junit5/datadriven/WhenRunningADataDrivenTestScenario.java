package net.serenitybdd.junit5.datadriven;

import net.serenitybdd.junit5.AbstractTestStepRunnerTest;
import net.serenitybdd.junit5.ParameterizedTestsOutcomeAggregator;
import net.serenitybdd.junit5.datadriven.samples.*;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.util.SystemEnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.samples.AddDifferentSortsOfTodos;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class WhenRunningADataDrivenTestScenario extends AbstractTestStepRunnerTest {

    MockEnvironmentVariables environmentVariables;

    WebDriverFactory webDriverFactory;

    @BeforeEach
    public void createATestableDriverFactory() throws Exception {
        MockitoAnnotations.initMocks(this);
        environmentVariables = new MockEnvironmentVariables();
        webDriverFactory = new WebDriverFactory(environmentVariables);
        StepEventBus.getEventBus().clear();
        StepEventBus.setNoCleanupForStickyBuses(true);
    }

    @TempDir
    Path anotherTempDir;

    Configuration configuration;

    @Test
    public void the_test_runner_records_the_steps_as_they_are_executed() {

        runTestForClass(MultipleDataDrivenTestScenariosWithValueSource.class);

        for(int i = 1; i<= 2; i++) {
            String eventBusName = String.format("[engine:junit-jupiter]/[class:net.serenitybdd.junit5.datadriven.samples.MultipleDataDrivenTestScenariosWithValueSource]/[test-template:withValueSource(java.lang.String)]/[test-template-invocation:#%s]",i);
            StepEventBus stepEventBus = StepEventBus.eventBusFor(eventBusName);
            List<TestOutcome> currentOutcomes = new ParameterizedTestsOutcomeAggregator(stepEventBus.getBaseStepListener()).getTestOutcomesForAllParameterSets();
            assertThat(currentOutcomes.size(), is(1));
            assertThat(currentOutcomes.get(0).getTestSteps().size(), is(2));
            StepEventBus.forceClearEventBusFor(eventBusName);
        }

        for(int i = 1; i<= 3; i++) {
            String eventBusName = String.format("[engine:junit-jupiter]/[class:net.serenitybdd.junit5.datadriven.samples.MultipleDataDrivenTestScenariosWithValueSource]/[test-template:withValueSourceIntegers(int)]/[test-template-invocation:#%s]",i);
            StepEventBus stepEventBus = StepEventBus.eventBusFor(eventBusName);
            List<TestOutcome> currentOutcomes = new ParameterizedTestsOutcomeAggregator(stepEventBus.getBaseStepListener()).getTestOutcomesForAllParameterSets();
            assertThat(currentOutcomes.size(), is(1));
            assertThat(currentOutcomes.get(0).getTestSteps().size(), is(2));
            StepEventBus.forceClearEventBusFor(eventBusName);
        }

        /*ConcurrentMap<Object, StepEventBus> stickyEventBuses = StepEventBus.getStickyEventBuses();
        System.out.println("Sticky buses size " + stickyEventBuses.size());
        stickyEventBuses.forEach((k,v)->System.out.println(k + "--" + v) );

        assertTrue(StepEventBus.getStickyEventBuses().size()==0);*/
    }

    private void runTestForClass(Class testClass){
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(testClass))
                .build();
        LauncherFactory.create().execute(request);
    }

    @Test
    public void a_data_driven_test_driver_should_run_one_test_per_row_of_data() throws Throwable {
        runTestForClass(MultipleDataDrivenTestScenariosWithValueSource.class);
        for(int i = 1; i <= 2; i++) {
            String eventBusName = String.format("[engine:junit-jupiter]/[class:net.serenitybdd.junit5.datadriven.samples.MultipleDataDrivenTestScenariosWithValueSource]/[test-template:withValueSource(java.lang.String)]/[test-template-invocation:#%s]",i);
            StepEventBus stepEventBus = StepEventBus.eventBusFor(eventBusName);
            List<TestOutcome> currentOutcomes = new ParameterizedTestsOutcomeAggregator(stepEventBus.getBaseStepListener()).getTestOutcomesForAllParameterSets();
            assertThat(currentOutcomes.size(), is(1));
            StepEventBus.forceClearEventBusFor(eventBusName);
        }
        for(int i = 1; i<= 3; i++) {
            String eventBusName = String.format("[engine:junit-jupiter]/[class:net.serenitybdd.junit5.datadriven.samples.MultipleDataDrivenTestScenariosWithValueSource]/[test-template:withValueSourceIntegers(int)]/[test-template-invocation:#%s]",i);
            StepEventBus stepEventBus = StepEventBus.eventBusFor(eventBusName);
            List<TestOutcome> currentOutcomes = new ParameterizedTestsOutcomeAggregator(stepEventBus.getBaseStepListener()).getTestOutcomesForAllParameterSets();
            assertThat(currentOutcomes.size(), is(1));
            StepEventBus.forceClearEventBusFor(eventBusName);
        }
    }

    @Test
    public void manual_data_driven_tests_should_be_allowed() throws Throwable {

        runTestForClass(AddDifferentSortsOfTodos.class);

        for (int i = 1; i<= 4; i++) {
            StepEventBus stepEventBus4 = StepEventBus.eventBusFor(String.format("[engine:junit-jupiter]/[class:net.thucydides.samples.AddDifferentSortsOfTodos]/[test-template:shouldBeAbleToAddANewTodoItem()]/[test-template-invocation:#%s]",i));
            List<TestOutcome> currentOutcomes = new ParameterizedTestsOutcomeAggregator(stepEventBus4.getBaseStepListener()).getTestOutcomesForAllParameterSets();
            assertThat(currentOutcomes.size(), is(1));
        }
    }

    /*@Test
    public void a_data_driven_test_driver_should_aggregate_test_outcomes() throws Throwable {

        runTestForClass(SimpleDataDrivenTestScenarioWithValueSource.class);

        List<TestOutcome> aggregatedScenarios = ParameterizedTestsOutcomeAggregator.getTestOutcomesForAllParameterSets();
        assertThat(aggregatedScenarios.size(), is(5));

        List<TestStep> testSteps = aggregatedScenarios.get(0).getTestSteps();
        assertThat(aggregatedScenarios.get(0).getStepCount(), is(2));
        assertThat(aggregatedScenarios.get(1).getStepCount(), is(2));
    }*/

    /*@Test //TODO clarify
    public void a_data_driven_test_driver_should_record_a_sample_scenario() throws Throwable {

        runTestForClass(SimpleDataDrivenTestScenarioWithValueSource.class);
        List<TestOutcome> aggregatedScenarios = new ParameterizedTestsOutcomeAggregator().aggregateTestOutcomesByTestMethods();
        assertThat(aggregatedScenarios.get(0).getDataDrivenSampleScenario(), containsString(
                "Step that succeeds\n" +
                "Another step that succeeds"));
    }*/

    @Test
    public void a_data_driven_test_driver_should_record_a_table_of_example() throws Throwable {

        runTestForClass(MultipleDataDrivenTestScenariosWithValueSource.class);

        for(int i = 1; i <= 2; i++) {
            String eventBusName = String.format("[engine:junit-jupiter]/[class:net.serenitybdd.junit5.datadriven.samples.MultipleDataDrivenTestScenariosWithValueSource]/[test-template:withValueSource(java.lang.String)]/[test-template-invocation:#%s]",i);
            StepEventBus stepEventBus = StepEventBus.eventBusFor(eventBusName);
            List<TestOutcome> currentOutcomes = new ParameterizedTestsOutcomeAggregator(stepEventBus.getBaseStepListener()).getTestOutcomesForAllParameterSets();
            assertThat(currentOutcomes.size(), is(1));
            StepEventBus.forceClearEventBusFor(eventBusName);
        }
        for(int i = 1; i <= 3; i++) {
            String eventBusName = String.format("[engine:junit-jupiter]/[class:net.serenitybdd.junit5.datadriven.samples.MultipleDataDrivenTestScenariosWithValueSource]/[test-template:withValueSourceIntegers(int)]/[test-template-invocation:#%s]",i);
            StepEventBus stepEventBus = StepEventBus.eventBusFor(eventBusName);
            List<TestOutcome> currentOutcomes = new ParameterizedTestsOutcomeAggregator(stepEventBus.getBaseStepListener()).getTestOutcomesForAllParameterSets();
            assertThat(currentOutcomes.size(), is(1));
            StepEventBus.forceClearEventBusFor(eventBusName);
        }


    }

    @Test
    public void a_data_driven_test_with_a_failing_assumption_should_be_ignored()  {
        runTestForClass(SampleSingleDataDrivenScenarioWithFailingAssumption.class);

        for(int i = 1; i <= 5; i++) {
            StepEventBus stepEventBus = StepEventBus.eventBusFor(String.format("[engine:junit-jupiter]/[class:net.serenitybdd.junit5.datadriven.samples.SampleSingleDataDrivenScenarioWithFailingAssumption]/[test-template:happy_day_scenario(java.lang.String, java.lang.Integer)]/[test-template-invocation:#%s]",i));
            List<TestOutcome> currentOutcomes = new ParameterizedTestsOutcomeAggregator(stepEventBus.getBaseStepListener()).getTestOutcomesForAllParameterSets();
            assertThat(currentOutcomes.size(), is(1));
            TestOutcome testOutcome1 = currentOutcomes.get(0);
            assertThat(testOutcome1.getResult(), is(TestResult.IGNORED));
        }
    }

    @Test
    public void a_data_driven_test_driver_should_aggregate_test_outcomes_without_steps()  {

        runTestForClass(SimpleSuccessfulParameterizedTestSample.class);

        for ( int i = 1; i <= 3; i++) {
            String stepEventBusName = String.format("[engine:junit-jupiter]/[class:net.serenitybdd.junit5.datadriven.samples.SimpleSuccessfulParameterizedTestSample]/[test-template:test1()]/[test-template-invocation:#%s]",i);
            StepEventBus stepEventBus = StepEventBus.eventBusFor(stepEventBusName);
            List<TestOutcome> currentOutcomes = new ParameterizedTestsOutcomeAggregator(stepEventBus.getBaseStepListener()).getTestOutcomesForAllParameterSets();
            assertThat(currentOutcomes.size(), is(1));
            StepEventBus.forceClearEventBusFor(stepEventBusName);
        }
        for ( int i = 1; i <= 3; i++) {
            String stepEventBusName = String.format("[engine:junit-jupiter]/[class:net.serenitybdd.junit5.datadriven.samples.SimpleSuccessfulParameterizedTestSample]/[test-template:test2()]/[test-template-invocation:#%s]",i);
            StepEventBus stepEventBus = StepEventBus.eventBusFor(stepEventBusName);
            List<TestOutcome> currentOutcomes = new ParameterizedTestsOutcomeAggregator(stepEventBus.getBaseStepListener()).getTestOutcomesForAllParameterSets();
            assertThat(currentOutcomes.size(), is(1));
            StepEventBus.forceClearEventBusFor(stepEventBusName);
        }

    }

    @Test
    public void data_driven_tests_should_pass_even_if_no_steps_are_called() throws Throwable {

        runTestForClass(SimpleSuccessfulParameterizedTestSample.class);

        for(int i = 1; i <= 2; i++) {
            String eventBusName = String.format("[engine:junit-jupiter]/[class:net.serenitybdd.junit5.datadriven.samples.SimpleSuccessfulParameterizedTestSample]/[test-template:test1()]/[test-template-invocation:#%s]",i);
            StepEventBus stepEventBus = StepEventBus.eventBusFor(eventBusName);
            List<TestOutcome> currentOutcomes = new ParameterizedTestsOutcomeAggregator(stepEventBus.getBaseStepListener()).getTestOutcomesForAllParameterSets();
            assertThat(currentOutcomes.size(), is(1));
            assertThat(currentOutcomes.get(0).getResult(), is(TestResult.SUCCESS));
            StepEventBus.forceClearEventBusFor(eventBusName);
        }
    }

    @Test
    public void an_ignored_data_driven_test_should_have_result_status_as_ignored() throws Throwable {

        runTestForClass(SampleDataDrivenIgnoredScenario.class);

        StepEventBus stepEventBus = StepEventBus.eventBusFor("[engine:junit-jupiter]/[class:net.serenitybdd.junit5.datadriven.samples.SampleDataDrivenIgnoredScenario]/[test-template:ignored_scenario(java.lang.String, int)]");
        List<TestOutcome> currentOutcomes = new ParameterizedTestsOutcomeAggregator(stepEventBus.getBaseStepListener()).getTestOutcomesForAllParameterSets();
        assertThat(currentOutcomes.size(), is(1));
        assertThat(currentOutcomes.get(0).getResult(), is(TestResult.IGNORED));

    }

    //TODO - implement pending tests for data driven scenarios
    /*@Test
    public void a_pending_data_driven_test_should_have_result_status_as_pending() throws Throwable {

        runTestForClass(SampleDataDrivenPendingScenario.class);

        List<TestOutcome> aggregatedScenarios = new ParameterizedTestsOutcomeAggregator().aggregateTestOutcomesByTestMethods();
        assertThat(aggregatedScenarios.size(), is(1));
        assertThat(aggregatedScenarios.get(0).getResult(), is(TestResult.PENDING));
    }*/

    @Test
    public void a_pending_data_driven_test_should_have_a_test_step_for_each_row() throws Throwable {

        runTestForClass(SampleDataDrivenPendingScenario.class);

        for(int i = 1; i <= 5; i++) {
            StepEventBus stepEventBus = StepEventBus.eventBusFor(String.format("[engine:junit-jupiter]/[class:net.serenitybdd.junit5.datadriven.samples.SampleDataDrivenPendingScenario]/[test-template:pending_scenario(java.lang.String, int)]/[test-template-invocation:#%s]",i));
            List<TestOutcome> currentOutcomes = new ParameterizedTestsOutcomeAggregator(stepEventBus.getBaseStepListener()).getTestOutcomesForAllParameterSets();
            assertThat(currentOutcomes.size(), is(1));
        }
    }

    @Test
    public void a_data_driven_test_should_also_be_able_to_use_data_from_a_CSV_file() throws Throwable {

        runTestForClass(SampleCSVDataDrivenScenario.class);

        for(int i = 1; i <= 12; i++) {
            StepEventBus stepEventBus = StepEventBus.eventBusFor(String.format("[engine:junit-jupiter]/[class:net.serenitybdd.junit5.datadriven.samples.SampleCSVDataDrivenScenario]/[test-template:data_driven_test(java.lang.String, int, java.lang.String)]/[test-template-invocation:#%s]",i));
            List<TestOutcome> currentOutcomes = new ParameterizedTestsOutcomeAggregator(stepEventBus.getBaseStepListener()).getTestOutcomesForAllParameterSets();
            assertThat(currentOutcomes.size(), is(1));
        }
        for(int i = 1; i <= 12; i++) {
            StepEventBus stepEventBus = StepEventBus.eventBusFor(String.format("[engine:junit-jupiter]/[class:net.serenitybdd.junit5.datadriven.samples.SampleCSVDataDrivenScenario]/[test-template:another_data_driven_test(java.lang.String, int, java.lang.String)]/[test-template-invocation:#%s]",i));
            List<TestOutcome> currentOutcomes = new ParameterizedTestsOutcomeAggregator(stepEventBus.getBaseStepListener()).getTestOutcomesForAllParameterSets();
            assertThat(currentOutcomes.size(), is(1));
        }
    }

    @Test
    public void a_separate_json_report_should_be_generated_for_each_scenario() throws Throwable {

        File outputDirectory = anotherTempDir.resolve("serenity").toFile();

        System.setProperty(ThucydidesSystemProperty.SERENITY_OUTPUT_DIRECTORY.getPropertyName(),
                outputDirectory.getAbsolutePath());
        SystemPropertiesConfiguration systemPropertiesConfiguration = new SystemPropertiesConfiguration(new SystemEnvironmentVariables());
        runTestForClass(MultipleDataDrivenTestScenariosWithValueSource.class);
        File[] reports = reload(systemPropertiesConfiguration.getOutputDirectory()).listFiles(new JSONFileFilter());
        assertThat(reports.length, is(2));

        for(int i = 1; i <= 2; i++) {
            String eventBusName = String.format("[engine:junit-jupiter]/[class:net.serenitybdd.junit5.datadriven.samples.MultipleDataDrivenTestScenariosWithValueSource]/[test-template:withValueSource(java.lang.String)]/[test-template-invocation:#%s]",i);
            StepEventBus.forceClearEventBusFor(eventBusName);
        }
        for(int i = 1; i <= 3; i++) {
            String eventBusName = String.format("[engine:junit-jupiter]/[class:net.serenitybdd.junit5.datadriven.samples.MultipleDataDrivenTestScenariosWithValueSource]/[test-template:withValueSourceIntegers(int)]/[test-template-invocation:#%s]",i);
            StepEventBus.forceClearEventBusFor(eventBusName);
        }
    }

    @Test
    public void a_separate_json_report_should_be_generated_for_each_scenario_when_using_data_from_a_CSV_file() throws Throwable {

        File outputDirectory = anotherTempDir.resolve("serenity").toFile();
        System.setProperty(ThucydidesSystemProperty.SERENITY_OUTPUT_DIRECTORY.getPropertyName(),
                outputDirectory.getAbsolutePath());
        runTestForClass(SampleCSVDataDrivenScenario.class);
        File[] reports = reload(outputDirectory).listFiles(new JSONFileFilter());
        assertThat(reports.length, is(2));
    }

    private class HTMLFileFilter implements FilenameFilter {
        public boolean accept(File directory, String filename) {
            return filename.endsWith(".html") && !filename.endsWith("screenshots.html");
        }
    }

    private class JSONFileFilter implements FilenameFilter {
        public boolean accept(File directory, String filename) {
            return filename.endsWith(".json") && !filename.startsWith("manifest");
        }
    }

    private List<String> filenamesOf(File[] files) {
        List filenames = new ArrayList<String>();
        for (File file : files) {
            filenames.add(file.getName());
        }
        return filenames;
    }


    private List<String> contentsOf(File[] files) throws IOException {
        List<String> contents = new ArrayList<>();
        for (File file : files) {
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

    @TempDir
    Path jsonTempDir;

    @Test
    @Disabled("Unstable on Windows: to review")
    public void json_report_contents_should_reflect_the_test_data_from_the_csv_file() throws Throwable {
        File outputDirectory = jsonTempDir.resolve("serenity").toFile();
        System.setProperty(ThucydidesSystemProperty.SERENITY_OUTPUT_DIRECTORY.getPropertyName(), outputDirectory.getAbsolutePath());
        runTestForClass(SampleCSVDataDrivenScenario.class);

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
            return values.stream()
                    .anyMatch(value -> value.contains(expectedValue));
        }

        public void describeTo(Description description) {
            description.appendText("Expecting a list containing a string that contains ").appendValue(expectedValue);
        }
    }

    @Test
    public void when_a_step_fails_for_a_row_the_other_rows_should_be_executed() throws Throwable {

        File outputDirectory = anotherTempDir.resolve("serenity").toFile();
        System.setProperty(ThucydidesSystemProperty.SERENITY_OUTPUT_DIRECTORY.getPropertyName(),
                outputDirectory.getAbsolutePath());

        runTestForClass(ScenarioWithTestSpecificDataAndAFailingTestSample.class);

         for( int i = 1; i <= 12; i++) {
            String eventBusName = String.format("[engine:junit-jupiter]/[class:net.serenitybdd.junit5.datadriven.samples.ScenarioWithTestSpecificDataAndAFailingTestSample]/[test-template:happy_day_scenario(java.lang.String, java.lang.String, java.lang.String)]/[test-template-invocation:#%s]",i);
            StepEventBus stepEventBus = StepEventBus.eventBusFor(eventBusName);
            List<TestOutcome> currentOutcomes = new ParameterizedTestsOutcomeAggregator(stepEventBus.getBaseStepListener()).getTestOutcomesForAllParameterSets();
            assertThat(currentOutcomes.size(), is(1));
            assertThat(currentOutcomes.get(0).getTestSteps().size(), is(1));
            StepEventBus.forceClearEventBusFor(eventBusName);
        }

    }

    @Test
    public void when_a_step_is_skipped_for_a_row_the_other_rows_should_be_executed() {

        File outputDirectory = anotherTempDir.resolve("serenity").toFile();
        System.setProperty(ThucydidesSystemProperty.SERENITY_OUTPUT_DIRECTORY.getPropertyName(),
                outputDirectory.getAbsolutePath());
        runTestForClass(ScenarioWithTestSpecificDataAndASkippedTestSample.class);

        for(int i = 1; i <= 12; i++) {
            StepEventBus stepEventBus = StepEventBus.eventBusFor(String.format("[engine:junit-jupiter]/[class:net.serenitybdd.junit5.datadriven.samples.ScenarioWithTestSpecificDataAndASkippedTestSample]/[test-template:happy_day_scenario(java.lang.String, int, java.lang.String)]/[test-template-invocation:#%s]",i));
            List<TestOutcome> currentOutcomes = new ParameterizedTestsOutcomeAggregator(stepEventBus.getBaseStepListener()).getTestOutcomesForAllParameterSets();
            assertThat(currentOutcomes.size(), is(1));
            TestOutcome testOutcome1 = currentOutcomes.get(0);
            List<TestStep> dataDrivenSteps = testOutcome1.getTestSteps();
            assertThat(dataDrivenSteps.size(), is(1));
        }
    }


    @Test
    public void when_a_step_fails_for_a_row_the_other_rows_should_not_be_skipped() {

        File outputDirectory = anotherTempDir.resolve("serenity").toFile();
        System.setProperty(ThucydidesSystemProperty.SERENITY_OUTPUT_DIRECTORY.getPropertyName(),
                outputDirectory.getAbsolutePath());
        runTestForClass(ScenarioWithTestSpecificDataAndAFailingTestSample.class);

        for(int i = 1; i <= 12; i++) {
            String eventBusName = String.format("[engine:junit-jupiter]/[class:net.serenitybdd.junit5.datadriven.samples.ScenarioWithTestSpecificDataAndAFailingTestSample]/[test-template:happy_day_scenario(java.lang.String, java.lang.String, java.lang.String)]/[test-template-invocation:#%s]",i);
            StepEventBus stepEventBus = StepEventBus.eventBusFor(eventBusName);
            List<TestOutcome> currentOutcomes = new ParameterizedTestsOutcomeAggregator(stepEventBus.getBaseStepListener()).getTestOutcomesForAllParameterSets();
            assertThat(currentOutcomes.size(), is(1));
            TestOutcome testOutcome1 = currentOutcomes.get(0);
            List<TestStep> dataDrivenSteps = testOutcome1.getTestSteps();
            assertThat(dataDrivenSteps.size(), is(1));
            if(i==2) {
                assertThat(dataDrivenSteps.get(0).getResult(), is(TestResult.FAILURE));
            } else {
                assertThat(dataDrivenSteps.get(0).getResult(), is(TestResult.SUCCESS));
            }
            StepEventBus.forceClearEventBusFor(eventBusName);
        }

    }

    @Test
    public void when_a_parameterized_test_fails_outside_a_step_a_failure_should_be_recorded() throws Throwable {

        File outputDirectory = anotherTempDir.resolve("serenity").toFile();
        System.setProperty(ThucydidesSystemProperty.SERENITY_OUTPUT_DIRECTORY.getPropertyName(),
                outputDirectory.getAbsolutePath());
        runTestForClass(SampleDataDrivenScenarioWithExternalFailure.class);

        for (int i = 1; i <= 5; i++) {
            String stepEventBusName = String.format("[engine:junit-jupiter]/[class:net.serenitybdd.junit5.datadriven.samples.SampleDataDrivenScenarioWithExternalFailure]/[test-template:happy_day_scenario_with_failure(java.lang.String, int)]/[test-template-invocation:#%s]",i);
            StepEventBus stepEventBus = StepEventBus.eventBusFor(stepEventBusName);
            List<TestOutcome> currentOutcomes = new ParameterizedTestsOutcomeAggregator(stepEventBus.getBaseStepListener()).getTestOutcomesForAllParameterSets();
            assertThat(currentOutcomes.size(), is(1));
            TestOutcome testOutcome = currentOutcomes.get(0);
            List<TestStep> dataDrivenSteps = testOutcome.getTestSteps();
            assertThat(dataDrivenSteps.size(), is(1));
            if (i==2) {
                assertThat(testOutcome.getResult(), is(TestResult.FAILURE));
            }
            else {
                assertThat(testOutcome.getResult(), is(TestResult.SUCCESS));
            }
        }
    }

    @Test
    public void when_a_step_fails_with_an_error_for_a_row_the_other_rows_should_be_executed() throws Throwable {

        File outputDirectory = anotherTempDir.resolve("serenity").toFile();
        System.setProperty(ThucydidesSystemProperty.SERENITY_OUTPUT_DIRECTORY.getPropertyName(),
                outputDirectory.getAbsolutePath());
        runTestForClass(ScenarioWithTestSpecificDataAndABreakingTestSample.class);


        for(int i = 1; i <= 12; i++) {
            StepEventBus stepEventBus = StepEventBus.eventBusFor(String.format("[engine:junit-jupiter]/[class:net.serenitybdd.junit5.datadriven.samples.ScenarioWithTestSpecificDataAndABreakingTestSample]/[test-template:happy_day_scenario(java.lang.String, java.lang.String, java.lang.String)]/[test-template-invocation:#%s]",i));
            List<TestOutcome> currentOutcomes = new ParameterizedTestsOutcomeAggregator(stepEventBus.getBaseStepListener()).getTestOutcomesForAllParameterSets();
            assertThat(currentOutcomes.size(), is(1));
            TestOutcome testOutcome1 = currentOutcomes.get(0);
            if (i==2) {
                assertThat(testOutcome1.getResult(), is(TestResult.ERROR));
            } else {
                assertThat(testOutcome1.getResult(), is(TestResult.SUCCESS));
            }
        }
    }

    @Test
    public void when_test_data_is_provided_for_a_step_then_a_step_should_be_reported_for_each_data_row() throws Throwable {

        File outputDirectory = anotherTempDir.resolve("serenity").toFile();
        System.setProperty(ThucydidesSystemProperty.SERENITY_OUTPUT_DIRECTORY.getPropertyName(),
                outputDirectory.getAbsolutePath());
        runTestForClass(ScenarioWithTestSpecificDataSample.class);


        for (int i = 1; i <= 12; i++) {
            StepEventBus stepEventBus = StepEventBus.eventBusFor(String.format("[engine:junit-jupiter]/[class:net.serenitybdd.junit5.datadriven.samples.ScenarioWithTestSpecificDataSample]/[test-template:check_each_row(java.lang.String, java.lang.String, java.lang.String)]/[test-template-invocation:#%s]",i));
            List<TestOutcome> currentOutcomes = new ParameterizedTestsOutcomeAggregator(stepEventBus.getBaseStepListener()).getTestOutcomesForAllParameterSets();
            assertThat(currentOutcomes.size(), is(1));
            TestOutcome testOutcome1 = currentOutcomes.get(0);
            List<TestStep> dataDrivenSteps = testOutcome1.getTestSteps();
            assertThat(dataDrivenSteps.size(), is(1));
        }

    }


    /*@Test //TODO clarify
    public void when_test_data_is_provided_for_a_nested_step_then_a_step_should_be_reported_for_each_data_row() throws Throwable {

        File outputDirectory = tempFolder.newFolder("serenity");
        System.setProperty(ThucydidesSystemProperty.SERENITY_OUTPUT_DIRECTORY.getPropertyName(),
                outputDirectory.getAbsolutePath());
        runTestForClass(ScenarioWithNestedTestSpecificDataSample.class);

        List<TestOutcome> testOutcomes = new ParameterizedTestsOutcomeAggregator().aggregateTestOutcomesByTestMethods();

        assertThat(testOutcomes.size(), is(1));
        TestOutcome testOutcome1 = testOutcomes.get(0);

        List<TestStep> allDataDrivenSteps =  testOutcome1.getTestSteps();
        assertThat(allDataDrivenSteps.size(), is(12));

        List<TestStep> dataDrivenSteps = testOutcome1.getTestSteps().get(0).getChildren();
        //TODO check for correctness
        assertThat(dataDrivenSteps.get(0).getChildren().size(), is(2));

    }*/

    @TempDir
    Path stepTitleTmpDir;


    @Test
    @Disabled("Unstable on Windows: to review")
    public void test_step_data_should_appear_in_the_step_titles() throws Throwable {

        File outputDirectory = stepTitleTmpDir.resolve("serenity").toFile();
        //File outputDirectory = tempFolder.newFolder("serenity");
        System.setProperty(ThucydidesSystemProperty.SERENITY_OUTPUT_DIRECTORY.getPropertyName(), outputDirectory.getAbsolutePath());
        runTestForClass(ScenarioWithTestSpecificDataSample.class);

        List<TestOutcome> testOutcomes = new ParameterizedTestsOutcomeAggregator().aggregateTestOutcomesByTestMethods();

        TestOutcome testOutcome1 = testOutcomes.get(0);
        List<TestStep> dataDrivenSteps = testOutcome1.getTestSteps();

        TestStep step1 = dataDrivenSteps.get(0);
        TestStep setNameStep1 = step1.getFlattenedSteps().get(0).getChildren().get(0);
        TestStep step2 = dataDrivenSteps.get(1);
        TestStep setNameStep2 = step2.getFlattenedSteps().get(0).getChildren().get(0);

        //TODO check for correctness
        assertThat(setNameStep1.getDescription(), containsString("Joe Smith"));
        assertThat(setNameStep2.getDescription(), containsString("Jack Black"));


    }

    @Test
    public void running_a_simple_parameterized_test_should_produce_an_outcome_per_data_row() throws Throwable {

        File outputDirectory = anotherTempDir.resolve("serenity").toFile();
        //File outputDirectory = tempFolder.newFolder("serenity");
        System.setProperty(ThucydidesSystemProperty.SERENITY_OUTPUT_DIRECTORY.getPropertyName(),
                outputDirectory.getAbsolutePath());
        runTestForClass(SimpleSuccessfulParameterizedTestSample.class);

        List<String> reportContents = contentsOf(reload(outputDirectory).listFiles(new JSONFileFilter()));

        assertThat(reportContents.size(), is(2));

    }





    /*
    //TODO
    @Test
    public void when_test_data_is_provided_for_a_step_a_single_test_should_be_executed() throws Throwable {

        File outputDirectory = tempFolder.newFolder("thucydides");
        environmentVariables.setProperty(ThucydidesSystemProperty.THUCYDIDES_OUTPUT_DIRECTORY.getPropertyName(),
                outputDirectory.getAbsolutePath());

        SerenityRunner runner = getNormalTestRunnerUsing(SamplePassingScenarioWithTestSpecificData.class);

        runner.run(new RunNotifier());

        List reportContents = contentsOf(reload(outputDirectory).listFiles(new JSONFileFilter()));
        assertThat(reportContents.size(), is(1));
    }

    @Test
    //TODO
    public void browser_should_be_restarted_periodically_if_requested() throws Throwable {

        File outputDirectory = tempFolder.newFolder("thucydides");
        environmentVariables.setProperty(ThucydidesSystemProperty.THUCYDIDES_OUTPUT_DIRECTORY.getPropertyName(), outputDirectory.getAbsolutePath());
        environmentVariables.setProperty("thucydides.restart.browser.frequency", "5");

        SerenityParameterizedRunner runner = getTestRunnerUsing(SampleSingleSessionDataDrivenScenario.class);

        runner.run(new RunNotifier());
    }



    @RunWith(SerenityRunner.class)
    public static class ScenarioWithDeeplyNestedTestSpecificDataSample {

        @Managed(driver = "chrome", options="--headless")
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

    @Test
    public void when_test_data_is_provided_for_a_deeply_nested_step_then_a_step_should_be_reported_for_each_data_row() throws Throwable {

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
    public void when_the_Concurrent_annotation_is_used_tests_should_be_run_in_parallel() throws Throwable {

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
    public void the_Concurrent_annotation_indicates_that_tests_should_be_run_in_parallel() throws Throwable {

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
            return Arrays.asList(new Object[][]{});
        }
        @Test
        public void foo() {
        }

    }

    @Test
    public void the_number_of_threads_can_be_overridden_in_the_concurrent_annotation() throws Throwable {

        SerenityParameterizedRunner runner
                = getTestRunnerUsing(ParallelDataDrivenScenarioWithSpecifiedThreadCountSample.class);
        int threadCount = runner.getThreadCountFor(ParallelDataDrivenScenarioWithSpecifiedThreadCountSample.class);

        assertThat(threadCount, is(7));

    }

    @Test
    public void the_number_of_threads_can_be_overridden_with_a_system_property() throws Throwable {

        environmentVariables.setProperty("thucydides.concurrent.threads", "4");
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
            return Arrays.asList(new Object[][]{});
        }

        @Test
        public void foo() {
        }
    }

    @Test
    public void the_number_of_threads_can_be_overridden_in_the_concurrent_annotation_using_a_relative_value() throws Throwable {

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
            return Arrays.asList(new Object[][]{});
        }

        @Test
        public void foo() {
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void if_the_thread_count_is_invalid_an_exception_should_be_thrown() throws Throwable {

        SerenityParameterizedRunner runner
                = getTestRunnerUsing(ParallelDataDrivenScenarioWithInvalidThreadCountSample.class);
        runner.getThreadCountFor(ParallelDataDrivenScenarioWithInvalidThreadCountSample.class);

    }

    private List<String> filenamesOf(File[] files) {
        List filenames = new ArrayList<String>();
        for (File file : files) {
            filenames.add(file.getName());
        }
        return filenames;
    }


    private List<String> contentsOf(File[] files) throws IOException {
        List<String> contents = new ArrayList<>();
        for (File file : files) {
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
    public void a_separate_html_report_should_be_generated_from_each_scenario() throws Throwable {

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
            return filename.endsWith(".json") && !filename.startsWith("manifest");
        }
    }

    protected SerenityRunner getNormalTestRunnerUsing(Class<?> testClass) throws Throwable {
        DriverConfiguration configuration = new WebDriverConfiguration(environmentVariables);
        WebDriverFactory factory = new WebDriverFactory(environmentVariables);
        return new SerenityRunner(testClass, factory, configuration);
    }

    protected SerenityParameterizedRunner getTestRunnerUsing(Class<?> testClass) throws Throwable {
        DriverConfiguration configuration = new WebDriverConfiguration(environmentVariables);
        WebDriverFactory factory = new WebDriverFactory(environmentVariables);
        BatchManager batchManager = new BatchManagerProvider(configuration).get();
        return new SerenityParameterizedRunner(testClass, configuration, factory, batchManager);
    }

    protected SerenityParameterizedRunner getStubbedTestRunnerUsing(Class<?> testClass) throws Throwable {
        DriverConfiguration configuration = new WebDriverConfiguration(environmentVariables);
        WebDriverFactory factory = new WebDriverFactory(environmentVariables);
        BatchManager batchManager = new BatchManagerProvider(configuration).get();
        return new SerenityParameterizedRunner(testClass, configuration, factory, batchManager) {
            @Override
            public void generateReports() {
                //do nothing
            }
        };
    }*/

}
