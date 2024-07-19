package net.serenitybdd.plugins.lambdatest;

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.util.EnvironmentVariables;

/**
 * LambdaTest can be activated by setting the lambdatest.active variable to true,
 * or if the "LT:Options? properties are defined in the webdriver capabilities section of the serenity.conf file.
 */
class LambdaTestConfiguration {

    public static final String LT_OPTIONS = "webdriver.capabilities.\"LT:Options\"";

    static boolean isActiveFor(EnvironmentVariables environmentVariables) {
        if (EnvironmentSpecificConfiguration.from(environmentVariables).getBooleanProperty("lambdatest.active", false)) {
            return true;
        }
        if (!EnvironmentSpecificConfiguration.from(environmentVariables).getPropertiesWithPrefix(LT_OPTIONS).isEmpty()) {
            return true;
        }
        return EnvironmentSpecificConfiguration.from(environmentVariables)
                .getOptionalProperty("webdriver.remote.url").orElse("")
                .contains("lambdatest");
    }
}
