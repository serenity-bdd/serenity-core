package net.thucydides.core.steps;

import net.serenitybdd.annotations.Story;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.samples.FlatScenarioStepsWithoutPages;
import net.thucydides.core.util.ExtendedTemporaryFolder;
import net.thucydides.model.configuration.SystemPropertiesConfiguration;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestResult;
import net.thucydides.model.environment.MockEnvironmentVariables;
import net.thucydides.model.webdriver.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * We record step execution results using a StepListener.
 * The BaseStepListener implementation provides most of the basic functionality
 * for recording and structuring step results.
 */
public class WhenRecordingStepExecutionResultsForNonWebTests {

    BaseStepListener stepListener;

    StepFactory stepFactory;

    @Rule
    public ExtendedTemporaryFolder temporaryFolder = new ExtendedTemporaryFolder();

    File outputDirectory;

    @Mock
    TestOutcome testOutcome;

    class AStory {
    }

    class AnotherStory {
    }

    @Story(AStory.class)
    class ATestCase {
        public void app_should_work() {
        }

        public void app_should_still_work() {
        }
    }

    class AStepLibrary extends ScenarioSteps {
        AStepLibrary(Pages pages) {
            super(pages);
        }
    }

    MockEnvironmentVariables environmentVariables;

    Configuration configuration;


    @Before
    public void createStepListenerAndFactory() throws IOException {
        MockitoAnnotations.initMocks(this);
        outputDirectory = temporaryFolder.newFolder("thucydides");

        environmentVariables = new MockEnvironmentVariables();
        configuration = new SystemPropertiesConfiguration(environmentVariables);

        stepFactory = StepFactory.getFactory();
        stepListener = new BaseStepListener(null, outputDirectory, configuration);

        StepEventBus.getParallelEventBus().reset();
        StepEventBus.getParallelEventBus().registerListener(stepListener);
    }

    @After
    public void dropListener() {
        StepEventBus.getParallelEventBus().dropListener(stepListener);
    }


    class MyStory {
    }

    class MyOtherStory {
    }

    @Story(MyStory.class)
    class MyTestCase {
        public void app_should_work() {
        }
    }

    @Story(MyOtherStory.class)
    class MyOtherTestCase {
        public void app_should_work() {
        }
    }

    class MyTestCaseWithoutAStory {
        public void app_should_work() {
        }
    }

    @Test
    public void the_listener_should_record_basic_step_execution_for_non_webtest_steps() {

        StepEventBus.getParallelEventBus().testStarted("app_should_work", MyTestCase.class);

        FlatScenarioStepsWithoutPages steps = stepFactory.getSharedStepLibraryFor(FlatScenarioStepsWithoutPages.class);

        steps.step_one();
        steps.step_two();

        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        assertThat(results.size(), is(1));
        assertThat(results.get(0).toString(), is("App should work:Step one, Step two"));
    }


    @Test
    public void the_listener_should_record_the_tested_story_for_non_webtest_steps() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work", MyTestCase.class);

        FlatScenarioStepsWithoutPages steps = stepFactory.getSharedStepLibraryFor(FlatScenarioStepsWithoutPages.class);

        steps.step_one();
        steps.step_two();

        StepEventBus.getParallelEventBus().testFinished(testOutcome);
        StepEventBus.getParallelEventBus().testSuiteFinished();
        TestOutcome outcome = stepListener.getTestOutcomes().get(0);
        assertThat(outcome.getUserStory().getName(), is("MyStory"));
    }

    @Test
    public void the_step_listener_should_record_the_overall_test_result_for_non_webtest_steps() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioStepsWithoutPages steps = stepFactory.getSharedStepLibraryFor(FlatScenarioStepsWithoutPages.class);
        steps.step_one();
        steps.step_two();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.getResult(), is(TestResult.SUCCESS));
    }


    @Test
    public void a_failing_step_should_record_the_failure_for_non_webtest_steps() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioStepsWithoutPages steps = stepFactory.getSharedStepLibraryFor(FlatScenarioStepsWithoutPages.class);
        steps.step_one();
        steps.failingStep();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);
        assertThat(testOutcome.getResult(), is(TestResult.FAILURE));
    }

    @Test
    public void a_failing_step_should_record_the_failure_cause_for_non_webtest_steps() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioStepsWithoutPages steps = stepFactory.getSharedStepLibraryFor(FlatScenarioStepsWithoutPages.class);
        steps.step_one();
        steps.failingStep();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);
        assertThat(testOutcome.getTestFailureCause().getMessage(), is("Step failed"));
    }


    @Test
    public void grouped_test_steps_should_appear_as_nested_for_non_webtest_steps() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioStepsWithoutPages steps = stepFactory.getSharedStepLibraryFor(FlatScenarioStepsWithoutPages.class);
        steps.step_one();
        steps.grouped_steps();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);

        assertThat(testOutcome.toString(), is("App should work:Step one, Grouped steps [Nested step one, Nested step two, Nested step one, Nested step two]"));
    }

    @Test
    public void if_all_child_steps_are_ignored_the_overall_step_should_be_ignored() {

        StepEventBus.getParallelEventBus().testSuiteStarted(MyTestCase.class);
        StepEventBus.getParallelEventBus().testStarted("app_should_work");

        FlatScenarioStepsWithoutPages steps =  stepFactory.getSharedStepLibraryFor(FlatScenarioStepsWithoutPages.class);
        steps.failingAssumption();
        steps.step_one();
        steps.step_two();
        steps.step_three();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        List<TestOutcome> results = stepListener.getTestOutcomes();
        TestOutcome testOutcome = results.get(0);
        assertThat(testOutcome.getResult(), is(TestResult.IGNORED));
    }
}
