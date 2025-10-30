package net.serenitybdd.core;

import net.serenitybdd.core.configurers.WebDriverConfigurer;
import net.serenitybdd.core.di.SerenityInfrastructure;
import net.serenitybdd.core.injectors.EnvironmentDependencyInjector;
import net.serenitybdd.core.lifecycle.LifecycleRegister;
import net.serenitybdd.core.reports.AddReportScreenshotEvent;
import net.serenitybdd.core.reports.ReportDataSaver;
import net.serenitybdd.core.reports.WithTitle;
import net.serenitybdd.core.sessions.TestSessionVariables;
import net.serenitybdd.model.IgnoredStepException;
import net.serenitybdd.model.PendingStepException;
import net.serenitybdd.model.collect.NewList;
import net.serenitybdd.model.di.DependencyInjector;
import net.serenitybdd.model.environment.ConfiguredEnvironment;
import net.thucydides.core.annotations.TestCaseAnnotations;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.*;
import net.thucydides.core.steps.session.TestSession;
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport;
import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.WebdriverManager;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.screenshots.ScreenshotAndHtmlSource;
import net.thucydides.model.steps.ExecutedStepDescription;
import net.thucydides.model.steps.StepFailure;
import net.thucydides.model.steps.StepListener;
import net.thucydides.model.steps.di.DependencyInjectorService;
import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.model.webdriver.Configuration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.serenitybdd.core.di.SerenityInfrastructure.getWebDriverFactory;
import static net.serenitybdd.core.webdriver.configuration.RestartBrowserForEach.NEVER;
import static net.serenitybdd.core.webdriver.configuration.RestartBrowserForEach.configuredIn;

/**
 * A utility class that provides services to initialize web testing and reporting-related fields in arbitrary objects.
 * It is designed to help integrate Serenity into other testing tools such as Cucumber.
 */
public class Serenity {

    private static final ThreadLocal<StepListener> stepListenerThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<TestSessionVariables> testSessionThreadLocal = ThreadLocal.withInitial(TestSessionVariables::new);
    private static final ThreadLocal<FirefoxProfile> firefoxProfileThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<Boolean> throwExceptionsImmediately = ThreadLocal.withInitial(() -> false);

    /**
     * Initialize Serenity-related fields in the specified object.
     * This includes managed WebDriver instances,
     *
     * @param testCase any object (testcase or other) containing injectable Serenity components
     */
    public static void initialize(final Object testCase) {
        ThucydidesWebDriverSupport.initialize();

        setupWebdriverManager();

        ThucydidesWebDriverSupport.initializeFieldsIn(testCase);

        initStepListener();

        injectDriverInto(testCase);
        injectAnnotatedPagesObjectInto(testCase);
        injectScenarioStepsInto(testCase);


        injectDependenciesInto(testCase);
    }

    public static void injectDependenciesInto(Object testCase) {
        for (DependencyInjector dependencyInjector : getDependencyInjectors()) {
            dependencyInjector.injectDependenciesInto(testCase);
        }
    }

    private static void resetDependencyInjectors() {
        for (DependencyInjector dependencyInjector : getDependencyInjectors()) {
            dependencyInjector.reset();
        }
    }

    private static List<DependencyInjector> getDependencyInjectors() {
        List<DependencyInjector> dependencyInjectors = getDependencyInjectorService().findDependencyInjectors();
        dependencyInjectors.addAll(getDefaultDependencyInjectors());
        return dependencyInjectors;
    }

    private static DependencyInjectorService getDependencyInjectorService() {
        return SerenityInfrastructure.getDependencyInjectorService();
//        return Injectors.getInjector().getInstance(DependencyInjectorService.class);
    }

    private static List<DependencyInjector> getDefaultDependencyInjectors() {

        return Arrays.asList(new PageObjectDependencyInjector(),
                new EnvironmentDependencyInjector());
    }

    /**
     * Initialize Serenity-related fields in the specified object.
     * This includes managed WebDriver instances,
     *
     * @param testCase any object (testcase or other) containing injectable Serenity components
     */
    public static void initializeWithNoStepListener(final Object testCase) {
        setupWebdriverManager();

        ThucydidesWebDriverSupport.initialize();
        ThucydidesWebDriverSupport.initializeFieldsIn(testCase);

        injectDriverInto(testCase);
        injectAnnotatedPagesObjectInto(testCase);
        injectScenarioStepsInto(testCase);
        injectDependenciesInto(testCase);
    }


