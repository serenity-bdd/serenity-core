package net.serenitybdd.cucumber;

import io.cucumber.core.options.RuntimeOptions;

/**
 * Standalone storage for Cucumber RuntimeOptions that does not depend on JUnit 4.
 *
 * <p>Previously, RuntimeOptions were stored as static fields in
 * {@code CucumberSerenityBaseRunner}, which extends JUnit 4's {@code ParentRunner}.
 * Any code that accessed the runtime options (even via static method calls) would
 * trigger class loading of the entire JUnit 4 hierarchy, causing
 * {@code NoClassDefFoundError} when JUnit 4 is not on the classpath (e.g. in
 * JUnit 5 + Cucumber setups).</p>
 *
 * <p>This class extracts the purely static runtime options storage so it can be
 * used without any JUnit 4 dependency.</p>
 */
public class CucumberRuntimeOptions {

    private static final ThreadLocal<RuntimeOptions> RUNTIME_OPTIONS = new ThreadLocal<>();

    private static volatile RuntimeOptions DEFAULT_RUNTIME_OPTIONS;

    private CucumberRuntimeOptions() {
        // utility class
    }

    public static void setRuntimeOptions(RuntimeOptions runtimeOptions) {
        RUNTIME_OPTIONS.set(runtimeOptions);
        DEFAULT_RUNTIME_OPTIONS = runtimeOptions;
    }

    public static RuntimeOptions currentRuntimeOptions() {
        RuntimeOptions threadLocal = RUNTIME_OPTIONS.get();
        return (threadLocal != null) ? threadLocal : DEFAULT_RUNTIME_OPTIONS;
    }
}
