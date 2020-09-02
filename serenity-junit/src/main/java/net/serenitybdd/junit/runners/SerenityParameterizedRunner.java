package net.serenitybdd.junit.runners;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.core.environment.WebDriverConfiguredEnvironment;
import net.thucydides.core.batches.BatchManager;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.DataTable;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.ReportService;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.tags.TagScanner;
import net.thucydides.core.tags.Taggable;
import net.thucydides.core.webdriver.DriverConfiguration;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.junit.ThucydidesJUnitSystemProperties;
import net.thucydides.junit.annotations.Concurrent;
import org.apache.commons.lang3.StringUtils;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Run a Serenity test suite using a set of data.
 * Similar to the JUnit parameterized tests, but better ;-).
 */
public class SerenityParameterizedRunner extends Suite implements Taggable {

    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    private List<Runner> runners = new ArrayList<>();

    private final DriverConfiguration configuration;
    private ReportService reportService;
    private final ParameterizedTestsOutcomeAggregator parameterizedTestsOutcomeAggregator = ParameterizedTestsOutcomeAggregator.from(this);
    private TagScanner tagScanner;

    /**
     * Test runner used for testing purposes.
     *
     * @param klass            The test class to run
     * @param configuration    current system configuration (usually mocked)
     * @param webDriverFactory a webdriver factory (can be mocked)
     * @param batchManager     a batch manager to process batched testing
     * @throws Throwable - cause anything can happen!
     */
    public SerenityParameterizedRunner(final Class<?> klass,
                                       DriverConfiguration configuration,
                                       final WebDriverFactory webDriverFactory,
                                       final BatchManager batchManager
    ) throws Throwable {
        super(klass, Collections.<Runner>emptyList());
        this.configuration = configuration;
        this.tagScanner = new TagScanner(configuration.getEnvironmentVariables());

        if (runTestsInParallelFor(klass)) {
            scheduleParallelTestRunsFor(klass);
        }

        DataDrivenAnnotations testClassAnnotations = getTestAnnotations();
        if (testClassAnnotations.hasTestDataDefined()) {
            runners = buildTestRunnersForEachDataSetUsing(webDriverFactory, batchManager);
        } else if (testClassAnnotations.hasTestDataSourceDefined()) {
            runners = buildTestRunnersFromADataSourceUsing(webDriverFactory, batchManager);
        }
    }

    private void scheduleParallelTestRunsFor(final Class<?> klass) {
        setScheduler(new ParameterizedRunnerScheduler(klass, getThreadCountFor(klass)));
    }

    public boolean runTestsInParallelFor(final Class<?> klass) {
        return (klass.getAnnotation(Concurrent.class) != null);
    }

    public int getThreadCountFor(final Class<?> klass) {
        Concurrent concurrent = klass.getAnnotation(Concurrent.class);
        String threadValue = getThreadParameter(concurrent);
        int threads = (AVAILABLE_PROCESSORS * 2);
        if (StringUtils.isNotEmpty(threadValue)) {
            if (StringUtils.isNumeric(threadValue)) {
                threads = Integer.parseInt(threadValue);
            } else if (threadValue.endsWith("x")) {
                threads = getRelativeThreadCount(threadValue);
            }

        }
        return threads;
    }

    private String getThreadParameter(Concurrent concurrent) {
//        String systemPropertyThreadValue =
//                configuration.getEnvironmentVariables().getProperty(ThucydidesJUnitSystemProperties.CONCURRENT_THREADS.getName());
//
        String systemPropertyThreadValue = EnvironmentSpecificConfiguration.from(configuration.getEnvironmentVariables())
                .getOptionalProperty(ThucydidesJUnitSystemProperties.CONCURRENT_THREADS.getName())
                .orElse(null);

        String annotatedThreadValue = concurrent.threads();
        return (StringUtils.isNotEmpty(systemPropertyThreadValue) ? systemPropertyThreadValue : annotatedThreadValue);

    }

    private int getRelativeThreadCount(final String threadValue) {
        try {
            String threadCount = threadValue.substring(0, threadValue.length() - 1);
            return Integer.parseInt(threadCount) * AVAILABLE_PROCESSORS;
        } catch (NumberFormatException cause) {
            throw new IllegalArgumentException("Illegal thread value: " + threadValue, cause);
        }
    }

