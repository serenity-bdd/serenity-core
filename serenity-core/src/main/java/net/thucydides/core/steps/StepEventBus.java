package net.thucydides.core.steps;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.serenitybdd.core.eventbus.Broadcaster;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.events.TestLifecycleEvents;
import net.thucydides.core.model.*;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * An event bus for Step-related notifications.
 * Use this to integrate Thucydides listeners with testing tools.
 * You create a listener (e.g. an instance of BaseStepListener, or your own), register it using
 * 'registerListener', and then implement the various methods (testStarted(), stepStarted()). Thucydides
 * will call these events on your listener as they occur.
 * You can register a new Thucydides listener by implementing the StepListener interface and
 * placing your class in the classpath. Thucydides will automatically detect the listener and add it to the
 * registered listeners. It will load custom listeners automatically when a test starts for the first time.
 */
public class StepEventBus {

    private static ThreadLocal<StepEventBus> stepEventBusThreadLocal = new ThreadLocal<StepEventBus>();
    private static final String CORE_THUCYDIDES_PACKAGE = "net.thucydides.core";
    private static final Logger LOGGER = LoggerFactory.getLogger(StepEventBus.class);

    public static final String TEST_SOURCE_JUNIT = "JUnit";
    public static final String TEST_SOURCE_JBEHAVE = "JBehave";
    public static final String TEST_SOURCE_CUCUMBER = "Cucumber";

    /**
     * The event bus used to inform listening classes about when tests and test steps start and finish.
     * There is a separate event bus for each thread.
     */
    public static synchronized StepEventBus getEventBus() {
        if (stepEventBusThreadLocal.get() == null) {
            stepEventBusThreadLocal.set(new StepEventBus(ConfiguredEnvironment.getEnvironmentVariables()));
        }
        return stepEventBusThreadLocal.get();
    }

    private List<StepListener> registeredListeners = new ArrayList<>();
    /**
     * A reference to the base step listener, if registered.
     */
    private BaseStepListener baseStepListener;

    private TestResultTally resultTally;

    private Stack<String> stepStack = new Stack<>();
    private Stack<Boolean> webdriverSuspensions = new Stack<>();

    private Set<StepListener> customListeners;

    private boolean stepFailed;
    private boolean suspendedTest;
    private boolean assumptionViolated;
    private String assumptionViolatedMessage;
    private boolean uniqueSession;

    private Class<?> classUnderTest;
    private Story storyUnderTest;
    private Optional<Boolean> isDryRun = Optional.absent();

    private final EnvironmentVariables environmentVariables;
    @Inject
    public StepEventBus(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;

//        Darkroom.isOpenForBusiness();
    }

    /**
     * Indicates the test source e.g : junit/jbehave/cucumber
     */
    private String testSource;

    /**
     * Register a listener to receive notification at different points during a test's execution.
     * If you are writing your own listener, you shouldn't need to call this method - just set up your
     * listener implementation as a service (see http://download.oracle.com/javase/6/docs/api/java/util/ServiceLoader.html),
     * place the listener class on the classpath and it will be detected automatically.
     */
    public StepEventBus registerListener(final StepListener listener) {
        if (!registeredListeners.contains(listener)) {
            registeredListeners.add(listener);
            if (BaseStepListener.class.isAssignableFrom(listener.getClass())) {
                baseStepListener = (BaseStepListener) listener;
                baseStepListener.setEventBus(this);
            }
        }
        return this;
    }

    public boolean isBaseStepListenerRegistered() {
        return baseStepListener != null;
    }

    public BaseStepListener getBaseStepListener() {
        Preconditions.checkNotNull(baseStepListener, "No BaseStepListener has been registered");
        return baseStepListener;
    }

    public void testStarted(final String testName) {
        clear();
        for (StepListener stepListener : getAllListeners()) {
            stepListener.testStarted(testName);
        }
    }

    public void testStarted(final String testName, final String id) {
        clear();
        for (StepListener stepListener : getAllListeners()) {
            stepListener.testStarted(testName, id);
        }
    }

