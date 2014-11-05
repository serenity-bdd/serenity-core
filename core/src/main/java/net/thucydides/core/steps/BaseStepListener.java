package net.thucydides.core.steps;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Injector;
import net.thucydides.core.PendingStepException;
import net.thucydides.core.annotations.TestAnnotations;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.*;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.pages.SystemClock;
import net.thucydides.core.screenshots.*;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.WebdriverManager;
import net.thucydides.core.webdriver.WebdriverProxyFactory;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.SessionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

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

    /**
     * Keeps track of what steps have been started but not finished, in order to structure nested steps.
     */
    private final Stack<TestStep> currentStepStack;

    /**
     * Keeps track of the current step group, if any.
     */
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

    private WebdriverManager webdriverManager;

    private File outputDirectory;

    private WebdriverProxyFactory proxyFactory;

    private Story testedStory;

    private Configuration configuration;

    ScreenshotProcessor screenshotProcessor;

    private boolean inFluentStepSequence;

    private List<String> storywideIssues;

    private List<TestTag> storywideTags;

    public void setEventBus(StepEventBus eventBus) {
        this.eventBus = eventBus;
    }

    public StepEventBus getEventBus() {
        if (eventBus == null) {
            eventBus = StepEventBus.getEventBus();
        }
        return eventBus;
    }

    public Optional<TestStep> cloneCurrentStep() {
        return (Optional<TestStep>) ((currentStepExists()) ? getCurrentStep().clone() : Optional.absent());
    }

    public void setAllStepsTo(TestResult result) {
        getCurrentTestOutcome().setAnnotatedResult(result);
        getCurrentTestOutcome().setAllStepsTo(result);
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
        this.testOutcomes = Lists.newArrayList();
        this.currentStepStack = new Stack<TestStep>();
        this.currentGroupStack = new Stack<TestStep>();
        this.outputDirectory = outputDirectory;
        this.inFluentStepSequence = false;
        this.storywideIssues = Lists.newArrayList();
        this.storywideTags = Lists.newArrayList();
        this.webdriverManager = injector.getInstance(WebdriverManager.class);
        this.clock = injector.getInstance(SystemClock.class);
        this.configuration = injector.getInstance(Configuration.class);
        this.screenshotProcessor = injector.getInstance(ScreenshotProcessor.class);
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
        this(outputDirectory);
        this.driver = getProxyFactory().proxyFor(driverClass);
        this.configuration = configuration;
    }

    public BaseStepListener(final File outputDirectory,
                            final WebdriverManager webdriverManager) {
        this(outputDirectory);
        this.webdriverManager = webdriverManager;
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
        if (pages.getDriver() != null) {
            setDriver(pages.getDriver());
        } else {
            createNewDriver();
            pages.setDriver(getDriver());
        }
    }

    protected WebdriverProxyFactory getProxyFactory() {
        return proxyFactory;
    }

    protected TestOutcome getCurrentTestOutcome() {
        Preconditions.checkState(!testOutcomes.isEmpty());
        return testOutcomes.get(testOutcomes.size() - 1);
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

    public void testSuiteFinished() {
        screenshotProcessor.waitUntilDone();
        clearStorywideTagsAndIssues();
        suiteStarted = false;
    }


    /**
     * An individual test starts.
     *
     * @param testMethod the name of the test method in the test suite class.
     */
    public void testStarted(final String testMethod) {
        TestOutcome newTestOutcome = TestOutcome.forTestInStory(testMethod, testSuite, testedStory);
        testOutcomes.add(newTestOutcome);
        updateSessionIdIfKnown();
        setAnnotatedResult(testMethod);
    }

    private void updateSessionIdIfKnown() {
        SessionId sessionId = webdriverManager.getSessionId();
        if (sessionId != null) {
            getCurrentTestOutcome().setSessionId(sessionId.toString());
        }
    }

    public void updateCurrentStepTitle(String updatedStepTitle) {
        if (currentStepExists()) {
            getCurrentStep().setDescription(updatedStepTitle);
        } else {
            stepStarted(ExecutedStepDescription.withTitle(updatedStepTitle));
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

    /**
     * A test has finished.
     *
     * @param outcome the result of the test that just finished.
     */
    public void testFinished(final TestOutcome outcome) {
        recordTestDuration();
        getCurrentTestOutcome().addIssues(storywideIssues);
        // TODO: Disable when run from an IDE
        getCurrentTestOutcome().addTags(storywideTags);
        currentStepStack.clear();
    }

    public void testRetried() {
        currentStepStack.clear();
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
        recordStep(description);
        takeInitialScreenshot();
        updateSessionIdIfKnown();
    }

    public void skippedStepStarted(final ExecutedStepDescription description) {
        recordStep(description);
    }

    private void recordStep(ExecutedStepDescription description) {
        String stepName = AnnotatedStepDescription.from(description).getName();

        updateFluentStepStatus(description, stepName);

        if (justStartedAFluentSequenceFor(description) || notInAFluentSequence()) {

            TestStep step = new TestStep(stepName);

            startNewGroupIfNested();
            setDefaultResultFromAnnotations(step, description);

            currentStepStack.push(step);
            recordStepToCurrentTestOutcome(step);
        }
        inFluentStepSequence = AnnotatedStepDescription.from(description).isFluent();
    }

    private void recordStepToCurrentTestOutcome(TestStep step) {
        getCurrentTestOutcome().recordStep(step);
    }

    private void updateFluentStepStatus(ExecutedStepDescription description, String stepName) {
        if (currentlyInAFluentSequenceFor(description) || justFinishedAFluentSequenceFor(description)) {
            addToFluentStepName(stepName);
        }
    }

    private void addToFluentStepName(String stepName) {
        String updatedStepName = getCurrentStep().getDescription() + " " + StringUtils.uncapitalize(stepName);
        getCurrentStep().setDescription(updatedStepName);
    }

    private boolean notInAFluentSequence() {
        return !inFluentStepSequence;
    }

    private boolean justFinishedAFluentSequenceFor(ExecutedStepDescription description) {
        boolean thisStepIsFluent = AnnotatedStepDescription.from(description).isFluent();
        return (inFluentStepSequence && !thisStepIsFluent);
    }

    private boolean justStartedAFluentSequenceFor(ExecutedStepDescription description) {
        boolean thisStepIsFluent = AnnotatedStepDescription.from(description).isFluent();
        return (!inFluentStepSequence && thisStepIsFluent);
    }

    private boolean currentlyInAFluentSequenceFor(ExecutedStepDescription description) {
        boolean thisStepIsFluent = AnnotatedStepDescription.from(description).isFluent();
        return (inFluentStepSequence && thisStepIsFluent);
    }

    private void setDefaultResultFromAnnotations(final TestStep step, final ExecutedStepDescription description) {
        if (TestAnnotations.isPending(description.getTestMethod())) {
            step.setResult(TestResult.PENDING);
        }
        if (TestAnnotations.isIgnored(description.getTestMethod())) {
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

    private TestStep getCurrentStep() {
        return currentStepStack.peek();
    }

    private Optional<TestStep> getPreviousStep() {
        if (getCurrentTestOutcome().getTestSteps().size() > 1) {
            List<TestStep> currentTestSteps = getCurrentTestOutcome().getTestSteps();
            return Optional.of(currentTestSteps.get(currentTestSteps.size() - 2));
        } else {
            return Optional.absent();
        }
    }

    private TestStep getCurrentGroup() {
        if (currentGroupStack.isEmpty()) {
            return null;
        } else {
            return currentGroupStack.peek();// findLastChildIn(currentGroupStack.peek());
        }
    }

    private boolean thereAreUnfinishedSteps() {
        return !currentStepStack.isEmpty();
    }

    public void stepFinished() {
        updateSessionIdIfKnown();
        takeEndOfStepScreenshotFor(SUCCESS);
        currentStepDone(SUCCESS);
//        markCurrentStepAs(SUCCESS);
        pauseIfRequired();
    }

    private void updateExampleTableIfNecessary(TestResult result) {
        if (getCurrentTestOutcome().isDataDriven()) {
            getCurrentTestOutcome().updateCurrentRowResult(result);
        }
    }

    private void finishGroup() {
        currentGroupStack.pop();
        getCurrentTestOutcome().endGroup();
    }

    private void pauseIfRequired() {
        int delay = configuration.getStepDelay();
        if (delay > 0) {
            getClock().pauseFor(delay);
        }
    }

    private void markCurrentStepAs(final TestResult result) {
        getCurrentTestOutcome().currentStep().setResult(result);
        updateExampleTableIfNecessary(result);
    }

    FailureAnalysis failureAnalysis = new FailureAnalysis();

    public void stepFailed(StepFailure failure) {
        takeEndOfStepScreenshotFor(FAILURE);
        getCurrentTestOutcome().determineTestFailureCause(failure.getException());
//        markCurrentStepAs(failureAnalysis.resultFor(failure));
        recordFailureDetailsInFailingTestStep(failure);
        currentStepDone(failureAnalysis.resultFor(failure));
    }

    public void lastStepFailed(StepFailure failure) {
        takeEndOfStepScreenshotFor(FAILURE);
        getCurrentTestOutcome().lastStepFailedWith(failure);
    }


    private void recordFailureDetailsInFailingTestStep(final StepFailure failure) {
        if (currentStepExists()) {
            getCurrentStep().failedWith(new StepFailureException(failure.getMessage(), failure.getException()));
        }
    }

    public void stepIgnored() {
        if (aStepHasFailed()) {
            markCurrentStepAs(SKIPPED);
            currentStepDone(SKIPPED);
        } else {
//            markCurrentStepAs(IGNORED);
            currentStepDone(IGNORED);
        }
    }

    public void stepPending() {
//        markCurrentStepAs(PENDING);
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

    private void currentStepDone(TestResult result) {
        if ((!inFluentStepSequence) && currentStepExists()) {
            TestStep finishedStep = currentStepStack.pop();
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
        return !currentStepStack.isEmpty();
    }

    private void takeEndOfStepScreenshotFor(final TestResult result) {
        if (shouldTakeEndOfStepScreenshotFor(result)) {
            take(OPTIONAL_SCREENSHOT);
        }
    }

    private void take(final ScreenshotType screenshotType) {
        if (currentStepExists() && browserIsOpen()) {
            try {
                Optional<ScreenshotAndHtmlSource> screenshotAndHtmlSource = grabScreenshot();
                if (screenshotAndHtmlSource.isPresent()) {
                    takeScreenshotIfRequired(screenshotType, screenshotAndHtmlSource.get());
                }
                removeDuplicatedInitalScreenshotsIfPresent();
            } catch (ScreenshotException e) {
                LOGGER.warn("Failed to take screenshot", e);
            }
        }
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

    private void takeScreenshotIfRequired(ScreenshotType screenshotType, ScreenshotAndHtmlSource screenshotAndHtmlSource) {
        if (shouldTakeScreenshot(screenshotType, screenshotAndHtmlSource) && screenshotWasTaken(screenshotAndHtmlSource)) {
            getCurrentStep().addScreenshot(screenshotAndHtmlSource);
        }
    }

    private boolean screenshotWasTaken(ScreenshotAndHtmlSource screenshotAndHtmlSource) {
        return screenshotAndHtmlSource.getScreenshotFile() != null;
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

    private Optional<ScreenshotAndHtmlSource> previousScreenshot() {
        List<ScreenshotAndHtmlSource> screenshotsToDate = getCurrentTestOutcome().getScreenshotAndHtmlSources();
        if (screenshotsToDate.isEmpty()) {
            return Optional.absent();
        } else {
            return Optional.of(screenshotsToDate.get(screenshotsToDate.size() - 1));
        }
    }

    private boolean browserIsOpen() {
        if (driver == null) {
            return false;
        }
        if (driver instanceof WebDriverFacade) {
            return (((WebDriverFacade) driver).isInstantiated());
        } else {
            return (driver.getCurrentUrl() != null);
        }
    }

    private void takeInitialScreenshot() {
        if ((currentStepExists()) && (screenshots().areAllowed(TakeScreenshots.BEFORE_AND_AFTER_EACH_STEP))) {
            take(OPTIONAL_SCREENSHOT);
        }
    }

    private Optional<ScreenshotAndHtmlSource> grabScreenshot() {
        Optional<File> screenshot = getPhotographer().takeScreenshot();
        if (screenshot.isPresent()) {
            if (shouldStoreSourcecode()) {
                File sourcecodeFile = sourcecodeForScreenshot(screenshot.get(), getPageSource());
                return Optional.of(new ScreenshotAndHtmlSource(screenshot.get(), sourcecodeFile));
            } else {
                return Optional.of(new ScreenshotAndHtmlSource(screenshot.get()));
            }
        }
        return Optional.absent();
    }

    public String getPageSource() {
        return getPhotographer().getPageSource();
    }

    private File sourcecodeForScreenshot(File screenshotFile, String pageSource) {
        File pageSourceFile = new File(screenshotFile.getAbsolutePath() + ".html");

        try {
            Files.write(pageSourceFile.toPath(), pageSource.getBytes());
        } catch (IOException e) {
            LOGGER.warn("Failed to write screen source code",e);
        }
        return pageSourceFile;
    }

    private boolean shouldStoreSourcecode() {
        return configuration.storeHtmlSourceCode();
    }

    public Photographer getPhotographer() {
        ScreenshotBlurCheck blurCheck = new ScreenshotBlurCheck();
        if (blurCheck.blurLevel().isPresent()) {
            return new Photographer(driver, outputDirectory, blurCheck.blurLevel().get());
        } else {
            return new Photographer(driver, outputDirectory);
        }
    }

    private boolean shouldTakeEndOfStepScreenshotFor(final TestResult result) {
        if (result == FAILURE) {
            return screenshots().areAllowed(TakeScreenshots.FOR_FAILURES);
        } else {
            return screenshots().areAllowed(TakeScreenshots.AFTER_EACH_STEP);
        }
    }

    public List<TestOutcome> getTestOutcomes() {
        List<TestOutcome> sortedOutcomes = Lists.newArrayList(testOutcomes);
        Collections.sort(sortedOutcomes, byStartTimeAndName());
        return ImmutableList.copyOf(sortedOutcomes);
    }

    private Comparator<? super TestOutcome> byStartTimeAndName() {
        return new Comparator<TestOutcome>() {
            public int compare(TestOutcome testOutcome1, TestOutcome testOutcome2) {
                String creationTimeAndName1 = testOutcome1.getStartTime().getMillis() + "_" + testOutcome1.getMethodName();
                String creationTimeAndName2 = testOutcome2.getStartTime().getMillis() + "_" + testOutcome2.getMethodName();
                return creationTimeAndName1.compareTo(creationTimeAndName2);
            }
        };
    }


    public void setDriver(final WebDriver driver) {
        this.driver = driver;
    }

    public WebDriver getDriver() {
        return driver;
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public boolean aStepHasFailed() {
        return ((!getTestOutcomes().isEmpty()) &&
                (getCurrentTestOutcome().getResult() == TestResult.FAILURE || getCurrentTestOutcome().getResult() == TestResult.ERROR));
    }

    public FailureCause getTestFailureCause() {
        return getCurrentTestOutcome().getTestFailureCause();
    }

    public void testFailed(TestOutcome testOutcome, final Throwable cause) {
        getCurrentTestOutcome().determineTestFailureCause(cause);
    }

    public void testIgnored() {
        getCurrentTestOutcome().setAnnotatedResult(IGNORED);
    }

    public void testSkipped() {
        getCurrentTestOutcome().setAnnotatedResult(SKIPPED);
    }

    public void testPending() {
        getCurrentTestOutcome().setAnnotatedResult(PENDING);
    }

    public void notifyScreenChange() {
        if (screenshots().areAllowed(TakeScreenshots.FOR_EACH_ACTION)) {
            take(OPTIONAL_SCREENSHOT);
        }
    }

    /**
     * Take a screenshot now.
     */
    public void takeScreenshot() {
        take(MANDATORY_SCREENSHOT);
    }

    int currentExample = 0;

    /**
     * The current scenario is a data-driven scenario using test data from the specified table.
     */
    public void useExamplesFrom(DataTable table) {
        getCurrentTestOutcome().useExamplesFrom(table);
        currentExample = 0;
    }

    public void exampleStarted(Map<String, String> data) {
        if (getCurrentTestOutcome().isDataDriven() && !getCurrentTestOutcome().dataIsPredefined()) {
            getCurrentTestOutcome().addRow(data);
        }
        currentExample++;
        getEventBus().stepStarted(ExecutedStepDescription.withTitle(exampleTitle(currentExample, data)));
    }

    private String exampleTitle(int exampleNumber, Map<String, String> data) {
        return String.format("[%s] %s", exampleNumber, data);
    }

    public void exampleFinished() {
        currentStepDone(null);
        getCurrentTestOutcome().moveToNextRow();
    }
}
