package net.thucydides.core.steps;

import net.serenitybdd.annotations.Pending;
import net.serenitybdd.annotations.Story;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestResult;
import net.thucydides.model.steps.ExecutedStepDescription;
import net.thucydides.model.steps.StepFailure;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

public class WhenTallyingTestStepResults {

    TestResultTally resultTally;

    @Mock
    StepFailure stepFailure1;

    @Mock
    StepFailure stepFailure2;

    @Mock
    ExecutedStepDescription description;

    @Mock
    TestOutcome testOutcome;

    @Mock
    File outputDirectory;

    class ClassUnderTest {
        public void someTest(){}
    }

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        resultTally = TestResultTally.forTestClass(ClassUnderTest.class);
        when(description.getName()).thenReturn("Some test");

        when(stepFailure1.getDescription()).thenReturn(description);
        when(stepFailure1.getException()).thenReturn(new AssertionError("Oops!"));
        when(stepFailure2.getDescription()).thenReturn(description);
        when(stepFailure2.getException()).thenReturn(new AssertionError("Oops!"));
    }

    @Test
    public void should_be_able_to_log_test_failures() {
        resultTally.logFailure(stepFailure1);

        assertThat(resultTally.getFailures(), hasItem(stepFailure1));
    }
    
    @Test
    public void should_know_what_test_class_the_results_come_from() {
        assertThat(resultTally.getClassUnderTest().getSimpleName(), is("ClassUnderTest"));
    }


    @Test
    public void should_be_able_to_count_step_failures() {
        resultTally.logFailure(stepFailure1);
        resultTally.logFailure(stepFailure2);

        assertThat(resultTally.getFailureCount(), is(2));
    }


    @Test
    public void should_be_able_to_count_executed_steps() {
        resultTally.logExecutedTest();
        resultTally.logExecutedTest();

        assertThat(resultTally.getRunCount(), is(2));
    }

    @Test
    public void should_be_able_to_count_ignored_steps() {
        resultTally.logIgnoredTest();
        resultTally.logIgnoredTest();
        resultTally.logIgnoredTest();

        assertThat(resultTally.getIgnoreCount(), is(3));
    }

    @Test
    public void a_test_run_succeeds_if_there_is_a_step_failure() {
        resultTally.logExecutedTest();
        assertThat(resultTally.wasSuccessful(), is(true));
    }

    @Test
    public void a_test_run_succeeds_if_all_steps_are_ignored() {
        resultTally.logExecutedTest();
        assertThat(resultTally.wasSuccessful(), is(true));
    }

    @Test
    public void a_test_run_succeeds_if_no_steps_are_executed() {
        assertThat(resultTally.wasSuccessful(), is(true));
    }

    @Test
    public void a_test_run_fails_if_there_is_a_step_failure() {
        resultTally.logExecutedTest();
    }

    @Test
    public void result_tally_should_also_keep_track_of_the_overall_test_result() {
        assertThat(resultTally.wasSuccessful(), is(true));
    }

    @Test
    public void no_tests_should_have_initially_failed() {
        BaseStepListener baseStepListener = new BaseStepListener(FirefoxDriver.class, outputDirectory);

        assertThat(baseStepListener.aStepHasFailed(), is(false));
    }

    class MyStory {}

    @Story(MyStory.class)
    class MyTestCase {
        public void app_should_work() {}
        @Ignore
        public void ignored_test() {}
        @Pending
        public void pending_test() {}

        public void normal_test() {}
    }

    @Test
    public void a_test_suite_can_be_started_directly_using_a_story_class() {
        BaseStepListener stepListener = new BaseStepListener(FirefoxDriver.class, outputDirectory);
        stepListener.testSuiteStarted(MyStory.class);
        stepListener.testStarted("app_should_work");
        assertThat(stepListener.getCurrentTestOutcome().getUserStory().getName(), is("MyStory"));
    }

    @Test
    public void pending_tests_should_be_marked_as_pending() {
        BaseStepListener stepListener = new BaseStepListener(FirefoxDriver.class, outputDirectory);
        stepListener.testSuiteStarted(MyTestCase.class);
        stepListener.testStarted("pending_test");
        assertThat(stepListener.getCurrentTestOutcome().getResult() , is(TestResult.PENDING));
    }

    @Test
    public void ignored_tests_should_be_marked_as_ignored() {
        BaseStepListener stepListener = new BaseStepListener(FirefoxDriver.class, outputDirectory);
        stepListener.testSuiteStarted(MyTestCase.class);
        stepListener.testStarted("ignored_test");
        assertThat(stepListener.getCurrentTestOutcome().getResult() , is(TestResult.IGNORED));
    }

    @Test
    public void tests_can_be_marked_as_ignored_externally() {
        BaseStepListener stepListener = new BaseStepListener(FirefoxDriver.class, outputDirectory);
        stepListener.testSuiteStarted(MyTestCase.class);
        stepListener.testStarted("normal_test");
        stepListener.testIgnored();
        assertThat(stepListener.getCurrentTestOutcome().getResult() , is(TestResult.IGNORED));
    }


    @Test
    public void a_test_suite_can_be_started_directly_using_a_story_class_without_a_test_class() {
        BaseStepListener stepListener = new BaseStepListener(FirefoxDriver.class, outputDirectory);
        stepListener.testSuiteStarted(MyStory.class);
        stepListener.testStarted("the app should work");
        assertThat(stepListener.getCurrentTestOutcome().getUserStory().getName(), is("MyStory"));
    }

    @Test
    public void a_test_suite_can_be_started_directly_using_a_story_instance_without_a_test_class() {
        BaseStepListener stepListener = new BaseStepListener(FirefoxDriver.class, outputDirectory);
        net.thucydides.model.domain.Story story = net.thucydides.model.domain.Story.from(MyStory.class);
        stepListener.testSuiteStarted(story);
        stepListener.testStarted("the app should work");
        assertThat(stepListener.getCurrentTestOutcome().getUserStory().getName(), is("MyStory"));
    }

    @Test
    public void we_can_check_if_a_test_suite_is_currently_running() {
        net.thucydides.model.domain.Story story = net.thucydides.model.domain.Story.from(MyStory.class);
        BaseStepListener stepListener = new BaseStepListener(FirefoxDriver.class, outputDirectory);

        assertThat(stepListener.testSuiteRunning(), is(false));
        stepListener.testSuiteStarted(story);
        assertThat(stepListener.testSuiteRunning(), is(true));
        stepListener.testStarted("the app should work");
        stepListener.testSuiteFinished();
        assertThat(stepListener.testSuiteRunning(), is(false));
    }


    @Test
    public void a_test_can_be_started_using_a_story_instance_without_a_test_class() {

        BaseStepListener stepListener = new BaseStepListener(FirefoxDriver.class, outputDirectory);
        net.thucydides.model.domain.Story story = net.thucydides.model.domain.Story.from(MyStory.class);
        stepListener.testSuiteStarted(story);
        stepListener.testStarted("the app should work");
        assertThat(stepListener.getCurrentTestOutcome().getUserStory().getName(), is("MyStory"));
    }


    @Test
    public void should_be_able_to_update_a_step_title_if_it_is_not_known_initially() {

        BaseStepListener stepListener = new BaseStepListener(FirefoxDriver.class, outputDirectory);
        net.thucydides.model.domain.Story story = net.thucydides.model.domain.Story.from(MyStory.class);
        stepListener.testSuiteStarted(story);
        stepListener.testStarted("the app should work");
        stepListener.stepStarted(ExecutedStepDescription.withTitle("provisory title"));
        stepListener.updateCurrentStepTitle("final title");
        assertThat(stepListener.getCurrentTestOutcome().getTestSteps().get(0).getDescription() , is("final title"));
    }

    @Test
    public void should_be_able_to_create_a_new_step_when_defining_the_title_if_no_step_exists() {

        BaseStepListener stepListener = new BaseStepListener(FirefoxDriver.class, outputDirectory);
        net.thucydides.model.domain.Story story = net.thucydides.model.domain.Story.from(MyStory.class);
        stepListener.testSuiteStarted(story);
        stepListener.testStarted("the app should work");
        stepListener.updateCurrentStepTitle("final title");
        assertThat(stepListener.getCurrentTestOutcome().getTestSteps().get(0).getDescription() , is("final title"));
    }


    @Test
    public void should_keep_track_of_when_a_test_has_failed() {
        BaseStepListener stepListener = new BaseStepListener(FirefoxDriver.class, outputDirectory);
        stepListener.testSuiteStarted(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        stepListener.stepStarted(ExecutedStepDescription.withTitle("A test"));

        StepFailure failure = new StepFailure(ExecutedStepDescription.withTitle("Oops!"), new AssertionError());
        stepListener.stepFailed(failure);
        assertThat(stepListener.aStepHasFailed(), is(true));
    }


    @Test
    public void step_failures_can_be_raised_after_the_last_step() {
        BaseStepListener stepListener = new BaseStepListener(FirefoxDriver.class, outputDirectory);
        stepListener.testSuiteStarted(MyTestCase.class);
        stepListener.testStarted("app_should_work");

        stepListener.stepStarted(ExecutedStepDescription.withTitle("A test"));
        stepListener.stepFinished();

        StepFailure failure = new StepFailure(ExecutedStepDescription.withTitle("Oops!"), new AssertionError());
        stepListener.lastStepFailed(failure);
        assertThat(stepListener.aStepHasFailed(), is(true));
    }



    @Test
    public void test_failures_should_be_reset_at_the_start_of_each_test_case() {
        BaseStepListener stepListener = new BaseStepListener(FirefoxDriver.class, outputDirectory);
        stepListener.testSuiteStarted(MyTestCase.class);
        stepListener.testStarted("app_should_work");
        stepListener.stepStarted(ExecutedStepDescription.withTitle("A test"));

        StepFailure failure = new StepFailure(ExecutedStepDescription.withTitle("Oops!"), new AssertionError());

        stepListener.stepFailed(failure);
        assertThat(stepListener.aStepHasFailed(), is(true));

        stepListener.testFinished(testOutcome);
        stepListener.testStarted("app_should_still_work");

        assertThat(stepListener.aStepHasFailed(), is(false));
    }

}