    public boolean isUniqueSession() {
        return uniqueSession;
    }

    public void setUniqueSession(boolean uniqueSession) {
        this.uniqueSession = uniqueSession;
    }

    public void testStarted(final String newTestName, final Story story) {
        startSuiteWithStoryForFirstTest(story);
        testStarted(newTestName);
    }

    public void testStarted(final String newTestName, final Class<?> testClass) {
        ensureThatTheTestSuiteStartedWith(testClass);
        if (newTestName != null) {
            testStarted(newTestName);
        }
    }

    private void ensureThatTheTestSuiteStartedWith(Class<?> testClass) {
        if (!getBaseStepListener().testSuiteRunning()) {
            testSuiteStarted(testClass);
        }
    }

    private void startSuiteWithStoryForFirstTest(final Story story) {
        if ((storyUnderTest == null) || (storyUnderTest != story)) {
            testSuiteStarted(story);
        }
    }

    protected List<StepListener> getAllListeners() {
        List<StepListener> allListeners = Lists.newArrayList(registeredListeners);
        allListeners.addAll(getCustomListeners());
        return ImmutableList.copyOf(allListeners);
    }

    private Set<StepListener> getCustomListeners() {

        if (customListeners == null) {
            customListeners = Collections.synchronizedSet(new HashSet<StepListener>());

            ServiceLoader<StepListener> stepListenerServiceLoader = ServiceLoader.load(StepListener.class);
            Iterator<StepListener> listenerImplementations = stepListenerServiceLoader.iterator();
//            Iterator<?> listenerImplementations = Service.providers(StepListener.class);

            while (listenerImplementations.hasNext()) {
                try {
                    StepListener listener = listenerImplementations.next();
                    if (!isACore(listener)) {
                        LOGGER.debug("Registering custom listener " + listener);
                        customListeners.add(listener);
                    }
                } catch (ServiceConfigurationError e) {
                    LOGGER.error("Could not instantiate listener ", e);
                }

            }
        }
        return customListeners;
    }

    private boolean isACore(final StepListener listener) {
        return listener.getClass().getPackage().getName().startsWith(CORE_THUCYDIDES_PACKAGE);
    }

    public void testSuiteStarted(final Class<?> testClass) {
        LOGGER.debug("Test suite started for {}", testClass);
        clear();
        updateClassUnderTest(testClass);
        for (StepListener stepListener : getAllListeners()) {
            stepListener.testSuiteStarted(testClass);
        }
        TestLifecycleEvents.postEvent(TestLifecycleEvents.testSuiteStarted());
    }

    private void updateClassUnderTest(final Class<?> testClass) {
        classUnderTest = testClass;
    }


    private void updateStoryUnderTest(final Story story) {
        storyUnderTest = story;
    }

    public void testSuiteStarted(final Story story) {
        LOGGER.debug("Test suite started for story {}", story);
        updateStoryUnderTest(story);
        for (StepListener stepListener : getAllListeners()) {
            stepListener.testSuiteStarted(story);
        }
        TestLifecycleEvents.postEvent(TestLifecycleEvents.testSuiteStarted());
    }

    public void clear() {
        stepStack.clear();
        clearStepFailures();
        currentTestIsNotSuspended();
        noAssumptionsViolated();
        disableSoftAsserts();
        resultTally = null;
        classUnderTest = null;
        webdriverSuspensions.clear();

        Broadcaster.unregisterAllListeners();
    }

    private void noAssumptionsViolated() {
        assumptionViolated = false;
        assumptionViolatedMessage = "";
    }

    private void currentTestIsNotSuspended() {
        suspendedTest = false;
    }

    private TestResultTally getResultTally() {
        if (resultTally == null) {
            resultTally = TestResultTally.forTestClass(classUnderTest);
        }
        return resultTally;
    }

