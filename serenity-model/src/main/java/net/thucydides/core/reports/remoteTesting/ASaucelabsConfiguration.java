package net.thucydides.core.reports.remoteTesting;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.util.EnvironmentVariables;

import static net.thucydides.core.ThucydidesSystemProperty.SAUCELABS_URL;

public class ASaucelabsConfiguration {

    public static boolean isDefinedIn(EnvironmentVariables environmentVariables) {
        return EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty(SAUCELABS_URL).isPresent();
    }

}
