package net.serenitybdd.core.environment;

import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;

public class ConfiguredEnvironment {
    private static final ThreadLocal<EnvironmentVariables> testEnvironmentVariables = new ThreadLocal<>();
    private static final ThreadLocal<Configuration> testConfiguration = new ThreadLocal<>();

    public static void setTestEnvironmentVariables(EnvironmentVariables testEnvironment) {
        testEnvironmentVariables.set(testEnvironment);
        testConfiguration.set(new SystemPropertiesConfiguration(testEnvironment));
    }

    public static EnvironmentVariables getEnvironmentVariables() {
        if (testEnvironmentVariables.get() != null) {
            return testEnvironmentVariables.get();
        }
        return Injectors.getInjector().getInstance(EnvironmentVariables.class);
    }

    public static Configuration getConfiguration() {
        if (testConfiguration.get() != null) {
            return testConfiguration.get();
        }
        return Injectors.getInjector().getInstance(Configuration.class);
    }

    public static void reset() {
        testEnvironmentVariables.remove();
        testConfiguration.remove();
    }
}