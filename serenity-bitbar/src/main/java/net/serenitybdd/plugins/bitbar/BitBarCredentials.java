package net.serenitybdd.plugins.bitbar;

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.util.EnvironmentVariables;

import java.util.Optional;

/**
 * The BitBar api key will be read from the BITBAR_API_KEY  system properties,
 * or (if not defined there) from the bitbar.apiKey configuration properties
 */
class BitBarCredentials {

    private final EnvironmentVariables environmentVariables;

    public BitBarCredentials(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public static BitBarCredentials from(EnvironmentVariables environmentVariables) {
        return new BitBarCredentials(environmentVariables);
    }

    public String getApiKey() {
        return Optional.ofNullable(environmentVariables.getValue("BITBAR_API_KEY")).orElseGet(() ->
                EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty("bitbar.apiKey").orElse(""));
    }
}
