package net.serenitybdd.junit5.samples.integration;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.serenitybdd.junit5.AbstractTestStepRunnerTest;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.junit5.extensions.TemporaryFolderExtension;
import net.thucydides.core.annotations.Pending;
import net.thucydides.core.guice.ThucydidesModule;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.samples.*;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.AssumptionViolatedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

import static net.serenitybdd.core.environment.ConfiguredEnvironment.getConfiguration;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import net.serenitybdd.junit5.TestLauncher;

@ExtendWith(TemporaryFolderExtension.class)
public class WhenRunningANonWebTestScenario extends AbstractTestStepRunnerTest {


    Injector injector;

    @BeforeEach
    public void createATestableDriverFactory() throws Exception {
        MockitoAnnotations.initMocks(this);
        injector = Guice.createInjector(new ThucydidesModule());
        StepEventBus.getParallelEventBus().clear();
        for(File f : getConfiguration().getOutputDirectory().listFiles())
        {
            f.delete();
        }
    }

    @Test
    @ExtendWith(TemporaryFolderExtension.class)
    public void the_test_runner_records_the_steps_as_they_are_executed() {

        TestLauncher.runTestForClass(SamplePassingNonWebScenario.class);
        List<TestOutcome> executedSteps = StepEventBus.getParallelEventBus().getBaseStepListener().getTestOutcomes();
        assertThat(executedSteps.size(), is(3));


        assertThat(inTheTestOutcomes(executedSteps).theOutcomeFor("happy_day_scenario").getTitle(), is("Happy day scenario"));
        assertThat(inTheTestOutcomes(executedSteps).theOutcomeFor("edge_case_1").getTitle(), is("Edge case 1"));
        assertThat(inTheTestOutcomes(executedSteps).theOutcomeFor("edge_case_2").getTitle(), is("Edge case 2"));
        //assertThat(inTheTestOutcomes(executedSteps).theOutcomeFor("My Custom Display Name For Edge Case 2").getTitle(), is("My Custom Display Name For Edge Case 2"));
    }

   @ExtendWith(SerenityJUnit5Extension.class)
    public static final class HasAFailingAssumptionInATest {
        @Test
        public void test_with_failing_assumption() {
            Assume.assumeThat(true, is(false));
        }

        @Test
        public void following_test() {}
    }

    @Test
    public void tests_with_failing_assumptions_should_be_aborted()  {

        TestLauncher.runTestForClass(HasAFailingAssumptionInATest.class);

        List<TestOutcome> executedSteps = StepEventBus.getParallelEventBus().getBaseStepListener().getTestOutcomes();

        assertThat(inTheTestOutcomes(executedSteps).theResultFor("test_with_failing_assumption"), is(TestResult.ABORTED));
        assertThat(inTheTestOutcomes(executedSteps).theOutcomeFor("test_with_failing_assumption").getTestFailureCause().asException(), Matchers.instanceOf(AssumptionViolatedException.class));
    }


    @Test
    public void tests_marked_as_manual_should_be_flagged_as_manual_tests()  {

        TestLauncher.runTestForClass(SamplePassingNonWebScenarioWithManualTests.class);

        List<TestOutcome> executedSteps = StepEventBus.getParallelEventBus().getBaseStepListener().getTestOutcomes();
        assertThat(inTheTestOutcomes(executedSteps).theResultFor("a_manual_test"), is(TestResult.PENDING));
        assertThat(inTheTestOutcomes(executedSteps).theOutcomeFor("a_manual_test").isManual(), equalTo(true));
        assertThat(inTheTestOutcomes(executedSteps).theOutcomeFor("a_manual_test").getTags(),
                hasItem(TestTag.withName("manual").andType("tag")));
    }

    @Test
    public void tests_marked_as_manual_should_be_given_the_requested_result_if_specified()  {

        TestLauncher.runTestForClass(SamplePassingNonWebScenarioWithManualTests.class);

        List<TestOutcome> executedSteps = StepEventBus.getParallelEventBus().getBaseStepListener().getTestOutcomes();
        assertThat(inTheTestOutcomes(executedSteps).theResultFor("a_failing_manual_test"), is(TestResult.FAILURE));
        assertThat(inTheTestOutcomes(executedSteps).theOutcomeFor("a_manual_test").isManual(), equalTo(true));
    }

    @ExtendWith(SerenityJUnit5Extension.class)
    public static final class ATestWithNoSteps {
        @Test
        public void test_with_no_steps() {}
    }

    @Test
    public void tests_with_no_steps_should_be_marked_as_successful()  {

        TestLauncher.runTestForClass(ATestWithNoSteps.class);

        List<TestOutcome> executedSteps = StepEventBus.getParallelEventBus().getBaseStepListener().getTestOutcomes();

        assertThat(inTheTestOutcomes(executedSteps).theResultFor("test_with_no_steps"), is(TestResult.SUCCESS));
    }

