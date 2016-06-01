package net.serenitybdd.core;

import com.google.common.collect.ImmutableList;
import net.serenitybdd.core.di.DependencyInjector;
import net.serenitybdd.core.injectors.EnvironmentDependencyInjector;
import net.serenitybdd.core.sessions.TestSessionVariables;
import net.thucydides.core.annotations.TestCaseAnnotations;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.*;
import net.thucydides.core.steps.di.DependencyInjectorService;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.core.webdriver.WebdriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.File;
import java.util.List;

/**
 * A utility class that provides services to initialize web testing and reporting-related fields in arbitrary objects.
 * It is designed to help integrate Serenity into other testing tools such as Cucumber.
 */
public class Serenity {

    private static final ThreadLocal<WebDriverFactory> factoryThreadLocal = new ThreadLocal<WebDriverFactory>();
    private static final ThreadLocal<WebdriverManager> webdriverManagerThreadLocal = new ThreadLocal<WebdriverManager>();
    private static final ThreadLocal<Pages> pagesThreadLocal = new ThreadLocal<Pages>();
    private static final ThreadLocal<StepFactory> stepFactoryThreadLocal = new ThreadLocal<StepFactory>();
    private static final ThreadLocal<StepListener> stepListenerThreadLocal = new ThreadLocal<StepListener>();
    private static final ThreadLocal<TestSessionVariables> testSessionThreadLocal = new ThreadLocal<TestSessionVariables>();
    private static final ThreadLocal<FirefoxProfile> firefoxProfileThreadLocal = new ThreadLocal<>();
    private static final boolean AND_CLOSE_ALL_DRIVERS = true;

    /**
     * Initialize Serenity-related fields in the specified object.
     * This includes managed WebDriver instances,
     * @param testCase any object (testcase or other) containing injectable Serenity components
     */
    public static void initialize(final Object testCase) {
        setupWebDriverFactory();
        setupWebdriverManager();

        initPagesObjectUsing(getDriver());
        initStepListener();
        initStepFactoryUsing(getPages());

        injectDriverInto(testCase);
        injectAnnotatedPagesObjectInto(testCase);
        injectScenarioStepsInto(testCase);

        ThucydidesWebDriverSupport.initialize();
        ThucydidesWebDriverSupport.initializeFieldsIn(testCase);

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
        return ImmutableList.of((DependencyInjector) new PageObjectDependencyInjector(getPages()),
                (DependencyInjector) new EnvironmentDependencyInjector());
    }

    /**
     * Initialize Serenity-related fields in the specified object.
     * This includes managed WebDriver instances,
     * @param testCase any object (testcase or other) containing injectable Serenity components
     */
    public static SerenityConfigurer initializeWithNoStepListener(final Object testCase) {
        setupWebDriverFactory();
        setupWebdriverManager();

        initPagesObjectUsing(getDriver());
        initStepFactoryUsing(getPages());

        injectDriverInto(testCase);
        injectAnnotatedPagesObjectInto(testCase);
        injectScenarioStepsInto(testCase);
        ThucydidesWebDriverSupport.initializeFieldsIn(testCase);
        injectDependenciesInto(testCase);

        return new SerenityConfigurer();
    }



    private static void initStepListener() {
        Configuration configuration = Injectors.getInjector().getInstance(Configuration.class);
        File outputDirectory = configuration.getOutputDirectory();
        StepListener listener  = new BaseStepListener(outputDirectory, getPages());
        stepListenerThreadLocal.set(listener);
        StepEventBus.getEventBus().registerListener(getStepListener());
    }

    private static void setupWebDriverFactory() {
        factoryThreadLocal.set(Injectors.getInjector().getInstance(WebDriverFactory.class));
    }

    private static void initPagesObjectUsing(final WebDriver driver) {
        pagesThreadLocal.set(new Pages(driver));
    }

    private static void initStepFactoryUsing(final Pages pagesObject) {
        stepFactoryThreadLocal.set(new StepFactory(pagesObject));
    }

    /**
     * Instantiate the @Managed-annotated WebDriver instance with current WebDriver.
     * @param testCase any object (testcase or other) containing injectable Serenity components
     */
    protected static void injectDriverInto(final Object testCase) {
        TestCaseAnnotations.forTestCase(testCase).injectDrivers(getWebdriverManager());

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
        done(AND_CLOSE_ALL_DRIVERS);
    }

    public static void done(boolean closeAllDrivers) {
        if (closeAllDrivers && getWebdriverManager() != null) {
            getWebdriverManager().closeAllDrivers();
        }
        resetDependencyInjectors();
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
        return pagesThreadLocal.get();
    }

    protected static void stopUsingMockDriver() {
        setupWebdriverManager();
    }

    public static WebdriverManager getWebdriverManager() {
        return webdriverManagerThreadLocal.get();
    }

    public static StepFactory getStepFactory() {
        return stepFactoryThreadLocal.get();
    }

    private static void setupWebdriverManager() {
        setupWebdriverManager(Injectors.getInjector().getInstance(WebdriverManager.class));
    }

    private static void setupWebdriverManager(WebdriverManager webdriverManager) {
        webdriverManagerThreadLocal.set(webdriverManager);
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