    public void testFinished() {
        //screenshotProcessor.waitUntilDone();
//        Darkroom.waitUntilClose();

        TestOutcome outcome = getBaseStepListener().getCurrentTestOutcome();
        for (StepListener stepListener : getAllListeners()) {
            stepListener.testFinished(outcome);
        }
        clear();
    }

    public void testFinished(TestOutcome result) {
//        Darkroom.waitUntilClose();

        for (StepListener stepListener : getAllListeners()) {
            stepListener.testFinished(result);
        }
        clear();
    }

    public void testRetried() {
        for (StepListener stepListener : getAllListeners()) {
            stepListener.testRetried();
        }
        clear();
    }

    private void pushStep(String stepName) {
        stepStack.push(stepName);
    }

    private void popStep() {
        stepStack.pop();
    }

    public void clearStepFailures() {
        stepFailed = false;
    }

    public boolean aStepInTheCurrentTestHasFailed() {
        return stepFailed;
    }

    public boolean isCurrentTestDataDriven() {
        return DataDrivenStep.inProgress();
    }

    /**
     * Start the execution of a test step.
     */
    public void stepStarted(final ExecutedStepDescription stepDescription) {

        stepStarted(stepDescription, false);
    }

    /**
     * Start the execution of a test step.
     */
    public void stepStarted(final ExecutedStepDescription stepDescription, boolean isPrecondition) {

        pushStep(stepDescription.getName());

        for (StepListener stepListener : getAllListeners()) {
            stepListener.stepStarted(stepDescription);
        }

        if (isPrecondition) {
            baseStepListener.currentStepIsAPrecondition();
        }
    }

    /**
     * Record a step that is not scheduled to be executed (e.g. a skipped or ignored step).
     */
    public void skippedStepStarted(final ExecutedStepDescription executedStepDescription) {

        pushStep(executedStepDescription.getName());

        for (StepListener stepListener : getAllListeners()) {
            stepListener.skippedStepStarted(executedStepDescription);
        }
    }

    public void stepFinished() {
        stepDone();
        getResultTally().logExecutedTest();
        for (StepListener stepListener : getAllListeners()) {
            stepListener.stepFinished();
        }
    }

    private void stepDone() {
        if (!stepStack.empty()) {
            popStep();
        }
    }

    public void stepFailed(final StepFailure failure) {

        stepDone();
        getResultTally().logFailure(failure);

        for (StepListener stepListener : getAllListeners()) {
            stepListener.stepFailed(failure);
        }
        stepFailed = true;
    }

    public void lastStepFailed(final StepFailure failure) {

        getResultTally().logFailure(failure);

        for (StepListener stepListener : getAllListeners()) {
            stepListener.stepFailed(failure);
        }
        stepFailed = true;
    }

    public void stepIgnored() {

        stepDone();
        getResultTally().logIgnoredTest();

        for (StepListener stepListener : getAllListeners()) {
            stepListener.stepIgnored();
        }
    }

    public void stepPending() {
        stepPending(null);
    }

    public void stepPending(String message) {
        testPending();
        stepDone();
        getResultTally().logIgnoredTest();

        for (StepListener stepListener : getAllListeners()) {
            if (message != null) {
                stepListener.stepPending(message);
            } else {
                stepListener.stepPending();
            }
        }
    }

    public void assumptionViolated(String message) {
        testIgnored();
        suspendTest();
        stepDone();
        getResultTally().logIgnoredTest();

        for (StepListener stepListener : getAllListeners()) {
            stepListener.assumptionViolated(message);
        }
        assumptionViolated = true;
        assumptionViolatedMessage = message;
    }

    public void dropListener(final StepListener stepListener) {
        registeredListeners.remove(stepListener);
    }

    public void dropAllListeners() {
        registeredListeners.clear();
    }

    public boolean webdriverCallsAreSuspended() {

        if (softAssertsActive()) {
            return !webdriverSuspensions.isEmpty();
        }
        return currentTestIsSuspended() || aStepInTheCurrentTestHasFailed() || !webdriverSuspensions.isEmpty();
    }

