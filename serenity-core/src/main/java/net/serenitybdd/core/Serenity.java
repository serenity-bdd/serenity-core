package net.serenitybdd.core;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.ImmutableList;
import net.serenitybdd.core.di.DependencyInjector;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.serenitybdd.core.injectors.EnvironmentDependencyInjector;
import net.serenitybdd.core.sessions.TestSessionVariables;
import net.thucydides.core.annotations.TestCaseAnnotations;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.*;
import net.thucydides.core.steps.di.DependencyInjectorService;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.core.webdriver.WebdriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.File;
import java.util.List;

import static net.serenitybdd.core.webdriver.configuration.RestartBrowserForEach.*;

/**
 * A utility class that provides services to initialize web testing and reporting-related fields in arbitrary objects.
 * It is designed to help integrate Serenity into other testing tools such as Cucumber.
 */
public class Serenity {

    private static final ThreadLocal<WebDriverFactory> factoryThreadLocal = new ThreadLocal<WebDriverFactory>();
    private static final ThreadLocal<StepListener> stepListenerThreadLocal = new ThreadLocal<StepListener>();
    private static final ThreadLocal<TestSessionVariables> testSessionThreadLocal = new ThreadLocal<TestSessionVariables>();
    private static final ThreadLocal<FirefoxProfile> firefoxProfileThreadLocal = new ThreadLocal<>();

    /**
     * Initialize Serenity-related fields in the specified object.
     * This includes managed WebDriver instances,
     * @param testCase any object (testcase or other) containing injectable Serenity components
     */
    public static void initialize(final Object testCase) {
        ThucydidesWebDriverSupport.initialize();

        setupWebDriverFactory();
        setupWebdriverManager();

        ThucydidesWebDriverSupport.initializeFieldsIn(testCase);

        initStepListener();

        injectDriverInto(testCase);
        injectAnnotatedPagesObjectInto(testCase);
        injectScenarioStepsInto(testCase);


        injectDependenciesInto(testCase);
    }

    private static void injectDependenciesInto(Object testCase) {
        for(DependencyInjector dependencyInjector : getDependencyInjectors()) {
            dependencyInjector.injectDependenciesInto(testCase);
        }
    }

    private static void resetDependencyInjectors() {
        for(DependencyInjector dependencyInjector : getDependencyInjectors()) {
            dependencyInjector.reset();
        }
    }

    private static List<DependencyInjector> getDependencyInjectors() {
        List<DependencyInjector> dependencyInjectors = getDependencyInjectorService().findDependencyInjectors();
        dependencyInjectors.addAll(getDefaultDependencyInjectors());
        return dependencyInjectors;
    }

    private static DependencyInjectorService getDependencyInjectorService() {
        return Injectors.getInjector().getInstance(DependencyInjectorService.class);
    }

    private static List<DependencyInjector> getDefaultDependencyInjectors() {
        return ImmutableList.of(new PageObjectDependencyInjector(getPages()),
                                new EnvironmentDependencyInjector());
    }

    /**
     * Initialize Serenity-related fields in the specified object.
     * This includes managed WebDriver instances,
     * @param testCase any object (testcase or other) containing injectable Serenity components
     */
    public static SerenityConfigurer initializeWithNoStepListener(final Object testCase) {
        setupWebDriverFactory();
        setupWebdriverManager();

        ThucydidesWebDriverSupport.initialize();
        ThucydidesWebDriverSupport.initializeFieldsIn(testCase);

        injectDriverInto(testCase);
        injectAnnotatedPagesObjectInto(testCase);
        injectScenarioStepsInto(testCase);
        injectDependenciesInto(testCase);

        return new SerenityConfigurer();
    }


    private static void initStepListener() {
        Configuration configuration = ConfiguredEnvironment.getConfiguration();
        File outputDirectory = configuration.getOutputDirectory();
        StepListener listener  = new BaseStepListener(outputDirectory, getPages());
        stepListenerThreadLocal.set(listener);
        StepEventBus.getEventBus().registerListener(getStepListener());
    }

    private static void setupWebDriverFactory() {
        factoryThreadLocal.set(Injectors.getInjector().getInstance(WebDriverFactory.class));
    }

    /**
     * Instantiate the @Managed-annotated WebDriver instance with current WebDriver.
     * @param testCase any object (testcase or other) containing injectable Serenity components
     */
    protected static void injectDriverInto(final Object testCase) {
        TestCaseAnnotations.forTestCase(testCase).injectDrivers(ThucydidesWebDriverSupport.getDriver(),
                                                                ThucydidesWebDriverSupport.getWebdriverManager());

    }

    /**
     * Instantiates the @ManagedPages-annotated Pages instance using current WebDriver.
     * @param testCase any object (testcase or other) containing injectable Serenity components
     */
    public static void injectScenarioStepsInto(final Object testCase) {
        StepAnnotations.injectScenarioStepsInto(testCase, getStepFactory());
    }

    /**
     * Instantiates the @ManagedPages-annotated Pages instance using current WebDriver.
     * @param testCase any object (testcase or other) containing injectable Serenity components
     */
    protected static void injectAnnotatedPagesObjectInto(final Object testCase) {
        StepAnnotations.injectOptionalAnnotatedPagesObjectInto(testCase, getPages());
    }

   /**
     * Indicate that the test run using this object is finished, and reports can be generated.
     */
    public static void done() {
        EnvironmentVariables environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);
        boolean restartBrowserIfNecessary = !configuredIn(environmentVariables).restartBrowserForANew(NEVER);

        done(restartBrowserIfNecessary);
    }

    public static void done(boolean closeAllDrivers) {
        if (closeAllDrivers && getWebdriverManager() != null) {
            getWebdriverManager().closeAllDrivers();
        }
        notifyTestFinished();
        resetDependencyInjectors();
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

    protected static WebDriver getDriver() {
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
        ThucydidesWebDriverSupport.initialize(webdriverManager,"");
    }

    private static List<StepListener> stepListeners() {
        if (getStepListener() == null) {
           return Lists.newArrayList();
        }
        return Lists.newArrayList(getStepListener());
    }

    public static StepListener getStepListener() {
        return stepListenerThreadLocal.get();
    }

    public static void initializeTestSession() {
        getCurrentSession().clear();
    }

    public static SessionMap<Object, Object> getCurrentSession() {

        if (testSessionThreadLocal.get() == null) {
            testSessionThreadLocal.set(new TestSessionVariables());
        }
        return testSessionThreadLocal.get();
    }

    public static void pendingStep(String reason) {
        throw new PendingStepException(reason);
    }

    public static void ignoredStep(String reason) {
        throw new IgnoredStepException(reason);
    }

    public static void takeScreenshot() {
        StepEventBus.getEventBus().takeScreenshot();
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

    public static Pages getPagesFactory() {
        return null;
    }

    private static boolean throwExceptionsImmediately = false;

    public static void throwExceptionsImmediately() {
        throwExceptionsImmediately = true;
    }

    public static boolean shouldThrowErrorsImmediately() {
        return throwExceptionsImmediately;
    }

    public static class SerenityConfigurer {
        public SerenityConfigurer throwExceptionsImmediately() {
            Serenity.throwExceptionsImmediately();
            return this;
        }
    }
}
