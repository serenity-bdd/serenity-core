package net.thucydides.core.steps;

import com.assertthat.selenium_shutterbug.utils.web.ScrollStrategy;
import com.google.inject.Injector;
import net.serenitybdd.core.PendingStepException;
import net.serenitybdd.core.annotations.events.AfterExample;
import net.serenitybdd.core.annotations.events.AfterScenario;
import net.serenitybdd.core.annotations.events.BeforeExample;
import net.serenitybdd.core.annotations.events.BeforeScenario;
import net.serenitybdd.core.di.WebDriverInjectors;
import net.serenitybdd.core.exceptions.TheErrorType;
import net.serenitybdd.core.lifecycle.LifecycleRegister;
import net.serenitybdd.core.photography.Darkroom;
import net.serenitybdd.core.photography.Photographer;
import net.serenitybdd.core.photography.ScreenshotPhoto;
import net.serenitybdd.core.photography.SoundEngineer;
import net.serenitybdd.core.photography.bluring.AnnotatedBluring;
import net.serenitybdd.core.rest.RestQuery;
import net.serenitybdd.core.strings.Joiner;
import net.serenitybdd.core.time.SystemClock;
import net.serenitybdd.core.webdriver.OverrideDriverCapabilities;
import net.serenitybdd.core.webdriver.configuration.RestartBrowserForEach;
import net.serenitybdd.core.webdriver.enhancers.AtTheEndOfAWebDriverTest;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.annotations.TestAnnotations;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.junit.SerenityJUnitTestCase;
import net.thucydides.core.model.*;
import net.thucydides.core.model.failures.FailureAnalysis;
import net.thucydides.core.model.screenshots.ScreenshotPermission;
import net.thucydides.core.model.stacktrace.FailureCause;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.screenshots.ScreenshotAndHtmlSource;
import net.thucydides.core.screenshots.ScreenshotException;
import net.thucydides.core.webdriver.*;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.SessionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static net.serenitybdd.core.webdriver.configuration.RestartBrowserForEach.EXAMPLE;
import static net.serenitybdd.core.webdriver.configuration.RestartBrowserForEach.SCENARIO;
import static net.thucydides.core.model.Stories.findStoryFrom;
import static net.thucydides.core.model.TestResult.*;
import static net.thucydides.core.steps.BaseStepListener.ScreenshotType.MANDATORY_SCREENSHOT;
import static net.thucydides.core.steps.BaseStepListener.ScreenshotType.OPTIONAL_SCREENSHOT;

/**
 * Observes the test run and stores test run details for later reporting.
 * Observations are recorded in an TestOutcome object. This includes
 * recording the names and results of each test, and taking and storing
 * screenshots at strategic points during the tests.
 */
public class BaseStepListener implements StepListener, StepPublisher {

    /**
     * Used to build the test outcome structure as the test step results come in.
     */
    private final List<TestOutcome> testOutcomes;

    private ThreadLocal<TestOutcome> currentTestOutcome;

    /**
     * Keeps track of what steps have been started but not finished, in order to structure nested steps.
     */
    private final ThreadLocal<Stack<TestStep>> currentStepStack;

    /**
     * Keeps track of the current step group, if any.
     */
    private final ThreadLocal<Stack<TestStep>> currentGroupStack;

    private StepEventBus eventBus;
    /**
     * Clock used to pause test execution.
     */
    private final SystemClock clock;

