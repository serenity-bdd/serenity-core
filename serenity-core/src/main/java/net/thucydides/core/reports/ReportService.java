package net.thucydides.core.reports;

import com.google.common.util.concurrent.*;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.junit.JUnitXMLOutcomeReporter;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generates different Thucydides reports in a given output directory.
 */
@SuppressWarnings("restriction")
public class ReportService {

    /**
     * Where will the reports go?
     */
    private File outputDirectory;

    /**
     * These classes generate the reports from the test results.
     */
    private List<AcceptanceTestReporter> subscribedReporters;

    private JUnitXMLOutcomeReporter jUnitXMLOutcomeReporter;

    private ExecutorService executorService;

    private final static Logger LOGGER = LoggerFactory.getLogger(ReportService.class);

    @Inject
    public ReportService(final Configuration configuration) {
        this(configuration.getOutputDirectory(), getDefaultReporters());
    }

    /**
     * Reports are generated using the test results in a given directory.
     * The actual reports are generated using a set of reporter objects. The report service passes test outcomes
     * to the reporter objects, which generate different types of reports.
     *
     * @param outputDirectory     Where the test data is stored, and where the generated reports will go.
     * @param subscribedReporters A set of reporters that generate the actual reports.
     */
    public ReportService(final File outputDirectory, final Collection<AcceptanceTestReporter> subscribedReporters) {
        this.outputDirectory = outputDirectory;
        getSubscribedReporters().addAll(subscribedReporters);
        jUnitXMLOutcomeReporter = new JUnitXMLOutcomeReporter(outputDirectory);
        this.executorService = Injectors.getInjector().getInstance(ExecutorServiceProvider.class).getExecutorService();

    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public List<AcceptanceTestReporter> getSubscribedReporters() {
        if (subscribedReporters == null) {
            subscribedReporters = new ArrayList<>();
        }
        return subscribedReporters;
    }

    public void subscribe(final AcceptanceTestReporter reporter) {
        getSubscribedReporters().add(reporter);
    }

    public void useQualifier(final String qualifier) {
        for (AcceptanceTestReporter reporter : getSubscribedReporters()) {
            reporter.setQualifier(qualifier);
        }
    }

    /**
     * A test runner can generate reports via Reporter instances that subscribe
     * to the test runner. The test runner tells the reporter what directory to
     * place the reports in. Then, at the end of the test, the test runner
     * notifies these reporters of the test outcomes. The reporter's job is to
     * process each test run outcome and do whatever is appropriate.
     *
     * @param testOutcomeResults A list of test outcomes to use in report generation.
     *                           These may be stored in memory (e.g. by a Listener instance) or read from the XML
     *                           test results.
     */

    public void generateReportsFor(final List<TestOutcome> testOutcomeResults) {
        final TestOutcomes allTestOutcomes = TestOutcomes.of(testOutcomeResults);
        for (final AcceptanceTestReporter reporter : getSubscribedReporters()) {
            generateReportsFor(reporter, allTestOutcomes);
        }
    }

    private void generateReportsFor(final AcceptanceTestReporter reporter, final TestOutcomes testOutcomes) {
        LOGGER.info("Generating reports for " + testOutcomes.getTotalTestScenarios() +" test outcomes using: " + reporter);
        long t0 = System.currentTimeMillis();

        List<? extends TestOutcome> outcomes = testOutcomes.getOutcomes();

        final AtomicInteger remainingReportCount = new AtomicInteger(outcomes.size());
        for(final TestOutcome outcome : outcomes) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    LOGGER.info("Processing test outcome " + outcome.getCompleteName());
                    generateReportFor(outcome, testOutcomes, reporter);
                    remainingReportCount.decrementAndGet();
                    LOGGER.info("Processing test outcome " + outcome.getCompleteName() + " done");
                }
            });
        }
        generateJUnitTestResults(testOutcomes);
        waitForReportGenerationToFinish(remainingReportCount);
        LOGGER.info("Reports generated in: " + (System.currentTimeMillis() - t0) + " ms");

    }

    /**
     * JUnit test results are a special case, since they create a output file per test class (or story, or feature) rather than for each test.
     * @param outcomes
     */
    private void generateJUnitTestResults(TestOutcomes outcomes) {
        try {
            jUnitXMLOutcomeReporter.generateReportsFor(outcomes);
        } catch (IOException e) {
            throw new RuntimeException("Report generation failure", e);
        }
    }

    private void waitForReportGenerationToFinish(AtomicInteger reportCount) {
        while (reportCount.get() > 0) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * The default reporters applicable for standard test runs.
     *
     * @return a list of default reporters.
     */
    public static List<AcceptanceTestReporter> getDefaultReporters() {
        List<AcceptanceTestReporter> reporters = new ArrayList<AcceptanceTestReporter>();

        FormatConfiguration formatConfiguration
                = new FormatConfiguration(Injectors.getInjector().getProvider(EnvironmentVariables.class).get());

        ServiceLoader<AcceptanceTestReporter> reporterServiceLoader = ServiceLoader.load(AcceptanceTestReporter.class);
        Iterator<AcceptanceTestReporter> reporterImplementations = reporterServiceLoader.iterator();
        // Service.providers(AcceptanceTestReporter.class);

        LOGGER.info("Reporting formats: " + formatConfiguration.getFormats());

        while (reporterImplementations.hasNext()) {
            AcceptanceTestReporter reporter = reporterImplementations.next();
            LOGGER.info("Found reporter: " + reporter + "(format = " + reporter.getFormat() + ")");
            if (!reporter.getFormat().isPresent() || formatConfiguration.getFormats().contains(reporter.getFormat().get())) {
                LOGGER.info("Registering reporter: " + reporter);
                reporters.add(reporter);
            }
        }
        return reporters;
    }

    private void generateReportFor(final TestOutcome testOutcome,
                                   final TestOutcomes allTestOutcomes,
                                   final AcceptanceTestReporter reporter) {
        try {
            LOGGER.info(reporter + ": Generating report for test outcome: " + testOutcome.getCompleteName());
            reporter.setOutputDirectory(outputDirectory);
            reporter.generateReportFor(testOutcome, allTestOutcomes);
        } catch (IOException e) {
            throw new ReportGenerationFailedError(
                    "Failed to generate reports using " + reporter, e);
        }
    }

}