    @ExtendWith(SerenityJUnit5Extension.class)
    public static final class ADisabledTest {
        @Test
        public void previous_test() {}

        @Disabled
        @Test
        public void disabled_test() {}

        @Test
        public void following_test() {}
    }

    @Test
    public void disabled_tests_should_be_skipped()  {

        TestLauncher.runTestForClass(ADisabledTest.class);
        List<TestOutcome> executedSteps = StepEventBus.getParallelEventBus().getBaseStepListener().getTestOutcomes();
        assertThat(inTheTestOutcomes(executedSteps).theResultFor("previous_test"), is(TestResult.SUCCESS));
        assertThat(inTheTestOutcomes(executedSteps).theResultFor("following_test"), is(TestResult.SUCCESS));
    }

    public static boolean pendingTestWasSuspended = false;

    @ExtendWith(SerenityJUnit5Extension.class)
    public static final class APendingTest {
        @Test
        public void previous_test() {}

        @Pending
        @Test
        public void pending_test() {
            pendingTestWasSuspended = StepEventBus.getParallelEventBus().currentTestIsSuspended();
        }

        @Test
        public void following_test() {}
    }

    @Test
    public void pending_tests_should_be_flagged_as_pending()  {

        TestLauncher.runTestForClass(APendingTest.class);
        List<TestOutcome> executedSteps = StepEventBus.getParallelEventBus().getBaseStepListener().getTestOutcomes();

        assertThat(inTheTestOutcomes(executedSteps).theResultFor("previous_test"), is(TestResult.SUCCESS));
        assertThat(inTheTestOutcomes(executedSteps).theResultFor("pending_test"), is(TestResult.PENDING));
        assertThat(inTheTestOutcomes(executedSteps).theResultFor("following_test"), is(TestResult.SUCCESS));
    }

    @Test
    public void pending_tests_should_be_run_in_suspended_mode()  {

        TestLauncher.runTestForClass(APendingTest.class);

        assertThat(pendingTestWasSuspended, is(true));
    }

    @Test
    public void tests_should_be_run_after_an_assertion_error()  {
        TestLauncher.runTestForClass(SampleNonWebScenarioWithAssertionError.class);
        List<TestOutcome> executedSteps = StepEventBus.getParallelEventBus().getBaseStepListener().getTestOutcomes();
        assertThat(executedSteps.size(), is(3));

        assertThat(inTheTestOutcomes(executedSteps).theResultFor("happy_day_scenario"), is(TestResult.FAILURE));
        assertThat(inTheTestOutcomes(executedSteps).theResultFor("edge_case_1"), is(TestResult.SUCCESS));
        assertThat(inTheTestOutcomes(executedSteps).theResultFor("edge_case_2"), is(TestResult.SUCCESS));
    }

    @Test
    public void failing_assertions_with_no_steps_should_still_record_the_error() {

        TestLauncher.runTestForClass(SampleNonWebScenarioWithAssertionError.class);
        List<TestOutcome> executedSteps = StepEventBus.getParallelEventBus().getBaseStepListener().getTestOutcomes();

        assertThat(inTheTestOutcomes(executedSteps).theResultFor("happy_day_scenario"), is(TestResult.FAILURE));
        assertThat(inTheTestOutcomes(executedSteps).theOutcomeFor("happy_day_scenario").getTestFailureMessage(), is("Oh bother!"));
    }

