package net.thucydides.core.steps;

import net.thucydides.core.logging.ConsoleEvent;
import net.thucydides.core.logging.ConsoleHeading;
import net.thucydides.core.logging.ConsoleLoggingListener;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestStepFactory;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.*;

public class WhenLoggingStepEvents {

    @Mock
    Logger logger;

    @Mock
    Logger quietLogger;

    @Mock
    StepFailure stepFailure;

    EnvironmentVariables environmentVariables;

    ConsoleLoggingListener consoleLoggingListener;

    ConsoleHeading consoleHeading;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        environmentVariables = new MockEnvironmentVariables();
        consoleLoggingListener = new ConsoleLoggingListener(environmentVariables, logger);
        consoleHeading = new ConsoleHeading(environmentVariables);
    }

    class SomeTestClass {
    }

    @Test
    public void should_print_header_banner_before_tests() {

        consoleLoggingListener.testSuiteStarted(SomeTestClass.class);

        verify(logger).info(contains(ConsoleLoggingListener.SERENITY_BIG_BANNER));

    }

    @Test
    public void should_print_shortened_header_banner_before_tests() {

        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("serenity.console.banner", "normal");

        new ConsoleLoggingListener(environmentVariables, logger);

        verify(logger).info(contains(ConsoleLoggingListener.SERENITY_SMALL_BANNER));

    }

    @Test
    public void should_print_full_header_banner_before_tests() {

        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("serenity.console.banner", "ascii");
        Logger logger = mock(Logger.class);

        new ConsoleLoggingListener(environmentVariables, logger);

        verify(logger).info(contains(ConsoleLoggingListener.SERENITY_BIG_BANNER));
    }

    @Test
    public void should_print_small_header_banner_before_tests_if_console_headings_is_set_to_none() {

        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("serenity.console.banner", "none");

        new ConsoleLoggingListener(environmentVariables, logger);

        verify(logger).info(contains(ConsoleLoggingListener.SERENITY_SMALL_BANNER));

    }


    @Test
    public void should_not_print_header_banner_before_tests_in_NONE_mode() {

        environmentVariables.setProperty("thucydides.logging", "NONE");
        new ConsoleLoggingListener(environmentVariables, quietLogger);

        verify(quietLogger, never()).info(contains(ConsoleLoggingListener.SERENITY_BIG_BANNER));

    }

    @Test
    public void should_log_test_suite_name_when_a_test_suite_starts() {
        consoleLoggingListener.testSuiteStarted(SomeTestClass.class);

        verify(logger).info(contains("Test Suite Started: Some test class"));
    }

    @Test
    public void should_not_log_test_suite_name_when_a_test_suite_starts_in_quiet_mode() {
        environmentVariables.setProperty("thucydides.logging", "QUIET");

        consoleLoggingListener.testSuiteStarted(SomeTestClass.class);

        verify(logger, never()).info(contains("Test Suite Started: Some test class"));
    }

    @Test
    public void should_log_test_name_when_test_starts() {
        consoleLoggingListener.testStarted("Some test");

        String expectedMessage = consoleHeading.bannerFor(ConsoleEvent.TEST_STARTED, "Some test");
        verify(logger).info(contains(expectedMessage));
    }

    @Test
    public void should_not_log_messages_when_test_fails_in_quiet_mode() {
        environmentVariables.setProperty("thucydides.logging", "QUIET");
        TestOutcome testOutcome = failingTestOutcome();

        consoleLoggingListener.testFinished(testOutcome);

        verify(logger, never()).info(contains("TEST FAILED: Some test"));
    }

    @Test
    public void should_log_message_when_test_is_pending() {

        String pendingMessage = consoleHeading.bannerFor(ConsoleEvent.TEST_PENDING, "Some test");

        TestOutcome testOutcome = pendingTestOutcome();

        consoleLoggingListener.testFinished(testOutcome);

        verify(logger).info(contains(pendingMessage));
    }

    @Test
    public void should_not_log_messages_when_test_is_pending_in_quiet_mode() {
        environmentVariables.setProperty("thucydides.logging", "QUIET");
        TestOutcome testOutcome = pendingTestOutcome();

        consoleLoggingListener.testFinished(testOutcome);

        verify(logger, never()).info(contains("TEST PENDING: {}"), eq("Some test"));
    }

    @Test
    public void should_log_message_when_test_is_skipped() {
        TestOutcome testOutcome = skippedTestOutcome();

        consoleLoggingListener.testFinished(testOutcome);

        String expectedMessage = consoleHeading.bannerFor(ConsoleEvent.TEST_SKIPPED, "Some test");
        verify(logger).info(contains(expectedMessage));
    }

    @Test
    public void should_not_log_messages_when_test_is_skipped_in_quiet_mode() {
        environmentVariables.setProperty("thucydides.logging", "QUIET");
        TestOutcome testOutcome = skippedTestOutcome();

        consoleLoggingListener.testFinished(testOutcome);

        verify(logger, never()).info(contains("TEST SKIPPED: "), eq("Some test"));
    }

