package net.thucydides.core.bootstrap;

import net.serenitybdd.model.environment.ConfiguredEnvironment;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.core.pages.Pages;
import net.thucydides.model.reports.ReportService;
import net.thucydides.core.steps.*;
import net.thucydides.model.steps.StepListener;
import net.thucydides.model.webdriver.Configuration;
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport;
import net.thucydides.core.webdriver.WebdriverManager;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * Container holding thread-local data related to a Thucydides test run.
 * This includes the StepFactory and the associated step listeners. These need to be thread-local so that,
 * if tests are run in parallel in different threads, the step listeners will still build up correct result trees
 * and report data.
 */
class ThucydidesContext {
    private static final ThreadLocal<ThucydidesContext> contextThreadLocal = new ThreadLocal<ThucydidesContext>();

    /**
     * Instruments step libraries to that any @Step-annotated methods called will appear in the test reports.
     */
    private final StepFactory stepFactory;

    /**
     * The main step listener used to record test results and outcomes.
     */
    private BaseStepListener stepListener;

    /**
     * Generates reports once the test outcomes have been recorded and built.
     */
    private final ReportService reportService;

    /**
     * Where are the Thucydides reports written to.
     * Normally defined in the system properties.
     */
    private File outputDirectory;

    private final String defaultDriver;

    private Pages pages;

    /**
     * Thucydides configuration data
     */
    private final Configuration configuration;

    private final WebdriverManager webdriverManager;

    private ThucydidesContext(StepListener... additionalListeners) {
        this(null, additionalListeners);
    }

    private ThucydidesContext(String defaultDriver, StepListener... additionalListeners) {
        configuration = ConfiguredEnvironment.getConfiguration();
        webdriverManager = ThucydidesWebDriverSupport.getWebdriverManager();
        outputDirectory = configuration.getOutputDirectory();
        this.defaultDriver = defaultDriver;
        if (defaultDriver != null) {
            pages =  new Pages(getDriver());
            stepFactory = StepFactory.getFactory().usingPages(pages);
        } else {
            stepFactory = StepFactory.getFactory();
        }
        registerStepListeners(additionalListeners);
        reportService = new ReportService(outputDirectory,
                ReportService.getDefaultReporters());
    }

    protected WebDriver getDriver() {
        return webdriverManager.getWebdriver(defaultDriver);
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    private void registerStepListeners(StepListener... additionalListeners) {
        stepListener = buildBaseStepListener();
        StepEventBus.getParallelEventBus().registerListener(stepListener);
        for (StepListener listener : additionalListeners) {
            StepEventBus.getParallelEventBus().registerListener(listener);
        }
    }

    public static ThucydidesContext newContext() {
        return new ThucydidesContext();
    }

    public static ThucydidesContext newContext(Optional<String> driver, StepListener... listeners) {
        ThucydidesContext context = null;
        context = (driver.isPresent()) ? new ThucydidesContext(driver.get(), listeners) : new ThucydidesContext(listeners);
        contextThreadLocal.set(context);
        return context;
    }

    public static ThucydidesContext getCurrentContext() {
        return contextThreadLocal.get();
    }

    /**
     * Injects instrumented step classes into any @Step annotated fields of the specified class.
     *
     * @param testCase
     */
    public void initialize(Object testCase) {
        StepAnnotations.injector().injectScenarioStepsInto(testCase, stepFactory);
    }


    public void generateReports() {
        reportService.generateReportsFor(latestTestOutcomes());
        reportService.generateConfigurationsReport();
    }

    private List<TestOutcome> latestTestOutcomes() {
        return stepListener.getTestOutcomes();
    }

    public void dropListeners() {
        StepEventBus.getParallelEventBus().dropAllListeners();
    }


    private BaseStepListener buildBaseStepListener() {
        return Listeners.getBaseStepListener().withOutputDirectory(outputDirectory);
    }
}
