package io.cucumber.junit;

import io.cucumber.core.eventbus.EventBus;
import io.cucumber.core.feature.FeatureParser;
import io.cucumber.core.filter.Filters;
import io.cucumber.core.gherkin.Feature;
import io.cucumber.core.gherkin.Pickle;
import io.cucumber.core.options.*;
import io.cucumber.core.plugin.PluginFactory;
import io.cucumber.core.plugin.Plugins;
import io.cucumber.core.plugin.SerenityReporter;
import io.cucumber.core.resource.ClassLoaders;
import io.cucumber.core.runtime.Runtime;
import io.cucumber.core.runtime.*;
import io.cucumber.plugin.Plugin;
import net.serenitybdd.cucumber.SerenityOptions;
import net.serenitybdd.cucumber.suiteslicing.ScenarioFilter;
import net.serenitybdd.cucumber.suiteslicing.WeightedCucumberScenarios;
import net.serenitybdd.cucumber.util.PathUtils;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.webdriver.Configuration;
import org.jetbrains.annotations.NotNull;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static io.cucumber.junit.FileNameCompatibleNames.uniqueSuffix;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * Glue code for running Cucumber via Serenity.
 * Sets up Serenity reporting and instrumentation.
 */
public class CucumberSerenityRunner extends CucumberSerenityBaseRunner {

    /**
     * Constructor called by JUnit.
     *
     * @param clazz the class with the @RunWith annotation.
     * @throws InitializationError if there is another problem
     */
    public CucumberSerenityRunner(Class clazz) throws InitializationError {
        super(clazz);
        Assertions.assertNoCucumberAnnotatedMethods(clazz);

        RuntimeOptions runtimeOptions = createRuntimeOptions(clazz);
        JUnitOptions junitOptions = createJUnitOptions(clazz);
        initializeBus();
        setRuntimeOptions(runtimeOptions);

        parseFeaturesEarly();

        // Create plugins after feature parsing to avoid the creation of empty files on lexer errors.
        plugins = new Plugins(new PluginFactory(), runtimeOptions);
        ExitStatus exitStatus = new ExitStatus(runtimeOptions);
        plugins.addPlugin(exitStatus);

        ThreadLocalRunnerSupplier runnerSupplier = initializeServices(clazz, runtimeOptions);

        Configuration systemConfiguration = Injectors.getInjector().getInstance(Configuration.class);
        SerenityReporter reporter = new SerenityReporter(systemConfiguration);
        addSerenityReporterPlugin(plugins, reporter);

        this.context = new CucumberExecutionContext(bus, exitStatus, runnerSupplier);

        createFeatureRunners(features, runtimeOptions, junitOptions);
    }


    public static Runtime createSerenityEnabledRuntime(/*ResourceLoader resourceLoader,*/
            Supplier<ClassLoader> classLoaderSupplier,
            RuntimeOptions runtimeOptions,
            Configuration systemConfiguration) {
        RuntimeOptionsBuilder runtimeOptionsBuilder = new RuntimeOptionsBuilder();
        Collection<String> allTagFilters = environmentSpecifiedTags(runtimeOptions.getTagExpressions());
        for (String tagFilter : allTagFilters) {
            runtimeOptionsBuilder.addTagFilter(new LiteralExpression(tagFilter));
        }
        runtimeOptionsBuilder.build(runtimeOptions);
        setRuntimeOptions(runtimeOptions);


        EventBus bus = new TimeServiceEventBus(Clock.systemUTC(), UUID::randomUUID);
        FeatureParser parser = new FeatureParser(bus::generateId);
        FeaturePathFeatureSupplier featureSupplier = new FeaturePathFeatureSupplier(classLoaderSupplier, runtimeOptions, parser);

        SerenityReporter serenityReporter = new SerenityReporter(systemConfiguration);
        Runtime runtime = Runtime.builder().withClassLoader(classLoaderSupplier).withRuntimeOptions(runtimeOptions).
                withAdditionalPlugins(serenityReporter).
                withEventBus(bus).withFeatureSupplier(featureSupplier).
                build();

        return runtime;
    }

    private static void addSerenityReporterPlugin(Plugins plugins, SerenityReporter plugin) {
        for (Plugin currentPlugin : plugins.getPlugins()) {
            if (currentPlugin instanceof SerenityReporter) {
                return;
            }
        }
        plugins.addPlugin(plugin);
    }
}
