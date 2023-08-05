package net.thucydides.core.bootstrap;

import net.thucydides.model.domain.Story;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.model.steps.StepListener;

import java.util.Optional;

/**
 * A utility class that provides services to initialize web testing and reporting-related fields in arbitrary objects.
 * Designed to replace the unfinished Thucydides class.
 */
public final class ThucydidesAgent {

    private final ThucydidesContext context;

    /**
     * Create a new Thucydides agent instance.
     * There is always a BaseStepListener configured in the context, but you can also specify other listeners,
     * such as for logging (Listeners.getLoggingListener()) and to enable statistics (Listeners.getStatisticsListener())
     * @param driver
     * @param additionalListeners
     */
    public ThucydidesAgent(Optional<String> driver, StepListener... additionalListeners) {
        context = ThucydidesContext.newContext(driver, additionalListeners);
    }

    /**
     * Initialize a class for use with Thucydides.
     * This involves instrumenting any @Step-annotated classes and setting up the Thucydides listeners.
     * @param testCase a Java object containing test methods.
     */
    public void enrich(Object testCase) {
        context.initialize(testCase);
    }

    /**
     * Tells Thucydides that a new test suite (test case, specification,...) has started.
     * @param name
     */
    public void testSuiteStarted(String name) {
        notifyEventBus().clear();
        notifyEventBus().testSuiteStarted(Story.called(name));
    }

    /**
     * Tell Thucydides to start recording test outcomes and step events for a new test.
     *
     * @param name A human-readable name that will identify this test in the test reports.
     */
    public void testStarted(final String name) {
        notifyEventBus().testStarted(name);
    }

    /**
     * Tell Thucydides that a test is finished, and to generate the test reports for that test.
     */
    public void testFinished() {
        notifyEventBus().testFinished();
    }

    public void testSuiteFinished() {
        notifyEventBus().testSuiteFinished();
        context.dropListeners();
        context.generateReports();
    }

    private StepEventBus notifyEventBus() {
        return StepEventBus.getParallelEventBus();
    }
}
