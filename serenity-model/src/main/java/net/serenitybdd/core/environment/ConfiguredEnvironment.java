package net.serenitybdd.core.environment;

import net.serenitybdd.core.di.ModelInfrastructure;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
import net.thucydides.core.environment.SystemEnvironmentVariables;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;

public class ConfiguredEnvironment {

    private static ThreadLocal<Configuration> currentConfiguration = ThreadLocal.withInitial(() -> ModelInfrastructure.getConfiguration());

    public static EnvironmentVariables getEnvironmentVariables() {
        return SystemEnvironmentVariables.currentEnvironmentVariables();
    }

    public static Configuration getConfiguration() {
        return currentConfiguration.get();
    }

    public static void updateConfiguration(EnvironmentVariables environmentVariables) {
        currentConfiguration.set(new SystemPropertiesConfiguration(environmentVariables));
    }

    public static void reset() {
        currentConfiguration.set(ModelInfrastructure.getConfiguration());
    }
}

