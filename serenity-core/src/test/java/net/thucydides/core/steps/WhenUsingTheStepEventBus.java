package net.thucydides.core.steps;

import net.serenitybdd.annotations.*;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.util.ExtendedTemporaryFolder;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.environment.MockEnvironmentVariables;
import net.thucydides.model.logging.ConsoleLoggingListener;
import net.thucydides.model.steps.ExecutedStepDescription;
import net.thucydides.model.steps.StepFailure;
import net.thucydides.model.steps.StepListener;
import net.thucydides.model.util.EnvironmentVariables;
import org.junit.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

public class WhenUsingTheStepEventBus {

    @SuppressWarnings("serial")
    static class SimpleTestScenarioSteps extends ScenarioSteps {

        @Managed
        WebDriver driver;

        public SimpleTestScenarioSteps(Pages pages) {
            super(pages);
        }

        @Step
        public void assumption_failed() {
            Assume.assumeTrue(false);
        }

        @Step
        public void step1() {
            getDriver().get("step_one");
        }

        @Step
        public void step2() {
            getDriver().get("step_two");
        }

        @Step
        public void step3() {
            getDriver().get("step_three");
        }

        @Step
        public void step4() {
            step5();
            step6();
        }

        @Step
        public void step5() {
            getDriver().get("step_five");
        }

        @Step
        public void step6() {
            getDriver().get("step_six");
        }

        @Step
        public void step7() {
            step1();
            failingStep();
            step2();
        }

        @Step
        public void step8() {
            step1();
            failingStep();
            step4();
        }

        @Step
        public void step9() {
            step1();
            pendingStep();
            step4();
        }

        @Step
        public void nested_steps() {
            step1();
            nested_steps1();
            step4();
        }

        @Step
        public void nested_steps1() {
            step1();
            nested_steps2();
            step4();
        }

        @Step
        public void nested_steps2() {
            step1();
            step4();
        }

        @Step
        public void failingStep() {
            getDriver().get("failing_step");
            assertThat(true, is(false));
        }

        @Pending
        @Step
        public void pendingStep() {
        }

        @Step
        public SimpleTestScenarioSteps stepThatReturnsAStep() {
            return this;
        }

        @Step
        public SimpleTestScenarioSteps stepThatFailsAndReturnsAStep() {
            assertThat(true, is(false));
            return this;
        }

        @StepGroup
        public void legacyStepGroup() {
            step1();
            step2();
            step3();

        }
    }

    static class SampleTestScenario {

        @Steps
        SimpleTestScenarioSteps steps;

        public void sampleTest() {
            steps.step1();
            steps.step2();
            steps.step3();
        }

        public void some_test() {}
    }

    static class SampleTestScenarioWithFailedAssumption {

        @Steps
        SimpleTestScenarioSteps steps;

        public void sampleTest() {
            steps.assumption_failed();
            steps.step1();
            steps.step2();
            steps.step3();
        }

        public void some_test() {}
    }

    @Mock
    WebDriver driver;

    @Mock
    StepListener listener;

    @Mock
    TestOutcome testOutcome;

    EnvironmentVariables environmentVariables;

    ConsoleStepListener consoleStepListener;
    BaseStepListener baseStepListener;
    ConsoleLoggingListener consoleLoggingListener;

    private StepFactory factory;

    Logger logger = LoggerFactory.getLogger(Serenity.class);

    @Rule
    public ExtendedTemporaryFolder temp = new ExtendedTemporaryFolder();

    @Before
    public void initMocks() throws IOException {
        MockitoAnnotations.initMocks(this);
        environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("thucydides.logging","VERBOSE");

        factory = StepFactory.getFactory().usingPages(new Pages(driver));

        consoleStepListener = new ConsoleStepListener();
        consoleLoggingListener = new ConsoleLoggingListener(environmentVariables);
        baseStepListener = new BaseStepListener(temp.newFolder());
        StepEventBus.getParallelEventBus().reset();
        StepEventBus.getParallelEventBus().registerListener(listener);
        StepEventBus.getParallelEventBus().registerListener(consoleStepListener);
        StepEventBus.getParallelEventBus().registerListener(consoleLoggingListener);
        StepEventBus.getParallelEventBus().registerListener(baseStepListener);

    }

