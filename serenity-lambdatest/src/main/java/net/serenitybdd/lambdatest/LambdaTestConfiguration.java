package net.serenitybdd.lambdatest;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.util.EnvironmentVariables;

class LambdaTestConfiguration {
    static boolean isActiveFor(EnvironmentVariables environmentVariables) {
        if (EnvironmentSpecificConfiguration.from(environmentVariables).getBooleanProperty("lambdatest.active", false)) {
            return true;
        }
        if (!EnvironmentSpecificConfiguration.from(environmentVariables).getPropertiesWithPrefix("lambdatest").isEmpty()) {
            return true;
        }
        return EnvironmentSpecificConfiguration.from(environmentVariables)
                .getOptionalProperty("webdriver.remote.url").orElse("")
                .contains("lambdatest");
    }
}
