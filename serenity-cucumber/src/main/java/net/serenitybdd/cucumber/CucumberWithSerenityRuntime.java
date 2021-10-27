package net.serenitybdd.cucumber;


import io.cucumber.core.options.RuntimeOptions;
import io.cucumber.core.plugin.SerenityReporter;
import io.cucumber.core.runtime.Runtime;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.webdriver.Configuration;

import java.util.function.Supplier;


public class CucumberWithSerenityRuntime {

    public static Runtime using(Supplier<ClassLoader> classLoaderSupplier,
                                RuntimeOptions runtimeOptions) {
        Configuration systemConfiguration = Injectors.getInjector().getInstance(Configuration.class);
        return createSerenityEnabledRuntime(classLoaderSupplier, runtimeOptions, systemConfiguration);
    }


    private static Runtime createSerenityEnabledRuntime(Supplier<ClassLoader> classLoaderSupplier,
                                                        RuntimeOptions runtimeOptions,
                                                        Configuration systemConfiguration) {
        //ClassFinder resolvedClassFinder = Optional.ofNullable(classFinder).orElse(new ResourceLoaderClassFinder(resourceLoader, classLoader));
        SerenityReporter reporter = new SerenityReporter(systemConfiguration);
        //Runtime runtime = Runtime.builder().withResourceLoader(resourceLoader).withClassFinder(resolvedClassFinder).
        //        withClassLoader(classLoader).withRuntimeOptions(runtimeOptions).withAdditionalPlugins(reporter).build();

        Runtime runtime = Runtime.builder().
                withClassLoader(classLoaderSupplier).
                withRuntimeOptions(runtimeOptions).
                withAdditionalPlugins(reporter).build();
        return runtime;
    }
}
