package net.serenitybdd.core.environment;

import net.serenitybdd.core.di.WebDriverInjectors;
import net.thucydides.core.configuration.WebDriverConfiguration;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.DriverConfiguration;

public class WebDriverConfiguredEnvironment {
    private static final ThreadLocal<EnvironmentVariables> testEnvironmentVariables = new ThreadLocal<>();
    private static final ThreadLocal<DriverConfiguration> testConfiguration = new ThreadLocal<>();
    
    public static void setTestEnvironmentVariables(EnvironmentVariables testEnvironment) {
        testEnvironmentVariables.set(testEnvironment);
        testConfiguration.set(new WebDriverConfiguration(testEnvironment));
    }

    public static EnvironmentVariables getEnvironmentVariables() {
        if (testEnvironmentVariables.get() != null) {
            return testEnvironmentVariables.get();
        }
        return Injectors.getInjector().getInstance(EnvironmentVariables.class);
    }

    public static DriverConfiguration getDriverConfiguration() {
        if (testConfiguration.get() != null) {
            return testConfiguration.get();
        }
        return WebDriverInjectors.getInjector().getInstance(DriverConfiguration.class);
    }

    public static void reset() {
        testEnvironmentVariables.remove();
        testConfiguration.remove();
    }
}