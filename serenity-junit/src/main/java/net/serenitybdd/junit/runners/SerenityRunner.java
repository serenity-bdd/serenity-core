package net.serenitybdd.junit.runners;

import com.google.common.base.Optional;
import com.google.inject.Injector;
import com.google.inject.Module;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.serenitybdd.core.injectors.EnvironmentDependencyInjector;
import net.thucydides.core.annotations.ManagedWebDriverAnnotatedField;
import net.thucydides.core.annotations.TestCaseAnnotations;
import net.thucydides.core.batches.BatchManager;
import net.thucydides.core.batches.BatchManagerProvider;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.ReportService;
import net.thucydides.core.steps.PageObjectDependencyInjector;
import net.thucydides.core.steps.StepAnnotations;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.StepFactory;
import net.thucydides.core.steps.stepdata.StepData;
import net.thucydides.core.tags.TagScanner;
import net.thucydides.core.webdriver.*;
import net.thucydides.junit.listeners.JUnitStepListener;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static net.serenitybdd.core.Serenity.initializeTestSession;
import static net.thucydides.core.ThucydidesSystemProperty.TEST_RETRY_COUNT;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * A test runner for WebDriver-based web tests. This test runner initializes a
 * WebDriver instance before running the tests in their order of appearance. At
 * the end of the tests, it closes and quits the WebDriver instance.
 * The test runner will by default produce output in XML and HTML. This
 * can extended by subscribing more reporter implementations to the test runner.
 *
 * @author johnsmart
 */
public class SerenityRunner extends BlockJUnit4ClassRunner {

    /**
     * Provides a proxy of the ScenarioSteps object used to invoke the test steps.
     * This proxy notifies the test runner about individual step outcomes.
     */
    private StepFactory stepFactory;
    private Pages pages;
    private final WebdriverManager webdriverManager;
    private String requestedDriver;
    private ReportService reportService;
    private final TestConfiguration theTest;
    private FailureRerunner failureRerunner;
    /**
     * Special listener that keeps track of test step execution and results.
     */
    private JUnitStepListener stepListener;

    private PageObjectDependencyInjector dependencyInjector;

    private FailureDetectingStepListener failureDetectingStepListener;


    /**
     * Retrieve the runner getConfiguration().from an external source.
     */
    private Configuration configuration;

    private TagScanner tagScanner;

    private BatchManager batchManager;

    private final Logger logger = LoggerFactory.getLogger(SerenityRunner.class);

    public Pages getPages() {
        return pages;
    }

    /**
     * Creates a new test runner for WebDriver web tests.
     *
     * @param klass the class under test
     * @throws InitializationError if some JUnit-related initialization problem occurred
     */
    public SerenityRunner(final Class<?> klass) throws InitializationError {
        this(klass, Injectors.getInjector());
    }

    /**
     * Creates a new test runner for WebDriver web tests.
     *
     * @param klass the class under test
     * @param module used to inject a custom Guice module
     * @throws InitializationError if some JUnit-related initialization problem occurred
     */
    public SerenityRunner(Class<?> klass, Module module) throws InitializationError {
        this(klass, Injectors.getInjector(module));
    }

    public SerenityRunner(final Class<?> klass,
                          final Injector injector) throws InitializationError {
        this(klass,
                ThucydidesWebDriverSupport.getWebdriverManager(),
                injector.getInstance(Configuration.class),
                injector.getInstance(BatchManager.class)
        );
    }

    public SerenityRunner(final Class<?> klass,
                          final WebDriverFactory webDriverFactory) throws InitializationError {
        this(klass, webDriverFactory, ConfiguredEnvironment.getConfiguration());
    }

    public SerenityRunner(final Class<?> klass,
                          final WebDriverFactory webDriverFactory,
                          final Configuration configuration) throws InitializationError {
        this(klass,
                webDriverFactory,
                configuration,
                new BatchManagerProvider(configuration).get()
        );
    }

