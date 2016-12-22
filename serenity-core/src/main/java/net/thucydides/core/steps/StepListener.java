package net.thucydides.core.steps;


import net.thucydides.core.model.DataTable;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;

import java.util.Map;

/**
 * Represents a class interested in knowing about test execution flow and results.
 */
public interface StepListener {

    /**
     * Start a test run using a test case or a user story.
     * For JUnit tests, the test case should be provided. The test case should be annotated with the
     * Story annotation to indicate what user story it tests. Otherwise, the test case itself will
     * be treated as a user story.
     * For easyb stories, the story class can be provided directly.
     */
    void testSuiteStarted(final Class<?> storyClass);

    /**
     * Start a test run using a specific story, without a corresponding Java class.
     */
    void testSuiteStarted(final Story story);

    /**
     * End of a test case or story.
     */
    void testSuiteFinished();

    /**
     * A test with a given name has started.
     */
    void testStarted(final String description);
    void testStarted(final String description, final String id);

    /**
     * Called when a test finishes.
     *
     */
    void testFinished(final TestOutcome result);

    /**
     * The last test run is about to be restarted
     */
    void testRetried();

    /**
     * Called when a test step is about to be started.
     *
     * @param description the description of the test that is about to be run
     *                    (generally a class and method name)
     */
    void stepStarted(final ExecutedStepDescription description);

    /**
     * Called when a test step is about to be started, but this step is scheduled to be skipped.
     *
     * @param description the description of the test that is about to be run
     *                    (generally a class and method name)
     */
    void skippedStepStarted(final ExecutedStepDescription description);

    /**
     * Called when a test step fails.
     *
     * @param failure describes the test that failed and the exception that was thrown
     */
    void stepFailed(final StepFailure failure);

    /**
     * Declare that a step has failed after it has finished.
     */
    void lastStepFailed(StepFailure failure);

    /**
     * Called when a step will not be run, generally because a test method is annotated
     * with {@link org.junit.Ignore}.
     */
    void stepIgnored();

    /**
     * The step is marked as pending.
     */
    void stepPending();

    /**
     * The step is marked as pending with a descriptive message.
     * @param message
     */
    void stepPending(String message);

    /**
     * Called when an test step has finished successfully
     */
    void stepFinished();

    /**
     * The test failed, but not while executing a step.
     * @param testOutcome The test outcome structure for the failing test
     * @param cause The exception that triggered the failure
     */
    void testFailed(TestOutcome testOutcome, final Throwable cause);

    /**
     * The test as a whole was ignored.
     */
    void testIgnored();

    /**
     * The test as a whole was skipped.
     */
    void testSkipped();

    /**
     * The test as a whole should be marked as 'pending'.
     */
    void testPending();

    void testIsManual();

    void notifyScreenChange();

    /**
     * The current scenario is a data-driven scenario using test data from the specified table.
     */
    void useExamplesFrom(DataTable table);

    /**
     * If multiple tables are used, this method will add any new rows to the test data
     */
    void addNewExamplesFrom(DataTable table);

    /**
     * A new example has just started.
     */
    void exampleStarted(Map<String,String> data);

    /**
     * An example has finished.
     */
    void exampleFinished();

    void assumptionViolated(String message);

    void testRunFinished();
}
