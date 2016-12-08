package net.serenitybdd.junit.runners;

import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.thucydides.core.batches.BatchManager;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.DataTable;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.ReportService;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.junit.ThucydidesJUnitSystemProperties;
import net.thucydides.junit.annotations.Concurrent;
import org.apache.commons.lang3.StringUtils;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Run a Serenity test suite using a set of data.
 * Similar to the JUnit parameterized tests, but better ;-).
 */
public class SerenityParameterizedRunner extends Suite {

    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    private final List<Runner> runners = new ArrayList<Runner>();

    private final Configuration configuration;
    private ReportService reportService;
    private final ParameterizedTestsOutcomeAggregator parameterizedTestsOutcomeAggregator = ParameterizedTestsOutcomeAggregator.from(this);

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
                                       Configuration configuration,
                                       final WebDriverFactory webDriverFactory,
                                       final BatchManager batchManager
    ) throws Throwable {
        super(klass, Collections.<Runner>emptyList());
        this.configuration = configuration;

        if (runTestsInParallelFor(klass)) {
            scheduleParallelTestRunsFor(klass);
        }

        DataDrivenAnnotations testClassAnnotations = getTestAnnotations();
        if (testClassAnnotations.hasTestDataDefined()) {
            buildTestRunnersForEachDataSetUsing(webDriverFactory, batchManager);
        } else if (testClassAnnotations.hasTestDataSourceDefined()) {
            buildTestRunnersFromADataSourceUsing(webDriverFactory, batchManager);
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
        String systemPropertyThreadValue =
                configuration.getEnvironmentVariables().getProperty(ThucydidesJUnitSystemProperties.CONCURRENT_THREADS.getName());
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

    private void buildTestRunnersForEachDataSetUsing(final WebDriverFactory webDriverFactory,
                                                     final BatchManager batchManager) throws Throwable {
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
    }

    private void buildTestRunnersFromADataSourceUsing(final WebDriverFactory webDriverFactory,
                                                      final BatchManager batchManager) throws Throwable {

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
        this(klass, ConfiguredEnvironment.getConfiguration(), new WebDriverFactory(),
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
