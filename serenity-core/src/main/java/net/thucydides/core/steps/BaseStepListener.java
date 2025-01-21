package net.thucydides.core.steps;

import net.serenitybdd.model.PendingStepException;
import net.serenitybdd.core.annotations.events.AfterExample;
import net.serenitybdd.core.annotations.events.AfterScenario;
import net.serenitybdd.core.annotations.events.BeforeExample;
import net.serenitybdd.core.annotations.events.BeforeScenario;
import net.serenitybdd.core.di.SerenityInfrastructure;
import net.serenitybdd.model.exceptions.TheErrorType;
import net.serenitybdd.core.lifecycle.LifecycleRegister;
import net.serenitybdd.core.photography.Darkroom;
import net.serenitybdd.core.photography.Photographer;
import net.serenitybdd.core.photography.SoundEngineer;
import net.serenitybdd.core.photography.WebDriverPhotoLens;
import net.serenitybdd.core.photography.bluring.AnnotatedBluring;
import net.serenitybdd.model.rest.RestQuery;
import net.serenitybdd.model.strings.Joiner;
import net.serenitybdd.model.time.SystemClock;
import net.serenitybdd.core.webdriver.OverrideDriverCapabilities;
import net.serenitybdd.core.webdriver.configuration.RestartBrowserForEach;
import net.serenitybdd.core.webdriver.enhancers.AtTheEndOfAWebDriverTest;
import net.thucydides.core.model.screenshots.ScreenshotPermission;
import net.thucydides.core.steps.events.UpdateCurrentStepFailureCause;
import net.thucydides.model.ThucydidesSystemProperty;
import net.serenitybdd.annotations.TestAnnotations;
import net.thucydides.core.junit.SerenityJUnitTestCase;
import net.thucydides.model.domain.*;
import net.thucydides.model.domain.failures.FailureAnalysis;
import net.thucydides.model.domain.stacktrace.FailureCause;
import net.thucydides.model.screenshots.ScreenshotAndHtmlSource;
import net.thucydides.core.steps.session.TestSession;
import net.thucydides.core.webdriver.*;
import net.thucydides.model.screenshots.ScreenshotException;
import net.thucydides.model.steps.*;
import net.thucydides.model.util.ConfigCache;
import net.thucydides.model.webdriver.Configuration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.SessionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static net.serenitybdd.core.webdriver.configuration.RestartBrowserForEach.EXAMPLE;
import static net.serenitybdd.core.webdriver.configuration.RestartBrowserForEach.SCENARIO;
import static net.thucydides.model.domain.Stories.findStoryFrom;
import static net.thucydides.model.domain.TestResult.*;
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

    //    private ThreadLocal<TestOutcome> currentTestOutcome;
    private TestOutcome currentTestOutcome;

    /**
     * Keeps track of what steps have been started but not finished, in order to structure nested steps.
     */
//    private final ThreadLocal<Stack<TestStep>> currentStepStack;
    private final Stack<TestStep> currentStepStack = new Stack<>();

    /**
     * Keeps track of the current step group, if any.
     */