    @Test
    public void the_test_runner_skips_any_tests_after_a_failure() {

        TestLauncher.runTestForClass(SampleTestScenario.class);
        List<TestOutcome> executedScenarios = StepEventBus.getParallelEventBus().getBaseStepListener().getTestOutcomes();
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
    public void the_test_runner_records_the_name_of_the_test_scenario()  {

        TestLauncher.runTestForClass(SamplePassingNonWebScenario.class);

        List<TestOutcome> executedScenarios = StepEventBus.getParallelEventBus().getBaseStepListener().getTestOutcomes();
        assertThat(executedScenarios.size(), greaterThan(0));
        assertThat(inTheTestOutcomes(executedScenarios).theOutcomeFor("happy_day_scenario").getTitle(), is("Happy day scenario"));

    }

//    @Test
//    public void the_test_runner_records_each_step_of_the_test_scenario()  {
//        TestLauncher.runTestForClass(SamplePassingNonWebScenario.class);
//        List<TestOutcome> executedScenarios = StepEventBus.getParallelEventBus().getBaseStepListener().getTestOutcomes();
//        assertThat(executedScenarios.size(), is(3));
//
//        assertThat(inTheTestOutcomes(executedScenarios).theOutcomeFor("happy_day_scenario").getTestSteps().size(), is(4));
//        assertThat(inTheTestOutcomes(executedScenarios).theOutcomeFor("edge_case_1").getTestSteps().size(), is(3));
//        assertThat(inTheTestOutcomes(executedScenarios).theOutcomeFor("edge_case_2").getTestSteps().size(), is(2));
//    }

    @Test
    public void the_test_runner_distinguishes_between_ignored_skipped_and_pending_steps()  {
        TestLauncher.runTestForClass(SampleTestScenario.class);
        List<TestOutcome> executedScenarios = StepEventBus.getParallelEventBus().getBaseStepListener().getTestOutcomes();

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

        TestLauncher.runTestForClass(SingleNonWebTestScenario.class);
        List<TestOutcome> executedScenarios = StepEventBus.getParallelEventBus().getBaseStepListener().getTestOutcomes();
        TestOutcome testOutcome = testOutcomeWithTitle("Happy day scenario", executedScenarios);

        List<TestStep> steps = testOutcome.getTestSteps();
        assertThat(steps.get(0).getScreenshots().size(), is(0));

    }


    @Test
    public void the_test_runner_records_each_step_with_a_nice_name_when_steps_have_parameters()  {

        TestLauncher.runTestForClass(NonWebTestScenarioWithParameterizedSteps.class);
        List<TestOutcome> executedScenarios = StepEventBus.getParallelEventBus().getBaseStepListener().getTestOutcomes();

        TestOutcome testOutcome = testOutcomeWithTitle("Happy day scenario", executedScenarios);
        TestStep firstStep = testOutcome.getTestSteps().get(0);

        assertThat(firstStep.getDescription(), containsString("Step with a parameter:"));
        assertThat(firstStep.getDescription(), containsString("proportionOf"));
    }

    @Test
    public void should_report_nested_class_parameters_correctly()  {

        TestLauncher.runTestForClass(NonWebTestScenarioWithParameterizedSteps.class);
        List<TestOutcome> executedScenarios = StepEventBus.getParallelEventBus().getBaseStepListener().getTestOutcomes();

        TestOutcome testOutcome = testOutcomeWithTitle("Should handle nested object parameters", executedScenarios);
        TestStep firstStep = testOutcome.getTestSteps().get(0);
        assertThat(firstStep.getDescription(), is("a step with an object parameter called $100.00"));
    }

    @Test
    public void should_report_correctly_customized_title_for_parameter_with_comma() {

        TestLauncher.runTestForClass(NonWebTestScenarioWithParameterizedSteps.class);
        List<TestOutcome> executedScenarios = StepEventBus.getParallelEventBus().getBaseStepListener().getTestOutcomes();
        TestOutcome testOutcome = testOutcomeWithTitle("Should be correct customized title for parameter with comma", executedScenarios);
        TestStep firstStep = testOutcome.getTestSteps().get(0);
        assertThat(firstStep.getDescription(), is("a step with two object parameters called 'Joe, Smith' and '20'"));
    }


    @Test
    public void the_test_runner_records_each_step_with_a_nice_name_when_steps_have_multiple_parameters() {

        TestLauncher.runTestForClass(NonWebTestScenarioWithParameterizedSteps.class);
        List<TestOutcome> executedScenarios = StepEventBus.getParallelEventBus().getBaseStepListener().getTestOutcomes();

        TestOutcome testOutcome = testOutcomeWithTitle("Happy day scenario", executedScenarios);
        TestStep secondStep = testOutcome.getTestSteps().get(1);

        assertThat(secondStep.getDescription(), containsString("Step with two parameters"));
        assertThat(secondStep.getDescription(), containsString("proportionOf, 2"));
    }

    @Test
    public void html_test_results_are_written_to_the_output_directory(){
        TestLauncher.runTestForClass(SamplePassingNonWebScenario.class);
        List<String> generatedHtmlReports = Arrays.asList(getConfiguration().getOutputDirectory().list(new HTMLFileFilter()));
        assertThat(generatedHtmlReports.size(), is(3));
    }

    @Test
    public void json_test_results_are_written_to_the_output_directory()  throws Exception {
        TestLauncher.runTestForClass(SamplePassingNonWebScenario.class);
        List<String> generatedHtmlReports = Arrays.asList(getConfiguration().getOutputDirectory().list(new JSONFileFilter()));
        assertThat(generatedHtmlReports.size(), is(3));
    }

    private class HTMLFileFilter implements FilenameFilter {
        public boolean accept(File file, String filename) {
            return filename.toLowerCase().endsWith(".html");
        }
    }
    private class JSONFileFilter implements FilenameFilter {
        public boolean accept(File file, String filename) {
            return filename.toLowerCase().endsWith(".json") && !filename.startsWith("manifest");
        }
    }

    private TestOutcome testOutcomeWithTitle(String title, List<TestOutcome> testOutcomes) {
        for(TestOutcome testOutcome : testOutcomes) {
            if (testOutcome.getTitle().startsWith(title)) {
                return testOutcome;
            }
        };
        return null;
    }


}
