package net.serenitybdd.core.di;

import net.serenitybdd.core.annotations.findby.di.ClasspathCustomFindByAnnotationProviderService;
import net.serenitybdd.core.annotations.findby.di.CustomFindByAnnotationProviderService;
import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.buildinfo.PropertyBasedDriverCapabilityRecord;
import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.core.time.SystemClock;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.annotations.locators.SmartElementProxyCreator;
import net.thucydides.core.annotations.locators.SmartWidgetProxyCreator;
import net.thucydides.core.batches.BatchManager;
import net.thucydides.core.batches.BatchStrategy;
import net.thucydides.core.batches.SystemVariableBasedBatchManager;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
import net.thucydides.core.configuration.WebDriverConfiguration;
import net.thucydides.core.environment.SystemEnvironmentVariables;
import net.thucydides.core.fixtureservices.ClasspathFixtureProviderService;
import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.core.logging.ConsoleLoggingListener;
import net.thucydides.core.reports.html.Formatter;
import net.thucydides.core.statistics.AtomicTestCount;
import net.thucydides.core.statistics.TestCount;
import net.thucydides.core.steps.StepListener;
import net.thucydides.core.steps.di.ClasspathDependencyInjectorService;
import net.thucydides.core.steps.di.DependencyInjectorService;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

/**
 * Maintain thread-local or thread-safe static instances of key infrastructure classes.
 * This class uses the Singleton design pattern to ensure that the same instance
 * of key classes are used throughout the application.
 */
public class SerenityInfrastructure {

    // Maintain a thread-safe static instance of the dependency injector service
    private static final DependencyInjectorService dependencyInjectorService = new ClasspathDependencyInjectorService();

    private static final ThreadLocal<CloseBrowser> closeBrowser
            = ThreadLocal.withInitial(() -> new WebdriverCloseBrowser(getEnvironmentVariables()));

    private static final CustomFindByAnnotationProviderService customFindByAnnotationProviderService
            = new ClasspathCustomFindByAnnotationProviderService();

    private static final FixtureProviderService fixtureProviderService = new ClasspathFixtureProviderService();

    private static final ThreadLocal<ConsoleLoggingListener> consoleLoggingListener = ThreadLocal.withInitial(() -> new ConsoleLoggingListener(getEnvironmentVariables()));

    private static final ThreadLocal<Formatter> formatter = ThreadLocal.withInitial(() -> new Formatter(getEnvironmentVariables()));

    private static final ElementProxyCreator elementProxyCreator = new SmartElementProxyCreator();

    private static final WidgetProxyCreator widgetProxyCreator = new SmartWidgetProxyCreator();

    private static final TestCount testCount = new AtomicTestCount();

    private static BatchManager batchManager;

    /**
     * Returns the application-wide {@link DependencyInjectorService} instance.
     *
     * @return the singleton {@link DependencyInjectorService} instance
     */
    public static DependencyInjectorService getDependencyInjectorService() {
        return dependencyInjectorService;
    }

    public static SystemClock getClock() {
        return ModelInfrastructure.getClock();
    }

    public static EnvironmentVariables getEnvironmentVariables() {
        return SystemEnvironmentVariables.currentEnvironmentVariables();
    }

    public static Configuration getConfiguration() {
        return ModelInfrastructure.getConfiguration();
    }

    public static DriverCapabilityRecord getDriverCapabilityRecord() {
        return new PropertyBasedDriverCapabilityRecord(getConfiguration());
    }

    public static DriverConfiguration getDriverConfiguration() {
        return new WebDriverConfiguration(getEnvironmentVariables());
    }

    public static CustomFindByAnnotationProviderService getCustomFindByAnnotationProviderService() {
        return customFindByAnnotationProviderService;
    }

    public static WebDriverFactory getWebDriverFactory() {
        return new WebDriverFactory(getEnvironmentVariables(),
                                    fixtureProviderService,
                                    closeBrowser.get());
    }

    public static FixtureProviderService getFixtureProviderService() {
        return fixtureProviderService;
    }

    public static CloseBrowser getCloseBrowser() {
        return closeBrowser.get();
    }

    public static StepListener getLoggingListener() {
        return consoleLoggingListener.get();
    }

    public static ElementProxyCreator getElementProxyCreator() {
        return elementProxyCreator;
    }

    public static WidgetProxyCreator getWidgetProxyCreator() {
        return widgetProxyCreator;
    }

    public static TestCount getTestCount() {
        return testCount;
    }

    public static Formatter getFormatter() {
        return formatter.get();
    }

    public static BatchManager getBatchManager() {
        if (batchManager == null) {
            Optional<String> batchStrategy = EnvironmentSpecificConfiguration.from(getEnvironmentVariables()).getOptionalProperty(ThucydidesSystemProperty.SERENITY_BATCH_STRATEGY);
            if (batchStrategy.isPresent()) {
                try {
                    batchManager = BatchStrategy.valueOf(batchStrategy.get()).instance(getEnvironmentVariables());
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            } else {
                batchManager = new SystemVariableBasedBatchManager(getEnvironmentVariables());
            }
        }
        return batchManager;
    }

    public static void resetBatchManager() {
        batchManager = null;
    }
}
