package net.thucydides.core.steps;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.annotations.Story;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class WhenRunningNonWebStepsThroughAScenarioProxy {

    StepListener listener;

    @Mock
    StepListener mockListener;

    private StepFactory factory;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);

        factory = StepFactory.getFactory();

        listener = new ConsoleStepListener();
        StepEventBus.getParallelEventBus().reset();
        StepEventBus.getParallelEventBus().registerListener(listener);
        StepEventBus.getParallelEventBus().registerListener(mockListener);

        StepEventBus.getParallelEventBus().testStarted("aTest");

    }

    @After
    public void deregisterListener() {
        StepEventBus.getParallelEventBus().dropListener(listener);
        StepEventBus.getParallelEventBus().dropListener(mockListener);
    }

    static class SimpleSteps {

        public SimpleSteps() {
        }

        @Steps
        NestedSteps nestedSteps;

        @Step
        public void step1() {
        }

        @Step
        public void step2() {
        }

        @Step
        public void step3() {
        }

        @Step
        public void step_with_parameter(String name) {
        }

        @Step
        public void step_with_parameters(String name, int age) {
        }

        @Step
        public void failing_step() {
            throw new AssertionError("Oh bother");
        }

        @Step
        public void nested_steps() {
            nestedSteps.step1_1();
            nestedSteps.step1_2();
            nestedSteps.step1_3();
        }
    }

    static class NestedSteps {

        public NestedSteps() {
        }

        @Step
        public void step1_1() {
        }

        @Step
        public void step1_2() {
        }

        @Step
        public void step1_3() {
        }

    }

    @Test
    public void the_proxy_should_execute_steps_transparently() {
        SimpleSteps steps = factory.getSharedStepLibraryFor(SimpleSteps.class);

        steps.step1();
        steps.step2();
        steps.step3();

        assertThat(listener.toString(), allOf(containsString("step1"), containsString("step2"), containsString("step3")));
    }

    @Test
    public void the_proxy_should_execute_nested_steps_transparently() {
        SimpleSteps steps = factory.getSharedStepLibraryFor(SimpleSteps.class);

        steps.nested_steps();
        assertThat(listener.toString(), allOf(containsString("nested_steps"),
                containsString("step1_1"),
                containsString("step1_2"),
                containsString("step1_3")));
    }


    @Test
    public void the_proxy_should_store_step_method_parameters() {
        SimpleSteps steps = factory.getSharedStepLibraryFor(SimpleSteps.class);

        steps.step_with_parameter("Joe");

        assertThat(listener.toString(), allOf(containsString("step_with_parameter"),
                containsString("Joe")));
    }

    @Test
    public void the_proxy_should_store_multiple_step_method_parameters() {
        SimpleSteps steps = factory.getSharedStepLibraryFor(SimpleSteps.class);

        steps.step_with_parameters("Joe", 10);

        assertThat(listener.toString(), allOf(containsString("step_with_parameter"),
                containsString("Joe"),
                containsString("10")));
    }


    @Test
    public void the_proxy_should_record_execution_structure() {
        SimpleSteps steps = factory.getSharedStepLibraryFor(SimpleSteps.class);

        steps.step1();
        steps.step2();
        steps.nested_steps();
        steps.step3();

        String executedSteps = listener.toString();
        String expectedSteps =
                "TEST aTest\n" +
                        "-step1\n" +
                        "---> STEP DONE\n" +
                        "-step2\n" +
                        "---> STEP DONE\n" +
                        "-nested_steps\n" +
                        "--step1_1\n" +
                        "----> STEP DONE\n" +
                        "--step1_2\n" +
                        "----> STEP DONE\n" +
                        "--step1_3\n" +
                        "----> STEP DONE\n" +
                        "---> STEP DONE\n" +
                        "-step3\n" +
                        "---> STEP DONE\n";

        assertThat(executedSteps, containsString(expectedSteps));
    }

    @Test
    public void the_proxy_should_notify_listeners_when_tests_are_starting() {
        SimpleSteps steps = factory.getSharedStepLibraryFor(SimpleSteps.class);

        steps.step1();
        steps.step2();
        steps.step3();

        verify(mockListener, times(3)).stepStarted(any(ExecutedStepDescription.class));
    }

    class AStory {
    }

    @Story(AStory.class)
    class ATestCase {
        public void app_should_work() {
        }
    }

    @Captor
    ArgumentCaptor<ExecutedStepDescription> argument;

    @Test
    public void the_proxy_should_notify_listeners_when_tests_are_starting_with_details_about_step_name_and_class() {
        SimpleSteps steps = factory.getSharedStepLibraryFor(SimpleSteps.class);

        steps.step1();

        verify(mockListener).stepStarted(argument.capture());
        assertThat(argument.getValue().getStepClass().getName(), is(SimpleSteps.class.getName()));
        assertThat(argument.getValue().getName(), is("step1"));
    }

    @Test
    public void the_proxy_should_notify_listeners_when_tests_have_finished() {
        SimpleSteps steps = factory.getSharedStepLibraryFor(SimpleSteps.class);

        steps.step1();
        steps.step2();
        steps.step3();

        verify(mockListener, times(3)).stepFinished();
    }


    @Test
    public void the_proxy_should_skip_tests_after_a_failure() {
        SimpleSteps steps = factory.getSharedStepLibraryFor(SimpleSteps.class);

        steps.step1();
        steps.failing_step();
        steps.step3();

        String expectedExecution =
                "TEST aTest\n" +
                    "-step1\n" +
                        "---> STEP DONE\n" +
                        "-failing_step\n" +
                        "---> STEP FAILED\n" +
                        "-step3\n" +
                        "---> STEP IGNORED\n";
        assertThat(listener.toString(), containsString(expectedExecution));

    }
}
