package io.cucumber.junit;

import io.cucumber.core.eventbus.EventBus;
import io.cucumber.core.feature.FeatureParser;
import io.cucumber.core.options.RuntimeOptions;
import io.cucumber.core.options.RuntimeOptionsBuilder;
import io.cucumber.core.runtime.*;
import io.cucumber.core.runtime.Runtime;
import net.serenitybdd.core.di.SerenityInfrastructure;
import net.serenitybdd.cucumber.core.plugin.SerenityReporter;
import net.serenitybdd.cucumber.util.CucumberInternalUtils;
import net.thucydides.model.webdriver.Configuration;
import org.junit.runners.model.InitializationError;

import java.time.Clock;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Glue code for running Cucumber via Serenity with JUnit 4.
 * Sets up Serenity reporting and instrumentation.
 *
 * @deprecated JUnit 4 Cucumber support is deprecated and will be removed in Serenity 6.0.0.
 *             Please migrate to JUnit 5 (released in 2017). See the Serenity documentation
 *             for migration guidance. 
 *
 *             This class must remain in the io.cucumber.junit package to access package-private
 *             Cucumber classes (FeatureRunner, PickleRunners). This creates a split package
 *             violation that will prevent JPMS compatibility when Cucumber migrates to Java 17+.
 */
@Deprecated(since="4.3.5", forRemoval = true)
public class CucumberSerenityRunner extends CucumberSerenityBaseRunner {

    /**
     * Constructor called by JUnit.
     *
     * @param clazz the class with the @RunWith annotation.
     * @throws InitializationError if there is another problem
     */
    public CucumberSerenityRunner(Class clazz) throws InitializationError {
        super(clazz);
        CucumberInternalUtils.assertNoCucumberAnnotatedMethods(clazz);

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

        Configuration systemConfiguration = SerenityInfrastructure.getConfiguration();
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
            runtimeOptionsBuilder.addTagFilter(new CucumberInternalUtils.LiteralExpression(tagFilter));
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