    public SerenityRunner(final Class<?> klass,
                          final WebDriverFactory webDriverFactory,
                          final Configuration configuration,
                          final BatchManager batchManager) throws InitializationError {
        this(klass,
                ThucydidesWebDriverSupport.getWebdriverManager(webDriverFactory, configuration),
                configuration,
                batchManager
        );
    }

    public SerenityRunner(final Class<?> klass, final BatchManager batchManager) throws InitializationError {
        this(klass,
                ThucydidesWebDriverSupport.getWebdriverManager(),
                ConfiguredEnvironment.getConfiguration(),
                batchManager);
    }

    public SerenityRunner(final Class<?> klass,
                          final WebdriverManager webDriverManager,
                          final Configuration configuration,
                          final BatchManager batchManager) throws InitializationError {
        super(klass);

        this.theTest = TestConfiguration.forClass(klass).withSystemConfiguration(configuration);
        this.webdriverManager = webDriverManager;
        this.configuration = configuration;
        this.requestedDriver = getSpecifiedDriver(klass);
        this.tagScanner = new TagScanner(configuration.getEnvironmentVariables());
        this.failureDetectingStepListener = new FailureDetectingStepListener();
        this.failureRerunner = new FailureRerunnerXml(configuration);

        if (TestCaseAnnotations.supportsWebTests(klass)) {
            checkRequestedDriverType();
        }

        this.batchManager = batchManager;

        batchManager.registerTestCase(klass);

    }


    private String getSpecifiedDriver(Class<?> klass) {
        if (ManagedWebDriverAnnotatedField.hasManagedWebdriverField(klass)) {
            return ManagedWebDriverAnnotatedField.findFirstAnnotatedField(klass).getDriver();
        } else {
            return null;
        }
    }

    /**
     * The Configuration class manages output directories and driver types.
     * They can be defined as system values, or have sensible defaults.
     * @return the current configuration
     */
    protected Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Batch Manager used for running tests in parallel batches
     * @return the current batch manager object
     */
    protected BatchManager getBatchManager() {
        return batchManager;
    }

    /**
     * Ensure that the requested driver type is valid before we start the tests.
     * Otherwise, throw an InitializationError.
     */
    private void checkRequestedDriverType() {
        if (requestedDriverSpecified()) {
            SupportedWebDriver.getDriverTypeFor(requestedDriver);
        } else {
            getConfiguration().getDriverType();
        }
    }

    private boolean requestedDriverSpecified() {
        return !isEmpty(this.requestedDriver);
    }

    public File getOutputDirectory() {
        return getConfiguration().getOutputDirectory();
    }

    /**
     * To generate reports, different AcceptanceTestReporter instances need to
     * subscribe to the listener. The listener will tell them when the test is
     * done, and the reporter can decide what to do.
     * @param reporter an implementation of the AcceptanceTestReporter interface.
     */
    public void subscribeReporter(final AcceptanceTestReporter reporter) {
        getReportService().subscribe(reporter);
    }

    public void useQualifier(final String qualifier) {
        getReportService().useQualifier(qualifier);
    }

    /**
     * Runs the tests in the acceptance test case.
     */

    @Override
    public void run(final RunNotifier notifier) {
        if (skipThisTest()) { return; }

        try {
            RunNotifier localNotifier = initializeRunNotifier(notifier);
            StepEventBus.getEventBus().registerListener(failureDetectingStepListener);

            super.run(localNotifier);
            fireNotificationsBasedOnTestResultsTo(notifier);
        } catch (Throwable someFailure) {
            someFailure.printStackTrace();
            throw someFailure;
        } finally {
            notifyTestSuiteFinished();
            generateReports();
            Map<String, List<String>> failedTests = stepListener.getFailedTests();
            failureRerunner.recordFailedTests(failedTests);
            dropListeners(notifier);
        }
    }



    private Optional<TestOutcome> latestOutcome() {
        if (StepEventBus.getEventBus().getBaseStepListener().getTestOutcomes().isEmpty()) {
            return Optional.absent();
        }
        return Optional.of(StepEventBus.getEventBus().getBaseStepListener().getTestOutcomes().get(0));
    }