    public void reenableWebdriverCalls() {
        webdriverSuspensions.pop();
    }

    public void temporarilySuspendWebdriverCalls() {
        webdriverSuspensions.push(true);
    }

    /**
     * The test failed, but not during the execution of a step.
     *
     * @param cause the underlying cause of the failure.
     */
    public void testFailed(final Throwable cause) {
        TestOutcome outcome = getBaseStepListener().getCurrentTestOutcome();
        for (StepListener stepListener : getAllListeners()) {
            try {
                stepListener.testFailed(outcome, cause);
            } catch (AbstractMethodError ame) {
                LOGGER.warn("Caught abstract method error - this seems to be mostly harmless.");
            }
        }
    }

    /**
     * Mark the current test method as pending.
     * The test will stil be executed to record the steps, but any webdriver calls will be skipped.
     */
    public void testPending() {
        for (StepListener stepListener : getAllListeners()) {
            stepListener.testPending();
        }
        suspendTest();
    }

    /**
     * Mark the current test method as pending.
     * The test will stil be executed to record the steps, but any webdriver calls will be skipped.
     */
    public void testIsManual() {
        for (StepListener stepListener : getAllListeners()) {
            stepListener.testIsManual();
        }
        suspendTest();
    }

    public void suspendTest() {
        suspendedTest = true;
    }

    public void suspendTest(TestResult result) {
        suspendTest();
        switch (result) {
            case PENDING:
                testPending();
                break;
            case IGNORED:
                testIgnored();
                break;
            case SKIPPED:
                testSkipped();
                break;
        }

    }

    public boolean currentTestIsSuspended() {
        return suspendedTest;
    }

    public boolean assumptionViolated() {
        return assumptionViolated;
    }

    public void testIgnored() {
        for (StepListener stepListener : getAllListeners()) {
            stepListener.testIgnored();
        }
        suspendTest();
    }

    public void testSkipped() {
        for (StepListener stepListener : getAllListeners()) {
            stepListener.testSkipped();
        }
        suspendTest();
    }

    public boolean areStepsRunning() {
        return !stepStack.isEmpty();
    }

    public void notifyScreenChange() {
        for (StepListener stepListener : getAllListeners()) {
            stepListener.notifyScreenChange();
        }
    }

    public void testSuiteFinished() {
        for (StepListener stepListener : getAllListeners()) {
            stepListener.testSuiteFinished();
        }
        TestLifecycleEvents.postEvent(TestLifecycleEvents.testSuiteFinished());
        storyUnderTest = null;
    }

    public void testRunFinished() {
        for (StepListener stepListener : getAllListeners()) {
            stepListener.testRunFinished();
        }
    }

    public void updateCurrentStepTitle(String stepTitle) {
        getBaseStepListener().updateCurrentStepTitle(stepTitle);
    }

    public void updateCurrentStepTitleAsPrecondition(String stepTitle) {
        getBaseStepListener().updateCurrentStepTitle(stepTitle).asAPrecondition();
    }

    public void addIssuesToCurrentStory(List<String> issues) {
        getBaseStepListener().addIssuesToCurrentStory(issues);
    }

    public void addIssuesToCurrentTest(List<String> issues) {
        getBaseStepListener().getCurrentTestOutcome().addIssues(issues);
    }

    public void addTagsToCurrentTest(List<TestTag> tags) {
        getBaseStepListener().getCurrentTestOutcome().addTags(tags);
    }

    public void addTagsToCurrentStory(List<TestTag> tags) {
        getBaseStepListener().addTagsToCurrentStory(tags);
    }

    public void addDescriptionToCurrentTest(String description) {
        getBaseStepListener().getCurrentTestOutcome().setDescription(description);
    }

    public void setBackgroundTitle(String title) {
        getBaseStepListener().getCurrentTestOutcome().setBackgroundTitle(title);
    }

    public void setBackgroundDescription(String description) {
        getBaseStepListener().getCurrentTestOutcome().setBackgroundDescription(description);
    }

