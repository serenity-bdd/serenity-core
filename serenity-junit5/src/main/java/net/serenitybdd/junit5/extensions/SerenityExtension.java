package net.serenitybdd.junit5.extensions;

import net.serenitybdd.core.injectors.EnvironmentDependencyInjector;
import net.serenitybdd.junit5.JUnit5TestMethodAnnotations;
import net.thucydides.core.annotations.ManagedWebDriverAnnotatedField;
import net.thucydides.core.annotations.TestCaseAnnotations;
import net.thucydides.core.batches.BatchManager;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.reports.ReportService;
import net.thucydides.core.steps.*;
import net.thucydides.core.steps.stepdata.StepData;
import net.thucydides.core.tags.TagScanner;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport;
import net.thucydides.core.webdriver.WebdriverManager;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;

import static net.thucydides.core.reports.ReportService.getDefaultReporters;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class SerenityExtension implements TestInstancePostProcessor, BeforeAllCallback, BeforeTestExecutionCallback,
        AfterTestExecutionCallback, AfterAllCallback, TestExecutionExceptionHandler {

    /**
     * Provides a proxy of the ScenarioSteps object used to invoke the test steps.
     * This proxy notifies the test runner about individual step outcomes.
     */
    private File outputDirectory;
    private StepFactory stepFactory;
    private Pages pages;
    private WebdriverManager webdriverManager;
    private String requestedDriver;
    private ReportService reportService;
    //private TestConfiguration theTest;
    //private FailureRerunner failureRerunner;
    private BaseStepListener baseStepListener;
    private StepListener[] extraListeners;
    private Map<String,List<String>> failedTests = Collections.synchronizedMap(new HashMap<String,List<String>>());
    private Class<?> testClass;
    private boolean testStarted;

    private PageObjectDependencyInjector dependencyInjector;

    //private FailureDetectingStepListener failureDetectingStepListener;

    /**
     * Retrieve the runner getConfiguration().from an external source.
     */
    private Configuration configuration;

    private TagScanner tagScanner;

    private BatchManager batchManager;

    private final Logger logger = LoggerFactory.getLogger(SerenityExtension.class);

    public Pages getPages() {
        return pages;
    }

    public SerenityExtension(){}

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        System.out.println("SerenityExtension:beforeall");
        try {
            initializeAll(extensionContext);
            initStepEventBus();
            if (webtestsAreSupported(extensionContext.getRequiredTestClass())) {
                ThucydidesWebDriverSupport.initialize(requestedDriver);
                WebDriver driver = ThucydidesWebDriverSupport.getWebdriverManager().getWebdriver();
                initPagesObjectUsing(driver);
                initStepFactoryUsing(getPages());
            } else {
                initStepFactory();
            }
            if (!getBaseStepListener().testSuiteRunning()) {
                stepEventBus().testSuiteStarted(extensionContext.getRequiredTestClass());
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeAll(ExtensionContext extensionContext) {
        Class<?> requiredTestClass = extensionContext.getRequiredTestClass();
        //this.theTest = TestConfiguration.forClass(extensionContext.getRequiredTestClass()).withSystemConfiguration(configuration);
        this.webdriverManager = ThucydidesWebDriverSupport.getWebdriverManager();
        this.configuration = Injectors.getInjector().getInstance(Configuration.class);
        this.requestedDriver = getSpecifiedDriver(requiredTestClass);
        this.tagScanner = new TagScanner(configuration.getEnvironmentVariables());
        //this.failureDetectingStepListener = new FailureDetectingStepListener();
        //this.failureRerunner = new FailureRerunnerXml(configuration);
        this.outputDirectory = getConfiguration().getOutputDirectory();
        this.baseStepListener = buildBaseStepListener();
        StepEventBus.getEventBus().registerListener(baseStepListener);
        if (TestCaseAnnotations.supportsWebTests(requiredTestClass)) {
            checkRequestedDriverType();
        }
        this.batchManager = Injectors.getInjector().getInstance(BatchManager.class);
        batchManager.registerTestCase(requiredTestClass);
    }

    @Override
    public void postProcessTestInstance(Object test, ExtensionContext extensionContext) throws Exception {
        try {
            if (webtestsAreSupported(test)) {
                ThucydidesWebDriverSupport.initialize(requestedDriver);
                WebDriver driver = ThucydidesWebDriverSupport.getWebdriverManager().getWebdriver();
                initPagesObjectUsing(driver);
                initStepFactoryUsing(getPages());
            } else {
                initStepFactory();
            }
            injectScenarioStepsInto(test);
            injectEnvironmentVariablesInto(test);
            useStepFactoryForDataDrivenSteps();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        notifyTestSuiteFinished();
        generateReports();
        StepEventBus.getEventBus().dropListener(baseStepListener);
    }

    @Override
    public void beforeTestExecution(ExtensionContext extensionContext)  {
        prepareTestBeforeExecution(extensionContext);
        stepEventBus().clear();
        stepEventBus().setTestSource(TestSourceType.TEST_SOURCE_JUNIT.getValue());
        String displayName = removeEndBracketsFromDisplayName(extensionContext.getDisplayName());
        stepEventBus().testStarted(
                Optional.ofNullable(displayName).orElse("Initialisation"),
                extensionContext.getRequiredTestClass());
    }

    private String removeEndBracketsFromDisplayName(String displayName){
        if(displayName != null && displayName.endsWith("()")) {
            displayName = displayName.substring(0,displayName.length()-2);
        }
        return displayName;
    }

    private void prepareTestBeforeExecution(ExtensionContext extensionContext) {

        Object testObject = extensionContext.getTestInstance().get();
        if (webtestsAreSupported(extensionContext.getRequiredTestClass())) {
            injectDriverInto(testObject);
            initPagesObjectUsing(driverFor(extensionContext.getTestMethod().get()));
            injectAnnotatedPagesObjectInto(testObject);
            initStepFactoryUsing(getPages());
        }
        injectScenarioStepsInto(testObject);
        injectEnvironmentVariablesInto(testObject);
        useStepFactoryForDataDrivenSteps();
    }


    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
        stepEventBus().testFinished();
        stepEventBus().setTestSource(null);
    }

    private boolean webtestsAreSupported(Class testClass) {
        return TestCaseAnnotations.supportsWebTests(testClass);
    }

    private boolean webtestsAreSupported(Object test) {
        return TestCaseAnnotations.supportsWebTests(test.getClass());
    }


    public BaseStepListener getBaseStepListener() {
        return baseStepListener;
    }

    private BaseStepListener buildBaseStepListener() {
        if (pages != null) {
            return Listeners.getBaseStepListener()
                    .withPages(pages)
                    .and().withOutputDirectory(outputDirectory);
        } else {
            return Listeners.getBaseStepListener()
                    .withOutputDirectory(outputDirectory);
        }
    }


    private String getSpecifiedDriver(Class<?> klass) {
        if (ManagedWebDriverAnnotatedField.hasManagedWebdriverField(klass)) {
            return ManagedWebDriverAnnotatedField.findFirstAnnotatedField(klass).getDriver();
        } else {
            return null;
        }
    }

    /**
     * Ensure that the requested driver type is valid before we start the tests.
     * Otherwise, throw an InitializationError.
     */
    private void checkRequestedDriverType() {
        //TODO
        /*if (requestedDriverSpecified()) {
            SupportedWebDriver.getDriverTypeFor(requestedDriver);
        } else {
            getConfiguration().getDriverType();
        }*/
    }

    private boolean requestedDriverSpecified() {
        return !isEmpty(this.requestedDriver);
    }

    /**
     * The Configuration class manages output directories and driver types.
     * They can be defined as system values, or have sensible defaults.
     * @return the current configuration
     */
    protected Configuration getConfiguration() {
        return configuration;
    }


    protected void initStepEventBus() {
        StepEventBus.getEventBus().clear();
    }

    protected void injectDriverInto(final Object testCase) {
        TestCaseAnnotations.forTestCase(testCase).injectDrivers(ThucydidesWebDriverSupport.getDriver(),
                ThucydidesWebDriverSupport.getWebdriverManager());
        dependencyInjector.injectDependenciesInto(testCase);
    }

    private void initPagesObjectUsing(final WebDriver driver) {
        pages = new Pages(driver, getConfiguration());
        dependencyInjector = new PageObjectDependencyInjector(pages);
    }


    protected WebDriver driverFor(final Method method) {
        if (JUnit5TestMethodAnnotations.forTest(method).isDriverSpecified()) {
            String testSpecificDriver = JUnit5TestMethodAnnotations.forTest(method).specifiedDriver();
            return getDriver(testSpecificDriver);
        } else {
            return getDriver();
        }
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
     * Instantiates the @ManagedPages-annotated Pages instance using current WebDriver.
     * @param testCase A Serenity-annotated test class
     */
    protected void injectAnnotatedPagesObjectInto(final Object testCase) {
        StepAnnotations.injector().injectAnnotatedPagesObjectInto(testCase, pages);
    }


    private void initStepFactoryUsing(final Pages pagesObject) {
        stepFactory = StepFactory.getFactory().usingPages(pagesObject);
    }

    private void initStepFactory() {
        stepFactory = StepFactory.getFactory();
    }

    /**
     * Instantiates the @ManagedPages-annotated Pages instance using current WebDriver.
     * @param testCase A Serenity-annotated test class
     */
    protected void injectScenarioStepsInto(final Object testCase) {
        StepAnnotations.injector().injectScenarioStepsInto(testCase, stepFactory);
    }

    protected void injectEnvironmentVariablesInto(final Object testCase) {
        EnvironmentDependencyInjector environmentDependencyInjector = new EnvironmentDependencyInjector();
        environmentDependencyInjector.injectDependenciesInto(testCase);
    }

    private void useStepFactoryForDataDrivenSteps() {
        StepData.setDefaultStepFactory(stepFactory);
    }

    @Override
    public void handleTestExecutionException(ExtensionContext extensionContext, Throwable throwable) throws Throwable {
        System.out.println("HandleTestExecutionException ");
        stepEventBus().testFailed(throwable);
        throw throwable;
    }

    StepEventBus stepEventBus() {
        return baseStepListener.getEventBus();
    }


    private boolean dataDrivenTest() {
        //TODO
        return false;
        //return this instanceof TestClassRunnerForParameters;
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

    /**
     * Find the current set of test outcomes produced by the test execution.
     * @return the current list of test outcomes
     */
    public List<TestOutcome> getTestOutcomes() {
        return baseStepListener.getTestOutcomes();
    }

    protected void generateReports() {
        generateReportsFor(getTestOutcomes());
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

    private ReportService getReportService() {
        if (reportService == null) {
            reportService = new ReportService(getOutputDirectory(), getDefaultReporters());
        }
        return reportService;
    }

    public File getOutputDirectory() {
        return getConfiguration().getOutputDirectory();
    }
}