    private void fireNotificationsBasedOnTestResultsTo(RunNotifier notifier) {
        if (!latestOutcome().isPresent()) {
            return;
        }
    }

    private void notifyTestSuiteFinished() {
        try {
            if (dataDrivenTest()) {
                StepEventBus.getEventBus().exampleFinished();
            } else {
                StepEventBus.getEventBus().testSuiteFinished();
            }
        } catch (Throwable listenerException) {
            // We report and ignore listener exceptions so as not to mess up the rest of the test mechanics.
            logger.error("Test event bus error: " + listenerException.getMessage(), listenerException);
        }
    }

    private boolean dataDrivenTest() {
        return this instanceof TestClassRunnerForParameters;
    }

    private void dropListeners(final RunNotifier notifier) {
        JUnitStepListener listener = getStepListener();
        notifier.removeListener(listener);
        getStepListener().dropListeners();
    }

    protected void generateReports() {
        generateReportsFor(getTestOutcomes());
    }

    private boolean skipThisTest() {
        return testNotInCurrentBatch();
    }

    private boolean testNotInCurrentBatch() {
        return (batchManager != null) && (!batchManager.shouldExecuteThisTest(getDescription().testCount()));
    }

    /**
     * The Step Listener observes and records what happens during the execution of the test.
     * Once the test is over, the Step Listener can provide the acceptance test outcome in the
     * form of an TestOutcome object.
     * @return the current step listener
     */
    protected JUnitStepListener getStepListener() {
        if (stepListener == null) {
            buildAndConfigureListeners();
        }
        return stepListener;
    }

    protected void setStepListener(JUnitStepListener stepListener) {
        this.stepListener = stepListener;
    }

    private void buildAndConfigureListeners() {

        initStepEventBus();
        if (webtestsAreSupported()) {
            ThucydidesWebDriverSupport.initialize(requestedDriver);
            WebDriver driver = ThucydidesWebDriverSupport.getWebdriverManager().getWebdriver();
            initPagesObjectUsing(driver);
            setStepListener(initListenersUsing(getPages()));
            initStepFactoryUsing(getPages());
        } else {
            setStepListener(initListeners());
            initStepFactory();
        }
    }

    private RunNotifier initializeRunNotifier(RunNotifier notifier) {
        notifier.addListener(getStepListener());
        return notifier;
    }

    private int maxRetries() {
        return TEST_RETRY_COUNT.integerFrom(configuration.getEnvironmentVariables(), 0);
    }


    protected void initStepEventBus() {
        StepEventBus.getEventBus().clear();
    }

    private void initPagesObjectUsing(final WebDriver driver) {
        pages = new Pages(driver, getConfiguration());
        dependencyInjector = new PageObjectDependencyInjector(pages);
    }

    protected JUnitStepListener initListenersUsing(final Pages pageFactory) {

        return JUnitStepListener.withOutputDirectory(getConfiguration().getOutputDirectory())
                .and().withPageFactory(pageFactory)
                .and().withTestClass(getTestClass().getJavaClass())
                .and().build();
    }

    protected JUnitStepListener initListeners() {
        return JUnitStepListener.withOutputDirectory(getConfiguration().getOutputDirectory())
                .and().withTestClass(getTestClass().getJavaClass())
                .and().build();
    }

    private boolean webtestsAreSupported() {
        return TestCaseAnnotations.supportsWebTests(this.getTestClass().getJavaClass());
    }

    private void initStepFactoryUsing(final Pages pagesObject) {
        stepFactory = new StepFactory(pagesObject);
    }

    private void initStepFactory() {
        stepFactory = new StepFactory();
    }

    private ReportService getReportService() {
        if (reportService == null) {
            reportService = new ReportService(getOutputDirectory(), getDefaultReporters());
        }
        return reportService;
    }