//    private final ThreadLocal<Stack<TestStep>> currentGroupStack;
    private final Stack<TestStep> currentGroupStack;

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

    private final File outputDirectory;

    private final WebdriverProxyFactory proxyFactory;

    private Story testedStory;

    private Configuration configuration;

    private List<String> storywideIssues;

    private List<TestTag> storywideTags;
    private Darkroom darkroom;
    private Photographer photographer;
    private SoundEngineer soundEngineer;
    private List<ScreenshotAndHtmlSource> usedScreenshots = new ArrayList<>();
    private List<ScreenshotAndHtmlSource> allScreenshots = new ArrayList<>();

    private final CloseBrowser closeBrowsers;

    public void setEventBus(StepEventBus eventBus) {
        this.eventBus = eventBus;
    }


    public StepEventBus getEventBus() {
        if (eventBus == null) {
            eventBus = StepEventBus.getParallelEventBus();
        }
        return eventBus;
    }

    private Darkroom getDarkroom() {
        if (darkroom == null) {
            darkroom = new Darkroom();
        }
        return darkroom;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public Optional<TestStep> cloneCurrentStep() {
        return ((currentStepExists()) ? Optional.of(getCurrentStep().clone()) : Optional.empty());
    }

    public Optional<TestResult> getAnnotatedResult() {
        return Optional.ofNullable(getCurrentTestOutcome().getAnnotatedResult());
    }

    public void setAllStepsTo(TestResult result) {
        getCurrentTestOutcome().setAnnotatedResult(result);
        getCurrentTestOutcome().setAllStepsTo(result);
    }

    public void overrideResultTo(TestResult result) {
        getCurrentTestOutcome().overrideResult(result);
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
                    testEvidence.ifPresent(evidence -> getCurrentTestOutcome().setManualTestEvidence(testEvidenceLinksFrom(testEvidence)));
                }
        );
    }

    public void recordManualTestResult(TestResult result) {
        getCurrentTestOutcome().overrideAnnotatedResult(result);
    }

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


        if (currentTestFailed()
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

    public boolean currentTestFailed() {
        return getCurrentTestOutcome().getTestFailureCause() != null;
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
            photographer = new Photographer(getDarkroom());
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

    public void addStepsFrom(List<TestStep> newSteps) {
        this.latestTestOutcome().ifPresent(
                outcome -> outcome.recordSteps(newSteps)
        );
    }

    public void addChildStepsFrom(List<TestStep> newSteps) {
        this.latestTestOutcome().ifPresent(
                outcome -> outcome.recordChildSteps(newSteps)
        );
    }

    public int currentStepDepth() {
        return currentStepStack.size();
    }

    public boolean previousScenarioWasASingleBrowserScenario() {
        if (getTestOutcomes().size() > 1) {
            TestOutcome previousOutcome = getTestOutcomes().get(getTestOutcomes().size() - 2);
            return previousOutcome.hasTag(TestTag.withValue("singlebrowser"));
        } else {
            return false;
        }
    }

    public boolean currentStoryHasTag(TestTag tag) {
        return storywideTags != null && storywideTags.contains(tag);
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

    public BaseStepListener childListenerFor(StepEventBus eventBus) {
        BaseStepListener baseStepListener = new BaseStepListener(outputDirectory);
        baseStepListener.photographer = photographer;
        baseStepListener.screenshots = screenshots;
        baseStepListener.darkroom = darkroom;
        baseStepListener.eventBus = eventBus;
        baseStepListener.soundEngineer = soundEngineer;
        baseStepListener.storywideIssues = storywideIssues;
        baseStepListener.storywideTags = storywideTags;
        baseStepListener.suiteStarted = suiteStarted;
        baseStepListener.testedStory = testedStory;
        baseStepListener.testSuite = testSuite;
        return baseStepListener;
    }

    /**
     * Creates a new base step listener with
     *
     * @param outcomeName
     * @return
     */
    public BaseStepListener spawn(String outcomeName) {
        BaseStepListener baseStepListener = new BaseStepListener(outputDirectory);
        baseStepListener.photographer = photographer;
        baseStepListener.screenshots = screenshots;
        baseStepListener.darkroom = darkroom;
        baseStepListener.eventBus = eventBus;
        baseStepListener.soundEngineer = soundEngineer;
        baseStepListener.storywideIssues = storywideIssues;
        baseStepListener.storywideTags = storywideTags;
        baseStepListener.suiteStarted = suiteStarted;
        baseStepListener.testedStory = testedStory;
        baseStepListener.testSuite = testSuite;

        baseStepListener.testStarted(outcomeName);

        return baseStepListener;
    }

    public BaseStepListener(final File outputDirectory) {
        this.proxyFactory = WebdriverProxyFactory.getFactory();
        this.testOutcomes = new ArrayList<>();
//        this.currentTestOutcome = new ThreadLocal<>();
//        this.currentStepStack = ThreadLocal.withInitial(Stack<TestStep>::new);
//        this.currentGroupStack = ThreadLocal.withInitial(Stack<TestStep>::new);
        this.currentGroupStack = new Stack<TestStep>();
        this.outputDirectory = outputDirectory;
        this.storywideIssues = new ArrayList<>();
        this.storywideTags = new ArrayList<>();
        //this.webdriverManager = injector.getInstance(WebdriverManager.class);
        this.clock = SerenityInfrastructure.getClock();
        this.configuration = SerenityInfrastructure.getConfiguration();
        //this.screenshotProcessor = injector.getInstance(ScreenshotProcessor.class);
        this.closeBrowsers = SerenityInfrastructure.getCloseBrowser();
        this.soundEngineer = new SoundEngineer(configuration.getEnvironmentVariables());
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

    protected ScreenshotPermission screenshots() {
        if (screenshots == null) {
            screenshots = new ScreenshotPermission(configuration);
        }
        return screenshots;
    }

    protected WebdriverProxyFactory getProxyFactory() {
        return proxyFactory;
    }

    public TestOutcome getCurrentTestOutcome() {
        return latestTestOutcome().orElse(UNAVAILABLE_TEST_OUTCOME);
    }

    private static final TestOutcome UNAVAILABLE_TEST_OUTCOME = new TestOutcome("Test outcome unavailable", null); // new UnavailableTestOutcome("Test outcome unavailable");

    private TestOutcome unavailableTestOutcome() {
        return UNAVAILABLE_TEST_OUTCOME;
    }

    public boolean isAvailable() {
        return true;
    }

    public Optional<TestOutcome> latestTestOutcome() {
        if (testOutcomes.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(currentTestOutcome);
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

    public void testSuiteStarted(final Class<?> startedTestSuite, String testName) {
        testSuite = startedTestSuite;
        testedStory = findStoryFrom(startedTestSuite)
                .withStoryName(testName)
                .withDisplayName(testName);
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
        testStarted(testMethod, ZonedDateTime.now());
    }

    public void testStarted(final String testMethod, ZonedDateTime startTime) {
        String testMethodName = testMethod;
        String qualifier = "";
        if (testMethod.contains("%")) {
            String[] splittedTestMethod = testMethod.split("%");
            testMethodName = splittedTestMethod[0];
            qualifier = splittedTestMethod[1];
        }
        TestOutcome newTestOutcome = TestOutcome.forTestInStory(testMethodName, testSuite, testedStory);
        if (!qualifier.isEmpty()) {
            newTestOutcome = newTestOutcome.withQualifier(qualifier);
        }
        this.currentTestOutcome = newTestOutcome;
        recordNewTestOutcome(testMethod, currentTestOutcome);
        this.currentTestOutcome.setStartTime(startTime);

        LifecycleRegister.invokeMethodsAnnotatedBy(BeforeScenario.class, newTestOutcome);
    }

    public void testStarted(final String testName, final String id) {
        TestOutcome newTestOutcome = TestOutcome.forTestInStory(testName, testSuite, testedStory).withId(id);
        this.currentTestOutcome = newTestOutcome;
        recordNewTestOutcome(testName, currentTestOutcome);

        LifecycleRegister.invokeMethodsAnnotatedBy(BeforeScenario.class, newTestOutcome);
    }

    public void testStarted(final String testName, String methodName, final String id, String scenarioId) {
        TestOutcome newTestOutcome = TestOutcome.forTestInStory(testName, testSuite, testedStory)
                .withId(id)
                .withScenarioId(scenarioId)
                .withTestMethodName(methodName);
        this.currentTestOutcome = newTestOutcome;
        recordNewTestOutcome(testName, currentTestOutcome);

        LifecycleRegister.invokeMethodsAnnotatedBy(BeforeScenario.class, newTestOutcome);
    }

    public void testStarted(final String testMethod, final String id, ZonedDateTime startTime) {
        TestOutcome newTestOutcome = TestOutcome.forTestInStory(testMethod, testSuite, testedStory).withId(id).withStartTime(startTime);
        this.currentTestOutcome = newTestOutcome;
        recordNewTestOutcome(testMethod, currentTestOutcome);
        LifecycleRegister.invokeMethodsAnnotatedBy(BeforeScenario.class, newTestOutcome);
    }

    private void recordNewTestOutcome(String testMethod, TestOutcome newTestOutcome) {
        newTestOutcome.setTestSource(getEventBus().getTestSource());
        synchronized (testOutcomes) {
            testOutcomes.add(newTestOutcome);
        }
        setAnnotatedResult(testMethod);
    }

    private void updateSessionIdIfKnown() {
        updateSessionIdIfKnown(getCurrentTestOutcome());
    }

    private void updateSessionIdIfKnown(TestOutcome testOutcome) {
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

    public void updateCurrentStepFailureCause(Throwable failure) {
        if (this.currentTestOutcome != null) {
            lastStepFailedWith(failure);
        } else {
            TestSession.addEvent(new UpdateCurrentStepFailureCause(failure));
        }
    }

    public void lastStepFailedWith(Throwable failure) {
        if (this.currentTestOutcome != null) {
            this.currentTestOutcome.lastStepFailedWith(failure);
        }
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

    public void testFinished(final TestOutcome result, boolean isInDataDrivenTest) {
        testFinished(result, isInDataDrivenTest, ZonedDateTime.now());
    }

    /**
     * A test has finished.
     */
    public void testFinished(final TestOutcome outcome, boolean isInDataDrivenTest, ZonedDateTime finishTime) {

        if (getTestOutcomes().isEmpty()) {
            return;
        }

        LifecycleRegister.invokeMethodsAnnotatedBy(AfterScenario.class, getCurrentTestOutcome());

        recordTestDuration(finishTime);
        getCurrentTestOutcome().addIssues(storywideIssues);
        // TODO: Disable when run from an IDE
        getCurrentTestOutcome().addTags(storywideTags);

        if (StepEventBus.getParallelEventBus().isDryRun() || getCurrentTestOutcome().getResult() == IGNORED) {
            testAndTopLevelStepsShouldBeIgnored();
        }

        OverrideDriverCapabilities.clear();

        if (TestSession.getTestSessionContext().getWebDriver() != null) {
            handlePostponedParallelExecution(outcome, isInDataDrivenTest);
        } else {
            cleanupWebdriverInstance(isInDataDrivenTest);
        }
        currentStepStack.clear();
        while (!currentGroupStack.isEmpty()) {
            finishGroup();
        }
        LifecycleRegister.clear();
    }

    public void cleanupWebdriverInstance(boolean isInDataDrivenTest, TestOutcome testOutcome) {
        if (currentTestIsABrowserTest()) {
            testOutcome.setDriver(getDriverUsedInThisTest());
            updateSessionIdIfKnown(testOutcome);

            AtTheEndOfAWebDriverTest.invokeCustomTeardownLogicWithDriver(
                    getEventBus().getEnvironmentVariables(),
                    testOutcome,
                    SerenityWebdriverManager.inThisTestThread().getCurrentDriver());

            if (isInDataDrivenTest) {
                closeBrowsers.forTestSuite(testSuite).closeIfConfiguredForANew(EXAMPLE);
            } else {
                closeBrowsers.forTestSuite(testSuite).closeIfConfiguredForANew(SCENARIO);
                ThucydidesWebDriverSupport.clearDefaultDriver();
            }
        }
    }

    public void cleanupWebdriverInstance(boolean isInDataDrivenTest) {
        cleanupWebdriverInstance(isInDataDrivenTest, getCurrentTestOutcome());
    }

    private void handlePostponedParallelExecution(TestOutcome outcome, boolean isInDataDrivenTest) {
        getCurrentTestOutcome().setDriver(TestSession.getTestSessionContext().getDriverUsedInThisTest());
        updateSessionIdIfKnown();

        AtTheEndOfAWebDriverTest.invokeCustomTeardownLogicWithDriver(
                getEventBus().getEnvironmentVariables(),
                outcome,
                TestSession.getTestSessionContext().getWebDriver());

        if (isInDataDrivenTest) {
            closeBrowsers.forTestSuite(testSuite).closeIfConfiguredForANew(EXAMPLE);
        } else {
            closeBrowsers.forTestSuite(testSuite).closeIfConfiguredForANew(SCENARIO);
            ThucydidesWebDriverSupport.clearDefaultDriver();
        }
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
    }

    public void testRetried() {
        currentStepStack.clear();
        testOutcomes.remove(getCurrentTestOutcome());
    }

    private void recordTestDuration(ZonedDateTime finishTime) {
        if (!testOutcomes.isEmpty()) {
            getCurrentTestOutcome().recordDuration(finishTime);
        }
    }

    private void recordTestDuration() {
        recordTestDuration(ZonedDateTime.now());
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

    public void stepStarted(final ExecutedStepDescription description, ZonedDateTime startTime) {
        pushStepMethodIn(description);
        TestStep newStep = recordStep(description);
        if (newStep != null) {
            newStep.setStartTime(startTime);
        }
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

    public Optional<Method> getCurrentStepMethod() {
        return currentStepMethodStack.empty() ? Optional.empty() : Optional.ofNullable(currentStepMethodStack.peek());
    }

    private TestStep recordStep(ExecutedStepDescription description) {

        if (!latestTestOutcome().isPresent()) {
            return null;
        }

        TestStep step = new TestStep(AnnotatedStepDescription.from(description).getName());

        startNewGroupIfNested();
        setDefaultResultFromAnnotations(step, description);

//        currentStepStack.get().push(step);
        currentStepStack.push(step);
        recordStepToCurrentTestOutcome(step);
        return step;
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
        currentGroupStack.push(getCurrentStep());
    }

    private Optional<TestStep> currentStep() {
        if (currentStepStack.isEmpty()) {
            return Optional.empty();
        }
        return (Optional.of(currentStepStack.peek()));
    }

    private TestStep getCurrentStep() {
        return currentStepStack.peek();
    }

    private Optional<TestStep> getPreviousStep() {
        if (getCurrentTestOutcome().getTestStepCount() > 1) {
            List<TestStep> currentTestSteps = getCurrentTestOutcome().getTestSteps();
            return Optional.of(currentTestSteps.get(currentTestSteps.size() - 2));
        } else {
            return Optional.empty();
        }
    }

    private TestStep getCurrentGroup() {
        return currentGroupStack.isEmpty() ? null : currentGroupStack.peek();
    }

    private boolean thereAreUnfinishedSteps() {
        return !currentStepStack.isEmpty();
    }

    public void stepFinished() {
        this.recordTestDuration();
        takeEndOfStepScreenshotFor(SUCCESS);
        currentStepDone(SUCCESS);
        pauseIfRequired();
    }

    public void stepFinished(List<ScreenshotAndHtmlSource> screenshotList) {
        stepFinished(screenshotList, ZonedDateTime.now());
    }

    public void stepFinished(List<ScreenshotAndHtmlSource> screenshotList, ZonedDateTime timestamp) {
        takeEndOfStepScreenshotForPlayback(SUCCESS, screenshotList);
        currentStepDone(SUCCESS, timestamp);
        pauseIfRequired();
    }

    private void updateExampleTableIfNecessary(TestResult result) {
        if (getCurrentTestOutcome().isDataDriven()) {
            getCurrentTestOutcome().updateCurrentRowResult(result);
        }
    }

    public void finishGroup() {
//        currentGroupStack.get().pop();
        if (!currentGroupStack.isEmpty()) {
            currentGroupStack.pop();
            getCurrentTestOutcome().endGroup();
        }
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

        if (!aStepHasFailed() || StepEventBus.getEventBus().softAssertsActive()) {
            // This is the actual failure, so record all the details
            takeEndOfStepScreenshotFor(FAILURE);

            TestFailureCause failureCause = TestFailureCause.from(failure.getException());
            getCurrentTestOutcome().appendTestFailure(failureCause);

            recordFailureDetails(failure);
        }

        // In all cases, mark the step as done with the appropriate result
        currentStepDone(failureAnalysis.resultFor(failure));
    }

    public void stepFailed(StepFailure failure,
                           List<ScreenshotAndHtmlSource> screenshotList,
                           boolean isInDataDrivenTest,
                           ZonedDateTime timestamp) {

        if (!aStepHasFailed()) {
            // This is the actual failure, so record all the details
            takeEndOfStepScreenshotForPlayback(FAILURE, screenshotList);

            TestFailureCause failureCause = TestFailureCause.from(failure.getException());
            getCurrentTestOutcome().appendTestFailure(failureCause);

            recordFailureDetails(failure);
        }
        // Step marked as done with the appropriate result before
        currentStepDone(failureAnalysis.resultFor(failure), timestamp);
    }


    public void stepFailedWithException(Throwable failure) {
        if (!aStepHasFailed()) {
            takeEndOfStepScreenshotFor(FAILURE);

            TestFailureCause failureCause = TestFailureCause.from(failure);
            getCurrentTestOutcome().appendTestFailure(failureCause);

            recordFailureDetails(failure);
        }
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

    private void recordFailureDetails(final Throwable failure) {
        if (currentStepExists()) {
            getCurrentStep().failedWith(new StepFailureException(failure.getMessage(), failure));
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
        ConfigCache.instance().clear();
    }

    @Override
    public void takeScreenshots(List<ScreenshotAndHtmlSource> screenshots) {
        takeEndOfStepScreenshotForRecording(SUCCESS, screenshots);
    }

    @Override
    public void takeScreenshots(TestResult result, List<ScreenshotAndHtmlSource> screenshots) {
        takeEndOfStepScreenshotForRecording(result, screenshots);
    }

    public void currentStepDone(TestResult result, ZonedDateTime time) {
        if (!currentStepMethodStack.isEmpty()) {
            currentStepMethodStack.pop();
        }
        if (currentStepExists()) {
            TestStep finishedStep = currentStepStack.pop();
            finishedStep.recordDuration(time);
            if ((result != null) && (result.isAtLeast(finishedStep.getResult()))) {
                finishedStep.setResult(result);
            }
            if (finishedStep == getCurrentGroup()) {
                finishGroup();
            }
            if ((result != null)
                    && (finishedStep.getException() != null)
                    && result.isAtLeast(FAILURE)
                    && (currentTestOutcome != null)
                    && (currentTestOutcome.isDataDriven())
                    && (currentTestOutcome.inGroup())
                    && (!currentTestOutcome.isTopLevelGroup())) {
                currentTestOutcome.endGroup();
            }

        }
        updateExampleTableIfNecessary(result);
    }

    public void currentStepDone(TestResult result) {
        currentStepDone(result, ZonedDateTime.now());
    }

    private boolean currentStepExists() {
        return !currentStepStack.isEmpty();
    }

    public int getCurrentLevel() {
        return currentStepStack.size();
    }

    private void takeEndOfStepScreenshotFor(final TestResult result) {
        if (currentTestIsABrowserTest() && shouldTakeEndOfStepScreenshotFor(result)) {
            take(MANDATORY_SCREENSHOT, result);
        }
    }

    private void takeEndOfStepScreenshotForRecording(final TestResult result, List<ScreenshotAndHtmlSource> screenshots) {
        if (currentTestIsABrowserTest()) {
            takeRecord(MANDATORY_SCREENSHOT, result, screenshots);
        }
    }

    private void takeEndOfStepScreenshotForPlayback(final TestResult result, List<ScreenshotAndHtmlSource> screenshots) {
        if ((screenshots != null && !screenshots.isEmpty())) {
            allScreenshots.addAll(screenshots);
        }
        if ((screenshots != null && !screenshots.isEmpty()) && shouldTakeEndOfStepScreenshotFor(result)) {
            takePlayback(MANDATORY_SCREENSHOT, result, screenshots);
            usedScreenshots.addAll(screenshots);
        }
    }

    public Optional<TestResult> getForcedResult() {
        return Optional.ofNullable(getCurrentTestOutcome().getAnnotatedResult());
    }

    public void clearForcedResult() {
        getCurrentTestOutcome().clearForcedResult();
    }

    private void take(final ScreenshotType screenshotType) {
        take(screenshotType, UNDEFINED);
    }

    private void take(final ScreenshotType screenshotType, TestResult result) {
        if (shouldTakeScreenshots(result)) {
            try {
                grabScreenshots(result).forEach(
                        screenshot -> recordScreenshotIfRequired(screenshotType, screenshot)
                );
                removeDuplicatedInitialScreenshotsIfPresent();
            } catch (ScreenshotException e) {
                LOGGER.warn("Failed to take screenshot", e);
            }
        }
    }

    private void takeRecord(final ScreenshotType screenshotType, TestResult result, List<ScreenshotAndHtmlSource> screenshots) {
        if (shouldTakeScreenshotsWithoutCurrentStep(result)) {
            try {
                grabScreenshots(result).forEach(
                        screenshot -> {
                            boolean screenshotExisting = screenshots.stream().map(screens -> screens.getScreenshot().getName()).collect(Collectors.toList()).contains(screenshot.getScreenshot().getName());
                            if (!screenshotExisting) {
                                screenshots.add(screenshot);
                            } else {
                                LOGGER.warn("SRP:Found duplicate snapshot " + screenshot.getScreenshot().getName());
                            }
                        }
                );
                //clarify why there is no currentStep
                if (currentStep().isPresent()) {
                    removeDuplicatedInitialScreenshotsIfPresent();
                }
            } catch (ScreenshotException e) {
                LOGGER.warn("Failed to take screenshot", e);
            }
        }
    }


    private void takePlayback(final ScreenshotType screenshotType, TestResult result, List<ScreenshotAndHtmlSource> screenshots) {
        if ((screenshots != null) && (screenshots.size() > 0)) {
            screenshots.forEach(screenshot -> {
                        currentStep().ifPresent(step -> step.addScreenshot(screenshot));
                    }
            );
            if (currentStep().isPresent()) {
                removeDuplicatedInitialScreenshotsIfPresent();
            }
        }
    }

    private boolean shouldTakeScreenshots(TestResult result) {
        if (StepEventBus.getParallelEventBus().webdriverCallsAreSuspended() && !StepEventBus.getParallelEventBus().softAssertsActive()) {
            return false;
        }
        if (screenshots().areDisabledForThisAction(result)) {
            return false;
        }
        return (currentStepExists()
                && browserIsOpen()
                && !StepEventBus.getParallelEventBus().isDryRun()
                && !StepEventBus.getParallelEventBus().currentTestIsSuspended());
    }

    private boolean shouldTakeScreenshotsWithoutCurrentStep(TestResult result) {
        if (StepEventBus.getParallelEventBus().webdriverCallsAreSuspended() && !StepEventBus.getParallelEventBus().softAssertsActive()) {
            return false;
        }
        if (screenshots().areDisabledForThisAction(result)) {
            return false;
        }
        return browserIsOpen()
                && !StepEventBus.getParallelEventBus().isDryRun()
                && !StepEventBus.getParallelEventBus().currentTestIsSuspended();
    }

    private void removeDuplicatedInitialScreenshotsIfPresent() {
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

    private void removeScreenshot(ScreenshotAndHtmlSource screenshot) {
        if (screenshot.getScreenshot().delete() && currentStep().isPresent()) {
            getCurrentStep().getScreenshots().remove(screenshot);
        }
    }

    private boolean currentStepHasMoreThanOneScreenshot() {
        return currentStepExists() && getCurrentStep().getScreenshotCount() > 1;
    }

    private ScreenshotAndHtmlSource lastScreenshotOf(TestStep testStep) {
        return testStep.getScreenshots().get(testStep.getScreenshots().size() - 1);
    }

    private void recordScreenshotIfRequired(ScreenshotType screenshotType, ScreenshotAndHtmlSource screenshotAndHtmlSource) {
        if (shouldTakeScreenshot(screenshotType, screenshotAndHtmlSource) && screenshotWasTaken(screenshotAndHtmlSource)) {
            currentStep().ifPresent(
                    step -> step.addScreenshot(screenshotAndHtmlSource)
            );
        }
    }


    private boolean screenshotWasTaken(ScreenshotAndHtmlSource screenshotAndHtmlSource) {
        return screenshotAndHtmlSource.getScreenshot() != null;
    }


    private boolean shouldTakeScreenshot(ScreenshotType screenshotType,
                                         ScreenshotAndHtmlSource screenshotAndHtmlSource) {
        if (screenshots().areDisabled()) {
            return false;
        }
        return (screenshotType == MANDATORY_SCREENSHOT)
                || (!currentStep().isPresent() || getCurrentStep().getScreenshots().isEmpty())
                || shouldTakeOptionalScreenshot(screenshotAndHtmlSource);
    }

    private boolean shouldTakeOptionalScreenshot(ScreenshotAndHtmlSource screenshotAndHtmlSource) {
        return (screenshotAndHtmlSource.wasTaken() && previousScreenshot().isPresent()
                && (!screenshotAndHtmlSource.hasIdenticalScreenshotsAs(previousScreenshot().get())));
    }

    private Optional<ScreenshotAndHtmlSource> previousScreenshot() {
        List<ScreenshotAndHtmlSource> screenshotsToDate = getCurrentTestOutcome().getScreenshotAndHtmlSources();
        if (screenshotsToDate.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(screenshotsToDate.get(screenshotsToDate.size() - 1));
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
        return SerenityWebdriverManager.inThisTestThread()
                .getCurrentDrivers()
                .stream()
                .map(driver -> new ScreenshotAndHtmlSource(screenshotFrom(driver), sourceFrom(result, driver)))
                .filter(ScreenshotAndHtmlSource::wasTaken)
                .collect(Collectors.toList());
    }

    private File screenshotFrom(WebDriver driver) {
        Path screenshotPath = getPhotographer().takesAScreenshot()
                .with(new WebDriverPhotoLens(driver))
                .andWithBlurring(AnnotatedBluring.blurLevel())
                .toDirectory(pathOf(outputDirectory))
                .takeScreenshot()
                .getPathToScreenshot();

        return (screenshotPath == null) ? null : screenshotPath.toFile();
    }

    private File sourceFrom(TestResult result, WebDriver driver) {
        return soundEngineer.ifRequiredForResult(result)
                .recordPageSourceUsing(driver)
                .intoDirectory(pathOf(outputDirectory)).orElse(null);
    }

    private Path pathOf(File directory) {
        return (directory == null) ? null : directory.toPath();
    }

    private boolean shouldTakeEndOfStepScreenshotFor(final TestResult result) {
        if (result.isAtLeast(FAILURE)) {
            return screenshots().areAllowed(TakeScreenshots.FOR_FAILURES, result);
        } else {
            return screenshots().areAllowed(TakeScreenshots.AFTER_EACH_STEP, result);
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
        TestResult currentResult = CurrentTestResult.forTestOutcome(getCurrentTestOutcome(), currentExample);
        return ((!getTestOutcomes().isEmpty()) && currentResult.isUnsuccessful());
    }

    public Optional<TestStep> firstFailingStep() {
        return latestTestOutcome()
                .flatMap(outcome -> outcome.getFlattenedTestSteps()
                        .stream()
                        .filter(step -> step.getException() != null)
                        .findFirst());
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

    @Override
    public void testAborted() {
        if (!testOutcomeRecorded()) {
            return;
        }
        getCurrentTestOutcome().setAnnotatedResult(ABORTED);
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

    public void notifyUIError() {
        if (currentTestIsABrowserTest() && screenshots().areAllowed(TakeScreenshots.FOR_FAILURES)) {
            take(OPTIONAL_SCREENSHOT, FAILURE);
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
        exampleStarted(data, "");
    }

    public void exampleStarted(Map<String, String> data, ZonedDateTime startTime) {
        exampleStarted(data, "", startTime);
    }

    public void exampleStarted(Map<String, String> data, String exampleName) {
        exampleStarted(data, exampleName, ZonedDateTime.now());
    }

    public void exampleStarted(Map<String, String> data, String exampleName, ZonedDateTime startTime) {

        clearForcedResult();
        if (getCurrentTestOutcome().isDataDriven()) {
            if (!getCurrentTestOutcome().dataIsPredefined()) {
                getCurrentTestOutcome().addRow(data);
            }
        }
        currentExample++;
        if (newStepForEachExample()) {
            String exampleTitle = (exampleName.isEmpty() ? exampleTitle(currentExample, data) : exampleTitle(currentExample, exampleName, data));
            getEventBus().stepStarted(ExecutedStepDescription.withTitle(exampleTitle), startTime);
        }

        LifecycleRegister.invokeMethodsAnnotatedBy(BeforeExample.class, getCurrentTestOutcome());
    }

    private String exampleTitle(int exampleNumber, Map<String, String> data) {
        return String.format("Example %d: %s", exampleNumber, data);
    }

    private String exampleTitle(int exampleNumber, String exampleName, Map<String, String> data) {
        return String.format("%d: %s (%s)", exampleNumber, exampleName, data);
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
        if (TestSession.getTestSessionContext().getWebDriver() != null) {
            getCurrentTestOutcome().setDriver(TestSession.getTestSessionContext().getDriverUsedInThisTest());
            AtTheEndOfAWebDriverTest.invokeCustomTeardownLogicWithDriver(
                    getEventBus().getEnvironmentVariables(),
                    getCurrentTestOutcome(),
                    TestSession.getTestSessionContext().getWebDriver());
        } else if (currentTestIsABrowserTest()) {
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
        if (latestTestOutcome().isEmpty()) {
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

    public void clearTestOutcomes() {
        testOutcomes.clear();
    }
}