    private ScreenshotPermission screenshots;
    /**
     * The Java class (if any) containing the tests.
     */
    private Class<?> testSuite;

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseStepListener.class);

    private WebDriver driver;

    private File outputDirectory;

    private WebdriverProxyFactory proxyFactory;

    private Story testedStory;

    private Configuration configuration;

    private List<String> storywideIssues;

    private List<TestTag> storywideTags;
    private ScrollStrategy scrollStrategy;
    private Darkroom darkroom;
    private Photographer photographer;
    private SoundEngineer soundEngineer = new SoundEngineer();

    private final CloseBrowser closeBrowsers;

    public void setEventBus(StepEventBus eventBus) {
        this.eventBus = eventBus;
    }

    public StepEventBus getEventBus() {
        if (eventBus == null) {
            eventBus = StepEventBus.getEventBus();
        }
        return eventBus;
    }

    private Darkroom getDarkroom() {
        if (darkroom == null) {
            darkroom = new Darkroom();
        }
        return darkroom;
    }

    public java.util.Optional<TestStep> cloneCurrentStep() {
        return ((currentStepExists()) ? Optional.of(getCurrentStep().clone()) : Optional.empty());
    }

    public java.util.Optional<TestResult> getAnnotatedResult() {
        return java.util.Optional.ofNullable(getCurrentTestOutcome().getAnnotatedResult());
    }

    public void setAllStepsTo(TestResult result) {
        getCurrentTestOutcome().setAnnotatedResult(result);
        getCurrentTestOutcome().setAllStepsTo(result);
    }

    public void overrideResultTo(TestResult result) {
        getCurrentTestOutcome().overrideAnnotatedResult(result);
    }

    public void recordManualTestResult(TestResult result,
                                       Optional<String> lastTestedVersion,
                                       Boolean isUpToDate,
                                       Optional<String> testEvidence) {
        getCurrentTestOutcome().overrideAnnotatedResult(result);
        lastTestedVersion.ifPresent(
                version -> {
                    getCurrentTestOutcome().setLastTested(version);
                    getCurrentTestOutcome().setManualTestingUpToDate(isUpToDate);
                    testEvidence.ifPresent( evidence -> getCurrentTestOutcome().setManualTestEvidence(testEvidenceLinksFrom(testEvidence)));
                }
        );
    }

    @NotNull
    private List<String> testEvidenceLinksFrom(Optional<String> testEvidence) {
        List<String> testEvidenceLinks = new ArrayList<>();
        if (testEvidence.isPresent()) {
            testEvidenceLinks = Arrays.stream(testEvidence.get().split(",")).map(String::trim).collect(Collectors.toList());
        }
        return testEvidenceLinks;
    }

    public void exceptionExpected(Class<? extends Throwable> expected) {
        if (isNotAnException(expected.getName())) {
            return;
        }


        if ((getCurrentTestOutcome().getTestFailureCause() != null)
                && TheErrorType.causedBy(getCurrentTestOutcome().getTestFailureErrorType()).isAKindOf(expected)) {
            getCurrentTestOutcome().resetFailingStepsCausedBy(expected);
            getCurrentTestOutcome().recordStep(
                    TestStep.forStepCalled("Expected exception thrown : " + expected.getName())
                            .withResult(TestResult.SUCCESS)
            );
        }
    }

    private boolean isNotAnException(String name) {
        return name.toLowerCase().endsWith("$none");
    }

    public StepMerger mergeLast(int maxStepsToMerge) {
        return new StepMerger(maxStepsToMerge);
    }

    public int getStepCount() {
        return getCurrentTestOutcome().getStepCount();
    }

    public int getRunningStepCount() {
        return getCurrentTestOutcome().getRunningStepCount();
    }

    public void updateOverallResults() {
        getCurrentTestOutcome().updateOverallResults();
    }

    public Photographer getPhotographer() {
        if (photographer == null) {
            if (shouldUseFullPageScreenshotStrategy()){
                scrollStrategy = ScrollStrategy.WHOLE_PAGE;
            } else {
                scrollStrategy = ScrollStrategy.VIEWPORT_ONLY;
            }
            photographer = new Photographer(getDarkroom(), scrollStrategy);
        }
        return photographer;
    }

    public void cancelPreviousTest() {
        synchronized (testOutcomes) {
            if (!testOutcomes.isEmpty()) {
                testOutcomes.remove(testOutcomes.size() - 1);
            }
        }
    }

    public void lastTestPassedAfterRetries(int attemptNum, List<String> failureMessages, TestFailureCause testfailureCause) {
        if (latestTestOutcome().isPresent()) {
            latestTestOutcome().get().recordStep(
                    TestStep.forStepCalled("UNSTABLE TEST:\n" + failureHistoryFor(failureMessages))
                            .withResult(UNDEFINED));
//                            .withResult(TestResult.IGNORED));

            latestTestOutcome().get().addTag(TestTag.withName("Retries: " + attemptNum).andType("unstable test"));
            latestTestOutcome().get().setFlakyTestFailureCause(testfailureCause);
        }
    }

    private String failureHistoryFor(List<String> failureMessages) {
        List<String> bulletPoints = failureMessages.stream()
                .map(from -> "* " + from)
                .collect(Collectors.toList());

        return Joiner.on("\n").join(bulletPoints);
    }

    public void currentStepIsAPrecondition() {
        getCurrentStep().setPrecondition(true);
    }

    public void updateExampleLineNumber(int lineNumber) {
        currentStep().ifPresent(
                step -> step.setLineNumber(lineNumber)
        );
    }

    public class StepMerger {

        final int maxStepsToMerge;

        public StepMerger(int maxStepsToMerge) {
            this.maxStepsToMerge = maxStepsToMerge;
        }

        public void steps() {
            getCurrentTestOutcome().mergeMostRecentSteps(maxStepsToMerge);
        }

    }

    protected enum ScreenshotType {
        OPTIONAL_SCREENSHOT,
        MANDATORY_SCREENSHOT
    }

    public BaseStepListener(final File outputDirectory) {
        this(outputDirectory, Injectors.getInjector());
    }

    public BaseStepListener(final File outputDirectory, Injector injector) {
        this.proxyFactory = WebdriverProxyFactory.getFactory();
        this.testOutcomes = new ArrayList<>();
        this.currentTestOutcome = new ThreadLocal<>();
        this.currentStepStack = ThreadLocal.withInitial(Stack<TestStep>::new);
        this.currentGroupStack = ThreadLocal.withInitial(Stack<TestStep>::new);
        this.outputDirectory = outputDirectory;
        this.storywideIssues = new ArrayList<>();
        this.storywideTags = new ArrayList<>();
        //this.webdriverManager = injector.getInstance(WebdriverManager.class);
        this.clock = injector.getInstance(SystemClock.class);
        this.configuration = injector.getInstance(Configuration.class);
        //this.screenshotProcessor = injector.getInstance(ScreenshotProcessor.class);
        this.closeBrowsers = WebDriverInjectors.getInjector().getInstance(CloseBrowser.class);

    }

    /**
     * Create a step listener with a given web driver type.
     *
     * @param driverClass     a driver of this type will be used
     * @param outputDirectory reports and screenshots are generated here
     */
    public BaseStepListener(final Class<? extends WebDriver> driverClass, final File outputDirectory) {
        this(outputDirectory);
        this.driver = getProxyFactory().proxyFor(driverClass);
    }

    public BaseStepListener(final Class<? extends WebDriver> driverClass,
                            final File outputDirectory,
                            final Configuration configuration) {
        this(driverClass, outputDirectory);
        this.configuration = configuration;
    }

    public BaseStepListener(final File outputDirectory,
                            final WebdriverManager webdriverManager) {
        this(outputDirectory);
        //this.webdriverManager = webdriverManager;
    }

    /**
     * Create a step listener using the driver from a given page factory.
     * If the pages factory is null, a new driver will be created based on the default system values.
     *
     * @param outputDirectory reports and screenshots are generated here
     * @param pages           a pages factory.
     */
    public BaseStepListener(final File outputDirectory, final Pages pages) {
        this(outputDirectory);
        if (pages != null) {
            setDriverUsingPagesDriverIfDefined(pages);
        } else {
            createNewDriver();
        }
    }

    protected ScreenshotPermission screenshots() {
        if (screenshots == null) {
            screenshots = new ScreenshotPermission(configuration);
        }
        return screenshots;
    }

    private void createNewDriver() {
        setDriver(getProxyFactory().proxyDriver());
    }

    private void setDriverUsingPagesDriverIfDefined(final Pages pages) {
        if (pages.getDriver() == null) {
            ThucydidesWebDriverSupport.initialize();
            ThucydidesWebDriverSupport.useDriver(getDriver());
            pages.setDriver(getDriver());
        }
    }

    protected WebdriverProxyFactory getProxyFactory() {
        return proxyFactory;
    }

    public TestOutcome getCurrentTestOutcome() {
    	return latestTestOutcome().orElse(unavailableTestOutcome());
    }

    private static final TestOutcome UNAVAILABLE_TEST_OUTCOME = new TestOutcome("Test outcome unavailable"); // new UnavailableTestOutcome("Test outcome unavailable");

    private TestOutcome unavailableTestOutcome() {
        return UNAVAILABLE_TEST_OUTCOME;
    }

    public boolean isAvailable() {
        return true;
    }

    public java.util.Optional<TestOutcome> latestTestOutcome() {
        if (testOutcomes.isEmpty()) {
            return java.util.Optional.empty();
        } else {
        	return java.util.Optional.ofNullable(currentTestOutcome.get());
        }
    }

    protected SystemClock getClock() {
        return clock;
    }

    /**
     * A test suite (containing a series of tests) starts.
     *
     * @param startedTestSuite the class implementing the test suite (e.g. a JUnit test case)
     */
    public void testSuiteStarted(final Class<?> startedTestSuite) {
        testSuite = startedTestSuite;
        testedStory = findStoryFrom(startedTestSuite);
        suiteStarted = true;
        clearStorywideTagsAndIssues();
    }

    private void clearStorywideTagsAndIssues() {
        storywideIssues.clear();
        storywideTags.clear();
    }

    private boolean suiteStarted = false;

    public void testSuiteStarted(final Story story) {
        testSuite = null;
        testedStory = story;
        suiteStarted = true;
        clearStorywideTagsAndIssues();

    }

    public boolean testSuiteRunning() {
        return suiteStarted;
    }

    public void addIssuesToCurrentStory(List<String> issues) {
        storywideIssues.addAll(issues);
    }

    public void addTagsToCurrentStory(List<TestTag> tags) {
        storywideTags.addAll(tags);
    }

    private void closeDarkroom() {
        if (darkroom != null) {
            darkroom.waitUntilClose();
        }
    }

    public void testSuiteFinished() {
        closeDarkroom();
        clearStorywideTagsAndIssues();
        ThucydidesWebDriverSupport.clearStepLibraries();
        ThucydidesWebDriverSupport.clearDefaultDriver();

        if (this.currentTestIsABrowserTest()) {
            this.closeBrowsers.forTestSuite(this.testSuite).closeIfConfiguredForANew(RestartBrowserForEach.FEATURE);
        }

        suiteStarted = false;
    }


    /**
     * An individual test starts.
     *
     * @param testMethod the name of the test method in the test suite class.
     */
    public void testStarted(final String testMethod) {
        TestOutcome newTestOutcome = TestOutcome.forTestInStory(testMethod, testSuite, testedStory);
        this.currentTestOutcome.set(newTestOutcome);
        recordNewTestOutcome(testMethod, currentTestOutcome.get());

        LifecycleRegister.invokeMethodsAnnotatedBy(BeforeScenario.class, newTestOutcome);
    }

    public void testStarted(final String testMethod, final String id) {
        TestOutcome newTestOutcome = TestOutcome.forTestInStory(testMethod, testSuite, testedStory).withId(id);
        this.currentTestOutcome.set(newTestOutcome);
        recordNewTestOutcome(testMethod, currentTestOutcome.get());

        LifecycleRegister.invokeMethodsAnnotatedBy(BeforeScenario.class, newTestOutcome);
    }

    private void recordNewTestOutcome(String testMethod, TestOutcome newTestOutcome) {
        newTestOutcome.setTestSource(StepEventBus.getEventBus().getTestSource());
        synchronized(testOutcomes) {
        	testOutcomes.add(newTestOutcome);
        }
        setAnnotatedResult(testMethod);
    }

    private void updateSessionIdIfKnown() {
        SessionId sessionId = ThucydidesWebDriverSupport.getSessionId();
        if (sessionId != null) {
            getCurrentTestOutcome().setSessionId(sessionId.toString());
        }
    }

    public StepMutator updateCurrentStepTitle(String updatedStepTitle) {
        if (currentStepExists()) {
            getCurrentStep().setDescription(updatedStepTitle);
        } else {
            stepStarted(ExecutedStepDescription.withTitle(updatedStepTitle));
        }
        return new StepMutator(this);
    }

    public class StepMutator {

        private final BaseStepListener baseStepListener;

        public StepMutator(BaseStepListener baseStepListener) {
            this.baseStepListener = baseStepListener;
        }

        public void asAPrecondition() {
            baseStepListener.getCurrentStep().setPrecondition(true);
        }

    }

    private void setAnnotatedResult(String testMethod) {
        if (TestAnnotations.forClass(testSuite).isIgnored(testMethod)) {
            getCurrentTestOutcome().setAnnotatedResult(IGNORED);
        }
        if (TestAnnotations.forClass(testSuite).isPending(testMethod)) {
            getCurrentTestOutcome().setAnnotatedResult(PENDING);
        }
    }

    public void testFinished(final TestOutcome outcome) {
        testFinished(outcome, false);
    }

    /**
     * A test has finished.
     *
     * @param outcome the result of the test that just finished.
     */
    public void testFinished(final TestOutcome outcome, boolean inDataDrivenTest) {

        if (getTestOutcomes().isEmpty()) {
            return;
        }

        LifecycleRegister.invokeMethodsAnnotatedBy(AfterScenario.class, getCurrentTestOutcome());

        recordTestDuration();
        getCurrentTestOutcome().addIssues(storywideIssues);
        // TODO: Disable when run from an IDE
        getCurrentTestOutcome().addTags(storywideTags);

        if (StepEventBus.getEventBus().isDryRun() || getCurrentTestOutcome().getResult() == IGNORED) {
            testAndTopLevelStepsShouldBeIgnored();
        }

        OverrideDriverCapabilities.clear();

        if (currentTestIsABrowserTest()) {
            getCurrentTestOutcome().setDriver(getDriverUsedInThisTest());
            updateSessionIdIfKnown();

            AtTheEndOfAWebDriverTest.invokeCustomTeardownLogicWithDriver(
                    getEventBus().getEnvironmentVariables(),
                    outcome,
                    SerenityWebdriverManager.inThisTestThread().getCurrentDriver());

            if (inDataDrivenTest) {
                closeBrowsers.forTestSuite(testSuite).closeIfConfiguredForANew(EXAMPLE);
            } else {
                closeBrowsers.forTestSuite(testSuite).closeIfConfiguredForANew(SCENARIO);
                ThucydidesWebDriverSupport.clearDefaultDriver();
            }

        }

        currentStepStack.get().clear();
        LifecycleRegister.clear();
    }

    private void testAndTopLevelStepsShouldBeIgnored() {
        getCurrentTestOutcome().setResult(IGNORED);
        if (getCurrentTestOutcome().isDataDriven()) {
            getCurrentTestOutcome().updateTopLevelStepResultsTo(IGNORED);
        }

    }


    private String getDriverUsedInThisTest() {
        return ThucydidesWebDriverSupport.getDriversUsed();
    }

    private boolean currentTestIsABrowserTest() {
        return SerenityJUnitTestCase.inClass(testSuite).isAWebTest()
                || (testSuite == null && ThucydidesWebDriverSupport.isDriverInstantiated());
//                || (!ThucydidesWebDriverSupport.getDriversUsed().isEmpty());
    }

    public void testRetried() {
        currentStepStack.get().clear();
        testOutcomes.remove(getCurrentTestOutcome());
    }

    private void recordTestDuration() {
        if (!testOutcomes.isEmpty()) {
            getCurrentTestOutcome().recordDuration();
        }
    }

    /**
     * A step within a test is called.
     * This step might be nested in another step, in which case the original step becomes a group of steps.
     *
     * @param description the description of the test that is about to be run
     */
    public void stepStarted(final ExecutedStepDescription description) {
        pushStepMethodIn(description);
        recordStep(description);
        if (currentTestIsABrowserTest() && browserIsOpen()) {
            takeInitialScreenshot();
        }
    }

    private void pushStepMethodIn(ExecutedStepDescription description) {
        if (description.isAQuestion()) {
            currentStepMethodStack.push(tokenQuestionMethod());
        } else {
            currentStepMethodStack.push(description.getStepMethod());
        }
    }

    static class Question {
        public void ask() {
        }
    }

    private Method tokenQuestionMethod() {
        try {
            return Question.class.getMethod("ask");
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public void skippedStepStarted(final ExecutedStepDescription description) {
        recordStep(description);
    }


    Stack<Method> currentStepMethodStack = new Stack<>();

    public java.util.Optional<Method> getCurrentStepMethod() {
        return currentStepMethodStack.empty() ? java.util.Optional.<Method>empty() : java.util.Optional.ofNullable(currentStepMethodStack.peek());
    }

    private void recordStep(ExecutedStepDescription description) {

        if (!latestTestOutcome().isPresent()) {
            return;
        }

        TestStep step = new TestStep(AnnotatedStepDescription.from(description).getName());

        startNewGroupIfNested();
        setDefaultResultFromAnnotations(step, description);

        currentStepStack.get().push(step);
        recordStepToCurrentTestOutcome(step);
    }

    private void recordStepToCurrentTestOutcome(TestStep step) {
        getCurrentTestOutcome().recordStep(step);
    }

    private void setDefaultResultFromAnnotations(final TestStep step, final ExecutedStepDescription description) {
        if (TestAnnotations.isPending(description.getStepMethod())) {
            step.setResult(TestResult.PENDING);
        }
        if (TestAnnotations.isIgnored(description.getStepMethod())) {
            step.setResult(TestResult.IGNORED);
        }
    }

    private void startNewGroupIfNested() {
        if (thereAreUnfinishedSteps()) {
            if (getCurrentStep() != getCurrentGroup()) {
                startNewGroup();
            }
        }
    }

    private void startNewGroup() {
        getCurrentTestOutcome().startGroup();
        currentGroupStack.get().push(getCurrentStep());
    }

    private java.util.Optional<TestStep> currentStep() {
        if (currentStepStack.get() == null || currentStepStack.get().isEmpty()) {
            return java.util.Optional.empty();
        }
        return (java.util.Optional.of(currentStepStack.get().peek()));
    }

    private TestStep getCurrentStep() {
        return currentStepStack.get().peek();
    }

    private java.util.Optional<TestStep> getPreviousStep() {
        if (getCurrentTestOutcome().getTestStepCount() > 1) {
            List<TestStep> currentTestSteps = getCurrentTestOutcome().getTestSteps();
            return java.util.Optional.of(currentTestSteps.get(currentTestSteps.size() - 2));
        } else {
            return java.util.Optional.empty();
        }
    }

    private TestStep getCurrentGroup() {
        if (currentGroupStack.get().isEmpty()) {
            return null;
        } else {
            return currentGroupStack.get().peek();// findLastChildIn(currentGroupStack.peek());
        }
    }

    private boolean thereAreUnfinishedSteps() {
        return !currentStepStack.get().isEmpty();
    }

    public void stepFinished() {
        takeEndOfStepScreenshotFor(SUCCESS);
        currentStepDone(SUCCESS);
        pauseIfRequired();
    }

    private void updateExampleTableIfNecessary(TestResult result) {
        if (getCurrentTestOutcome().isDataDriven()) {
            getCurrentTestOutcome().updateCurrentRowResult(result);
        }
    }

    private void finishGroup() {
        currentGroupStack.get().pop();
        getCurrentTestOutcome().endGroup();
    }

    private void pauseIfRequired() {
        int delay = configuration.getStepDelay();
        if (delay > 0) {
            getClock().pauseFor(delay);
        }
    }

    private void markCurrentStepAs(final TestResult result) {
        getCurrentTestOutcome().currentStep().ifPresent(
                step -> {
                    step.setResult(result);
                    updateExampleTableIfNecessary(result);
                }
        );
    }

    FailureAnalysis failureAnalysis = new FailureAnalysis();

    public void stepFailed(StepFailure failure) {
        takeEndOfStepScreenshotFor(FAILURE);

        TestFailureCause failureCause = TestFailureCause.from(failure.getException());
        getCurrentTestOutcome().appendTestFailure(failureCause);

        recordFailureDetails(failure);
        currentStepDone(failureAnalysis.resultFor(failure));
    }

    public void lastStepFailed(StepFailure failure) {
        takeEndOfStepScreenshotFor(FAILURE);
        getCurrentTestOutcome().lastStepFailedWith(failure);
        lastFailingExample = currentExample;
    }


    private void recordFailureDetails(final StepFailure failure) {
        if (currentStepExists()) {
            getCurrentStep().failedWith(new StepFailureException(failure.getMessage(), failure.getException()));
        }
        if (shouldTagErrors()) {
            addTagFor(getCurrentTestOutcome());
        }
        lastFailingExample = currentExample;
    }

    private void addTagFor(TestOutcome testOutcome) {
        testOutcome.addTag(TestTag.withName(testOutcome.getTestFailureCause().getSimpleErrorType()).andType("error"));
    }

    private boolean shouldTagErrors() {
        return ThucydidesSystemProperty.SERENITY_TAG_FAILURES.booleanFrom(configuration.getEnvironmentVariables());
    }

    private boolean shouldUseFullPageScreenshotStrategy() {
        return ThucydidesSystemProperty.SERENITY_FULL_PAGE_SCREENSHOT_STRATEGY.booleanFrom(configuration.getEnvironmentVariables());
    }

    public void stepIgnored() {
        if (aStepHasFailed()) {
            markCurrentStepAs(SKIPPED);
            currentStepDone(SKIPPED);
        } else {
            currentStepDone(IGNORED);
        }
    }

    public void stepPending() {
        currentStepDone(PENDING);
    }

    public void stepPending(String message) {
        getCurrentStep().testAborted(new PendingStepException(message));
        stepPending();
    }

    public void assumptionViolated(String message) {
        if (thereAreUnfinishedSteps()) {
            getCurrentStep().testAborted(new PendingStepException(message));
            stepIgnored();
        }
        testIgnored();
    }

    @Override
    public void testRunFinished() {
        closeDarkroom();
    }

    private void currentStepDone(TestResult result) {
        if (!currentStepMethodStack.isEmpty()) {
            currentStepMethodStack.pop();
        }
        if (currentStepExists()) {
            TestStep finishedStep = currentStepStack.get().pop();
            finishedStep.recordDuration();
            if (result != null) {
                finishedStep.setResult(result);
            }
            if ((finishedStep == getCurrentGroup())) {
                finishGroup();
            }
        }
        updateExampleTableIfNecessary(result);
    }

    private boolean currentStepExists() {
        return !currentStepStack.get().isEmpty();
    }

    public int getCurrentLevel() { return currentStepStack.get().size(); }

    private void takeEndOfStepScreenshotFor(final TestResult result) {
        if (currentTestIsABrowserTest() && shouldTakeEndOfStepScreenshotFor(result)) {
            take(MANDATORY_SCREENSHOT, result);
        }
    }

    public java.util.Optional<TestResult> getForcedResult() {
        return java.util.Optional.ofNullable(getCurrentTestOutcome().getAnnotatedResult());
    }

    public void clearForcedResult() {
        getCurrentTestOutcome().clearForcedResult();
    }

    private void take(final ScreenshotType screenshotType) {
        take(screenshotType, UNDEFINED);
    }

    private void take(final ScreenshotType screenshotType, TestResult result) {
        if (shouldTakeScreenshots()) {
            try {
                grabScreenshots(result).forEach(
                        screenshot -> recordScreenshotIfRequired(screenshotType, screenshot)
                );
                removeDuplicatedInitalScreenshotsIfPresent();
            } catch (ScreenshotException e) {
                LOGGER.warn("Failed to take screenshot", e);
            }
        }
    }

    private boolean shouldTakeScreenshots() {
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended() && !StepEventBus.getEventBus().softAssertsActive()) {
            return false;
        }

        if (screenshots.areDisabledForThisAction()) {
            return false;
        }

        return (currentStepExists()
                && browserIsOpen()
                && !StepEventBus.getEventBus().isDryRun()
                && !StepEventBus.getEventBus().currentTestIsSuspended());
    }

    private void removeDuplicatedInitalScreenshotsIfPresent() {
        if (currentStepHasMoreThanOneScreenshot() && getPreviousStep().isPresent() && getPreviousStep().get().hasScreenshots()) {
            ScreenshotAndHtmlSource lastScreenshotOfPreviousStep = lastScreenshotOf(getPreviousStep().get());
            ScreenshotAndHtmlSource firstScreenshotOfThisStep = getCurrentStep().getFirstScreenshot();
            if (firstScreenshotOfThisStep.hasIdenticalScreenshotsAs(lastScreenshotOfPreviousStep)) {
                removeFirstScreenshotOfCurrentStep();
            }
        }
    }

    private void removeFirstScreenshotOfCurrentStep() {
        getCurrentStep().removeScreenshot(0);
    }

    private boolean currentStepHasMoreThanOneScreenshot() {
        return getCurrentStep().getScreenshotCount() > 1;
    }

    private ScreenshotAndHtmlSource lastScreenshotOf(TestStep testStep) {
        return testStep.getScreenshots().get(testStep.getScreenshots().size() - 1);
    }

    private void recordScreenshotIfRequired(ScreenshotType screenshotType, ScreenshotAndHtmlSource screenshotAndHtmlSource) {
        if (shouldTakeScreenshot(screenshotType, screenshotAndHtmlSource) && screenshotWasTaken(screenshotAndHtmlSource)) {
            getCurrentStep().addScreenshot(screenshotAndHtmlSource);
        }
    }

    private boolean screenshotWasTaken(ScreenshotAndHtmlSource screenshotAndHtmlSource) {
        return screenshotAndHtmlSource.getScreenshot() != null;
    }


    private boolean shouldTakeScreenshot(ScreenshotType screenshotType,
                                         ScreenshotAndHtmlSource screenshotAndHtmlSource) {
        return (screenshotType == MANDATORY_SCREENSHOT)
                || getCurrentStep().getScreenshots().isEmpty()
                || shouldTakeOptionalScreenshot(screenshotAndHtmlSource);
    }

    private boolean shouldTakeOptionalScreenshot(ScreenshotAndHtmlSource screenshotAndHtmlSource) {
        return (screenshotAndHtmlSource.wasTaken() && previousScreenshot().isPresent()
                && (!screenshotAndHtmlSource.hasIdenticalScreenshotsAs(previousScreenshot().get())));
    }

    private java.util.Optional<ScreenshotAndHtmlSource> previousScreenshot() {
        List<ScreenshotAndHtmlSource> screenshotsToDate = getCurrentTestOutcome().getScreenshotAndHtmlSources();
        if (screenshotsToDate.isEmpty()) {
            return java.util.Optional.empty();
        } else {
            return java.util.Optional.of(screenshotsToDate.get(screenshotsToDate.size() - 1));
        }
    }

    private boolean browserIsOpen() {
        return ThucydidesWebDriverSupport.isDriverInstantiated();
    }

    private void takeInitialScreenshot() {
        if ((currentStepExists()) && (screenshots().areAllowed(TakeScreenshots.BEFORE_AND_AFTER_EACH_STEP))) {
            take(OPTIONAL_SCREENSHOT);
        }
    }

    public Map<String, WebDriver> getActiveDrivers() {
        return SerenityWebdriverManager.inThisTestThread().getActiveDriverMap();
    }

    private List<ScreenshotAndHtmlSource> grabScreenshots(TestResult result) {
        if (pathOf(outputDirectory) == null) { // Output directory may be null for some tests
            return new ArrayList<>();
        }

        return SerenityWebdriverManager.inThisTestThread().getCurrentDrivers().stream().map(
                driver -> new ScreenshotAndHtmlSource(
                        screenshotFrom(driver),
                        sourceFrom(result, driver))
        ).collect(Collectors.toList());
    }

    private File screenshotFrom(WebDriver driver) {
        Path screenshotPath = getPhotographer().takesAScreenshot()
                .with(driver)
                .andWithBlurring(AnnotatedBluring.blurLevel())
                .andSaveToDirectory(pathOf(outputDirectory))
                .getPathToScreenshot();

        return (screenshotPath == null) ? null : screenshotPath.toFile();
    }

    private File sourceFrom(TestResult result, WebDriver driver) {
        return soundEngineer.ifRequiredForResult(result)
                .recordPageSourceUsing(driver)
                .intoDirectory(pathOf(outputDirectory)).orElse(null);
    }

    private java.util.Optional<ScreenshotAndHtmlSource> grabScreenshot(TestResult result) {

        ScreenshotPhoto newPhoto = ScreenshotPhoto.None;
        java.util.Optional<File> pageSource = java.util.Optional.empty();

        if (pathOf(outputDirectory) != null) { // Output directory may be null for some tests
            newPhoto = getPhotographer().takesAScreenshot()
                    .with(getDriver())
                    .andWithBlurring(AnnotatedBluring.blurLevel())
                    .andSaveToDirectory(pathOf(outputDirectory));

            pageSource = soundEngineer.ifRequiredForResult(result)
                    .recordPageSourceUsing(getDriver())
                    .intoDirectory(pathOf(outputDirectory));

        }
        return (newPhoto == ScreenshotPhoto.None) ?
                java.util.Optional.empty()
                : java.util.Optional.of(new ScreenshotAndHtmlSource(newPhoto.getPathToScreenshot().toFile(), pageSource.orElse(null)));
    }

    private Path pathOf(File directory) {
        return (directory == null) ? null : directory.toPath();
    }

    private boolean shouldTakeEndOfStepScreenshotFor(final TestResult result) {
        if (result == FAILURE) {
            return screenshots().areAllowed(TakeScreenshots.FOR_FAILURES);
        } else {
            return screenshots().areAllowed(TakeScreenshots.AFTER_EACH_STEP);
        }
    }

    public List<TestOutcome> getTestOutcomes() {
        return testOutcomes.stream()
                .sorted((o1, o2) -> {
                    String creationTimeAndName1 = o1.getStartTime() + "_" + o1.getName();
                    String creationTimeAndName2 = o1.getStartTime() + "_" + o1.getName();
                    return creationTimeAndName1.compareTo(creationTimeAndName2);
                })
                .collect(Collectors.toList());
    }

    public void setDriver(final WebDriver driver) {
        this.driver = driver;
    }

    public WebDriver getDriver() {
        return ThucydidesWebDriverSupport.getDriver();
    }

    public boolean aStepHasFailed() {
        return ((!getTestOutcomes().isEmpty()) &&
                (getCurrentTestOutcome().getResult() == TestResult.FAILURE
                        || getCurrentTestOutcome().getResult() == TestResult.ERROR
                        || getCurrentTestOutcome().getResult() == TestResult.COMPROMISED));
    }

    public boolean aStepHasFailedInTheCurrentExample() {
        if (currentExample == 0) {
            return aStepHasFailed();
        }
        return (aStepHasFailed() && (currentExample == lastFailingExample));
    }

    public FailureCause getTestFailureCause() {
        return getCurrentTestOutcome().getTestFailureCause();
    }

    public void testFailed(TestOutcome testOutcome, final Throwable cause) {
        if (!testOutcomeRecorded()) {
            return;
        }

        getCurrentTestOutcome().determineTestFailureCause(cause);
    }

    public void testIgnored() {
        if (!testOutcomeRecorded()) {
            return;
        }
        getCurrentTestOutcome().setAnnotatedResult(IGNORED);
    }

    public void testSkipped() {
        if (!testOutcomeRecorded()) {
            return;
        }

        getCurrentTestOutcome().setAnnotatedResult(SKIPPED);
    }

    public void testPending() {
        if (!testOutcomeRecorded()) {
            return;
        }

        getCurrentTestOutcome().setAnnotatedResult(PENDING);
        updateExampleTableIfNecessary(PENDING);
    }

    private boolean testOutcomeRecorded() {
        return !testOutcomes.isEmpty();
    }

    @Override
    public void testIsManual() {
        if (!testOutcomeRecorded()) {
            return;
        }

        getCurrentTestOutcome().setAnnotatedResult(defaulManualTestReportResult());
        getCurrentTestOutcome().setToManual();
    }

    private TestResult defaulManualTestReportResult() {
        String manualTestResultValue = ThucydidesSystemProperty.MANUAL_TEST_REPORT_RESULT.from(configuration.getEnvironmentVariables(),
                TestResult.PENDING.toString());
        TestResult manualTestResult = TestResult.PENDING;
        try {
            manualTestResult = TestResult.valueOf(manualTestResultValue.toUpperCase());
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Badly configured value for manual.test.report.result: should be one of " + Arrays.toString(TestResult.values()));
        }
        return manualTestResult;
    }

    public void notifyScreenChange() {
        if (currentTestIsABrowserTest() && screenshots().areAllowed(TakeScreenshots.FOR_EACH_ACTION)) {
            take(OPTIONAL_SCREENSHOT);
        }
    }

    /**
     * Take a screenshot now.
     */
    public void takeScreenshot() {
        take(MANDATORY_SCREENSHOT);
    }

    private int currentExample = 0;
    private int lastFailingExample = 0;

    /**
     * The current scenario is a data-driven scenario using test data from the specified table.
     */
    public void useExamplesFrom(DataTable table) {
        getCurrentTestOutcome().useExamplesFrom(table);
        currentExample = 0;
        lastFailingExample = 0;
    }

    public void addNewExamplesFrom(DataTable table) {
        getCurrentTestOutcome().addNewExamplesFrom(table);
        //currentExample = 0;
    }


    public void exampleStarted(Map<String, String> data) {

        clearForcedResult();
        if (getCurrentTestOutcome().isDataDriven()) {
            if (!getCurrentTestOutcome().dataIsPredefined()) {
                getCurrentTestOutcome().addRow(data);
            }
        }
        currentExample++;
        if (newStepForEachExample()) {
            getEventBus().stepStarted(ExecutedStepDescription.withTitle(exampleTitle(currentExample, data)));
        }

        LifecycleRegister.invokeMethodsAnnotatedBy(BeforeExample.class, getCurrentTestOutcome());
    }

    private String exampleTitle(int exampleNumber, Map<String, String> data) {
        return String.format("Example: %s", data);
    }

    public void exampleFinished() {

        LifecycleRegister.invokeMethodsAnnotatedBy(AfterExample.class, getCurrentTestOutcome());

        if (newStepForEachExample()) {
            currentStepDone(null);
        }
        if (latestTestOutcome().isPresent()) {
            latestTestOutcome().get().moveToNextRow();
        }
        OverrideDriverCapabilities.clear();
        if (currentTestIsABrowserTest()) {
            getCurrentTestOutcome().setDriver(getDriverUsedInThisTest());
        //    updateSessionIdIfKnown();

            AtTheEndOfAWebDriverTest.invokeCustomTeardownLogicWithDriver(
                    getEventBus().getEnvironmentVariables(),
                    getCurrentTestOutcome(),
                    SerenityWebdriverManager.inThisTestThread().getCurrentDriver());
        }

        closeBrowsers.forTestSuite(testSuite).closeIfConfiguredForANew(EXAMPLE);
    }

    private boolean newStepForEachExample() {
        if (!latestTestOutcome().isPresent()) {
            return false;
        }
        return (getCurrentTestOutcome().getTestSource() != null) && (!getCurrentTestOutcome().getTestSource().equalsIgnoreCase("junit"));
    }

    public void recordRestQuery(RestQuery restQuery) {
        stepStarted(ExecutedStepDescription.withTitle(restQuery.toString()));
        addRestQuery(restQuery);
        stepFinished();
    }

    private void addRestQuery(RestQuery restQuery) {
        currentStep().ifPresent(
                step -> step.recordRestQuery(restQuery)
        );
    }
}