    /**
     * A test runner can generate reports via Reporter instances that subscribe
     * to the test runner. The test runner tells the reporter what directory to
     * place the reports in. Then, at the end of the test, the test runner
     * notifies these reporters of the test outcomes. The reporter's job is to
     * process each test run outcome and do whatever is appropriate.
     *
     * @param testOutcomeResults the test results from the previous test run.
     */
    private void generateReportsFor(final List<TestOutcome> testOutcomeResults) {
        getReportService().generateReportsFor(testOutcomeResults);
        getReportService().generateConfigurationsReport();
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {

        TestMethodConfiguration theMethod = TestMethodConfiguration.forMethod(method);

        clearMetadataIfRequired();

        if(!failureRerunner.hasToRunTest(method.getDeclaringClass().getCanonicalName(),method.getMethod().getName()))
        {
            return;
        }

        if (shouldSkipTest(method)) {
            return;
        }

        if (theMethod.isManual()) {
            markAsManual(method);
            notifier.fireTestIgnored(describeChild(method));
            return;
        } else if (theMethod.isPending()) {
            markAsPending(method);
            notifier.fireTestIgnored(describeChild(method));
            return;
        } else {
            processTestMethodAnnotationsFor(method);
        }

        initializeTestSession();
        prepareBrowserForTest();
        additionalBrowserCleanup();

        performRunChild(method, notifier);

        if (failureDetectingStepListener.lastTestFailed() && maxRetries() > 0) {
            retryAtMost(maxRetries(), new RerunSerenityTest(method, notifier));
        }
    }

    private void retryAtMost(int remainingTries,
                             RerunTest rerunTest) {
        if (remainingTries <= 0) { return; }

        logger.info(rerunTest.toString() + ": attempt " + (maxRetries() - remainingTries));
        StepEventBus.getEventBus().cancelPreviousTest();
        rerunTest.perform();

        if (failureDetectingStepListener.lastTestFailed()) {
            retryAtMost(remainingTries - 1, rerunTest);
        } else {
            StepEventBus.getEventBus().lastTestPassedAfterRetries(remainingTries,
                                                                  failureDetectingStepListener.getFailureMessages(),failureDetectingStepListener.getTestFailureCause());
        }
    }


    private void performRunChild(FrameworkMethod method, RunNotifier notifier) {
        super.runChild(method, notifier);
    }

    interface RerunTest {
        void perform();
    }

    class RerunSerenityTest implements RerunTest {
        private final FrameworkMethod method;
        private final RunNotifier notifier;

        RerunSerenityTest(FrameworkMethod method, RunNotifier notifier) {
            this.method = method;
            this.notifier = notifier;
        }

        @Override
        public void perform() {
            performRunChild(method, notifier);
        }

        @Override
        public String toString() {
            return "Retrying " + method.getDeclaringClass() + " " + method.getMethod().getName();
        }
    }

    private void clearMetadataIfRequired() {
        if (theTest.shouldClearMetadata()) {
            Serenity.getCurrentSession().clearMetaData();
        }
    }

    protected void additionalBrowserCleanup() {
        // Template method. Override this to do additional cleanup e.g. killing IE processes.
    }

    private boolean shouldSkipTest(FrameworkMethod method) {
        return !tagScanner.shouldRunMethod(getTestClass().getJavaClass(), method.getName());
    }

    private void markAsPending(FrameworkMethod method) {
        testStarted(method);
        StepEventBus.getEventBus().testPending();
        StepEventBus.getEventBus().testFinished();
    }

    private void markAsManual(FrameworkMethod method) {
        testStarted(method);
        StepEventBus.getEventBus().testIsManual();
        StepEventBus.getEventBus().testPending();
        StepEventBus.getEventBus().testFinished();
    }

    private void testStarted(FrameworkMethod method) {
        getStepListener().testStarted(Description.createTestDescription(method.getMethod().getDeclaringClass(), testName(method)));
    }

    /**
     * Process any Serenity annotations in the test class.
     * Ignored tests will just be skipped by JUnit - we need to ensure
     * that they are included in the Serenity reports
     * If a test method is pending, all the steps should be skipped.
     */
    private void processTestMethodAnnotationsFor(FrameworkMethod method) {
        if (isIgnored(method)) {
            testStarted(method);
            StepEventBus.getEventBus().testIgnored();
            StepEventBus.getEventBus().testFinished();
        }
    }

    protected void prepareBrowserForTest() {
        if (theTest.shouldClearTheBrowserSession()) {
            WebdriverProxyFactory.clearBrowserSession(getDriver());
        }
    }

    /**
     * Running a unit test, which represents a test scenario.
     */
    @Override
    protected Statement methodInvoker(final FrameworkMethod method, final Object test) {

        if (webtestsAreSupported()) {
            injectDriverInto(test);
            initPagesObjectUsing(driverFor(method));
            injectAnnotatedPagesObjectInto(test);
            initStepFactoryUsing(getPages());
        }

        injectScenarioStepsInto(test);
        injectEnvironmentVariablesInto(test);
        useStepFactoryForDataDrivenSteps();

        Statement baseStatement = super.methodInvoker(method, test);
        return new SerenityStatement(baseStatement, stepListener.getBaseStepListener());
    }

    private void useStepFactoryForDataDrivenSteps() {
        StepData.setDefaultStepFactory(stepFactory);
    }

    /**
     * Instantiate the @Managed-annotated WebDriver instance with current WebDriver.
     * @param testCase A Serenity-annotated test class
     */
    protected void injectDriverInto(final Object testCase) {
        TestCaseAnnotations.forTestCase(testCase).injectDrivers(ThucydidesWebDriverSupport.getDriver(),
                                                                ThucydidesWebDriverSupport.getWebdriverManager());
        dependencyInjector.injectDependenciesInto(testCase);
    }

    protected WebDriver driverFor(final FrameworkMethod method) {
        if (TestMethodAnnotations.forTest(method).isDriverSpecified()) {
            String testSpecificDriver = TestMethodAnnotations.forTest(method).specifiedDriver();
            return getDriver(testSpecificDriver);
        } else {
            return getDriver();
        }
    }

    /**
     * Instantiates the @ManagedPages-annotated Pages instance using current WebDriver.
     * @param testCase A Serenity-annotated test class
     */
    protected void injectScenarioStepsInto(final Object testCase) {
        StepAnnotations.injectScenarioStepsInto(testCase, stepFactory);
    }

    /**
     * Instantiates the @ManagedPages-annotated Pages instance using current WebDriver.
     * @param testCase A Serenity-annotated test class
         */
    protected void injectAnnotatedPagesObjectInto(final Object testCase) {
        StepAnnotations.injectAnnotatedPagesObjectInto(testCase, pages);
    }

    protected void injectEnvironmentVariablesInto(final Object testCase) {
        EnvironmentDependencyInjector environmentDependencyInjector = new EnvironmentDependencyInjector();
        environmentDependencyInjector.injectDependenciesInto(testCase);
    }

    protected WebDriver getDriver() {
        return (isEmpty(requestedDriver)) ? ThucydidesWebDriverSupport.getWebdriverManager().getWebdriver()
                : ThucydidesWebDriverSupport.getWebdriverManager().getWebdriver(requestedDriver);
    }

    protected WebDriver getDriver(final String driver) {
        return (isEmpty(driver)) ? ThucydidesWebDriverSupport.getWebdriverManager().getWebdriver()
                                 : ThucydidesWebDriverSupport.getWebdriverManager().getWebdriver(driver);
    }

    /**
     * Find the current set of test outcomes produced by the test execution.
     * @return the current list of test outcomes
     */
    public List<TestOutcome> getTestOutcomes() {
        return getStepListener().getTestOutcomes();
    }

    /**
     *  @return The default reporters applicable for standard test runs.
     */
    protected Collection<AcceptanceTestReporter> getDefaultReporters() {
        return ReportService.getDefaultReporters();
    }
}
