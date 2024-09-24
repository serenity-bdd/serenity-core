package net.thucydides.core.steps;

import com.google.common.base.Preconditions;

import io.cucumber.core.resource.ClassLoaders;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.model.collect.NewList;
import net.serenitybdd.model.environment.ConfiguredEnvironment;
import net.serenitybdd.core.eventbus.Broadcaster;
import net.serenitybdd.core.parallel.Agency;
import net.serenitybdd.core.parallel.Agent;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.domain.*;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.environment.TestLocalEnvironmentVariables;
import net.thucydides.core.events.TestLifecycleEvents;
import net.thucydides.model.screenshots.ScreenshotAndHtmlSource;
import net.thucydides.core.steps.session.TestSession;
import net.thucydides.model.steps.ExecutedStepDescription;
import net.thucydides.model.steps.StepFailure;
import net.thucydides.model.steps.StepListener;
import net.thucydides.model.steps.TestFailureCause;
import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.model.webdriver.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static net.thucydides.core.steps.BaseStepListener.ScreenshotType.OPTIONAL_SCREENSHOT;
import static net.thucydides.model.ThucydidesSystemProperty.SERENITY_ENABLE_WEBDRIVER_IN_FIXTURE_METHODS;

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

    private static final ThreadLocal<StepEventBus> stepEventBusThreadLocal = new ThreadLocal<>();

    private static final ConcurrentMap<Object, StepEventBus> STICKY_EVENT_BUSES = new ConcurrentHashMap<>();

    private static final String CORE_THUCYDIDES_PACKAGE = "net.thucydides.core";

    private static final Logger LOGGER = LoggerFactory.getLogger(StepEventBus.class);
    public static final String JUNIT_JUPITER_EXECUTION_PARALLEL_ENABLED = "junit.jupiter.execution.parallel.enabled";

    private static boolean noCleanupForStickyBuses = false;

    private static final String JUNIT_CONFIG_FILE_NAME = "junit-platform.properties";

    private static boolean jUnit5ParallelMode = false;

    private final Map<Class<?>, String> testCaseDisplayNames = new HashMap<>();

    static {
        jUnit5ParallelMode =  isJUnit5ParallelMode();
    }

    /**
     * The event bus used to inform listening classes about when tests and test steps start and finish.
     * There is a separate event bus for each thread.
     */
    public static StepEventBus getEventBus() {
        if (stepEventBusThreadLocal.get() == null) {
            synchronized (stepEventBusThreadLocal) {
                stepEventBusThreadLocal.set(new StepEventBus(ConfiguredEnvironment.getEnvironmentVariables(),
                        ConfiguredEnvironment.getConfiguration()));
            }
        }
        return stepEventBusThreadLocal.get();
    }

    /**
     * If called from a Cucumber parallel Test session will return the corresponding StepEventBus, otherwise
     * will redirect to getEventBus()
     */
    public static StepEventBus getParallelEventBus() {
        if (TestSession.isSessionStarted()) {
            return TestSession.getTestSessionContext().getStepEventBus();
        } else {
            return getEventBus();
        }
    }

    /**
     * @param key
     * @return
     */

    public static StepEventBus eventBusFor(Object key) {
        if (key == null) {
            return new SilentEventBus(ConfiguredEnvironment.getEnvironmentVariables());
        }
        STICKY_EVENT_BUSES.putIfAbsent(key, new StepEventBus(ConfiguredEnvironment.getEnvironmentVariables(),
                ConfiguredEnvironment.getConfiguration()));
        return STICKY_EVENT_BUSES.get(key);
    }

    public static void setCurrentBusToEventBusFor(Object key) {
        synchronized (stepEventBusThreadLocal) {
            stepEventBusThreadLocal.set(eventBusFor(key));
        }
    }

    public static void clearEventBusFor(Object key) {
        if(noCleanupForStickyBuses) {
            return;
        }
        STICKY_EVENT_BUSES.remove(key);
    }

    public static void forceClearEventBusFor(Object key) {
        STICKY_EVENT_BUSES.remove(key);
    }

    private List<StepListener> registeredListeners = new CopyOnWriteArrayList<>();
    /**
     * A reference to the base step listener, if registered.
     */
    private BaseStepListener baseStepListener;

    private TestResultTally resultTally;

    private final Stack<String> stepStack = new Stack<>();
    private final Stack<Boolean> webdriverSuspensions = new Stack<>();

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
    private final File outputDirectory;

    
    public StepEventBus(EnvironmentVariables environmentVariables, Configuration configuration) {
        this.environmentVariables = environmentVariables;
        this.cleanupMethodLocator = new CleanupMethodLocator();
        this.outputDirectory = configuration.getOutputDirectory();
    }

    /**
     * Method used for testing purposes to find an event bus for a given test.
     */
    public static Optional<StepEventBus> eventBusForTest(String testName) {
        return STICKY_EVENT_BUSES.keySet().stream()
                .filter(key -> key.toString().contains(testName))
                .map(STICKY_EVENT_BUSES::get)
                .findFirst();
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
        return currentBaseStepListener() != null;
    }

    public BaseStepListener getBaseStepListener() {

        BaseStepListener currentListener = currentBaseStepListener();

        if(currentListener == null) {
            LOGGER.error("CurrentListener is null");
            Thread.dumpStack();
        }

        Preconditions.checkNotNull(currentListener, "No BaseStepListener has been registered - are you running your test using the Serenity runners?");

        return currentListener;
    }

    private BaseStepListener currentBaseStepListener() {
        return Agency.getInstance().currentAgentSpecificListener().orElse(baseStepListener);
    }

    public void testStarted(final String testName) {
        clear();
        for (StepListener stepListener : getAllListeners()) {
            stepListener.testStarted(testName);
        }
        StepEventBus.getParallelEventBus().setTestSource(testSource);
        TestLifecycleEvents.postEvent(TestLifecycleEvents.testStarted());
    }

    public void testScenarioStarted(String testName, String testMethod, String testId, String scenarioId) {
        clear();
        for (StepListener stepListener : getAllListeners()) {
            stepListener.testStarted(testName, testMethod, testId, scenarioId);
        }
        StepEventBus.getParallelEventBus().setTestSource(testSource);
        TestLifecycleEvents.postEvent(TestLifecycleEvents.testStarted());
    }

    public void testStarted(final String testName, final String id) {
        clear();
        for (StepListener stepListener : getAllListeners()) {
            stepListener.testStarted(testName, id);
        }
        TestLifecycleEvents.postEvent(TestLifecycleEvents.testStarted());
    }

    public void testStarted(final String testName, ZonedDateTime startTime) {
        clear();
        for (StepListener stepListener : getAllListeners()) {
            stepListener.testStarted(testName, startTime);
        }
        TestLifecycleEvents.postEvent(TestLifecycleEvents.testStarted());
    }

    public void testStarted(final String testName, final String id, ZonedDateTime startTime) {
        clear();
        for (StepListener stepListener : getAllListeners()) {
            stepListener.testStarted(testName, id, startTime);
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

    public void testStarted(final String newTestName,
                            final Class<?> testClass,
                            String testMethod,
                            String uniqueId,
                            String scenarioId) {
        ensureThatTheTestSuiteStartedWith(testClass);
        if (newTestName != null) {
            testScenarioStarted(newTestName, testMethod, uniqueId, scenarioId);
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
        List<StepListener> allListeners = registeredListeners();
        allListeners.addAll(getCustomListeners());
        return NewList.copyOf(allListeners);
    }

    private List<StepListener> registeredListeners() {
        List<StepListener> listeners = new ArrayList<>(registeredListeners);

        if (baseStepListener != null) {
            listeners.remove(baseStepListener);
        }

        if (currentBaseStepListener() != null) {
            listeners.add(currentBaseStepListener());
        }
        return listeners;
    }

    private Set<StepListener> getCustomListeners() {

        if (customListeners == null) {
            customListeners = Collections.synchronizedSet(new HashSet<>());

            ServiceLoader<StepListener> stepListenerServiceLoader = ServiceLoader.load(StepListener.class);
            Iterator<StepListener> listenerImplementations = stepListenerServiceLoader.iterator();
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
            if (testCaseDisplayNames.containsKey(testClass)) {
                stepListener.testSuiteStarted(testClass, testCaseDisplayNames.get(testClass));
            } else {
                stepListener.testSuiteStarted(testClass);
            }
        }
        TestLifecycleEvents.postEvent(TestLifecycleEvents.testSuiteStarted());
    }

    public void testSuiteStarted(final Class<?> testClass, String testCaseName) {
        LOGGER.debug("Test suite started for {} in {}", testCaseName, testClass);
        clear();
        testCaseDisplayNames.put(testClass, testCaseName);
        updateClassUnderTest(testClass);
        for (StepListener stepListener : getAllListeners()) {
            stepListener.testSuiteStarted(testClass,testCaseName);
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
        isDryRun = Optional.empty();
        suspendedTest = false;

        Broadcaster.unregisterAllListeners();
        dropClosableListeners();
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

    private void recordTestMetadataFor(TestOutcome outcome) {
        outcome.setTestSource(testSource);
        if (!TestContext.forTheCurrentTest().getContext().isEmpty()) {
            outcome.setContext(TestContext.forTheCurrentTest().getContext());
        }
    }

    private void recordTestContext() {
        TestOutcome outcome = getBaseStepListener().getCurrentTestOutcome();
    }

    public void testFinished(boolean inDataDrivenTest, ZonedDateTime finishTime) {
        TestOutcome outcome = getBaseStepListener().getCurrentTestOutcome();
        outcome = checkForEmptyScenarioIn(outcome);
        recordTestMetadataFor(outcome);

        try {
            for (StepListener stepListener : getAllListeners()) {
                stepListener.testFinished(outcome, inDataDrivenTest, finishTime);
            }
        } catch(Throwable testFailedInTeardownOperations) {
            getBaseStepListener().stepFailedWithException(testFailedInTeardownOperations);
        }

        TestLifecycleEvents.postEvent(TestLifecycleEvents.testFinished());

        SystemEnvironmentVariables.currentEnvironment().reset();
        TestLocalEnvironmentVariables.clear();
        clear();
    }

    public void testFinished(boolean inDataDrivenTest) {
        testFinished(inDataDrivenTest, ZonedDateTime.now());
    }


    public void finishTestRun() {
        for (StepListener stepListener : getAllListeners()) {
            stepListener.testRunFinished();
        }
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
        stepStarted(stepDescription, false, ZonedDateTime.now());
    }

    public void stepStarted(final ExecutedStepDescription stepDescription, ZonedDateTime startTime) {
        stepStarted(stepDescription, false, startTime);
    }

    /**
     * Start the execution of a test step.
     */
    public void stepStarted(final ExecutedStepDescription stepDescription,
                            boolean isPrecondition,
                            ZonedDateTime startTime) {

        pushStep(stepDescription.getName());

        for (StepListener stepListener : getAllListeners()) {
            stepListener.stepStarted(stepDescription, startTime);
        }

        if (isPrecondition) {
            getBaseStepListener().currentStepIsAPrecondition();
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

    /**
     * Called from serial replay - StepFinishedEvent
     * @param screenshots - screenshots that were recorded when the step was finished
     */
    public void stepFinished(List<ScreenshotAndHtmlSource> screenshots, ZonedDateTime time) {
        stepDone();
        getResultTally().logExecutedTest();
        for (StepListener stepListener : getAllListeners()) {
            stepListener.stepFinished(screenshots, time);
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

    public void stepFailed(final StepFailure failure,
                           List<ScreenshotAndHtmlSource> screenshotList,
                           boolean isInDataDrivenTest,
                           ZonedDateTime timestamp) {

        stepDone();
        getResultTally().logFailure(failure);

        for (StepListener stepListener : getAllListeners()) {
            stepListener.stepFailed(failure,screenshotList,isInDataDrivenTest, timestamp);
        }
        stepFailed = true;
    }

    public void stepFailed(final StepFailure failure, List<ScreenshotAndHtmlSource> screenshotList, boolean isInDataDrivenTest) {
        stepFailed(failure,screenshotList,isInDataDrivenTest, ZonedDateTime.now());
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

    private void dropClosableListeners() {
        registeredListeners = registeredListeners.stream().filter(stepListener -> (!(stepListener instanceof Droppable))).collect(Collectors.toList());
    }

    public void dropAllListeners() {
        registeredListeners.clear();
    }

    private boolean driverReenabled = false;

    public void reenableWebDriver() {
        driverReenabled = true;
    }

    public boolean inFixtureMethod() {
        return cleanupMethodLocator.currentMethodWasCalledFromACleanupMethod();
    }

    private boolean activateWebDriverInFixtureMethods() {
        return SERENITY_ENABLE_WEBDRIVER_IN_FIXTURE_METHODS.booleanFrom(environmentVariables, true);
    }

    public boolean webdriverCallsAreSuspended() {

        if (driverReenabled || (inFixtureMethod() && activateWebDriverInFixtureMethods())) {
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

    public void testFailed(final Throwable cause, ZonedDateTime timestamp) {
        TestOutcome outcome = getBaseStepListener().getCurrentTestOutcome();
        outcome.recordDuration(timestamp);
        testFailed(cause);
    }

    /**
     * The test failed, but not during the execution of a step.
     *
     * @param cause the underlying cause of the failure.
     */
    public void testFailed(final Throwable cause) {
        TestOutcome outcome = getBaseStepListener().getCurrentTestOutcome();
        recordTestMetadataFor(outcome);
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
        recordTestMetadataFor(getBaseStepListener().getCurrentTestOutcome());
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
            case ABORTED:
                testAborted();
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
        recordTestMetadataFor(getBaseStepListener().getCurrentTestOutcome());
    }

    public void testSkipped() {
        for (StepListener stepListener : getAllListeners()) {
            stepListener.testSkipped();
        }
        suspendTest();
        recordTestMetadataFor(getBaseStepListener().getCurrentTestOutcome());
    }

    public void testAborted() {
        for (StepListener stepListener : getAllListeners()) {
            stepListener.testAborted();
        }
        suspendTest();
        recordTestMetadataFor(getBaseStepListener().getCurrentTestOutcome());
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

    public void setRule(Rule rule) {
        getBaseStepListener().getCurrentTestOutcome().setRule(rule);
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

    public void exampleStarted(Map<String, String> data, ZonedDateTime time) {
        for (StepListener stepListener : getAllListeners()) {
            stepListener.exampleStarted(data, time);
        }
    }

    public void exampleStarted(Map<String, String> data, String exampleName) {
        for (StepListener stepListener : getAllListeners()) {
            stepListener.exampleStarted(data, exampleName);
        }
    }

    public void exampleStarted(Map<String, String> data, String exampleName, ZonedDateTime time) {
        for (StepListener stepListener : getAllListeners()) {
            stepListener.exampleStarted(data, exampleName, time);
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
        if (!isDryRun()) {
//            getBaseStepListener().notifyUIError();
            getBaseStepListener().takeScreenshot();
        }
    }

    public void notifyFailure() {
        getBaseStepListener().notifyUIError();
    }

    public boolean testSuiteHasStarted() {
        return getBaseStepListener().testSuiteRunning();
    }

    public String getAssumptionViolatedMessage() {
        return assumptionViolatedMessage;
    }

    public java.util.Optional<TestStep> getCurrentStep() {
        return getBaseStepListener().cloneCurrentStep();
    }

    /**
     * Set all steps in the current test outcome to a given result.
     * Used to set all steps to PENDING or SKIPPED, for example.
     *
     * @param result
     */
    public void setAllStepsTo(TestResult result) {
        getBaseStepListener().setAllStepsTo(result);
    }

    private final Optional<TestResult> NO_FORCED_RESULT = Optional.empty();

    public java.util.Optional<TestResult> getForcedResult() {
        return (getBaseStepListener() != null) ? getBaseStepListener().getForcedResult() : NO_FORCED_RESULT;
    }

    public synchronized boolean isDryRun() {
        if (this.isDryRun.isPresent()) {
            return this.isDryRun.get();
        } else {
            return ThucydidesSystemProperty.SERENITY_DRY_RUN.booleanFrom(environmentVariables);
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
        getBaseStepListener().mergeLast(2).steps();
    }

    public void updateOverallResults() {
        if (getBaseStepListener() != null) {
            getBaseStepListener().updateOverallResults();
        }
    }

    public void reset() {
        if (isBaseStepListenerRegistered()) {
            getBaseStepListener().testSuiteFinished();
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
        getBaseStepListener().cancelPreviousTest();
    }

    public void lastTestPassedAfterRetries(int remainingTries, List<String> failureMessages, TestFailureCause testFailureCause) {
        getBaseStepListener().lastTestPassedAfterRetries(remainingTries, failureMessages, testFailureCause);
    }

    public static void overrideEventBusWith(StepEventBus stepEventBus) {
        stepEventBusThreadLocal.set(stepEventBus);
    }

    public void castActor(String name) {
        if ((getBaseStepListener() != null) && (getBaseStepListener().latestTestOutcome() != null)) {
            getBaseStepListener().latestTestOutcome().ifPresent(
                    testOutcome -> testOutcome.castActor(name)
            );
        }
    }

    public void initialiseSession() {
        if (clearSessionForEachTest()) {
            Serenity.clearCurrentSession();
        }
    }

    public void registerAgent(Agent agent) {
        Agency.getInstance().registerAgent(agent);
    }

    public void registerAgents(Agent... agents) {
        for (Agent agent : agents) {
            Agency.getInstance().registerAgent(agent);
        }
    }

    public void dropAgents(Agent... agents) {
        for (Agent agent : agents) {
            Agency.getInstance().dropAgent(agent);
        }
    }

    public void dropAgent(Agent agent) {
        Agency.getInstance().dropAgent(agent);
    }

    public void mergeActivitiesToDefaultStepListener(Agent... agents) {
        mergeActivitiesToDefaultStepListener("{0}", agents);
    }

    public void mergeActivitiesToDefaultStepListener(String stepName, Agent... agents) {
        stream(agents)
                .map(agent -> Agency.getInstance().baseListenerFor(agent))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(BaseStepListener::getTestOutcomes)
                .filter(testOutcomes -> !testOutcomes.isEmpty())    // There should always be exactly one test outcome
                .map(testOutcomes -> testOutcomes.get(0))
                .forEach(outcome -> recordOutcomeAsSteps(stepName, outcome, baseStepListener));               // Record the steps of this outcome in the main test outcome

        stream(agents).forEach(agent -> Agency.getInstance().dropAgent(agent));
    }

    private void recordOutcomeAsSteps(String topLevelStepName, TestOutcome testOutcome, BaseStepListener stepListener) {
        stepListener.stepStarted(ExecutedStepDescription.withTitle(formattedDescription(topLevelStepName, testOutcome.getName())));
        stepListener.addChildStepsFrom(testOutcome.getTestSteps());
        stepListener.stepFinished();
    }

    private String formattedDescription(String topLevelStepName, String agent) {
        return topLevelStepName.replace("{0}", agent);
    }


    public void wrapUpCurrentCucumberStep() {
        if (isBaseStepListenerRegistered() && CurrentTestResult.isCucumber(getBaseStepListener().getCurrentTestOutcome()) && getBaseStepListener().currentStepDepth() == 1) {
            getBaseStepListener().currentStepDone(TestResult.UNDEFINED);
        }
    }

    public boolean currentTestHasTag(TestTag tag) {
        if (isBaseStepListenerRegistered()) {
            if (getBaseStepListener().latestTestOutcome().isPresent()) {
                return getBaseStepListener().getCurrentTestOutcome().getTags().contains(tag);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isASingleBrowserScenario() {
        if(jUnit5ParallelMode) {
            return false;
        }
        return uniqueSession
                || currentTestHasTag(TestTag.withValue("singlebrowser"))
                || getBaseStepListener().currentStoryHasTag(TestTag.withValue("singlebrowser"));
    }

    public boolean isNewSingleBrowserScenario() {
        return isASingleBrowserScenario() && !previousScenarioWasASingleBrowserScenario();
    }

    private boolean previousScenarioWasASingleBrowserScenario() {
        if (isBaseStepListenerRegistered()) {
            return getBaseStepListener().previousScenarioWasASingleBrowserScenario();
        } else {
            return false;
        }
    }

    private static boolean isJUnit5ParallelMode() {
        Properties junitProperties = fromClasspathResource(JUNIT_CONFIG_FILE_NAME);
        if(junitProperties.size() > 0) {
            return junitProperties.getProperty(JUNIT_JUPITER_EXECUTION_PARALLEL_ENABLED, "false").equalsIgnoreCase("true");
        } else {
            return System.getProperty("junit.jupiter.execution.parallel.enabled", "false").equalsIgnoreCase("true");
        }
    }

    private static Properties fromClasspathResource(String configFileName) {
		Properties props = new Properties();
		try {
			InputStream inputStream = ClassLoaders.getDefaultClassLoader().getResourceAsStream(configFileName);
			if (inputStream != null) {
				LOGGER.info(String.format(
					"Loading JUnit Platform configuration parameters from classpath resource [%s].", configFileName));
				props.load(inputStream);
			}
		}
		catch (Exception ex) {
			LOGGER.info(String.format("Failed to load JUnit Platform configuration parameters from classpath resource [%s].", configFileName));
		}

		return props;
	}

    public static void setNoCleanupForStickyBuses(boolean noCleanup) {
        noCleanupForStickyBuses = noCleanup;
    }

    public List<ScreenshotAndHtmlSource> takeScreenshots() {
        if (!isDryRun()) {
            List<ScreenshotAndHtmlSource> screenshots = new ArrayList<>();
            for (StepListener stepListener : getAllListeners()) {
                stepListener.takeScreenshots(screenshots);
            }
            return screenshots;
        } else {
            return new ArrayList<>();
        }
    }

    public List<ScreenshotAndHtmlSource> takeScreenshots(TestResult testResult) {
        if (!isDryRun()) {
            List<ScreenshotAndHtmlSource> screenshots = new ArrayList<>();
            for (StepListener stepListener : getAllListeners()) {
                stepListener.takeScreenshots(testResult, screenshots);
            }
            return screenshots;
        } else {
            return new ArrayList<>();
        }
    }
}