//    @Test
//    public void should_log_message_when_test_fails_directly() {
//        consoleLoggingListener.testFailed(null, new AssertionError("something broke"));
//
//        verify(logger).info(contains("something broke"));
//    }

    @Test
    public void should_not_log_messages_when_test_fails_directly_in_quiet_mode() {
        environmentVariables.setProperty("thucydides.logging", "QUIET");

        consoleLoggingListener.testFailed(null, new AssertionError("something broke"));

        verify(logger, never()).info(contains("something broke"));
    }

    @Test
    public void should_log_message_when_test_is_ignored() {
        consoleLoggingListener.testIgnored();

        verify(logger).info(contains("TEST IGNORED"));
    }

    @Test
    public void should_not_log_messages_when_test_is_ignored_in_quiet_mode() {
        environmentVariables.setProperty("thucydides.logging", "QUIET");

        consoleLoggingListener.testIgnored();

        verify(logger, never()).info(contains("TEST IGNORED"));
    }

    @Test
    public void should_log_message_when_test_passes() {
        TestOutcome testOutcome = successfulTestOutcome();

        consoleLoggingListener.testFinished(testOutcome);

        String expectedMessage = consoleHeading.bannerFor(ConsoleEvent.TEST_PASSED, "Some test");
        verify(logger).info(contains(expectedMessage));
    }

    @Test
    public void should_not_log_messages_when_test_passes_in_quiet_mode() {
        environmentVariables.setProperty("thucydides.logging", "QUIET");
        TestOutcome testOutcome = successfulTestOutcome();

        consoleLoggingListener.testFinished(testOutcome);

        verify(logger, never()).info(contains("TEST PASSED: {}"), eq("Some test"));
    }

    @Test
    public void should_log_test_suite_name_when_a_test_story_starts() {
        consoleLoggingListener.testSuiteStarted(Story.from(SomeTestClass.class));

        verify(logger).info(contains("Test Suite Started: Some test class"));
    }

    @Test
    public void should_log_test_name_when_a_test_starts() {
        consoleLoggingListener.testStarted("some_test");

        String expectedMessage = consoleHeading.bannerFor(ConsoleEvent.TEST_STARTED, "some_test");
        verify(logger).info(contains(expectedMessage));
    }

    @Test
    public void should_log_step_name_when_a_step_starts_if_in_verbose_mode() {
        environmentVariables.setProperty("thucydides.logging", "VERBOSE");
        consoleLoggingListener.stepStarted(ExecutedStepDescription.withTitle("some step"));

        verify(logger).info(contains("  * some step"));
    }

    @Test
    public void should_log_step_name_when_a_skipped_step_starts_if_in_verbose_mode() {
        environmentVariables.setProperty("thucydides.logging", "VERBOSE");
        consoleLoggingListener.skippedStepStarted(ExecutedStepDescription.withTitle("some step"));

        verify(logger).info(contains("  * some step"));
    }

    @Test
    public void should_log_skipped_step_if_in_verbose_mode() {
        environmentVariables.setProperty("thucydides.logging", "VERBOSE");

        consoleLoggingListener.stepIgnored();

        verify(logger).info(contains("STEP IGNORED"));
    }

    @Test
    public void should_log_pending_step_if_in_verbose_mode() {
        environmentVariables.setProperty("thucydides.logging", "VERBOSE");

        consoleLoggingListener.stepPending();

        verify(logger).info(contains("STEP IS PENDING"));
    }

    @Test
    public void should_log_pending_step_with_message_if_in_verbose_mode() {
        environmentVariables.setProperty("thucydides.logging", "VERBOSE");

        consoleLoggingListener.stepPending("for some reason");

        verify(logger).info(contains("PENDING STEP ({})"), eq("for some reason"));
    }

    private TestOutcome pendingTestOutcome() {
        TestOutcome testOutcome = TestOutcome.forTest("some_test", SomeTestClass.class);
        testOutcome.recordStep(TestStepFactory.forAPendingTestStepCalled("do_something"));
        return testOutcome;
    }

    private TestOutcome skippedTestOutcome() {
        TestOutcome testOutcome = TestOutcome.forTest("some_test", SomeTestClass.class);
        testOutcome.recordStep(TestStepFactory.forASkippedTestStepCalled("do_something"));
        return testOutcome;
    }

    private TestOutcome successfulTestOutcome() {
        TestOutcome testOutcome = TestOutcome.forTest("some_test", SomeTestClass.class);
        testOutcome.recordStep(TestStepFactory.forASuccessfulTestStepCalled("do_something"));
        return testOutcome;
    }

    private TestOutcome failingTestOutcome() {
        TestOutcome testOutcome = TestOutcome.forTest("some_test", SomeTestClass.class);
        testOutcome.recordStep(TestStepFactory.forABrokenTestStepCalled("do_something", new AssertionError("something broke")));
        return testOutcome;
    }

}