    @After
    public void clearListener() {
        StepEventBus.getParallelEventBus().dropAllListeners();
    }

    @Test
    public void should_execute_steps_transparently() {
        SimpleTestScenarioSteps steps = factory.getSharedStepLibraryFor(SimpleTestScenarioSteps.class);

        StepEventBus.getParallelEventBus().testStarted("some_test", SampleTestScenario.class);
        steps.step1();
        steps.step2();
        steps.step3();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        verify(driver).get("step_one");
        verify(driver).get("step_two");
        verify(driver).get("step_three");
    }

    @Test
    public void the_step_event_bus_can_be_used_to_sent_notification_events_about_steps() {
        SimpleTestScenarioSteps steps = factory.getSharedStepLibraryFor(SimpleTestScenarioSteps.class);

        StepEventBus.getParallelEventBus().testSuiteStarted(SampleTestScenario.class);
        StepEventBus.getParallelEventBus().testStarted("some_test");
        StepEventBus.getParallelEventBus().stepStarted(ExecutedStepDescription.withTitle("a step"));

        verify(listener).stepStarted(any(ExecutedStepDescription.class), any());
    }

    @Test
    public void should_notify_listeners_when_a_step_starts() {
        SimpleTestScenarioSteps steps = factory.getSharedStepLibraryFor(SimpleTestScenarioSteps.class);

        StepEventBus.getParallelEventBus().testStarted("some_test", SampleTestScenario.class);
        steps.step1();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        ArgumentCaptor<ExecutedStepDescription> argument = ArgumentCaptor.forClass(ExecutedStepDescription.class);

        verify(listener).stepStarted(argument.capture(), any());

        assertThat(argument.getValue().getName(), is("step1"));
    }

    @Test
    public void should_record_when_a_test_starts_and_finishes() {
        SimpleTestScenarioSteps steps = factory.getSharedStepLibraryFor(SimpleTestScenarioSteps.class);

        StepEventBus.getParallelEventBus().testStarted("a_test", SampleTestScenario.class);
        steps.step1();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        String expectedSteps =
                "TEST a_test\n"
                + "-step1\n"
                + "---> STEP DONE\n"
                + "TEST DONE\n";
        assertThat(consoleStepListener.toString(), is(expectedSteps));
    }

    @Test
    public void should_notify_listeners_when_the_test_suite_finishes() {
        SimpleTestScenarioSteps steps = factory.getSharedStepLibraryFor(SimpleTestScenarioSteps.class);

        StepEventBus.getParallelEventBus().testSuiteStarted(SampleTestScenario.class);
        StepEventBus.getParallelEventBus().testStarted("some_test", SampleTestScenario.class);
        steps.step1();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);
        StepEventBus.getParallelEventBus().testSuiteFinished();

