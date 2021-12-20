package io.cucumber.junit;

import io.cucumber.core.options.CucumberOptionsAnnotationParser;
import io.cucumber.core.options.RuntimeOptions;
import io.cucumber.core.options.RuntimeOptionsBuilder;
import io.cucumber.core.resource.ClassLoaders;
import io.cucumber.core.runtime.Runtime;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.SystemEnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import org.junit.runner.Computer;
import org.junit.runner.JUnitCore;

import java.io.File;
import java.util.function.Supplier;

/**
 * Created by john on 31/07/2014.
 */
public class CucumberRunner {

    public static void run(Class testClass) {
        JUnitCore jUnitCore = new JUnitCore();
        jUnitCore.run(new Computer(), testClass);
    }

    public static Runtime serenityRunnerForCucumberTestRunner(Class testClass, File outputDirectory) {
        return serenityRunnerForCucumberTestRunner(testClass, outputDirectory, new SystemEnvironmentVariables());
    }

    public static Runtime serenityRunnerForCucumberTestRunner(Class testClass,
                                                              File outputDirectory,
                                                              EnvironmentVariables environmentVariables) {

        // Parse the options early to provide fast feedback about invalid options
        RuntimeOptions annotationOptions = new CucumberOptionsAnnotationParser()
                .withOptionsProvider(new JUnitCucumberOptionsProvider())
                .parse(testClass)
                .build();

        Configuration systemConfiguration = new SystemPropertiesConfiguration(environmentVariables);
        systemConfiguration.setOutputDirectory(outputDirectory);
        Supplier<ClassLoader> classLoaderSupplier = ClassLoaders::getDefaultClassLoader;
        return CucumberWithSerenity.createSerenityEnabledRuntime(classLoaderSupplier, annotationOptions, systemConfiguration);
    }

    public static Runtime serenityRunnerForCucumberTestRunner(Class testClass, Configuration systemConfiguration) {
        ClassLoader classLoader = testClass.getClassLoader();

        // Parse the options early to provide fast feedback about invalid options
        RuntimeOptions annotationOptions = new CucumberOptionsAnnotationParser()
                .withOptionsProvider(new JUnitCucumberOptionsProvider())
                .parse(testClass)
                .build();

        RuntimeOptions runtimeOptionsBuilder =  new RuntimeOptionsBuilder().build(annotationOptions);

        Supplier<ClassLoader> classLoaderSupplier = ClassLoaders::getDefaultClassLoader;
        return CucumberWithSerenity.createSerenityEnabledRuntime( classLoaderSupplier,  runtimeOptionsBuilder, systemConfiguration);
    }

}
