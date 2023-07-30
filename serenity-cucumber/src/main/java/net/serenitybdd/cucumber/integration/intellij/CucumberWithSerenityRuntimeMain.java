package net.serenitybdd.cucumber.integration.intellij;

import io.cucumber.core.options.CommandlineOptionsParser;
import io.cucumber.core.options.RuntimeOptions;
import io.cucumber.core.resource.ClassLoaders;
import io.cucumber.core.runtime.Runtime;
import net.serenitybdd.core.di.SerenityInfrastructure;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import net.thucydides.core.webdriver.Configuration;

import java.io.IOException;
import java.util.function.Supplier;


/**
 * A test runner that allows you to run feature files directly from IntelliJ.
 * This avoids having to write specific runners for each feature file.
 * Contributed by Vladimir Ivanov
 * Deprecated: Replaced with cucumber.runtime.cli.Main
 */
@Deprecated
public class CucumberWithSerenityRuntimeMain {
    public static void main(String[] argv) throws Throwable {

        Supplier<ClassLoader> classLoader = ClassLoaders::getDefaultClassLoader;
        //byte exitStatus = run(argv, Thread.currentThread().getContextClassLoader());
        byte exitStatus = run(argv, classLoader);
        System.exit(exitStatus);
    }

    /**
     * Launches the Cucumber-JVM command line
     * @param argv runtime options. See details in the {@code cucumber.api.cli.Usage.txt} resource
     * @param classLoaderSupplier classloader used to load the runtime
     * @return 0 if execution was successful, 1 if not (there were test failures)
     * @throws IOException if resources couldn't be loaded during execution
     */
    public static byte run(String[] argv, Supplier<ClassLoader> classLoaderSupplier) throws IOException {
        
        RuntimeOptions  runtimeOptions = new CommandlineOptionsParser(System.out).parse(argv).build() ;

        //ResourceLoader resourceLoader = new MultiLoader(classLoader);
        Configuration systemConfiguration = SerenityInfrastructure.getConfiguration();
        //Supplier<ClassLoader> classLoader = ClassLoaders::getDefaultClassLoader;
        Runtime serenityRuntime = CucumberWithSerenity.createSerenityEnabledRuntime(/*resourceLoader,*/
                classLoaderSupplier,
                runtimeOptions,
                systemConfiguration);

        serenityRuntime.run();

        return serenityRuntime.exitStatus();
    }
}
