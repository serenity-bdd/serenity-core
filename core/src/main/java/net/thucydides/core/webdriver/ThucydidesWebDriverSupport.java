package net.thucydides.core.webdriver;

import net.thucydides.core.annotations.TestCaseAnnotations;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.StepAnnotations;
import net.thucydides.core.steps.StepFactory;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A utility class that provides services to initialize web testing and reporting-related fields in arbitrary objects.
 * It is designed to help integrate Thucydides into other testing tools such as Cucumber.
 */
public class ThucydidesWebDriverSupport {

    private static final List<WebdriverManager> registeredWebdriverManagers = Collections.synchronizedList(new ArrayList<WebdriverManager>());

    private static final ThreadLocal<WebdriverManager> webdriverManagerThreadLocal = new ThreadLocal<WebdriverManager>();
    private static final ThreadLocal<Pages> pagesThreadLocal = new ThreadLocal<Pages>();
    private static final ThreadLocal<StepFactory> stepFactoryThreadLocal = new ThreadLocal<StepFactory>();
    private static final ThreadLocal<String> currentRequestedDriver = new ThreadLocal<String>();

    public static void initialize(String requestedDriver) {
        setRequestedDriverIfPresent(requestedDriver);
        setupWebdriverManager();
        initPagesObjectUsing(getDriver());
        initStepFactoryUsing(getPages());
    }

    private static void setRequestedDriverIfPresent(String requestedDriver) {
        if (StringUtils.isNotEmpty(requestedDriver)) {
            currentRequestedDriver.set(requestedDriver);
        } else {
            currentRequestedDriver.remove();
        }
    }

    public static void initialize() {
        initialize(null);
    }

    private static boolean webdriversInitialized() {
        return (webdriverManagerThreadLocal.get() != null);
    }

    private static void lazyInitalize() {
        if (!webdriversInitialized()) {
            initialize();
        }
    }

    public static void initializeFieldsIn(final Object testCase) {
        injectDriverInto(testCase);
        injectAnnotatedPagesObjectInto(testCase);
    }

    public static StepFactory getStepFactory() {
        lazyInitalize();
        return stepFactoryThreadLocal.get();
    }

    public static WebDriver getDriver() {
        if (currentRequestedDriver.get() != null) {
            return getWebdriverManager().getWebdriver(currentRequestedDriver.get());
        } else {
            return getWebdriverManager().getWebdriver();
        }
    }

    public static void closeCurrentDrivers() {
        if (webdriversInitialized()) {
            getWebdriverManager().closeAllCurrentDrivers();
        }
    }

    public static void closeAllDrivers() {
        if (webdriversInitialized()) {
            getWebdriverManager().closeAllDrivers();
        }
    }

    private static void setupWebdriverManager() {
        setupWebdriverManager(Injectors.getInjector().getInstance(WebdriverManager.class));
    }

    private static void setupWebdriverManager(WebdriverManager webdriverManager) {
        webdriverManagerThreadLocal.set(webdriverManager);
    }

    private static void initStepFactoryUsing(final Pages pagesObject) {
        stepFactoryThreadLocal.set(new StepFactory(pagesObject));
    }


    private static WebdriverManager getWebdriverManager() {
        lazyInitalize();
        return webdriverManagerThreadLocal.get();
    }

    private static void initPagesObjectUsing(final WebDriver driver) {
        pagesThreadLocal.set(new Pages(driver));
    }

    public static Pages getPages() {
        lazyInitalize();
        return pagesThreadLocal.get();
    }

    /**
     * Instantiate the @Managed-annotated WebDriver instance with current WebDriver.
     */
    protected static void injectDriverInto(final Object testCase) {
        TestCaseAnnotations.forTestCase(testCase).injectDriver(getDriver());
    }

    /**
     * Instantiates the @ManagedPages-annotated Pages instance using current WebDriver.
     */
    protected static void injectAnnotatedPagesObjectInto(final Object testCase) {
        StepAnnotations.injectOptionalAnnotatedPagesObjectInto(testCase, getPages());
    }
}