    public static void initStepListener() {
        Configuration configuration = ConfiguredEnvironment.getConfiguration();
        File outputDirectory = configuration.getOutputDirectory();
        StepListener listener = new BaseStepListener(outputDirectory);
        stepListenerThreadLocal.set(listener);
        getStepEventBus().registerListener(getStepListener());
    }

    /**
     * Instantiate the @Managed-annotated WebDriver instance with current WebDriver.
     *
     * @param testCase any object (testcase or other) containing injectable Serenity components
     */
    public static void injectDriverInto(final Object testCase) {
        TestCaseAnnotations.forTestCase(testCase).injectDrivers(ThucydidesWebDriverSupport.getDriver(),
                ThucydidesWebDriverSupport.getWebdriverManager());
    }

    /**
     * Instantiates the @ManagedPages-annotated Pages instance using current WebDriver.
     *
     * @param testCase any object (testcase or other) containing injectable Serenity components
     */
    public static void injectScenarioStepsInto(final Object testCase) {
        StepAnnotations.injector().injectScenarioStepsInto(testCase, getStepFactory());
    }

    /**
     * Instantiates the @ManagedPages-annotated Pages instance using current WebDriver.
     *
     * @param testCase any object (testcase or other) containing injectable Serenity components
     */
    public static void injectAnnotatedPagesObjectInto(final Object testCase) {
        StepAnnotations.injector().injectOptionalAnnotatedPagesObjectInto(testCase, getPages());
    }

    /**
     * Indicate that the test run using this object is finished, and reports can be generated.
     */
    public static void done() {
        boolean restartBrowserIfNecessary = !configuredIn(environmentVariables()).restartBrowserForANew(NEVER);
        done(restartBrowserIfNecessary);
    }

    /**
     * Return the current environment variables configured for this test run.
     */
    public static EnvironmentVariables environmentVariables() {
        return SystemEnvironmentVariables.currentEnvironmentVariables();
    }

    public static boolean currentDriverIsDisabled() {
        WebDriver currentDriver = getWebdriverManager().getCurrentDriver();
        return (currentDriver instanceof WebDriverFacade) && (((WebDriverFacade) currentDriver).isDisabled());
    }

    public static void done(boolean closeAllDrivers) {
        if (closeAllDrivers && getWebdriverManager() != null) {
            getWebdriverManager().closeAllDrivers();
        }
        notifyTestFinished();
        resetDependencyInjectors();
        LifecycleRegister.clear();
    }

    private static void notifyTestFinished() {
        for (StepListener listener : stepListeners()) {
            listener.testRunFinished();
        }
    }

    public static String getCurrentSessionID() {
        if ((getWebdriverManager() != null) && (getWebdriverManager().getSessionId() != null)) {
            return getWebdriverManager().getSessionId().toString();
        }
        return null;
    }

    public static WebDriver getDriver() {
        return getWebdriverManager().getWebdriver();
    }

    /**
     * Return the actual WebDriver instance used by Serenity
     */
    public static WebDriver getProxiedDriver() {
        return ((WebDriverFacade)getWebdriverManager().getWebdriver()).getProxiedDriver();
    }

    /**
     * A convenience method that allows you to set your own driver for Serenity to use.
     * The driver will be used whenever the default driver for the current thread is requested.
     */
    public static WebDriver useDriver(WebDriver driver) {
        WebDriverFacade driverFacade = new WebDriverFacade(driver, getWebDriverFactory());
        getWebdriverManager().setCurrentDriver(driverFacade);
        return getWebdriverManager().getWebdriver();
    }

    protected static Pages getPages() {
        return ThucydidesWebDriverSupport.getPages();
    }

    protected static void stopUsingMockDriver() {
        setupWebdriverManager();
    }

    public static WebdriverManager getWebdriverManager() {
        return ThucydidesWebDriverSupport.getWebdriverManager();
    }

    public static StepFactory getStepFactory() {
        return ThucydidesWebDriverSupport.getStepFactory();
    }

    private static void setupWebdriverManager() {
        setupWebdriverManager(ThucydidesWebDriverSupport.getWebdriverManager());
    }

    private static void setupWebdriverManager(WebdriverManager webdriverManager) {
        ThucydidesWebDriverSupport.initialize(webdriverManager, "");
    }

