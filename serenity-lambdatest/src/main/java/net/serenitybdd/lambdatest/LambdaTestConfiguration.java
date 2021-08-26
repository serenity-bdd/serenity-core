package net.serenitybdd.lambdatest;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.util.EnvironmentVariables;

class LambdaTestConfiguration {
    static boolean isActiveFor(EnvironmentVariables environmentVariables) {
        return EnvironmentSpecificConfiguration.from(environmentVariables)
                .getOptionalProperty("webdriver.remote.url").orElse("")
                .contains("lambdatest");
    }
}
