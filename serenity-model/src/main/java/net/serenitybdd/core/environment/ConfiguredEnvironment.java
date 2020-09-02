package net.serenitybdd.core.environment;

import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;

public class ConfiguredEnvironment {
    private static final ThreadLocal<EnvironmentVariables> testEnvironmentVariables = new ThreadLocal<>();

    public static EnvironmentVariables getEnvironmentVariables() {
        return Injectors.getInjector().getInstance(EnvironmentVariables.class);
    }

    public static Configuration getConfiguration() {
        return Injectors.getInjector().getInstance(Configuration.class);
    }

    public static void reset() {
        testEnvironmentVariables.remove();
    }
}