    private List<Runner> buildTestRunnersForEachDataSetUsing(final WebDriverFactory webDriverFactory,
                                                     final BatchManager batchManager) throws Throwable {
        if (shouldSkipAllTests()) {
            return new ArrayList<>();
        }

        List<Runner> runners = new ArrayList<>();
        DataTable parametersTable = getTestAnnotations().getParametersTableFromTestDataAnnotation();
        for (int i = 0; i < parametersTable.getRows().size(); i++) {
            Class<?> testClass = getTestClass().getJavaClass();
            SerenityRunner runner = new TestClassRunnerForParameters(testClass,
                    configuration,
                    webDriverFactory,
                    batchManager,
                    parametersTable,
                    i);
            runner.useQualifier(from(parametersTable.getRows().get(i).getValues()));
            runners.add(runner);
        }
        return runners;
    }

    private List<Runner> buildTestRunnersFromADataSourceUsing(final WebDriverFactory webDriverFactory,
                                                      final BatchManager batchManager) throws Throwable {
        if (shouldSkipAllTests()) {
            return new ArrayList<>();
        }

        List<Runner> runners = new ArrayList<>();
        List<?> testCases = getTestAnnotations().getDataAsInstancesOf(getTestClass().getJavaClass());
        DataTable parametersTable = getTestAnnotations().getParametersTableFromTestDataSource();
        for (int i = 0; i < testCases.size(); i++) {
            Object testCase = testCases.get(i);
            SerenityRunner runner = new TestClassRunnerForInstanciatedTestCase(testCase,
                    configuration,
                    webDriverFactory,
                    batchManager,
                    parametersTable,
                    i);
            runner.useQualifier(getQualifierFor(testCase));
            runners.add(runner);
        }
        return runners;
    }

    private boolean shouldSkipTest(FrameworkMethod method) {
        return !tagScanner.shouldRunMethod(getTestClass().getJavaClass(), method.getName());
    }

    private boolean shouldSkipAllTests() {
        return getTestAnnotations()
                .getTestMethods()
                .stream()
                .allMatch(this::shouldSkipTest);
    }

    private String getQualifierFor(final Object testCase) {
        return QualifierFinder.forTestCase(testCase).getQualifier();
    }

    private DataDrivenAnnotations getTestAnnotations() {
        return DataDrivenAnnotations.forClass(getTestClass());
    }

    private String from(final Collection testData) {
        StringBuffer testDataQualifier = new StringBuffer();
        boolean firstEntry = true;
        for (Object testDataValue : testData) {
            if (!firstEntry) {
                testDataQualifier.append("/");
            }
            testDataQualifier.append(testDataValue);
            firstEntry = false;
        }
        return testDataQualifier.toString();
    }


    /**
     * Only called reflectively. Do not use programmatically.
     *
     * @param klass The test class to run
     * @throws Throwable Cause shit happens
     */
    public SerenityParameterizedRunner(final Class<?> klass) throws Throwable {
        this(klass, WebDriverConfiguredEnvironment.getDriverConfiguration(), new WebDriverFactory(),
                Injectors.getInjector().getInstance(BatchManager.class));
    }

    @Override
    protected List<Runner> getChildren() {
        return runners;
    }

    @Override
    public void run(final RunNotifier notifier) {
        try {
            super.run(notifier);
        } finally {
            StepEventBus.getEventBus().testSuiteFinished();
            generateReports();
        }
    }

    public void generateReports() {
        generateReportsFor(parameterizedTestsOutcomeAggregator.aggregateTestOutcomesByTestMethods());
    }

    private void generateReportsFor(List<TestOutcome> testOutcomes) {
        getReportService().generateReportsFor(testOutcomes);
        getReportService().generateConfigurationsReport();
    }

    private ReportService getReportService() {
        if (reportService == null) {
            reportService = new ReportService(getOutputDirectory(), getDefaultReporters());
        }
        return reportService;

    }

    private Collection<AcceptanceTestReporter> getDefaultReporters() {
        return ReportService.getDefaultReporters();
    }

    private File getOutputDirectory() {
        return this.configuration.getOutputDirectory();
    }

    public void subscribeReporter(final AcceptanceTestReporter reporter) {
        getReportService().subscribe(reporter);
    }

    public List<Runner> getRunners() {
        return runners;
    }


}
