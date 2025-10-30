package net.serenitybdd.core.environment;

import net.serenitybdd.core.di.SerenityInfrastructure;
import net.thucydides.core.configuration.WebDriverConfiguration;
import net.thucydides.core.webdriver.DriverConfiguration;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;

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
        return SystemEnvironmentVariables.currentEnvironmentVariables();
    }

    public static DriverConfiguration getDriverConfiguration() {
        if (testConfiguration.get() != null) {
            return testConfiguration.get();
        }
        return SerenityInfrastructure.getDriverConfiguration();
    }

    public static void reset() {
        testEnvironmentVariables.remove();
        testConfiguration.remove();
    }
}
