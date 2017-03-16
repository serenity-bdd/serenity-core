package net.serenitybdd.junit.runners.integration;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.serenitybdd.junit.runners.AbstractTestStepRunnerTest;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Pending;
import net.thucydides.core.guice.ThucydidesModule;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.junit.rules.DisableThucydidesHistoryRule;
import net.thucydides.samples.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class WhenRunningANonWebTestScenario extends AbstractTestStepRunnerTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public DisableThucydidesHistoryRule disableThucydidesHistoryRule = new DisableThucydidesHistoryRule();

    Injector injector;

    @Before
    public void createATestableDriverFactory() throws Exception {
        MockitoAnnotations.initMocks(this);
        injector = Guice.createInjector(new ThucydidesModule());
        StepEventBus.getEventBus().clear();
    }

    @Test
    public void the_test_runner_records_the_steps_as_they_are_executed() throws InitializationError {

        SerenityRunner runner = new SerenityRunner(SamplePassingNonWebScenario.class, injector);
        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        assertThat(executedSteps.size(), is(3));

        assertThat(inTheTesOutcomes(executedSteps).theOutcomeFor("happy_day_scenario").getTitle(), is("Happy day scenario"));
        assertThat(inTheTesOutcomes(executedSteps).theOutcomeFor("edge_case_1").getTitle(), is("Edge case 1"));
        assertThat(inTheTesOutcomes(executedSteps).theOutcomeFor("edge_case_2").getTitle(), is("Edge case 2"));
    }


    @Test
    public void tests_marked_as_pending_should_be_pending() throws InitializationError {

        SerenityRunner runner = new SerenityRunner(SamplePassingNonWebScenarioWithPendingTests.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        assertThat(executedSteps.size(), is(3));

        assertThat(inTheTesOutcomes(executedSteps).theResultFor("happy_day_scenario"), is(TestResult.SUCCESS));
        assertThat(inTheTesOutcomes(executedSteps).theResultFor("edge_case_1"), is(TestResult.PENDING));
        assertThat(inTheTesOutcomes(executedSteps).theResultFor("edge_case_2"), is(TestResult.PENDING));
    }

    @Test
    public void tests_with_failing_assumptions_should_be_ignored() throws InitializationError {

        SerenityRunner runner = new SerenityRunner(SampleScenarioWithFailingAssumption.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        assertThat(executedSteps.size(), is(3));

        assertThat(inTheTesOutcomes(executedSteps).theResultFor("happy_day_scenario"), is(TestResult.IGNORED));
    }


    @Test
    public void tests_marked_as_ignored_should_be_skipped() throws InitializationError {

        SerenityRunner runner = new SerenityRunner(SamplePassingNonWebScenarioWithIgnoredTests.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        assertThat(executedSteps.size(), is(3));

        assertThat(inTheTesOutcomes(executedSteps).theResultFor("happy_day_scenario"), is(TestResult.SUCCESS));
        assertThat(inTheTesOutcomes(executedSteps).theResultFor("edge_case_1"), is(TestResult.IGNORED));
        assertThat(inTheTesOutcomes(executedSteps).theResultFor("edge_case_2"), is(TestResult.IGNORED));

    }

    @Test
    public void tests_marked_as_manual_should_be_skipped_and_be_flagged_as_manual_tests() throws InitializationError {

        SerenityRunner runner = new SerenityRunner(SamplePassingNonWebScenarioWithManualTests.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        assertThat(inTheTesOutcomes(executedSteps).theResultFor("a_manual_test"), is(TestResult.PENDING));
        assertThat(inTheTesOutcomes(executedSteps).theOutcomeFor("a_manual_test").isManual(), equalTo(true));
        assertThat(inTheTesOutcomes(executedSteps).theOutcomeFor("a_manual_test").getTags(),
                hasItem(TestTag.withName("Manual").andType("External Tests")));
    }

    public static final class ATestWithNoSteps {
        @Test
        public void test_with_no_steps() {}
    }

    @Test
    public void tests_with_no_steps_should_be_marked_as_successful() throws InitializationError {

        SerenityRunner runner = new SerenityRunner(ATestWithNoSteps.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();

        assertThat(inTheTesOutcomes(executedSteps).theResultFor("test_with_no_steps"), is(TestResult.SUCCESS));
    }

    public static final class AnIgnoredTest {
        @Test
        public void previous_test() {}

        @Ignore
        @Test
        public void ignored_test() {}

        @Test
        public void following_test() {}
    }

    @Test
    public void ignored_tests_should_be_flagged_as_ignored() throws InitializationError {

        SerenityRunner runner = new SerenityRunner(AnIgnoredTest.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();

        assertThat(inTheTesOutcomes(executedSteps).theResultFor("previous_test"), is(TestResult.SUCCESS));
        assertThat(inTheTesOutcomes(executedSteps).theResultFor("ignored_test"), is(TestResult.IGNORED));
        assertThat(inTheTesOutcomes(executedSteps).theResultFor("following_test"), is(TestResult.SUCCESS));
    }

    public static final class APendingTest {
        @Test
        public void previous_test() {}

        @Pending
        @Test
        public void pending_test() {}

        @Test
        public void following_test() {}
    }

    @Test
    public void pending_tests_should_be_flagged_as_pending() throws InitializationError {

        SerenityRunner runner = new SerenityRunner(APendingTest.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();

        assertThat(inTheTesOutcomes(executedSteps).theResultFor("previous_test"), is(TestResult.SUCCESS));
        assertThat(inTheTesOutcomes(executedSteps).theResultFor("pending_test"), is(TestResult.PENDING));
        assertThat(inTheTesOutcomes(executedSteps).theResultFor("following_test"), is(TestResult.SUCCESS));
    }


    @Test
    public void tests_should_be_run_after_an_assertion_error() throws InitializationError {

        SerenityRunner runner = new SerenityRunner(SampleNonWebScenarioWithError.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        assertThat(executedSteps.size(), is(3));

        assertThat(inTheTesOutcomes(executedSteps).theResultFor("happy_day_scenario"), is(TestResult.FAILURE));
        assertThat(inTheTesOutcomes(executedSteps).theResultFor("edge_case_1"), is(TestResult.SUCCESS));
        assertThat(inTheTesOutcomes(executedSteps).theResultFor("edge_case_2"), is(TestResult.SUCCESS));
    }

    @Test
    public void failing_tests_with_no_steps_should_still_record_the_error() throws InitializationError {

        SerenityRunner runner = new SerenityRunner(SampleNonWebScenarioWithError.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();

        assertThat(inTheTesOutcomes(executedSteps).theResultFor("happy_day_scenario"), is(TestResult.FAILURE));
        assertThat(inTheTesOutcomes(executedSteps).theOutcomeFor("happy_day_scenario").getTestFailureMessage(),
                                    is("Oh bother!"));
    }

    @Test
    public void the_test_runner_skips_any_tests_after_a_failure() throws Exception {

        SerenityRunner runner = new SerenityRunner(SampleTestScenario.class);

        runner.run(new RunNotifier());
        List<TestOutcome> executedScenarios = runner.getTestOutcomes();
        TestOutcome testOutcome = testOutcomeWithTitle("Happy day scenario", executedScenarios);

        List<TestStep> steps = testOutcome.getTestSteps();
        assertThat(steps.size(), is(6));
        assertThat(steps.get(0).isSuccessful(), is(true));
        assertThat(steps.get(1).isIgnored(), is(true));
        assertThat(steps.get(2).isPending(), is(true));
        assertThat(steps.get(3).isIgnored(), is(true));
        assertThat(steps.get(4).isIgnored(), is(true));
        assertThat(steps.get(5).isIgnored(), is(true));
    }


    @Test
    public void the_test_runner_should_notify_test_failures() throws Exception {

        SerenityRunner runner = new SerenityRunner(SingleNonWebTestScenario.class);
        RunNotifier notifier = mock(RunNotifier.class);
        runner.run(notifier);

        verify(notifier, atLeast(1)).fireTestFailure((Failure) anyObject());
    }

    @Test
    public void the_test_runner_records_the_name_of_the_test_scenario() throws InitializationError {

        SerenityRunner runner = new SerenityRunner(SamplePassingNonWebScenario.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();
        assertThat(executedScenarios.size(), greaterThan(0));
        assertThat(inTheTesOutcomes(executedScenarios).theOutcomeFor("happy_day_scenario").getTitle(), is("Happy day scenario"));

    }

    @Test
    public void the_test_runner_records_each_step_of_the_test_scenario() throws InitializationError {
        SerenityRunner runner = new SerenityRunner(SamplePassingNonWebScenario.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();
        assertThat(executedScenarios.size(), is(3));

        assertThat(inTheTesOutcomes(executedScenarios).theOutcomeFor("happy_day_scenario").getTestSteps().size(), is(4));
        assertThat(inTheTesOutcomes(executedScenarios).theOutcomeFor("edge_case_1").getTestSteps().size(), is(3));
        assertThat(inTheTesOutcomes(executedScenarios).theOutcomeFor("edge_case_2").getTestSteps().size(), is(2));
    }

    @Test
    public void the_test_runner_distinguishes_between_ignored_skipped_and_pending_steps() throws InitializationError {
        SerenityRunner runner = new SerenityRunner(SampleTestScenario.class);

        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();
        assertThat(executedScenarios.size(), is(2));
        TestOutcome testOutcome = testOutcomeWithTitle("Happy day scenario", executedScenarios);
        TestOutcome failingTestOutcome = executedScenarios.get(1);
        TestStep succeeds = testOutcome.getTestSteps().get(0);
        TestStep ignored = testOutcome.getTestSteps().get(1);
        TestStep pending = testOutcome.getTestSteps().get(2);
        TestStep failed = failingTestOutcome.getTestSteps().get(0);
        TestStep skipped = failingTestOutcome.getTestSteps().get(1);

        assertThat(succeeds.getResult(), is(TestResult.SUCCESS));
        assertThat(ignored.getResult(), is(TestResult.IGNORED));
        assertThat(pending.getResult(), is(TestResult.PENDING));
        assertThat(failed.getResult(), is(TestResult.FAILURE));
        assertThat(skipped.getResult(), is(TestResult.SKIPPED));
    }


    @Test
    public void the_test_runner_should_not_store_screenshots_for_non_web_tests() throws Exception {

        SerenityRunner runner = new SerenityRunner(SingleNonWebTestScenario.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();
        TestOutcome testOutcome = testOutcomeWithTitle("Happy day scenario", executedScenarios);

        List<TestStep> steps = testOutcome.getTestSteps();
        assertThat(steps.get(0).getScreenshots().size(), is(0));


    }


    @Test
    public void the_test_runner_records_each_step_with_a_nice_name_when_steps_have_parameters() throws InitializationError {

        SerenityRunner runner = new SerenityRunner(NonWebTestScenarioWithParameterizedSteps.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();

        TestOutcome testOutcome = testOutcomeWithTitle("Happy day scenario", executedScenarios);
        TestStep firstStep = testOutcome.getTestSteps().get(0);

        assertThat(firstStep.getDescription(), containsString("Step with a parameter:"));
        assertThat(firstStep.getDescription(), containsString("proportionOf"));
    }

    @Test
    public void should_not_fail_test_if_an_exception_is_expected() throws InitializationError {

        SerenityRunner runner = new SerenityRunner(NonWebTestScenarioWithParameterizedSteps.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();

        TestOutcome testOutcome = testOutcomeWithTitle("Should throw correct exception", executedScenarios);
        TestStep firstStep = testOutcome.getTestSteps().get(0);
        assertThat(firstStep.getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void should_report_nested_class_parameters_correctly() throws InitializationError {

        SerenityRunner runner = new SerenityRunner(NonWebTestScenarioWithParameterizedSteps.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();

        TestOutcome testOutcome = testOutcomeWithTitle("Should handle nested object parameters", executedScenarios);
        TestStep firstStep = testOutcome.getTestSteps().get(0);
        assertThat(firstStep.getDescription(), is("a step with an object parameter called $100.00"));
    }

    @Test
    public void should_report_correctly_customized_title_for_parameter_with_comma() throws InitializationError {

        SerenityRunner runner = new SerenityRunner(NonWebTestScenarioWithParameterizedSteps.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();

        TestOutcome testOutcome = testOutcomeWithTitle("Should be correct customized title for parameter with comma", executedScenarios);
        TestStep firstStep = testOutcome.getTestSteps().get(0);
        assertThat(firstStep.getDescription(), is("a step with two object parameters called 'Joe, Smith' and '20'"));
    }

    private TestOutcome testOutcomeWithTitle(String title, List<TestOutcome> testOutcomes) {
        for(TestOutcome testOutcome : testOutcomes) {
            if (testOutcome.getTitle().startsWith(title)) {
                return testOutcome;
            }
        };
        return null;
    }

    @Test
    public void the_test_runner_records_each_step_with_a_nice_name_when_steps_have_multiple_parameters() throws InitializationError {

        SerenityRunner runner = new SerenityRunner(NonWebTestScenarioWithParameterizedSteps.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();

        TestOutcome testOutcome = testOutcomeWithTitle("Happy day scenario", executedScenarios);
        TestStep secondStep = testOutcome.getTestSteps().get(1);

        assertThat(secondStep.getDescription(), containsString("Step with two parameters"));
        assertThat(secondStep.getDescription(), containsString("proportionOf, 2"));
    }




    class TestableSerenityRunnerSample extends SerenityRunner {

        private final File testOutputDirectory;

        public TestableSerenityRunnerSample(final Class<?> klass, File outputDirectory) throws InitializationError {
            super(klass);
            testOutputDirectory = outputDirectory;
        }

        public TestableSerenityRunnerSample(Class<?> klass,
                                              WebDriverFactory webDriverFactory,
                                              File outputDirectory) throws InitializationError {
            super(klass, webDriverFactory);
            testOutputDirectory = outputDirectory;
        }

        @Override
        public File getOutputDirectory() {
            return testOutputDirectory;
        }
    }

    @Test
    public void html_test_results_are_written_to_the_output_directory()  throws Exception {

        File outputDirectory =  temporaryFolder.newFolder();

        SerenityRunner runner = new TestableSerenityRunnerSample(SamplePassingNonWebScenario.class,outputDirectory);
        runner.run(new RunNotifier());

        List<String> generatedHtmlReports = Arrays.asList(outputDirectory.list(new HTMLFileFilter()));
        assertThat(generatedHtmlReports.size(), is(3));
    }

    @Test
    public void json_test_results_are_written_to_the_output_directory()  throws Exception {

        File outputDirectory = temporaryFolder.newFolder();

        SerenityRunner runner = new TestableSerenityRunnerSample(SamplePassingNonWebScenario.class,
                outputDirectory);
        runner.run(new RunNotifier());

        List<String> generatedHtmlReports = Arrays.asList(outputDirectory.list(new JSONFileFilter()));
        assertThat(generatedHtmlReports.size(), is(3));
    }

    private class HTMLFileFilter implements FilenameFilter {
        public boolean accept(File file, String filename) {
            return filename.toLowerCase().endsWith(".html");
        }
    }
    private class JSONFileFilter implements FilenameFilter {
        public boolean accept(File file, String filename) {
            return filename.toLowerCase().endsWith(".json");
        }
    }
}