        verify(listener).testSuiteFinished();
    }
    @Test
    public void should_record_nested_test_steps() {
        SimpleTestScenarioSteps steps = factory.getSharedStepLibraryFor(SimpleTestScenarioSteps.class);

        StepEventBus.getParallelEventBus().testStarted("a_test", SampleTestScenario.class);
        steps.step4();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        String expectedSteps =
                "TEST a_test\n"
                + "-step4\n"
                + "--step5\n"
                + "----> STEP DONE\n"
                + "--step6\n"
                + "----> STEP DONE\n"
                + "---> STEP DONE\n"
                + "TEST DONE\n";
        assertThat(consoleStepListener.toString(), is(expectedSteps));
    }

    @Test
    public void should_record_groups_as_nested_test_steps() {
        SimpleTestScenarioSteps steps = factory.getSharedStepLibraryFor(SimpleTestScenarioSteps.class);

        StepEventBus.getParallelEventBus().testStarted("a_test", SampleTestScenario.class);
        steps.nested_steps();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        String expectedSteps =
                "TEST a_test\n"
                + "-nested_steps\n"
                + "--step1\n"
                + "----> STEP DONE\n"
                + "--nested_steps1\n"
                + "---step1\n"
                + "-----> STEP DONE\n"
                + "---nested_steps2\n"
                + "----step1\n"
                + "------> STEP DONE\n"
                + "----step4\n"
                + "-----step5\n"
                + "-------> STEP DONE\n"
                + "-----step6\n"
                + "-------> STEP DONE\n"
                + "------> STEP DONE\n"
                + "-----> STEP DONE\n"
                + "---step4\n"
                + "----step5\n"
                + "------> STEP DONE\n"
                + "----step6\n"
                + "------> STEP DONE\n"
                + "-----> STEP DONE\n"
                + "----> STEP DONE\n"
                + "--step4\n"
                + "---step5\n"
                + "-----> STEP DONE\n"
                + "---step6\n"
                + "-----> STEP DONE\n"
                + "----> STEP DONE\n"
                + "---> STEP DONE\n"
                + "TEST DONE\n";

        assertThat(consoleStepListener.toString(), is(expectedSteps));
    }

    @Test
    public void should_record_deeply_nested_test_steps() {
        SimpleTestScenarioSteps steps = factory.getSharedStepLibraryFor(SimpleTestScenarioSteps.class);
        StepEventBus.getParallelEventBus().testStarted("a_test", SampleTestScenario.class);
        steps.legacyStepGroup();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        String expectedSteps =
                "TEST a_test\n"
                + "-legacyStepGroup\n"
                + "--step1\n"
                + "----> STEP DONE\n"
                + "--step2\n"
                + "----> STEP DONE\n"
                + "--step3\n"
                + "----> STEP DONE\n"
                + "---> STEP DONE\n"
                + "TEST DONE\n";
        assertThat(consoleStepListener.toString(), is(expectedSteps));
    }

    @Test
    public void should_record_step_failures() {
        SimpleTestScenarioSteps steps = factory.getSharedStepLibraryFor(SimpleTestScenarioSteps.class);

        StepEventBus.getParallelEventBus().testStarted("a_test", SampleTestScenario.class);
        steps.step1();
        steps.failingStep();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        String expectedSteps =
                "TEST a_test\n"
                + "-step1\n"
                + "---> STEP DONE\n"
                + "-failingStep\n"
                + "---> STEP FAILED\n"
                + "TEST DONE\n";
        assertThat(consoleStepListener.toString(), is(expectedSteps));
    }

    @Test
    public void should_be_able_to_record_step_failures_after_a_step_ends() {
        SimpleTestScenarioSteps steps = factory.getSharedStepLibraryFor(SimpleTestScenarioSteps.class);

        StepEventBus.getParallelEventBus().testStarted("a_test", SampleTestScenario.class);
        steps.step1();
        steps.step2();

        StepFailure failure = new StepFailure(ExecutedStepDescription.withTitle("Oops!"), new AssertionError());

        StepEventBus.getParallelEventBus().lastStepFailed(failure);
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        String expectedSteps =
                "TEST a_test\n"
                        + "-step1\n"
                        + "---> STEP DONE\n"
                        + "-step2\n"
                        + "---> STEP DONE\n"
                        + "--> STEP FAILED\n"
                        + "TEST DONE\n";
        assertThat(consoleStepListener.toString(), is(expectedSteps));
    }

    @Test
    public void should_record_pending_steps() {
        SimpleTestScenarioSteps steps = factory.getSharedStepLibraryFor(SimpleTestScenarioSteps.class);

        StepEventBus.getParallelEventBus().testStarted("a_test", SampleTestScenario.class);
        steps.step1();
        steps.pendingStep();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        String expectedSteps =
                "TEST a_test\n"
                + "-step1\n"
                + "---> STEP DONE\n"
                + "-pendingStep\n"
                + "--> TEST PENDING\n"
                + "---> STEP PENDING\n"
                + "TEST DONE\n";
        assertThat(consoleStepListener.toString(), is(expectedSteps));
    }

    @Test
    @Ignore
    public void should_record_nested_step_failures_when_configured() {
        SimpleTestScenarioSteps steps = factory.getSharedStepLibraryFor(SimpleTestScenarioSteps.class);

        StepEventBus.getParallelEventBus().testStarted("a_test", SampleTestScenario.class);
        steps.step1();
        steps.step8();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        String expectedSteps =
                "TEST a_test\n"
                + "-step1\n"
                + "---> STEP DONE\n"
                + "-step8\n"
                + "--step1\n"
                + "----> STEP DONE\n"
                + "--failingStep\n"
                + "----> STEP FAILED\n"
                + "--step4\n"
                + "---step5\n"
                + "-----> STEP IGNORED\n"
                + "---step6\n"
                + "-----> STEP IGNORED\n"
                + "----> STEP IGNORED\n"
                + "---> STEP DONE\n"
                + "TEST DONE\n";

        assertThat(consoleStepListener.toString(), is(expectedSteps));


    }

    @Test
    public void should_record_nested_pending_steps() {
        SimpleTestScenarioSteps steps = factory.getSharedStepLibraryFor(SimpleTestScenarioSteps.class);

        StepEventBus.getParallelEventBus().testStarted("a_test", SampleTestScenario.class);
        steps.step1();
        steps.step9();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        String expectedSteps =
                "TEST a_test\n"
                + "-step1\n"
                + "---> STEP DONE\n"
                + "-step9\n"
                + "--step1\n"
                + "----> STEP DONE\n"
                + "--pendingStep\n"
                + "--> TEST PENDING\n"
                + "----> STEP PENDING\n"
                + "--step4\n"
                + "---step5\n"
                + "-----> STEP IGNORED\n"
                + "---step6\n"
                + "-----> STEP IGNORED\n"
                + "----> STEP IGNORED\n"
                + "---> STEP DONE\n"
                + "TEST DONE\n";

        assertThat(consoleStepListener.toString(), is(expectedSteps));


    }

    @Test
    public void should_skip_steps_after_a_step_failure() {
        SimpleTestScenarioSteps steps = factory.getSharedStepLibraryFor(SimpleTestScenarioSteps.class);

        StepEventBus.getParallelEventBus().testStarted("a_test", SampleTestScenario.class);
        steps.step1();
        steps.step7();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        String expectedSteps =
                "TEST a_test\n"
                + "-step1\n"
                + "---> STEP DONE\n"
                + "-step7\n"
                + "--step1\n"
                + "----> STEP DONE\n"
                + "--failingStep\n"
                + "----> STEP FAILED\n"
                + "--step2\n"
                + "----> STEP IGNORED\n"
                + "---> STEP DONE\n"
                + "TEST DONE\n";
        assertThat(consoleStepListener.toString(), is(expectedSteps));
    }

    @Test
    public void should_skip_steps_after_a_failed_assumption() {
        SampleTestScenarioWithFailedAssumption sampleTest = factory.getSharedStepLibraryFor(SampleTestScenarioWithFailedAssumption.class);

        StepEventBus.getParallelEventBus().testStarted("a_test", SampleTestScenarioWithFailedAssumption.class);
        sampleTest.sampleTest();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        String expectedSteps =
                "TEST a_test\n" +
                        "-assumption_failed\n" +
                        "--> TEST IGNORED\n" +
                        "---> ASSUMPTION VIOLATED\n" +
                        "-step1\n" +
                        "---> STEP IGNORED\n" +
                        "-step2\n" +
                        "---> STEP IGNORED\n" +
                        "-step3\n" +
                        "---> STEP IGNORED\n" +
                        "TEST DONE\n";

        assertThat(consoleStepListener.toString(), is(expectedSteps));
    }

    @Test
    public void should_skip_nested_steps_after_a_step_failure() {
        SimpleTestScenarioSteps steps = factory.getSharedStepLibraryFor(SimpleTestScenarioSteps.class);

        StepEventBus.getParallelEventBus().testStarted("a_test", SampleTestScenario.class);
        steps.step1();
        steps.step7();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        String expectedSteps =
                "TEST a_test\n"
                + "-step1\n"
                + "---> STEP DONE\n"
                + "-step7\n"
                + "--step1\n"
                + "----> STEP DONE\n"
                + "--failingStep\n"
                + "----> STEP FAILED\n"
                + "--step2\n"
                + "----> STEP IGNORED\n"
                + "---> STEP DONE\n"
                + "TEST DONE\n";
        assertThat(consoleStepListener.toString(), is(expectedSteps));
    }

    @Test
    public void should_not_use_the_browser() {
        SimpleTestScenarioSteps steps = factory.getSharedStepLibraryFor(SimpleTestScenarioSteps.class);

        StepEventBus.getParallelEventBus().testStarted("a_test", SampleTestScenario.class);
        steps.step1();
        steps.step7();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        String expectedSteps =
                "TEST a_test\n"
                + "-step1\n"
                + "---> STEP DONE\n"
                + "-step7\n"
                + "--step1\n"
                + "----> STEP DONE\n"
                + "--failingStep\n"
                + "----> STEP FAILED\n"
                + "--step2\n"
                + "----> STEP IGNORED\n"
                + "---> STEP DONE\n"
                + "TEST DONE\n";
        assertThat(consoleStepListener.toString(), is(expectedSteps));
    }

    @Test
    public void a_step_can_return_a_step_object() {
        SimpleTestScenarioSteps steps = factory.getSharedStepLibraryFor(SimpleTestScenarioSteps.class);

        StepEventBus.getParallelEventBus().testStarted("a_test", SampleTestScenario.class);
        steps.stepThatReturnsAStep().stepThatReturnsAStep().stepThatReturnsAStep();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        String expectedSteps =
                "TEST a_test\n"
                + "-stepThatReturnsAStep\n"
                + "---> STEP DONE\n"
                + "-stepThatReturnsAStep\n"
                + "---> STEP DONE\n"
                + "-stepThatReturnsAStep\n"
                + "---> STEP DONE\n"
                + "TEST DONE\n";
        assertThat(consoleStepListener.toString(), is(expectedSteps));
    }

    @Test
    public void a_step_can_return_a_step_object_if_a_failure_occurs() {
        SimpleTestScenarioSteps steps = factory.getSharedStepLibraryFor(SimpleTestScenarioSteps.class);

        StepEventBus.getParallelEventBus().testStarted("a_test", SampleTestScenario.class);
        steps.stepThatFailsAndReturnsAStep().stepThatReturnsAStep();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        String expectedSteps =
                "TEST a_test\n"
                + "-stepThatFailsAndReturnsAStep\n"
                + "---> STEP FAILED\n"
                + "-stepThatReturnsAStep\n"
                + "---> STEP IGNORED\n"
                + "TEST DONE\n";
        assertThat(consoleStepListener.toString(), is(expectedSteps));
    }

    @Test
    public void when_an_entier_test_is_pending_all_the_contained_steps_are_skipped() {
        SimpleTestScenarioSteps steps = factory.getSharedStepLibraryFor(SimpleTestScenarioSteps.class);

        StepEventBus.getParallelEventBus().testStarted("a_test", SampleTestScenario.class);
        StepEventBus.getParallelEventBus().testPending();
        steps.step1();
        steps.step2();
        steps.step3();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        String expectedSteps =
                "TEST a_test\n"
                + "--> TEST PENDING\n"
                + "-step1\n"
                + "---> STEP IGNORED\n"
                + "-step2\n"
                + "---> STEP IGNORED\n"
                + "-step3\n"
                + "---> STEP IGNORED\n"
                + "TEST DONE\n";
        assertThat(consoleStepListener.toString(), is(expectedSteps));
    }

    @Test
    public void a_step_can_be_marked_pending() {
        StepEventBus.getParallelEventBus().testStarted("a_test", SampleTestScenario.class);
        StepEventBus.getParallelEventBus().stepStarted(ExecutedStepDescription.withTitle("a step"));
        StepEventBus.getParallelEventBus().stepPending();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        String expectedSteps =
                "TEST a_test\n"
                + "-a step\n"
                + "--> TEST PENDING\n"
                + "---> STEP PENDING\n"
                + "TEST DONE\n";
        assertThat(consoleStepListener.toString(), is(expectedSteps));
    }

    @Test
    public void when_an_entier_test_is_ignored_the_test_is_marked_as_ignored() {
        StepEventBus.getParallelEventBus().testStarted("a_test", SampleTestScenario.class);
        StepEventBus.getParallelEventBus().testIgnored();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        String expectedSteps =
                "TEST a_test\n"
                + "--> TEST IGNORED\n"
                + "TEST DONE\n";
        assertThat(consoleStepListener.toString(), is(expectedSteps));
    }

    @Test
    public void when_an_entier_test_is_skipped_the_test_is_marked_as_skipped() {
        StepEventBus.getParallelEventBus().testStarted("a_test", SampleTestScenario.class);
        StepEventBus.getParallelEventBus().testSkipped();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        String expectedSteps =
                "TEST a_test\n"
                        + "--> TEST SKIPPED\n"
                        + "TEST DONE\n";
        assertThat(consoleStepListener.toString(), is(expectedSteps));
    }

    @Test
    public void should_clear_all_listeners_when_requested() {
        StepEventBus.getParallelEventBus().dropAllListeners();

        StepEventBus.getParallelEventBus().testStarted("a_test", SampleTestScenario.class);
        StepEventBus.getParallelEventBus().stepStarted(ExecutedStepDescription.withTitle("a step"));
        StepEventBus.getParallelEventBus().stepPending();
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        assertThat(consoleStepListener.toString(), is(""));
    }

    @Test
    public void shouldIndicateWhenATestIsRunning() {

        StepEventBus.getParallelEventBus().dropAllListeners();

        StepEventBus.getParallelEventBus().testStarted("a_test", SampleTestScenario.class);
        StepEventBus.getParallelEventBus().stepStarted(ExecutedStepDescription.withTitle("a step"));

        assertThat(StepEventBus.getParallelEventBus().areStepsRunning(), is(true));
    }

    @Test
    public void shouldIndicateWhenALongerTestIsRunning() {

        StepEventBus.getParallelEventBus().dropAllListeners();

        StepEventBus.getParallelEventBus().testStarted("a_test", SampleTestScenario.class);
        StepEventBus.getParallelEventBus().testPending();

        StepEventBus.getParallelEventBus().testStarted("another_test");

        StepEventBus.getParallelEventBus().stepStarted(ExecutedStepDescription.withTitle("a step"));

        assertThat(StepEventBus.getParallelEventBus().areStepsRunning(), is(true));
    }

    @Test
    public void shouldIndicateWhenATestIsRunningHasNotStarted() {

        StepEventBus.getParallelEventBus().dropAllListeners();

        assertThat(StepEventBus.getParallelEventBus().areStepsRunning(), is(false));
    }

    @Test
    public void shouldIndicateWhenATestHasFinished() {

        StepEventBus.getParallelEventBus().dropAllListeners();

        StepEventBus.getParallelEventBus().testStarted("a_test", SampleTestScenario.class);
        StepEventBus.getParallelEventBus().stepStarted(ExecutedStepDescription.withTitle("a step"));
        StepEventBus.getParallelEventBus().testFinished(testOutcome);

        assertThat(StepEventBus.getParallelEventBus().areStepsRunning(), is(false));
    }
}
