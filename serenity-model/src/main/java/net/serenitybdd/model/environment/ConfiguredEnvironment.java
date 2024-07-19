package net.serenitybdd.model.environment;

import net.serenitybdd.model.di.ModelInfrastructure;
import net.thucydides.model.configuration.SystemPropertiesConfiguration;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.model.webdriver.Configuration;

public class ConfiguredEnvironment {

    private static final ThreadLocal<Configuration> currentConfiguration = ThreadLocal.withInitial(() -> ModelInfrastructure.getConfiguration());

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