    private static List<StepListener> stepListeners() {
        if (getStepListener() == null) {
            return new ArrayList<>();
        }
        return NewList.of(getStepListener());
    }

    public static StepListener getStepListener() {
        return stepListenerThreadLocal.get();
    }

    public static void initializeTestSession() {
        getCurrentSession().clear();
    }

    public static SessionMap<Object, Object> getCurrentSession() {
        return testSessionThreadLocal.get();
    }

    public static void pendingStep(String reason) {
        throw new PendingStepException(reason);
    }

    public static void ignoredStep(String reason) {
        throw new IgnoredStepException(reason);
    }

    public static void takeScreenshot() {
        getStepEventBus().takeScreenshot();
    }

    /**
     * Records a screenshot under the 'screenshotName' file name, having as content
     * the screenshot byte array.
     *
     * @param screenshotName the screenshot file name.
     * @param screenshot screenshot content as byte array.
     */
    public static void recordScreenshot(String screenshotName, byte[] screenshot) {
        if(!TestSession.isSessionStarted()) {
            getStepEventBus().recordScreenshot(screenshotName,screenshot);
        } else {
            TestSession.addEvent(new AddReportScreenshotEvent(screenshotName,screenshot));
        }
    }

    public static List<ScreenshotAndHtmlSource> takeScreenshots() {
        return getStepEventBus().takeScreenshots();
    }

    public static WithTitle recordReportData() {
        return new ReportDataSaver(getStepEventBus());
    }

    /**
     * @return The current working directory name is used as a default project key if no other key is provided.
     */
    public static String getDefaultProjectKey() {
        String workingDirPath = System.getProperty("user.dir");
        return new File(workingDirPath).getName();
    }

    public static void useFirefoxProfile(FirefoxProfile profile) {
        firefoxProfileThreadLocal.set(profile);
    }

    public static FirefoxProfile getFirefoxProfile() {
        return firefoxProfileThreadLocal.get();
    }

    public static boolean hasASessionVariableCalled(Object key) {
        return getCurrentSession().containsKey(key);
    }

    @SuppressWarnings("unchecked")
    public static <T> T sessionVariableCalled(Object key) {
        return (T) getCurrentSession().get(key);
    }

    public static SessionVariableSetter setSessionVariable(Object key) {
        return new SessionVariableSetter(key);
    }

    public static void clearSessionVariable(String key) {
        getCurrentSession().remove(key);
    }

    public static class SessionVariableSetter {
        final Object key;

        public SessionVariableSetter(Object key) {
            this.key = key;
        }

        public <T> void to(T value) {
            if (value != null) {
                Serenity.getCurrentSession().put(key, value);
            } else {
                Serenity.getCurrentSession().remove(key);
            }
        }
    }

    public static void clearCurrentSession() {
        Serenity.getCurrentSession().clear();
    }

    public static Pages getPagesFactory() {
        return null;
    }

    public static void throwExceptionsImmediately() {
        throwExceptionsImmediately.set(true);
    }

    public static boolean shouldThrowErrorsImmediately() {
        // Throw errors immediately if this is a Cucumber test
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return throwExceptionsImmediately.get() || Arrays.stream(stackTrace).anyMatch(element -> element.getClassName().contains("io.cucumber.core"));
    }

    public static WebDriverConfigurer webdriver() {
        return new WebDriverConfigurer();
    }

    /**
     * Perform an arbitrary task and record it as a step in the reports.
     * Any exceptions that occur will be reported and thrown
     */
    public static void reportThat(String message, Reportable reportableAction) {
        getStepEventBus().stepStarted(ExecutedStepDescription.withTitle(message));
        try {
            reportableAction.perform();
            getStepEventBus().stepFinished();
        } catch (Throwable assertionFailed) {
            getStepEventBus().stepFailed(new StepFailure(ExecutedStepDescription.withTitle(message), assertionFailed));
            throw assertionFailed;
        }
    }

    private static StepEventBus getStepEventBus() {
        if (TestSession.isSessionStarted()) {
            return TestSession.getTestSessionContext().getStepEventBus();
        }
        return StepEventBus.getParallelEventBus();
    }

    private static final LocalDateTime TEST_SUITE_START_TIME = LocalDateTime.now();

    public static LocalDateTime getTestSuiteStartTime() {
        return TEST_SUITE_START_TIME;
    }
}