    public void useExamplesFrom(DataTable table) {
        for (StepListener stepListener : getAllListeners()) {
            stepListener.useExamplesFrom(table);
        }
    }

    public void addNewExamplesFrom(DataTable newTable) {
        for (StepListener stepListener : getAllListeners()) {
            stepListener.addNewExamplesFrom(newTable);
        }
    }

    public void exampleStarted(Map<String, String> data) {
        for (StepListener stepListener : getAllListeners()) {
            stepListener.exampleStarted(data);
        }
    }

    public void exampleFinished() {
        for (StepListener stepListener : getAllListeners()) {
            stepListener.exampleFinished();
        }
    }

    public boolean currentTestOutcomeIsDataDriven() {
        return (getBaseStepListener().latestTestOutcome().isPresent() && getBaseStepListener().latestTestOutcome().get().isDataDriven());
    }
    /**
     * Forces Thucydides to take a screenshot now.
     */
    public void takeScreenshot() {
        getBaseStepListener().takeScreenshot();
    }

    public boolean testSuiteHasStarted() {
        return getBaseStepListener().testSuiteRunning();
    }

    public String getAssumptionViolatedMessage() {
        return assumptionViolatedMessage;
    }

    public Optional<TestStep> getCurrentStep() {return getBaseStepListener().cloneCurrentStep(); }

    /**
     * Set all steps in the current test outcome to a given result.
     * Used to set all steps to PENDING or SKIPPED, for example.
     * @param result
     */
    public void setAllStepsTo(TestResult result) {
        baseStepListener.setAllStepsTo(result);
    }

    private final Optional<TestResult> NO_FORCED_RESULT = Optional.absent();

    public Optional<TestResult> getForcedResult() {
        return (baseStepListener != null) ? baseStepListener.getForcedResult() : NO_FORCED_RESULT;
    }

    public synchronized boolean isDryRun() {
        if (this.isDryRun.isPresent()) {
            return this.isDryRun.get();
        } else {
            return ThucydidesSystemProperty.THUCYDIDES_DRY_RUN.booleanFrom(environmentVariables);
        }
    }

    public synchronized void enableDryRun() {
        this.isDryRun = Optional.of(true);
    }

    public void exceptionExpected(Class<? extends Throwable> expected) {
        getBaseStepListener().exceptionExpected(expected);
    }

    Optional<TestResult> NO_RESULT_YET = Optional.absent();
    public Optional<TestResult> resultSoFar() {

        return (getBaseStepListener().latestTestOutcome().isPresent()) ?
                Optional.fromNullable(getBaseStepListener().latestTestOutcome().get().getResult()) : NO_RESULT_YET;
    }

    public void mergePreviousStep() {
        baseStepListener.mergeLast(2).steps();
    }

    public void updateOverallResults() {
        baseStepListener.updateOverallResults();
    }

    public void reset() {
        if (baseStepListener != null) {
            baseStepListener.testSuiteFinished();
        }
        stepEventBusThreadLocal.remove();
    }

    private boolean softAssertsEnabled = false;

    public void disableSoftAsserts() {
        softAssertsEnabled = false;
    }

    public void enableSoftAsserts() {
        softAssertsEnabled = true;
    }

    public boolean softAssertsActive() {
        return softAssertsEnabled;
    }

    public String getTestSource() {
        return testSource;
    }

    public void setTestSource(String testSource) {
        this.testSource = testSource;
    }

    public void cancelPreviousTest() {
        baseStepListener.cancelPreviousTest();
    }

    public void lastTestPassedAfterRetries(int remainingTries, List<String> failureMessages,TestFailureCause testFailureCause) {
        baseStepListener.lastTestPassedAfterRetries(remainingTries, failureMessages,testFailureCause);
    }

    public static void overrideEventBusWith(StepEventBus stepEventBus) {
        stepEventBusThreadLocal.set(stepEventBus);
    }
}
