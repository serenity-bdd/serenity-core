package net.thucydides.junit.listeners;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Story;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import net.thucydides.core.steps.StepFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class WhenListeningForTestEvents {

    @Mock
    Description testDescription;

    @Mock
    Description failureDescription;


    @Mock
    File outputDirectory;

    @Mock
    Pages pages;

    StepFactory stepFactory;

    class ScenarioWithSomeFailingTests {
        public void failingTest() {}
        public void passingTest() {}
    }

    class MyStory {}

    @Story(storyClass = MyStory.class)
    final static class MyTestCase {
        public void app_should_work() {}
    }

    static class MyTestSteps extends ScenarioSteps {
        public MyTestSteps(final Pages pages) {
            super(pages);
        }

        @Step
        public void step1() {}
        @Step
        public void step2() {}
        @Step
        public void failingStep() { throw new AssertionError("Step failed");}

        public void failingNormalMethod() { throw new AssertionError("Method failed");}
    }

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        Mockito.<Class<?>>when(failureDescription.getTestClass()).thenReturn(ScenarioWithSomeFailingTests.class);

    }

    private JUnitStepListener listener;

    private JUnitStepListener failureTestListener;

    @Before
    public void setupListeners() throws Exception {
        setupDefaultListener();
        setupFailureListener();
    }

    private void setupDefaultListener() throws Exception {
        listener = JUnitStepListener.withOutputDirectory(outputDirectory)
                                    .and().withPageFactory(pages)
                                    .and().withTestClass(MyTestCase.class)
                                    .and().build();
        stepFactory = StepFactory.getFactory().usingPages(pages);
        listener.testRunStarted(Description.createSuiteDescription(MyTestCase.class));
        listener.testStarted(Description.createTestDescription(MyTestCase.class,"app_should_work"));
    }

    private void setupFailureListener() throws Exception {
        failureTestListener = JUnitStepListener.withOutputDirectory(outputDirectory)
                .and().withPageFactory(pages)
                .and().withTestClass(ScenarioWithSomeFailingTests.class)
                .and().build();
        stepFactory = StepFactory.getFactory().usingPages(pages);
        failureTestListener.testRunStarted(Description.createSuiteDescription(ScenarioWithSomeFailingTests.class));
        failureTestListener.testStarted(Description.createTestDescription(ScenarioWithSomeFailingTests.class,"app_should_work"));
    }

    @Test
    public void there_should_be_no_failing_steps_at_the_start_of_the_test() throws Exception {
        assertThat(listener.hasRecordedFailures(), is(false));
    }


    @Test
    public void a_junit_listener_should_record_test_results() throws Exception {
        Failure failure = new Failure(failureDescription, new AssertionError("Test failed."));
        failureTestListener.testRunStarted(Description.createSuiteDescription(ScenarioWithSomeFailingTests.class));
        failureTestListener.testStarted(Description.createTestDescription(ScenarioWithSomeFailingTests.class, "failingTest"));

        failureTestListener.testFailure(failure);

        assertThat(failureTestListener.hasRecordedFailures(), is(true));
        assertThat(failureTestListener.getError().getMessage(), is("Test failed."));
    }

    @Test
    public void a_junit_listener_should_keep_track_of_failed_test_steps() throws Exception {

        MyTestSteps steps =  stepFactory.getSharedStepLibraryFor(MyTestSteps.class);

        steps.step1();
        steps.failingStep();

        assertThat(failureTestListener.hasRecordedFailures(), is(true));
        assertThat(failureTestListener.getError().getMessage(), is("Step failed"));
    }

    @Test
    public void a_junit_listener_should_keep_track_of_failed_non_step_methods() throws Exception {

        MyTestSteps steps =  stepFactory.getSharedStepLibraryFor(MyTestSteps.class);

        steps.failingNormalMethod();

        assertThat(failureTestListener.hasRecordedFailures(), is(true));
        assertThat(failureTestListener.getError().getMessage(), endsWith("Method failed"));
    }


    @Test
    public void a_junit_listener_should_keep_track_of_failure_exceptions() throws Exception {

        Throwable cause = new AssertionError("Test failed");
        Failure failure = new Failure(failureDescription, cause);

        failureTestListener.testFailure(failure);

        assertThat(failureTestListener.getError().getMessage(), is("Test failed"));
    }

    @Test
    public void any_failing_test_steps_should_be_cleared_at_the_start_of_each_new_test() throws Exception {

        Failure failure = new Failure(failureDescription, new AssertionError("Test failed"));

        failureTestListener.testFailure(failure);

        assertThat(failureTestListener.hasRecordedFailures(), is(true));

        failureTestListener.testStarted(Description.createTestDescription(ScenarioWithSomeFailingTests.class,"app_should_still_work"));

        assertThat(failureTestListener.hasRecordedFailures(), is(false));
    }

    @Test
    public void any_failing_exceptions_should_be_cleared_at_the_start_of_each_new_test() throws Exception {

        Failure failure = new Failure(failureDescription, new AssertionError("Test failed"));

        failureTestListener.testFailure(failure);

        assertThat(failureTestListener.getError(), is(not(nullValue())));

        failureTestListener.testStarted(Description.createTestDescription(ScenarioWithSomeFailingTests.class,"app_should_still_work"));

        assertThat(failureTestListener.getError(), is(nullValue()));
    }

}
