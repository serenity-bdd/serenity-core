package net.thucydides.core.steps;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.core.collect.NewList;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.serenitybdd.core.eventbus.Broadcaster;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.events.TestLifecycleEvents;
import net.thucydides.core.model.*;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_ENABLE_WEBDRIVER_IN_FIXTURE_METHODS;

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

    private static final ThreadLocal<StepEventBus> stepEventBusThreadLocal = new ThreadLocal<StepEventBus>();

    private static final ConcurrentMap<Object, StepEventBus> STICKY_EVENT_BUSES = new ConcurrentHashMap<>();

    private static final String CORE_THUCYDIDES_PACKAGE = "net.thucydides.core";
    private static final Logger LOGGER = LoggerFactory.getLogger(StepEventBus.class);

    /**
     * The event bus used to inform listening classes about when tests and test steps start and finish.
     * There is a separate event bus for each thread.
     */
    public static StepEventBus getEventBus() {
        if (stepEventBusThreadLocal.get() == null) {
            synchronized (stepEventBusThreadLocal) {
                stepEventBusThreadLocal.set(new StepEventBus(ConfiguredEnvironment.getEnvironmentVariables()));
            }
        }
        return stepEventBusThreadLocal.get();
    }

    public static StepEventBus eventBusFor(Object key) {
        if (key == null) { return new SilentEventBus(ConfiguredEnvironment.getEnvironmentVariables()); }
        STICKY_EVENT_BUSES.putIfAbsent(key, new StepEventBus(ConfiguredEnvironment.getEnvironmentVariables()));
        return STICKY_EVENT_BUSES.get(key);
    }

    public static void setCurrentBusToEventBusFor(Object key) {
        synchronized (stepEventBusThreadLocal) {
            stepEventBusThreadLocal.set(eventBusFor(key));
        }
    }

    public static void clearEventBusFor(Object key) {
        STICKY_EVENT_BUSES.remove(key);
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
    private Optional<Boolean> isDryRun = Optional.empty();

    private final EnvironmentVariables environmentVariables;
    private final CleanupMethodLocator cleanupMethodLocator;

    @Inject
    public StepEventBus(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.cleanupMethodLocator = new CleanupMethodLocator();
//        Darkroom.isOpenForBusiness();
    }

    public EnvironmentVariables getEnvironmentVariables() {
        return environmentVariables;
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
        if (baseStepListener == null) {
            LOGGER.error("No base step listener registered - this is generally a bad sign.");
        }
        Preconditions.checkNotNull(baseStepListener, "No BaseStepListener has been registered");
        return baseStepListener;
    }

    public void testStarted(final String testName) {
        clear();
        for (StepListener stepListener : getAllListeners()) {
            stepListener.testStarted(testName);
        }
        TestLifecycleEvents.postEvent(TestLifecycleEvents.testStarted());
    }

    public void testStarted(final String testName, final String id) {
        clear();
        for (StepListener stepListener : getAllListeners()) {
            stepListener.testStarted(testName, id);
        }
        TestLifecycleEvents.postEvent(TestLifecycleEvents.testStarted());
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
        List<StepListener> allListeners = new ArrayList(registeredListeners);
        allListeners.addAll(getCustomListeners());
        return NewList.copyOf(allListeners);
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

    public void updateExampleLineNumber(int lineNumber) {
        getBaseStepListener().updateExampleLineNumber(lineNumber);
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

        if (clearSessionForEachTest()) {
            Serenity.clearCurrentSession();
            StepFactory.getFactory().reset();
        }

        resultTally = null;
        classUnderTest = null;
        webdriverSuspensions.clear();

        Broadcaster.unregisterAllListeners();
    }

    private boolean clearSessionForEachTest() {
        return !ThucydidesSystemProperty.SERENITY_MAINTAIN_SESSION.booleanFrom(environmentVariables, false);
    }

    private void noAssumptionsViolated() {
        assumptionViolated = false;
        assumptionViolatedMessage = "";
    }

    /**
     * Removes a test suspension
     */
    public void unsuspend() {
        suspendedTest = false;
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

    public void testFinished(boolean inDataDrivenTest) {
        TestOutcome outcome = getBaseStepListener().getCurrentTestOutcome();
        outcome = checkForEmptyScenarioIn(outcome);

        for (StepListener stepListener : getAllListeners()) {
            stepListener.testFinished(outcome, inDataDrivenTest);
        }

        TestLifecycleEvents.postEvent(TestLifecycleEvents.testFinished());
        clear();
    }

    public void testFinished() {
        testFinished(false);
    }

    private TestOutcome checkForEmptyScenarioIn(TestOutcome outcome) {
        if (isAGherkinScenario(outcome)) {
            if (outcome.hasNoSteps()) {
                return outcome.withResult(TestResult.PENDING);
            }
        }
        return outcome;
    }

    private boolean isAGherkinScenario(TestOutcome outcome) {
        if ((outcome == null) || (outcome.getTestSource() == null)) return false;
        return outcome.getTestSource().equalsIgnoreCase("cucumber") || outcome.getTestSource().equalsIgnoreCase("jbehave");
    }

    public void testFinished(TestOutcome result) {
        for (StepListener stepListener : getAllListeners()) {
            stepListener.testFinished(result);
        }
        TestLifecycleEvents.postEvent(TestLifecycleEvents.testFinished());
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

    private boolean driverReenabled = false;

    public void reenableWebDriver() {
        driverReenabled = true;
    }

    private boolean inFixureMethod() {
        boolean activateWebDriverInFixtureMethods = SERENITY_ENABLE_WEBDRIVER_IN_FIXTURE_METHODS.booleanFrom(environmentVariables, true);

        return (activateWebDriverInFixtureMethods && cleanupMethodLocator.currentMethodWasCalledFromACleanupMethod());
    }

    public boolean webdriverCallsAreSuspended() {

        if (driverReenabled || inFixureMethod()) {
            return false;
        }
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

    public void useScenarioOutline(String scenarioOutline) {
        getBaseStepListener().latestTestOutcome().ifPresent(
                testOutcome -> testOutcome.useScenarioOutline(scenarioOutline)
        );
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

    public java.util.Optional<TestStep> getCurrentStep() {return getBaseStepListener().cloneCurrentStep(); }

    /**
     * Set all steps in the current test outcome to a given result.
     * Used to set all steps to PENDING or SKIPPED, for example.
     * @param result
     */
    public void setAllStepsTo(TestResult result) {
        baseStepListener.setAllStepsTo(result);
    }

    private final Optional<TestResult> NO_FORCED_RESULT = Optional.empty();

    public java.util.Optional<TestResult> getForcedResult() {
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

    java.util.Optional<TestResult> NO_RESULT_YET = java.util.Optional.empty();
    public java.util.Optional<TestResult> resultSoFar() {

        return (getBaseStepListener().latestTestOutcome().isPresent()) ?
                java.util.Optional.ofNullable(getBaseStepListener().latestTestOutcome().get().getResult()) : NO_RESULT_YET;
    }

    public void mergePreviousStep() {
        baseStepListener.mergeLast(2).steps();
    }

    public void updateOverallResults() {
        if (baseStepListener != null) {
            baseStepListener.updateOverallResults();
        }
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

    public void castActor(String name) {
        if ((baseStepListener != null) && (baseStepListener.latestTestOutcome() != null)) {
            baseStepListener.latestTestOutcome().ifPresent(
                    testOutcome -> testOutcome.castActor(name)
            );
        }
    }

    public void initialiseSession() {
        if (clearSessionForEachTest()) {
            Serenity.clearCurrentSession();
        }
    }
}
