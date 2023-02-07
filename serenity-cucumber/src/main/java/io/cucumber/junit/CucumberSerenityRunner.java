package io.cucumber.junit;

import io.cucumber.core.eventbus.EventBus;
import io.cucumber.core.feature.FeatureParser;
import io.cucumber.core.options.RuntimeOptions;
import io.cucumber.core.options.RuntimeOptionsBuilder;
import io.cucumber.core.plugin.SerenityReporter;
import io.cucumber.core.runtime.Runtime;
import io.cucumber.core.runtime.*;
import java.time.Clock;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Supplier;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.webdriver.Configuration;
import org.junit.runners.model.InitializationError;

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
        initiatePluginsList(runtimeOptions);
        ExitStatus exitStatus = new ExitStatus(runtimeOptions);
        addPlugin(exitStatus);

        ThreadLocalRunnerSupplier runnerSupplier = initializeServices(clazz, runtimeOptions);

        Configuration systemConfiguration = Injectors.getInjector().getInstance(Configuration.class);
        SerenityReporter reporter = new SerenityReporter(systemConfiguration);
        addPluginIfNotInList(reporter, SerenityReporter.class);

        initiateContext(exitStatus, runnerSupplier);

        createFeatureRunners(getFeatures(), runtimeOptions, junitOptions);
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

        return Runtime.builder().withClassLoader(classLoaderSupplier).withRuntimeOptions(runtimeOptions).
                withAdditionalPlugins(serenityReporter).
                withEventBus(bus).withFeatureSupplier(featureSupplier).
                build();
    }
}
