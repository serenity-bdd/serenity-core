package net.serenitybdd.cucumber.cli;

import io.cucumber.core.options.CommandlineOptionsParser;
import io.cucumber.core.options.RuntimeOptions;
import io.cucumber.core.resource.ClassLoaders;
import io.cucumber.core.runtime.Runtime;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import net.serenitybdd.cucumber.CucumberWithSerenityRuntime;

import java.io.IOException;
import java.util.function.Supplier;


public class Main {

    public static void main(String[] argv) throws Throwable {
        Supplier<ClassLoader> classLoaderSupplier = ClassLoaders::getDefaultClassLoader;
        byte exitstatus = run(argv,classLoaderSupplier );
        System.exit(exitstatus);
    }

    public static byte run(String[] argv, Supplier<ClassLoader> classLoaderSupplier) throws IOException {
        RuntimeOptions  runtimeOptions = new CommandlineOptionsParser(System.out).parse(argv).build() ;
        CucumberWithSerenity.setRuntimeOptions(runtimeOptions);
        Runtime runtime = CucumberWithSerenityRuntime.using(classLoaderSupplier, runtimeOptions);

        runtime.run();
        return runtime.exitStatus();
    }
}
